package nl.theepicblock.polycreate;

import io.github.theepicblock.polymc.api.item.CustomModelDataManager;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.json.JModelOverride;
import io.github.theepicblock.polymc.impl.resource.ResourceConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CMDHelper {
    public static ItemStack itemStackFromManager(CustomModelDataManager manager, Item[] profile) {
        var pair = manager.requestCMD(profile);
        var stack = new ItemStack(pair.getLeft());
        addCmdToNbt(stack, pair.getRight());
        return stack;
    }

    public static void addCmdToNbt(ItemStack stack, int cmd) {
        var nbt = new NbtCompound();
        nbt.putInt("CustomModelData", cmd);
        stack.setNbt(nbt);
    }

    /**
     * Adds an override for the appropriate client model to it's definition in the pack
     */
    public static void addOverrideToPack(ItemStack stack, PolyMcResourcePack pack, Identifier moddedModel) {
        var cmdValue = stack.getNbt().getInt("CustomModelData");
        var clientitemId = Registry.ITEM.getId(stack.getItem());

        // Get the json for the vanilla item, so we can inject an override into it
        var clientItemModel = pack.getOrDefaultVanillaItemModel(clientitemId.getNamespace(), clientitemId.getPath());
        // Add the override
        clientItemModel.getOverrides().add(JModelOverride.ofCMD(cmdValue, ResourceConstants.itemLocation(moddedModel)));
    }
}
