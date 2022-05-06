package nl.theepicblock.polycreate;

import io.github.theepicblock.polymc.api.resource.json.JModel;
import io.github.theepicblock.polymc.api.resource.json.JModelDisplay;
import io.github.theepicblock.polymc.api.resource.json.JModelDisplayType;
import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class VSmallItemStand extends VArmorStand {
    public static final double HEAD_Y_OFFSET = 0.72599999999999997868371792719699442386627197265625;
    private final ItemStack stack;

    public VSmallItemStand(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void spawn(PacketConsumer players, Vec3d pos) {
        super.spawn(players, pos.add(0, -HEAD_Y_OFFSET, 0));
        this.sendArmorStandFlags(players, true, false, true, true);
        this.sendFlags(players, false, false, false, false, true, false, false);
        this.sendSingleSlot(players, EquipmentSlot.HEAD, this.stack);
    }

    @Override
    public void move(PacketConsumer players, double x, double y, double z, byte yaw, byte pitch, boolean onGround) {
        super.move(players, x, y-HEAD_Y_OFFSET, z, yaw, pitch, onGround);
    }

    public static void applyDisplay(JModel model) {
        double scaling = 1f/((10f/16f)*0.7f);
        model.setDisplay(JModelDisplayType.HEAD,
                new JModelDisplay(
                        new double[]{0, 0, 0},
                        new double[]{0,-6.4,0},
                        new double[]{scaling, scaling, scaling}));
    }
}
