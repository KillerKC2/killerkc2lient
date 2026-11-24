package ru.rvsteal.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.rvsteal.RvSteal;

@Mixin(class_757.class)
public class NightVisionScaleMixin {
   @Inject(method = "method_3174", at = @At("HEAD"), cancellable = true)
   private static void killerkc2$vision(class_1309 entity, float delta, CallbackInfoReturnable<Float> callback) {
      if (RvSteal.nightVisionEnabled()) {
         callback.setReturnValue(1.0F);
      }
   }
}
