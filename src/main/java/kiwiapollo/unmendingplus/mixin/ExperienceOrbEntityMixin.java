package kiwiapollo.unmendingplus.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
	@Inject(method = "repairPlayerGears", at = @At("HEAD"), cancellable = true)
	private void disableRepairPlayerGears(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> info) {
		info.setReturnValue(amount);
		info.cancel();
	}
}