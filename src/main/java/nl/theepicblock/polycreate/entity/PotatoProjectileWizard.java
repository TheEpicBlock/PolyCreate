package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.curiosities.weapons.PotatoProjectileEntity;
import io.github.theepicblock.polymc.api.wizard.PlayerView;
import io.github.theepicblock.polymc.api.wizard.VSnowball;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.poly.entity.EntityWizard;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PotatoProjectileWizard extends EntityWizard<PotatoProjectileEntity> {
    private final VSnowball snowball = new VSnowball();

    public PotatoProjectileWizard(WizardInfo info, PotatoProjectileEntity entity) {
        super(info, entity);
    }

    @Override
    public void addPlayer(ServerPlayerEntity playerEntity) {
        var source = this.getEntity();
        var ownerid = source.getOwner() == null ? 0 : source.getOwner().getId();
        snowball.spawn(playerEntity, this.getPosition(), source.getPitch(), source.getYaw(), ownerid, new Vec3d(source.powerX, source.powerY, source.powerZ));
        snowball.sendItem(playerEntity, source.getItem());
    }

    @Override
    public void onMove(PlayerView players) {
        var source = this.getEntity();
        players.forEach(player -> {
            snowball.move(player, this.getPosition(), (byte)((int)(source.getYaw() * 256.0F / 360.0F)), (byte)((int)(source.getPitch() * 256.0F / 360.0F)), source.isOnGround());
            snowball.sendVelocity(player, source.getVelocity().add(source.powerX, source.powerY, source.powerZ));
        });
    }

    @Override
    public void removePlayer(ServerPlayerEntity playerEntity) {
        snowball.remove(playerEntity);
    }
}
