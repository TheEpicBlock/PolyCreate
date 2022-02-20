package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.impl.poly.entity.EntityWizard;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;
import nl.theepicblock.polycreate.VSmallItemStand;

import java.util.HashMap;
import java.util.Map;

public class ContraptionWizard extends EntityWizard<AbstractContraptionEntity> {
    private final Map<BlockPos,Wizard> wizardBlocks = new HashMap<>();
    private final Map<BlockPos,VSmallItemStand> standBlocks = new HashMap<>();

    public ContraptionWizard(ServerWorld world, Vec3d position, AbstractContraptionEntity entity, SmallModelForAllBlocksManager blocksManager) {
        super(world, position, entity);

        var polyMap= PolyMc.getMainMap();
        getContraption().getBlocks().values().forEach(blockInfo -> {
            var blockPoly = polyMap.getBlockPoly(blockInfo.state.getBlock());
            if (blockPoly != null && blockPoly.hasWizard()) {
                var pos = this.getPosition().add(blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ());
                wizardBlocks.put(blockInfo.pos, blockPoly.createWizard(world, pos, WizardState.MISC_MOVING));
            } else {
                standBlocks.put(blockInfo.pos, new VSmallItemStand(blocksManager.getItemForState(blockInfo.state)));
            }
        });
    }

    @Override
    public void addPlayer(ServerPlayerEntity playerEntity) {
        standBlocks.forEach((pos, stand) -> {
            stand.spawn(playerEntity, this.getPosition().add(pos.getX(), pos.getY()+0.5, pos.getZ()));
        });
    }

    @Override
    public void onMove() {
        var players = this.getPlayersWatchingChunk();
        standBlocks.forEach((pos, stand) -> {
            players.forEach(player -> {
                stand.move(player, this.getPosition().add(pos.getX(), pos.getY()+0.5, pos.getZ()), (byte)0, (byte)0, false);
            });
        });
    }

    @Override
    public void removePlayer(ServerPlayerEntity playerEntity) {
        standBlocks.values().forEach(stand -> {
            stand.remove(playerEntity);
        });
    }

    private Contraption getContraption() {
        return this.getEntity().getContraption();
    }
}
