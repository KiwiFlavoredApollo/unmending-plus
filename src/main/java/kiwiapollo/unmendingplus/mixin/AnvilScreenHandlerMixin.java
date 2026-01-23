package kiwiapollo.unmendingplus.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    private static final int LEVEL_COST = 7;

    @Shadow
    private int repairItemUsage;

    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type,
            int syncId,
            PlayerInventory playerInventory,
            ScreenHandlerContext context
    ) {
        super(type, syncId, playerInventory, context);
    }

    @Redirect(
            method = "updateResult()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/Property;get()I",
                    ordinal = 1
            )
    )
    private int getLevelCost(Property instance) {
        if (!isApplyingMendingBookToItem(this.input)) {
            return instance.get();
        }

        return 0;
    }

    @Redirect(
            method = "updateResult()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V"
            )
    )
    private void resetRepairCost(ItemStack instance, int repairCost) {
        if (!isApplyingMendingBookToItem(this.input)) {
            instance.setRepairCost(repairCost);
            return;
        }

        instance.setRepairCost(0);
    }

    @Redirect(
            method = "updateResult()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private void removeMending(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        if (!isApplyingMendingBookToItem(this.input)) {
            EnchantmentHelper.set(enchantments, stack);
            return;
        }

        enchantments.remove(Enchantments.MENDING);
        EnchantmentHelper.set(enchantments, stack);
    }

    @Inject(
            method = "updateResult()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V",
                    ordinal = 4,
                    shift = At.Shift.AFTER,
                    by = 2
            )
    )
    private void updateField(CallbackInfo ci) {
        if (!isApplyingMendingBookToItem(this.input)) {
            return;
        }

        this.levelCost.set(LEVEL_COST);
        this.repairItemUsage = 0;
    }

    private boolean isApplyingMendingBookToItem(Inventory input) {
        return isDamageableItem(input.getStack(0)) && isMendingBook(input.getStack(1));
    }

    private boolean isDamageableItem(ItemStack stack) {
        return stack.isDamageable();
    }

    private boolean isMendingBook(ItemStack stack) {
        return stack.isOf(Items.ENCHANTED_BOOK) && EnchantmentHelper.get(stack).containsKey(Enchantments.MENDING);
    }
}