package ru.rvsteal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1293;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_268;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_634;
import net.minecraft.class_640;
import org.lwjgl.glfw.GLFW;

final class ScreenHud {
   private static List<String> hotkeys = List.of();
   private static List<String> potions = List.of();
   private static List<String> staffs = List.of();
   private static String watermark = "";
   private static int ticks;
   private static int leftBottom = 72;

   static int leftBottom() {
      return leftBottom;
   }

   static void tick(class_310 c) {
      if (c.field_1724 != null && c.field_1687 != null) {
         if (ticks++ % 10 == 0) {
            class_634 connection = c.method_1562();
            class_640 self = connection == null ? null : connection.method_2871(c.field_1724.method_5667());
            watermark = "KillerKC2  |  "
               + c.field_1724.method_5477().getString()
               + "  |  "
               + c.method_47599()
               + " fps  |  "
               + (self == null ? "--" : self.method_2959())
               + " ms";
            ArrayList<String> keys = new ArrayList<>();

            for (RvScreen.Module m : RvScreen.allModules()) {
               Integer key = ModuleSettings.binds.get(m.name());
               if (key != null && m.get().getAsBoolean()) {
                  String name = GLFW.glfwGetKeyName(key, 0);
                  keys.add(m.name() + " [" + (name == null ? "KEY " + key : name.toUpperCase(Locale.ROOT)) + "]");
               }
            }

            hotkeys = keys;
            ArrayList<String> effects = new ArrayList<>();

            for (class_1293 e : c.field_1724.method_6026()) {
               int seconds = Math.max(0, e.method_5584() / 20);
               effects.add(
                  class_2561.method_43471(e.method_5586()).getString()
                     + " "
                     + (e.method_5578() + 1)
                     + "  "
                     + (e.method_48559() ? "∞" : String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60))
               );
            }

            if (ModuleSettings.nightVision) {
               effects.add("Night Vision [client]");
            }

            potions = effects;
            ArrayList<String> online = new ArrayList<>();
            if (connection != null) {
               for (class_640 info : connection.method_2880()) {
                  class_268 team = info.method_2955();
                  String rank = team == null ? "" : team.method_1197() + " " + team.method_1144().getString();
                  if (info.method_2971() != null) {
                     rank = rank + " " + info.method_2971().getString();
                  }

                  String value = rank.toLowerCase(Locale.ROOT);
                  if (value.matches(".*(?:admin|moder|staff|helper|админ|модер|хелпер).*")) {
                     online.add(info.method_2966().name());
                  }
               }
            }

            staffs = online;
         }
      } else {
         ticks = 0;
         hotkeys = potions = staffs = List.of();
      }
   }

   static void render(class_332 g) {
      class_310 c = class_310.method_1551();
      if (c.field_1724 != null && c.field_1687 != null && !c.field_1690.field_1842) {
         if (ModuleSettings.watermark) {
            int w = Math.min(g.method_51421() - 16, c.field_1772.method_1727(watermark) + 16);
            int x = HudLayout.x("Watermark", g.method_51421(), 8);
            int y = HudLayout.y("Watermark", g.method_51443(), 8);
            Ui.round(g, x, y, w, 20, 6, -1072095191);
            g.method_25303(c.field_1772, c.field_1772.method_27523(watermark, w - 12), x + 6, y + 6, -2567436);
         }

         leftBottom = 80;
         if (ModuleSettings.hotkeysHud) {
            panel(g, "Hotkeys", hotkeys, HudLayout.x("Hotkeys", g.method_51421(), 8), HudLayout.y("Hotkeys", g.method_51443(), leftBottom));
            leftBottom = leftBottom + 30 + Math.min(5, hotkeys.size()) * 12;
         }

         if (ModuleSettings.potionsHud) {
            panel(g, "Potions", potions, HudLayout.x("Potions", g.method_51421(), 8), HudLayout.y("Potions", g.method_51443(), leftBottom));
            leftBottom = leftBottom + 30 + Math.min(5, potions.size()) * 12;
         }

         if (ModuleSettings.staffHud) {
            panel(
               g,
               "Staffs [TAB rank]",
               staffs,
               HudLayout.x("Staffs", g.method_51421(), Math.max(8, g.method_51421() - 188)),
               HudLayout.y("Staffs", g.method_51443(), 72)
            );
         }

         if (ModuleSettings.targetHud) {
            renderTarget(g, c);
         }
      }
   }

   private static void renderTarget(class_332 g, class_310 c) {
      class_1309 target = ClientModules.target;
      if (target == null && c.field_1692 instanceof class_1309 entity) {
         target = entity;
      }

      if (target != null && !target.method_31481() && target.method_5805()) {
         int x = HudLayout.x("Target", g.method_51421(), g.method_51421() / 2 + 20);
         int y = HudLayout.y("Target", g.method_51443(), g.method_51443() / 2 + 20);
         int w = Math.min(150, g.method_51421() - x - 4);
         if (w >= 40 && y + 62 <= g.method_51443()) {
            Ui.round(g, x, y, w, 60, 6, -803659735);
            g.method_25303(c.field_1772, c.field_1772.method_27523(target.method_5477().getString(), w - 12), x + 6, y + 5, -1516806);
            float hp = Math.clamp(target.method_6032() / Math.max(1.0F, target.method_6063()), 0.0F, 1.0F);
            g.method_25303(c.field_1772, String.format(Locale.ROOT, "HP %.1f / %.1f", target.method_6032(), target.method_6063()), x + 6, y + 16, -2631703);
            g.method_25294(x + 6, y + 28, x + w - 6, y + 31, -12565423);
            g.method_25294(x + 6, y + 28, x + 6 + Math.round((w - 12) * hp), y + 31, -5727494);
            int ix = x + 6;

            for (class_1304 slot : new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166}) {
               class_1799 stack = target.method_6118(slot);
               if (!stack.method_7960()) {
                  g.method_51427(stack, ix, y + 35);
                  ix += 18;
               }
            }

            String effects = target.method_6026()
               .stream()
               .map(e -> class_2561.method_43471(e.method_5586()).getString())
               .limit(2L)
               .reduce((a, b) -> a + ", " + b)
               .orElse("");
            if (!effects.isEmpty()) {
               g.method_25303(c.field_1772, c.field_1772.method_27523(effects, w - 12), x + 6, y + 51, -4082215);
            }
         }
      }
   }

   private static void panel(class_332 g, String title, List<String> lines, int x, int y) {
      class_327 font = class_310.method_1551().field_1772;
      if (y <= g.method_51443() - 22) {
         int w = font.method_1727(title) + 16;

         for (String line : lines) {
            w = Math.max(w, font.method_1727(line) + 16);
         }

         w = Math.min(w, Math.min(180, g.method_51421() - x - 4));
         int rows = Math.min(Math.min(5, lines.size()), Math.max(0, (g.method_51443() - y - 26) / 12));
         Ui.round(g, x, y, w, 22 + rows * 12, 5, -1072095191);
         g.method_25303(font, title, x + 7, y + 7, -4016395);

         for (int i = 0; i < rows; i++) {
            g.method_25303(font, font.method_27523(lines.get(i), w - 14), x + 7, y + 23 + i * 12, -2959904);
         }
      }
   }
}
