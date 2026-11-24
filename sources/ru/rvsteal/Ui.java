package ru.rvsteal;

import net.minecraft.class_332;

final class Ui {
   static void round(class_332 g, int x, int y, int w, int h, int radius, int color) {
      int r = Math.min(radius, Math.min(w, h) / 2);
      g.method_25294(x, y + r, x + w, y + h - r, color);

      for (int i = 0; i < r; i++) {
         int inset = r - (int)Math.sqrt(r * r - (r - i - 1) * (r - i - 1));
         g.method_25294(x + inset, y + i, x + w - inset, y + i + 1, color);
         g.method_25294(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
      }
   }
}
