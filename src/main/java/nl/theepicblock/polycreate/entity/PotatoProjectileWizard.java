package nl.theepicblock.polycreate.entity;

import com.simibubi.create.content.curiosities.weapons.PotatoProjectileEntity;
import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.VSnowball;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.poly.entity.EntityWizard;
import net.minecraft.util.math.Vec3d;

public class PotatoProjectileWizard extends EntityWizard<PotatoProjectileEntity> {
    private final VSnowball snowball = new VSnowball();

    public PotatoProjectileWizard(WizardInfo info, PotatoProjectileEntity entity) {
        super(info, entity);
    }

    @Override
    public void addPlayer(PacketConsumer players) {
        var source = this.getEntity();
        var ownerid = source.getOwner() == null ? 0 : source.getOwner().getId();
        snowball.spawn(players, this.getPosition(), source.getPitch(), source.getYaw(), ownerid, new Vec3d(source.powerX, source.powerY, source.powerZ));
        snowball.sendItem(players, source.getItem());
    }

    @Override
    public void onMove(PacketConsumer players) {
        var source = this.getEntity();
        snowball.move(players, this.getPosition(), (byte)((int)(source.getYaw() * 256.0F / 360.0F)), (byte)((int)(source.getPitch() * 256.0F / 360.0F)), source.isOnGround());
        snowball.sendVelocity(players, source.getVelocity().add(source.powerX, source.powerY, source.powerZ));
    }

    @Override
    public void removePlayer(PacketConsumer players) {
        snowball.remove(players);
    }
}
