package nl.theepicblock.polycreate.entity;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.poly.entity.EntityWizard;
import net.minecraft.entity.Entity;

public class SuperGlueWizard extends EntityWizard<Entity> {
    public SuperGlueWizard(WizardInfo info, Entity entity) {
        super(info, entity);
    }

    @Override
    public void addPlayer(PacketConsumer player) {

    }

    @Override
    public void removePlayer(PacketConsumer player) {

    }
}
