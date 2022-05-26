package nl.theepicblock.polycreate;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

import java.util.ArrayList;
import java.util.List;

public class ExtendedEntityUtil {
    public static <T> EntityTrackerUpdateS2CPacket createDataTrackerUpdate(int id, TrackedData<T> tracker, T value) {
        return new EntityTrackerUpdateS2CPacket(id,
                new DataTracker(null) {
                    @Override
                    public List<Entry<?>> getDirtyEntries() {
                        List<Entry<?>> list = new ArrayList<>(1);
                        list.add(new Entry<>(tracker, value));
                        return list;
                    }
                },
                false);
    }

    public static <T,V> EntityTrackerUpdateS2CPacket createDataTrackerUpdate(int id,
                                                                             TrackedData<T> tracker, T value,
                                                                             TrackedData<V> tracker2, V value2) {
        return new EntityTrackerUpdateS2CPacket(id,
                new DataTracker(null) {
                    @Override
                    public List<Entry<?>> getDirtyEntries() {
                        List<Entry<?>> list = new ArrayList<>(2);
                        list.add(new Entry<>(tracker, value));
                        list.add(new Entry<>(tracker2, value2));
                        return list;
                    }
                },
                false);
    }

    public static <T,V,U> EntityTrackerUpdateS2CPacket createDataTrackerUpdate(int id,
                                                                             TrackedData<T> tracker, T value,
                                                                             TrackedData<V> tracker2, V value2,
                                                                             TrackedData<U> tracker3, U value3) {
        return new EntityTrackerUpdateS2CPacket(id,
                new DataTracker(null) {
                    @Override
                    public List<Entry<?>> getDirtyEntries() {
                        List<Entry<?>> list = new ArrayList<>(3);
                        list.add(new Entry<>(tracker, value));
                        list.add(new Entry<>(tracker2, value2));
                        return list;
                    }
                },
                false);
    }

    public static <T,V,U,W> EntityTrackerUpdateS2CPacket createDataTrackerUpdate(int id,
                                                                             TrackedData<T> tracker, T value,
                                                                             TrackedData<V> tracker2, V value2,
                                                                             TrackedData<U> tracker3, U value3,
                                                                             TrackedData<W> tracker4, W value4) {
        return new EntityTrackerUpdateS2CPacket(id,
                new DataTracker(null) {
                    @Override
                    public List<Entry<?>> getDirtyEntries() {
                        List<Entry<?>> list = new ArrayList<>(4);
                        list.add(new Entry<>(tracker, value));
                        list.add(new Entry<>(tracker2, value2));
                        list.add(new Entry<>(tracker3, value3));
                        list.add(new Entry<>(tracker4, value4));
                        return list;
                    }
                },
                false);
    }
}
