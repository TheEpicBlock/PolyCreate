package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import io.github.theepicblock.polymc.api.wizard.PlayerView;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;

public class OrientedContraptionWizard extends ContraptionWizard<OrientedContraptionEntity> {
    public OrientedContraptionWizard(WizardInfo info, OrientedContraptionEntity entity, SmallModelForAllBlocksManager blocksManager) {
        super(info, entity, blocksManager);
    }

    @Override
    public void onMove(PlayerView players) {
        var entity = this.getEntity();
        var hasRotated = entity.prevYaw != entity.yaw || entity.prevPitch != entity.pitch;
        standBlocks.forEach((pos, stand) -> {
            var standPos = getStandPos(pos);
            players.forEach(player -> {
                stand.move(player, standPos, (byte)0, (byte)0, false);
                if (hasRotated) {
                    stand.sendHeadRotation(player, entity.pitch, entity.yaw, 0);
                }
            });
        });
        wizardBlocks.forEach((pos, wizard) -> {
            // TODO rotation
            wizard.onMove(players);
        });
    }
}
