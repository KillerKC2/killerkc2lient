package ru.rvsteal;

import net.minecraft.class_12197;
import net.minecraft.class_12206;
import net.minecraft.class_2398;
import net.minecraft.class_2400;
import net.minecraft.class_310;
import net.minecraft.class_5819;
import net.minecraft.class_746;

public final class WorldVisuals {
   private WorldVisuals() {
   }

   public static Integer color(class_12197<?> attribute) {
      if (!RvSteal.active() || !ModuleSettings.worldTheme) {
         return null;
      } else if (attribute == class_12206.field_63746) {
         return ModuleSettings.skyColor;
      } else if (attribute == class_12206.field_63742) {
         return ModuleSettings.fogColor;
      } else {
         return attribute == class_12206.field_64126 ? 0xFF000000 | ModuleSettings.cloudColor : null;
      }
   }

   static void tick(class_310 client) {
      if (ModuleSettings.ambientParticles && client.field_1724 != null && client.field_1687 != null && !client.method_1493()) {
         class_5819 random = client.field_1687.field_9229;
         class_746 player = client.field_1724;

         class_2400 particle = switch (ModuleSettings.particleStyle) {
            case 1 -> class_2398.field_43379;
            case 2 -> class_2398.field_56509;
            default -> class_2398.field_11207;
         };

         for (int i = 0; i < ModuleSettings.particleDensity; i++) {
            client.field_1687
               .method_8406(
                  particle,
                  player.method_23317() + (random.method_43058() - 0.5) * 18.0,
                  player.method_23318() + random.method_43058() * 6.0,
                  player.method_23321() + (random.method_43058() - 0.5) * 18.0,
                  0.0,
                  -0.005,
                  0.0
               );
         }
      }
   }
}
