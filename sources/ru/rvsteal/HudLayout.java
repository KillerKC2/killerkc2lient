package ru.rvsteal;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

final class HudLayout {
   static final String[] NAMES = new String[]{
      "Watermark", "Hotkeys", "Potions", "Staffs", "Target", "Coordinates", "Health", "Armor", "Better Near", "Block ESP"
   };
   private static final Map<String, int[]> positions = new HashMap<>();

   static int x(String name, int width, int fallback) {
      int[] p = positions.get(name);
      return p == null ? fallback : Math.round(p[0] * width / 10000.0F);
   }

   static int y(String name, int height, int fallback) {
      int[] p = positions.get(name);
      return p == null ? fallback : Math.round(p[1] * height / 10000.0F);
   }

   static void move(String name, int x, int y, int width, int height) {
      positions.put(
         name,
         new int[]{Math.clamp(Math.round(x * 10000.0F / Math.max(1, width)), 0, 10000), Math.clamp(Math.round(y * 10000.0F / Math.max(1, height)), 0, 10000)}
      );
   }

   static void reset() {
      positions.clear();
   }

   static void load(Properties p) {
      positions.clear();

      for (String name : NAMES) {
         try {
            String value = p.getProperty("hud." + name);
            if (value != null) {
               String[] pair = value.split(",");
               if (pair.length == 2) {
                  positions.put(name, new int[]{Math.clamp(Integer.parseInt(pair[0]), 0, 10000), Math.clamp(Integer.parseInt(pair[1]), 0, 10000)});
               }
            }
         } catch (NumberFormatException var7) {
         }
      }
   }

   static void store(Properties p) {
      positions.forEach((k, v) -> p.setProperty("hud." + k, v[0] + "," + v[1]));
   }
}
