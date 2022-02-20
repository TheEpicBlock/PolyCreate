package nl.theepicblock.polycreate;

import net.minecraft.util.math.Quaternion;
import org.jetbrains.annotations.Nullable;

public interface WizardWithRotation {
    void setRotation(Quaternion q);
    @Nullable Quaternion getRotation();
}
