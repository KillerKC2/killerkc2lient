package ru.rvsteal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3857;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import ru.rvsteal.mixin.GameRendererAccess;

final class PearlTrails {
   private static final Map<UUID, PearlTrails.Trail> trails = new HashMap<>();
   private static int tick;
   private static Object world;

   static void tick(class_310 c) {
      if (world != c.field_1687 || !ModuleSettings.pearlTrails || c.field_1687 == null) {
         world = c.field_1687;
         trails.clear();
      }

      if (ModuleSettings.pearlTrails && c.field_1687 != null) {
         tick++;

         for (class_1297 entity : c.field_1687.method_18112()) {
            if (entity instanceof class_3857 pearl && (trails.containsKey(pearl.method_5667()) || trails.size() < 64)) {
               PearlTrails.Trail trail = trails.computeIfAbsent(pearl.method_5667(), k -> new PearlTrails.Trail());
               if (trail.points.isEmpty() || trail.points.getLast().method_1025(pearl.method_73189()) > 0.01) {
                  trail.points.add(pearl.method_73189());
               }

               if (trail.points.size() > 80) {
                  trail.points.removeFirst();
               }

               trail.lastTick = tick;
            }
         }

         trails.values().removeIf(t -> tick - t.lastTick > 40);
      }
   }

   static void render(class_332 g, float delta) {
      class_310 c = class_310.method_1551();
      if (ModuleSettings.pearlTrails && c.field_1724 != null && c.field_1687 != null && !c.field_1690.field_1842) {
         class_4184 camera = c.field_1773.method_19418();
         Matrix4f matrix = c.field_1773
            .method_22973(((GameRendererAccess)c.field_1773).killerkc2$getFov(camera, delta, true))
            .rotate(new Quaternionf(camera.method_23767()).conjugate());
         class_243 cp = camera.method_71156();

         for (PearlTrails.Trail trail : trails.values()) {
            for (class_243 point : trail.points) {
               int[] b = EspProjection.bounds(
                  class_238.method_30048(point, 0.1, 0.1, 0.1).method_989(-cp.field_1352, -cp.field_1351, -cp.field_1350),
                  matrix,
                  g.method_51421(),
                  g.method_51443()
               );
               if (b != null) {
                  int x = (b[0] + b[2]) / 2;
                  int y = (b[1] + b[3]) / 2;
                  g.method_25294(x - 1, y - 1, x + 2, y + 2, -4416769);
               }
            }
         }
      }
   }

   private static class Trail {
      final List<class_243> points = new ArrayList<>();
      int lastTick;
   }
}
