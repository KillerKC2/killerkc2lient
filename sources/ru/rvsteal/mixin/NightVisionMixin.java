package ru.rvsteal.mixin;

import net.minecraft.class_1291;
import net.minecraft.class_1294;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.rvsteal.RvSteal;

@Mixin(class_765.class)
public class NightVisionMixin {
   @ModifyArg(
      method = "method_3313",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 0),
      index = 0
   )
   private float killerkc2$ambient(float original) {
      return RvSteal.nightVisionEnabled() ? 1.0F : original;
   }

   @Redirect(method = "method_3313", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_746;method_6059(Lnet/minecraft/class_6880;)Z"))
   private boolean killerkc2$vision(class_746 player, class_6880<class_1291> effect) {
      return RvSteal.nightVisionEnabled() && effect == class_1294.field_5925 || player.method_6059(effect);
   }
}
