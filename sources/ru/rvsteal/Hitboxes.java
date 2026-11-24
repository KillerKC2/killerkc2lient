package ru.rvsteal;

import java.util.Optional;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public final class Hitboxes {
   private Hitboxes() {
   }

   public static void pick(float delta) {
      class_310 c = class_310.method_1551();
      if (RvSteal.active() && ModuleSettings.hitboxes && c.field_1724 != null && c.field_1687 != null && c.method_1560() == c.field_1724) {
         class_243 eye = c.field_1724.method_5836(delta);
         double range = c.field_1724.method_55755();
         class_243 end = eye.method_1019(c.field_1724.method_5828(delta).method_1021(range));
         double nearest = range * range;
         if (c.field_1765 != null && c.field_1765.method_17783() != class_240.field_1333) {
            nearest = Math.min(nearest, eye.method_1025(c.field_1765.method_17784()));
         }

         class_3966 best = null;

         for (class_1657 other : c.field_1687.method_18456()) {
            if (other != c.field_1724 && !other.method_7325() && other.method_5805() && !other.method_31481()) {
               class_238 box = other.method_5829()
                  .method_997(other.method_30950(delta).method_1020(other.method_73189()))
                  .method_1014(ModuleSettings.hitboxSize / 100.0);
               Optional<class_243> point = box.method_992(eye, end);
               if (point.isPresent() && eye.method_1025(point.get()) < nearest) {
                  nearest = eye.method_1025(point.get());
                  best = new class_3966(other, point.get());
               }
            }
         }

         if (best != null) {
            c.field_1765 = best;
            c.field_1692 = best.method_17782();
         }
      }
   }
}
