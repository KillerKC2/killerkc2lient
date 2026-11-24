package ru.rvsteal.mixin;

import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.rvsteal.Hitboxes;

@Mixin(class_757.class)
public class HitboxPickMixin {
   @Inject(method = "method_3190", at = @At("TAIL"))
   private void killerkc2$pick(float delta, CallbackInfo ci) {
      Hitboxes.pick(delta);
   }
}
