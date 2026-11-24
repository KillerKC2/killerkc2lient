package ru.rvsteal.mixin;

import net.minecraft.class_4587;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.rvsteal.RvSteal;

@Mixin(class_757.class)
public class HurtCameraMixin {
   @Inject(method = "method_3198", at = @At("HEAD"), cancellable = true)
   private void killerkc2$noHurt(class_4587 pose, float delta, CallbackInfo callback) {
      if (RvSteal.noHurtCamEnabled()) {
         callback.cancel();
      }
   }
}
