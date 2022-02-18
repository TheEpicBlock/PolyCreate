package nl.theepicblock.polycreate.mixin;

import com.simibubi.create.content.curiosities.armor.AllArmorMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AllArmorMaterials.class)
public class FixArmorMaterial {
    @Shadow @Final private String name;

    public String getName() {
        return this.name;
    }
}
