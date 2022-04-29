package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.foundation.utility.VecHelper;
import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.api.wizard.PlayerView;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.poly.entity.EntityWizard;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nl.theepicblock.polycreate.SmallModelForAllBlocksManager;
import nl.theepicblock.polycreate.VSmallItemStand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ContraptionWizard<T extends AbstractContraptionEntity> extends EntityWizard<T> {
    protected final Map<BlockPos,Wizard> wizardBlocks = new HashMap<>();
    protected final Map<BlockPos,VSmallItemStand> standBlocks = new HashMap<>();

    public ContraptionWizard(WizardInfo info, T entity, SmallModelForAllBlocksManager blocksManager) {
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
            stand.spawn(playerEntity, getStandPos(pos));
        });
        wizardBlocks.forEach((pos, wizard) -> {
            wizard.addPlayer(playerEntity);
        });
    }

    @Override
    public void onMove(PlayerView players) {
        standBlocks.forEach((pos, stand) -> {
            var standPos = getStandPos(pos);
            players.forEach(player -> {
                stand.move(player, standPos, (byte)0, (byte)0, false);
            });
        });
        wizardBlocks.forEach((pos, wizard) -> {
            wizard.onMove();
        });
    }

    protected Vec3d getStandPos(BlockPos pos) {
        return this.getEntity().toGlobalVector(VecHelper.getCenterOf(pos), 0);
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

    @Override
    public @NotNull Vec3d getPosition() {
        return getEntity().getAnchorVec();
    }

    private Contraption getContraption() {
        return this.getEntity().getContraption();
    }
}
