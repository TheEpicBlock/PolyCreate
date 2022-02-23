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

                Identifier wrapperId = new Identifier("polycreate", "smallstand/" + Integer.toHexString(variant.hashCode()));
                if (pack.getItemModel(wrapperId.getNamespace(), wrapperId.getPath()) == null) {
                    var wrapperModel = JModel.create();
                    wrapperModel.setParent(variant.model());
                    VSmallItemStand.applyDisplay(wrapperModel);

                    var originalDisplay = wrapperModel.getDisplay(JModelDisplayType.HEAD);
                    assert originalDisplay != null;
                    // Fixes rotations to match those specified
                    wrapperModel.setDisplay(JModelDisplayType.HEAD, new JModelDisplay(
                            new double[]{(originalDisplay.rotation()[0] + variant.x()) % 360, (originalDisplay.rotation()[1] + (variant.y() * (variant.x() == 180 ? 1 : -1)) + 180) % 360, originalDisplay.rotation()[2]},
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
}
