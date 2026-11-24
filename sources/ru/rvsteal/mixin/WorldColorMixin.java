package ru.rvsteal.mixin;

import net.minecraft.class_12197;
import net.minecraft.class_12202;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.rvsteal.WorldVisuals;

@Mixin(class_12202.class)
public class WorldColorMixin {
   @Shadow
   class_1937 field_63732;

   @Inject(method = "method_75689", at = @At("HEAD"), cancellable = true)
   private <T> void killerkc2$color(class_12197<T> attribute, float delta, CallbackInfoReturnable<T> callback) {
      if (this.field_63732 != null && this.field_63732.method_8608()) {
         Integer color = WorldVisuals.color(attribute);
         if (color != null) {
            T value = (T)color;
            callback.setReturnValue(value);
         }
      }
   }
}
