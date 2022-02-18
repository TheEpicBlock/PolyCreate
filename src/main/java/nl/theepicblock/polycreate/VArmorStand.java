package nl.theepicblock.polycreate;

import com.mojang.datafixers.util.Pair;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import io.github.theepicblock.polymc.mixins.wizards.EntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.EulerAngle;

import java.util.Collections;

public class VArmorStand extends AbstractVirtualEntity {
    @Override
    public EntityType<?> getEntityType() {
        return EntityType.ARMOR_STAND;
    }

    public void sendArmorStandFlags(ServerPlayerEntity playerEntity, boolean small, boolean showArms, boolean hideBasePlate, boolean isMarker) {
        byte flag = 0;
        if (small)         flag += ArmorStandEntity.SMALL_FLAG;
        if (showArms)      flag += ArmorStandEntity.SHOW_ARMS_FLAG;
        if (hideBasePlate) flag += ArmorStandEntity.HIDE_BASE_PLATE_FLAG;
        if (isMarker)      flag += ArmorStandEntity.MARKER_FLAG;

        playerEntity.networkHandler.sendPacket(EntityUtil.createDataTrackerUpdate(
                this.id,
                ArmorStandEntity.ARMOR_STAND_FLAGS,
                flag
        ));
    }

    public void sendSingleSlot(ServerPlayerEntity playerEntity, EquipmentSlot slot, ItemStack stack) {
        playerEntity.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(this.id, Collections.singletonList(new Pair<>(slot, stack))));
    }

    public void sendHeadRotation(ServerPlayerEntity playerEntity, EulerAngle angle) {
        playerEntity.networkHandler.sendPacket(EntityUtil.createDataTrackerUpdate(
                this.id,
                ArmorStandEntity.TRACKER_HEAD_ROTATION,
                angle
        ));
    }

    public void sendHeadRotation(ServerPlayerEntity playerEntity, float pitch, float yaw, float roll) {
        sendHeadRotation(playerEntity, new EulerAngle(pitch, yaw, roll));
    }
}
