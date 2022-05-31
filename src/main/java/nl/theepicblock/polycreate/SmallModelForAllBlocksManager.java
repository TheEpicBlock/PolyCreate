package nl.theepicblock.polycreate;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.SharedValuesKey;
import io.github.theepicblock.polymc.api.item.CustomModelDataManager;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.json.JModel;
import io.github.theepicblock.polymc.api.resource.json.JModelDisplay;
import io.github.theepicblock.polymc.api.resource.json.JModelDisplayType;
import io.github.theepicblock.polymc.api.resource.json.JModelOverride;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.ResourceConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

public class SmallModelForAllBlocksManager implements SharedValuesKey.ResourceContainer{
    public static final SharedValuesKey<SmallModelForAllBlocksManager> KEY = new SharedValuesKey<>(SmallModelForAllBlocksManager::new, (it) -> it);
    private final int cmdOffset;
    private final Item item;

    public SmallModelForAllBlocksManager(PolyRegistry registry) {
        var cmdManager = registry.getSharedValues(CustomModelDataManager.KEY);
        var idList = Block.STATE_IDS;
        var cmdValues = cmdManager.requestCMD(new Item[]{Items.RED_DYE}, idList.size());
        this.cmdOffset = cmdValues.getRight();
        this.item = cmdValues.getLeft();
    }

    public ItemStack getItemForState(BlockState state) {
        var stack = new ItemStack(this.item);
        CMDHelper.addCmdToNbt(stack, cmdOffset + Block.getRawIdFromState(state));
        return stack;
    }

    @Override
    public void addToResourcePack(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {
        // We need to access vanilla blockstate files
        moddedResources = moddedResources.includeClientJar(logger);

        var clientItemId = Registry.ITEM.getId(this.item);
        for (var block : Registry.BLOCK) {
            var blockId = Registry.BLOCK.getId(block);
            var blockstateDef = moddedResources.getBlockState(blockId.getNamespace(), blockId.getPath());
            if (blockstateDef == null) continue;
            for (var state : block.getStateManager().getStates()) {
                var cmd = this.cmdOffset + Block.getRawIdFromState(state);

                var variants = blockstateDef.getVariantsBestMatching(state);
                if (variants == null || variants.length < 1) {
                    //logger.warn("(small stand model generator) couldn't find an appropriate blockstate variant for "+state);
                    continue;
                }
                var variant = variants[0]; // We won't support randomized models here

                if (isModelBanned(variant.model())) {
                    continue;
                }
                Identifier wrapperId = new Identifier("polycreate", "smallstand/" + Integer.toHexString(variant.hashCode()));
                if (pack.getItemModel(wrapperId.getNamespace(), wrapperId.getPath()) == null) {
                    var wrapperModel = JModel.create();
                    wrapperModel.setParent(variant.model());
                    VSmallItemStand.applyDisplay(wrapperModel);

                    var originalDisplay = wrapperModel.getDisplay(JModelDisplayType.HEAD);
                    assert originalDisplay != null;

                    // Add the rotations of the block state variant to the model display
                    var rotation = new Quaternion((float)originalDisplay.rotation()[0], (float)originalDisplay.rotation()[1], (float)originalDisplay.rotation()[2], true);
                    rotation.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(180)); //Counteracts native stand rotation
                    rotation.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(-variant.y()));
                    rotation.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(-variant.x()));
                    var rotationEuler = quaterniun(rotation);
                    wrapperModel.setDisplay(JModelDisplayType.HEAD, new JModelDisplay(
                            new double[]{rotationEuler.getX(), rotationEuler.getY(), rotationEuler.getZ()},
                            originalDisplay.translation(),
                            originalDisplay.scale()
                    ));

                    pack.setItemModel(wrapperId.getNamespace(), wrapperId.getPath(), wrapperModel);
                    pack.importRequirements(moddedResources, wrapperModel, logger);
                }

                // Get the json for the vanilla item, so we can inject an override into it
                var clientItemModel = pack.getOrDefaultVanillaItemModel(clientItemId.getNamespace(), clientItemId.getPath());
                // Add the override
                clientItemModel.getOverrides().add(JModelOverride.ofCMD(cmd, ResourceConstants.itemLocation(wrapperId)));
            }
        }
    }

    public static Vec3f quaterniun(Quaternion q) {
        var x = q.getX();
        var y = q.getY();
        var z = q.getZ();
        var w = q.getW();
        return new Vec3f(
                round(radians2degrees((float)Math.atan2(-2.0*(y*z - w*x), w*w - y*y - x*x + z*z))),
                round(radians2degrees((float)Math.asin(2.0*(x*z + w*y)))),
                round(radians2degrees((float)Math.atan2(-2.0*(y*x - w*z), w*w - y*y + x*x - z*z)))
        );
    }

    public static float radians2degrees(float in) {
        return (float)(in/(2*Math.PI)*360);
    }

    public static float round(float in) {
        if (89.5 < in && in < 90.5) {
            return 90;
        }
        if (-90.5 < in && in < -89.5) {
            return -90;
        }
        if (179.5 < in && in < 180.5) {
            return 180;
        }
        if (-0.5 < in && in < 0.5 || 359.5 < in && in < 360.5) {
            return 0;
        }
        return in;
    }

    private static boolean isModelBanned(String id) {
        return id.equals("packages:special/package") || id.equals("bitsandchisels:block/bits_block_model") || id.equals("packages:special/package_maker") || id.contains("consistency_plus"); // I'm blocking consistency plus just to keep the amount of models reasonable
    }
}
