package nl.theepicblock.polycreate.blocks;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.item.CustomModelDataManager;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.json.JModelWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import nl.theepicblock.polycreate.CMDHelper;
import nl.theepicblock.polycreate.VSmallItemStand;

public class KineticBlockPoly implements BlockPoly {
    public final static Item[] RESOURCE_GEN_ITEMS = {
            Items.WHITE_GLAZED_TERRACOTTA,
            Items.GRAY_GLAZED_TERRACOTTA,
            Items.BLACK_GLAZED_TERRACOTTA
    };
    private final ItemStack stack;

    public KineticBlockPoly(PolyRegistry registry) {
        stack = CMDHelper.itemStackFromManager(registry.getSharedValues(CustomModelDataManager.KEY), RESOURCE_GEN_ITEMS);
    }

    @Override
    public BlockState getClientBlock(BlockState input) {
        return Blocks.CHAIN.getDefaultState().with(Properties.AXIS, input.get(Properties.AXIS));
    }

    @Override
    public void addToResourcePack(Block block, ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {
        var blockId = Registry.BLOCK.getId(block);
        var generatedModelId = new Identifier("polycreate", blockId.getPath());
        CMDHelper.addToResourcePack(stack, pack, generatedModelId);

        var generatedModel = new JModelWrapper();
        generatedModel.setParent("create:item/"+blockId.getPath());
        VSmallItemStand.applyDisplay(generatedModel);
        pack.setItemModel(generatedModelId.getNamespace(), generatedModelId.getPath(), generatedModel);
    }

    @Override
    public boolean hasWizard() {
        return true;
    }

    @Override
    public Wizard createWizard(ServerWorld world, Vec3d pos, Wizard.WizardState state) {
        return new KineticWizard(world, pos, state, stack);
    }

    static class KineticWizard extends Wizard {
        private final VSmallItemStand mainStand;
        private final Direction.Axis axis;

        public KineticWizard(ServerWorld world, Vec3d position, WizardState state, ItemStack displayStack) {
            super(world, position, state);
            mainStand = new VSmallItemStand(displayStack);
            this.axis = this.getBlockState().get(Properties.AXIS);
        }

        @Override
        public void addPlayer(ServerPlayerEntity playerEntity) {
            mainStand.spawn(playerEntity, this.getPosition().add(0, 0.5, 0));
            var be = (KineticTileEntity)this.getBlockEntity();
            if (be == null) return;

            var rotation = (this.getWorld().getServer().getTicks() * be.getSpeed() * 3f / 10) % 360;
            float y,r,p;
            if (axis == Direction.Axis.X) {
                p = 0;
                y = rotation;
                r = 90;
            } else if (axis == Direction.Axis.Y) {
                p = 0;
                y = rotation;
                r = 0;
            } else {
                p = 90;
                y = 0;
                r = rotation;
            }
            mainStand.sendHeadRotation(playerEntity, p, y, r);
        }

        @Override
        public void onMove() {
            this.getPlayersWatchingChunk().forEach(player -> {
                mainStand.move(player, getPosition(), (byte)0, (byte)0, false);
            });
            super.onMove();
        }

        @Override
        public void onTick() {
            var be = (KineticTileEntity)this.getBlockEntity();
            if (be == null) return;

            if (be.getSpeed() == 0) return;
            var rotation = (this.getWorld().getServer().getTicks() * be.getSpeed() * 3f / 10) % 360;
            float y,r,p;
            if (axis == Direction.Axis.X) {
                p = 0;
                y = rotation;
                r = 90;
            } else if (axis == Direction.Axis.Y) {
                p = 0;
                y = rotation;
                r = 0;
            } else {
                p = 90;
                y = 0;
                r = rotation;
            }
            this.getPlayersWatchingChunk().forEach(player -> mainStand.sendHeadRotation(player, p, y, r));
        }

        @Override
        public boolean needsTicking() {
            return true;
        }

        @Override
        public void removePlayer(ServerPlayerEntity playerEntity) {
            mainStand.remove(playerEntity);
        }
    }
}
