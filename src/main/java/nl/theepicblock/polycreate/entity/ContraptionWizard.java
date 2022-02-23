package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.poly.entity.EntityWizard;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;
import nl.theepicblock.polycreate.VSmallItemStand;

import java.util.HashMap;
import java.util.Map;

public class ContraptionWizard extends EntityWizard<AbstractContraptionEntity> {
    private final Map<BlockPos,Wizard> wizardBlocks = new HashMap<>();
    private final Map<BlockPos,VSmallItemStand> standBlocks = new HashMap<>();

    public ContraptionWizard(WizardInfo info, AbstractContraptionEntity entity, SmallModelForAllBlocksManager blocksManager) {
        super(info, entity);

        var polyMap= PolyMc.getMainMap();
        getContraption().getBlocks().values().forEach(blockInfo -> {
            var blockPoly = polyMap.getBlockPoly(blockInfo.state.getBlock());
            if (blockPoly != null && blockPoly.hasWizard()) {
                wizardBlocks.put(blockInfo.pos, blockPoly.createWizard(new ContraptionWizardInfo(blockInfo.pos, this, getContraption().getContraptionWorld())));
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
        wizardBlocks.forEach((pos, wizard) -> {
            wizard.addPlayer(playerEntity);
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
        wizardBlocks.forEach((pos, wizard) -> {
            wizard.onMove();
        });
    }

    @Override
    public void removePlayer(ServerPlayerEntity playerEntity) {
        standBlocks.values().forEach(stand -> {
            stand.remove(playerEntity);
        });
        wizardBlocks.forEach((pos, wizard) -> {
            wizard.removePlayer(playerEntity);
        });
    }

    private Contraption getContraption() {
        return this.getEntity().getContraption();
    }
}
