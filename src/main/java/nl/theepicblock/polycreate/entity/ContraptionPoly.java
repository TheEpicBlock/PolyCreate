package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;

public class ContraptionPoly implements EntityPoly<AbstractContraptionEntity> {
    private final SmallModelForAllBlocksManager blocksManager;

    public ContraptionPoly(PolyRegistry registry) {
        this.blocksManager = registry.getSharedValues(SmallModelForAllBlocksManager.KEY);
    }


    @Override
    public Wizard createWizard(ServerWorld world, Vec3d pos, AbstractContraptionEntity entity) {
        return new ContraptionWizard(world, pos, entity, this.blocksManager);
    }
}
