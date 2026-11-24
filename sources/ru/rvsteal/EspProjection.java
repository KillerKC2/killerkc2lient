package ru.rvsteal;

import net.minecraft.class_238;
import org.joml.Matrix4f;
import org.joml.Vector4f;

final class EspProjection {
   private static final float NEAR = 0.05F;

   static int[] bounds(class_238 box, Matrix4f matrix, int width, int height) {
      Vector4f[] points = new Vector4f[8];

      for (int i = 0; i < 8; i++) {
         points[i] = matrix.transform(
            new Vector4f(
               (float)((i & 1) == 0 ? box.field_1323 : box.field_1320),
               (float)((i & 2) == 0 ? box.field_1322 : box.field_1325),
               (float)((i & 4) == 0 ? box.field_1321 : box.field_1324),
               1.0F
            )
         );
      }

      float[] bounds = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};

      for (int i = 0; i < 8; i++) {
         if (points[i].w >= 0.05F) {
            include(points[i], bounds, width, height);
         }

         for (int bit = 1; bit <= 4; bit *= 2) {
            int j = i ^ bit;
            if (j > i && points[i].w >= 0.05F != points[j].w >= 0.05F) {
               float t = (0.05F - points[i].w) / (points[j].w - points[i].w);
               include(new Vector4f(points[i]).lerp(points[j], t), bounds, width, height);
            }
         }
      }

      return Float.isFinite(bounds[0]) && !(bounds[2] < 0.0F) && !(bounds[3] < 0.0F) && !(bounds[0] > width) && !(bounds[1] > height)
         ? new int[]{
            Math.clamp((int)Math.floor(bounds[0]), 0, width),
            Math.clamp((int)Math.floor(bounds[1]), 0, height),
            Math.clamp((int)Math.ceil(bounds[2]), 0, width),
            Math.clamp((int)Math.ceil(bounds[3]), 0, height)
         }
         : null;
   }

   private static void include(Vector4f v, float[] b, int width, int height) {
      float x = (v.x / v.w + 1.0F) * width / 2.0F;
      float y = (1.0F - v.y / v.w) * height / 2.0F;
      b[0] = Math.min(b[0], x);
      b[1] = Math.min(b[1], y);
      b[2] = Math.max(b[2], x);
      b[3] = Math.max(b[3], y);
   }
}
