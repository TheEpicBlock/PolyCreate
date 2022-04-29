package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import io.github.theepicblock.polymc.api.wizard.PlayerView;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.util.math.Direction;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;
import nl.theepicblock.polycreate.mixin.ControlledContraptionEntityAccessor;

public class ControlledContraptionWizard extends ContraptionWizard<ControlledContraptionEntity> {
    public ControlledContraptionWizard(WizardInfo info, ControlledContraptionEntity entity, SmallModelForAllBlocksManager blocksManager) {
        super(info, entity, blocksManager);
    }

    @Override
    public void onMove(PlayerView players) {
        var entity = (ControlledContraptionEntityAccessor)this.getEntity();
        var hasRotated = entity.getPrevAngle() != entity.getAngle();
        standBlocks.forEach((pos, stand) -> {
            var standPos = getStandPos(pos);
            players.forEach(player -> {
                if (hasRotated) {
                    switch (entity.getRotationAxis()) {
                        case X -> stand.sendHeadRotation(player, entity.getAngle(), 0, 0);
                        case Y -> stand.move(player, standPos, (byte)((int)(-entity.getAngle() * 256.0F / 360.0F)), (byte)0, false);
                        case Z -> stand.sendHeadRotation(player, 0, 0, -entity.getAngle());
                    }
                }
                if (!hasRotated || entity.getRotationAxis() != Direction.Axis.Y) {
                    stand.move(player, standPos, (byte)0, (byte)0, false);
                }
            });
        });
        wizardBlocks.forEach((pos, wizard) -> {
            // TODO rotation
            wizard.onMove();
        });
    }
}
