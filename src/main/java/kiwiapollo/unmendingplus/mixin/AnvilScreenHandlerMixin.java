package kiwiapollo.unmendingplus.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import kiwiapollo.unmendingplus.AnvilUpdateEvent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow @Final private Property levelCost;

    @Shadow private int repairItemUsage;

    public AnvilScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type,
            int syncId,
            PlayerInventory playerInventory,
            ScreenHandlerContext context
    ) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(
            method = "updateResult()V",
            at = @At(
                    value ="FIELD",
                    target = "Lnet/minecraft/screen/AnvilScreenHandler;repairItemUsage:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0
            ),
            cancellable = true
    )
    private void injectChanged(CallbackInfo ci, @Local(ordinal = 0) ItemStack left, @Local(ordinal = 2) ItemStack right) {
        AnvilUpdateEvent event = new AnvilUpdateEvent(left, right, 0);
        ActionResult result = AnvilUpdateEvent.EVENT.invoker().update(event);

        if (result == ActionResult.FAIL) {
            ci.cancel();
            return;
        }

        if (result == ActionResult.CONSUME) {
            output.setStack(0, event.getOutput());
            this.levelCost.set(event.getCost());
            this.repairItemUsage = 0;
            ci.cancel();
        }
    }
}