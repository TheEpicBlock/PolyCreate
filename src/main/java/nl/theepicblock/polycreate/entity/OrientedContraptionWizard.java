package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.UpdateInfo;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;

public class OrientedContraptionWizard extends ContraptionWizard<OrientedContraptionEntity> {
    public OrientedContraptionWizard(WizardInfo info, OrientedContraptionEntity entity, SmallModelForAllBlocksManager blocksManager) {
        super(info, entity, blocksManager);
    }

    @Override
    public void update(PacketConsumer players, UpdateInfo info) {
        var entity = this.getEntity();
        var hasRotated = entity.prevYaw != entity.yaw || entity.prevPitch != entity.pitch;
        standBlocks.forEach((pos, stand) -> {
            var standPos = getStandPos(pos, info);
            stand.move(players, standPos, (byte)0, (byte)0, false);
            if (hasRotated) {
                stand.sendHeadRotation(players, entity.pitch, entity.yaw, 0);
            }
        });
        wizardBlocks.forEach((pos, wizard) -> {
            // TODO rotation
            wizard.onMove(players);
        });
    }

    @Override
    protected Vec3d getStandPos(BlockPos pos, UpdateInfo info) {
        return super.getStandPos(pos, info).subtract(0.5, 0, 0.5);
    }
}
