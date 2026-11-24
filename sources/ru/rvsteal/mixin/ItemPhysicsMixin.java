package ru.rvsteal.mixin;

import java.util.WeakHashMap;
import net.minecraft.class_10039;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_916;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.rvsteal.RvSteal;

@Mixin(class_916.class)
public class ItemPhysicsMixin {
   @Unique
   private static final WeakHashMap<class_10039, Boolean> killerkc2$grounded = new WeakHashMap<>();
   @Unique
   private boolean killerkc2$lying;

   @Inject(method = "method_62470(Lnet/minecraft/class_1542;Lnet/minecraft/class_10039;F)V", at = @At("TAIL"))
   private void killerkc2$state(class_1542 entity, class_10039 state, float delta, CallbackInfo ci) {
      killerkc2$grounded.put(state, entity.method_24828());
   }

   @Inject(
      method = "method_3996(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At("HEAD")
   )
   private void killerkc2$submit(class_10039 state, class_4587 pose, class_11659 nodes, class_12075 camera, CallbackInfo ci) {
      this.killerkc2$lying = RvSteal.itemPhysicsEnabled() && killerkc2$grounded.getOrDefault(state, false);
   }

   @Redirect(
      method = "method_3996(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4587;method_46416(FFF)V", ordinal = 0)
   )
   private void killerkc2$translate(class_4587 pose, float x, float y, float z) {
      pose.method_46416(x, this.killerkc2$lying ? 0.13F : y, z);
   }

   @Redirect(
      method = "method_3996(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionfc;)V", ordinal = 0)
   )
   private void killerkc2$rotate(class_4587 pose, Quaternionfc rotation) {
      pose.method_22907((Quaternionfc)(this.killerkc2$lying ? new Quaternionf().rotateX((float) (Math.PI / 2)) : rotation));
   }
}
