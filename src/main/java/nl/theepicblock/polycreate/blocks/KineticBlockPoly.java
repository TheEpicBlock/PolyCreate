package nl.theepicblock.polycreate.blocks;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.item.CustomModelDataManager;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.UpdateInfo;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.json.JModelWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import nl.theepicblock.polycreate.CMDHelper;
import nl.theepicblock.polycreate.VSmallItemStand;

public class KineticBlockPoly implements BlockPoly {
    public final static Item[] RESOURCE_GEN_ITEMS = {
            Items.WHITE_GLAZED_TERRACOTTA,
            Items.GRAY_GLAZED_TERRACOTTA,
            Items.BLACK_GLAZED_TERRACOTTA
    };
    protected final ItemStack stack;

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
        CMDHelper.addOverrideToPack(stack, pack, generatedModelId);

        var generatedModel = new JModelWrapper();
        generatedModel.setParent("create:item/"+blockId.getPath());
        VSmallItemStand.applyDisplay(generatedModel);
        pack.setItemModel(generatedModelId.getNamespace(), generatedModelId.getPath(), generatedModel);
        pack.importRequirements(moddedResources, generatedModel, logger);
    }

    @Override
    public boolean hasWizard() {
        return true;
    }

    @Override
    public Wizard createWizard(WizardInfo info) {
        return new KineticWizard(info, stack);
    }

    static class KineticWizard extends Wizard {
        private final VSmallItemStand mainStand;
        private final Direction.Axis axis;
        private volatile float speed = 0;

        public KineticWizard(WizardInfo info, ItemStack displayStack) {
            super(info);
            mainStand = new VSmallItemStand(displayStack);
            this.axis = this.getBlockState().get(Properties.AXIS);
        }

        @Override
        public void addPlayer(PacketConsumer players) {
            mainStand.spawn(players, this.getPosition().add(0, 0.5, 0));
            var be = (KineticTileEntity)this.getBlockEntity();
            if (be == null) return;

            players.sendPacket(getHeadRotationPacket(be.getSpeed(), this.getWorld().getServer().getTicks(), 0));
        }

        @Override
        public void onMove(PacketConsumer players) {
            mainStand.move(players, getPosition(), (byte)0, (byte)0, false);
        }

        @Override
        public void onTick(PacketConsumer players) {
            var be = (KineticTileEntity)this.getBlockEntity();
            if (be == null) return;
            this.speed = be.getSpeed();
        }

        @Override
        public void update(PacketConsumer players, UpdateInfo info) {
            if (speed == 0) return;
            var packet = getHeadRotationPacket(this.speed, info.getTick(), info.getTickDelta());

            players.sendPacket(packet);
        }

        public EntityTrackerUpdateS2CPacket getHeadRotationPacket(float speed, int tick, float tickDelta) {
            var pos = this.getBlockPos();
            // Usually this offset only applies to the cog itself. But we're rendering the cog together with the shaft,
            // this is why we can't offset only large cogs.
            float offset = /*ICogWheel.isLargeCog(be.getCachedState()) ? 11.25f :*/ 0;
            double d = (((axis == Direction.Axis.X) ? 0 : pos.getX()) + ((axis == Direction.Axis.Y) ? 0 : pos.getY())
                    + ((axis == Direction.Axis.Z) ? 0 : pos.getZ())) % 2;
            if (d == 0)
                offset = 22.5f;

            var rotation = this.getWorld() == null ? 0 : ((tick+tickDelta) * -speed * 3f / 10 + offset) % 360;
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
            return mainStand.headRotationPacket(p, y, r);
        }

        @Override
        public boolean needsTicking() {
            return true;
        }

        @Override
        public void removePlayer(PacketConsumer players) {
            mainStand.remove(players);
        }
    }
}
