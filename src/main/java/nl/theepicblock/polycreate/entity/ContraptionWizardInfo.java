package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionWorld;
import io.github.theepicblock.polymc.api.wizard.UpdateInfo;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContraptionWizardInfo implements WizardInfo {
    private final BlockPos pos;
    private final ContraptionWizard source;
    private final ContraptionWorld world;

    public ContraptionWizardInfo(BlockPos pos, ContraptionWizard source, ContraptionWorld world) {
        this.pos = pos;
        this.source = source;
        this.world = world;
    }

    @Override
    public @NotNull Vec3d getPosition() {
        return source.getPosition().add(pos.getX(), pos.getY()+0.5, pos.getZ());
    }

    @Override
    public @NotNull Vec3d getPosition(UpdateInfo info) {
        return source.getPosition(info).add(pos.getX(), pos.getY()+0.5, pos.getZ());
    }

    @Override
    public @Nullable BlockPos getBlockPos() {
        return pos;
    }

    @Override
    public @Nullable BlockState getBlockState() {
        return world.getBlockState(pos);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return world.getBlockEntity(pos);
    }

    @Override
    public @Nullable ServerWorld getWorld() {
        return source.getWorld();
    }
}
