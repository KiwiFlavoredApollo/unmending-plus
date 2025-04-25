package kiwiapollo.unmendingplus;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;

import java.util.Map;

public class AnvilUpdateDisabler implements AnvilUpdateEvent.AnvilUpdateCallback {
    @Override
    public ActionResult update(AnvilUpdateEvent event) {
        final ItemStack left = event.getLeft();
        final ItemStack right = event.getRight();
        ItemStack out = event.getOutput();

        if (out.isEmpty() && (left.isEmpty() || right.isEmpty())) {
            return ActionResult.PASS;
        }

        boolean isMended = false;

        final Map<Enchantment, Integer> enchantmentsLeft = EnchantmentHelper.get(left);
        final Map<Enchantment, Integer> enchantmentsRight = EnchantmentHelper.get(right);

        if (enchantmentsLeft.containsKey(Enchantments.MENDING) || enchantmentsRight.containsKey(Enchantments.MENDING)) {
            if (left.getItem() == right.getItem()) {
                isMended = true;
            }

            if (right.getItem() == Items.ENCHANTED_BOOK) {
                isMended = true;
            }
        }

        if (!isMended) {
            return ActionResult.PASS;
        }

        if (out.isEmpty()) {
            out = left.copy();
        }

        if (!out.hasNbt()) {
            out.setNbt(new NbtCompound());
        }

        final Map<Enchantment, Integer> enchantmentsOutput = EnchantmentHelper.get(out);
        enchantmentsOutput.putAll(enchantmentsRight);
        enchantmentsOutput.remove(Enchantments.MENDING);
        EnchantmentHelper.set(enchantmentsOutput, out);

        out.setRepairCost(0);
        if (out.isDamageable()) {
            out.setDamage(0);
        }

        event.setOutput(out);
        if (event.getCost() == 0) {
            event.setCost(1);
        }

        return ActionResult.CONSUME;

    }
}
