package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;

public class ContraptionPoly implements EntityPoly<AbstractContraptionEntity> {
    private final SmallModelForAllBlocksManager blocksManager;

    public ContraptionPoly(PolyRegistry registry) {
        this.blocksManager = registry.getSharedValues(SmallModelForAllBlocksManager.KEY);
    }


    @Override
    public Wizard createWizard(WizardInfo info, AbstractContraptionEntity entity) {
        if (entity instanceof OrientedContraptionEntity oEntity) {
            return new OrientedContraptionWizard(info, oEntity, this.blocksManager);
        }
        if (entity instanceof ControlledContraptionEntity cEntity) {
            return new ControlledContraptionWizard(info, cEntity, this.blocksManager);
        }
        return new ContraptionWizard<>(info, entity, this.blocksManager);
    }
}
