package ru.rvsteal.mixin;

import net.minecraft.class_4184;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_757.class)
public interface GameRendererAccess {
   @Invoker("method_3196")
   float killerkc2$getFov(class_4184 var1, float var2, boolean var3);
}
