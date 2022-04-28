package nl.theepicblock.polycreate.mixin;

import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlledContraptionEntity.class)
public interface ControlledContraptionEntityAccessor {
    @Accessor
    float getPrevAngle();
    @Accessor
    float getAngle();
    @Accessor
    Direction.Axis getRotationAxis();
}
