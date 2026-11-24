package ru.rvsteal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_742;
import net.minecraft.class_746;

final class HudCache {
   private static final class_1304[] SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
   private static String coordinates = "";
   private static String health = "";
   private static List<String> nearby = List.of();
   private static List<HudCache.Armor> armor = List.of();
   private static Object level;
   private static int ticks;

   static void tick(class_310 client) {
      if (client.field_1724 != null && client.field_1687 != null) {
         if (level != client.field_1687) {
            level = client.field_1687;
            ticks = 0;
         }

         if (ticks++ % 5 == 0) {
            class_746 player = client.field_1724;
            coordinates = RvSteal.coordinatesHud
               ? String.format(
                  Locale.ROOT, "XYZ: %.1f / %.1f / %.1f | FPS: %d", player.method_23317(), player.method_23318(), player.method_23321(), client.method_47599()
               )
               : "";
            health = RvSteal.healthHud
               ? String.format(Locale.ROOT, "HP: %.0f / %.0f | Food: %d", player.method_6032(), player.method_6063(), player.method_7344().method_7586())
               : "";
            ArrayList<String> lines = new ArrayList<>();
            if (RvSteal.betterNear) {
               int maxRows = Math.max(0, (client.method_22683().method_4502() - 84) / 10);

               for (class_742 other : client.field_1687.method_18456()) {
                  if (other != player && !other.method_31481() && !(player.method_5858(other) > 16384.0)) {
                     if (lines.size() >= maxRows) {
                        break;
                     }

                     lines.add(other.method_5477().getString() + "  " + Math.round(player.method_5739(other)) + "m");
                  }
               }
            }

            nearby = List.copyOf(lines);
            ArrayList<HudCache.Armor> items = new ArrayList<>();
            if (RvSteal.armorHud) {
               for (class_1304 slot : SLOTS) {
                  class_1799 stack = player.method_6118(slot);
                  if (!stack.method_7960()) {
                     String durability = "";
                     if (stack.method_7963() && stack.method_7936() > 0) {
                        int percent = Math.clamp(Math.round(100.0F * (stack.method_7936() - stack.method_7919()) / stack.method_7936()), 0, 100);
                        durability = percent + "%";
                     }

                     items.add(new HudCache.Armor(stack.method_7972(), durability));
                  }
               }
            }

            armor = List.copyOf(items);
         }
      } else {
         coordinates = "";
         nearby = List.of();
         armor = List.of();
         level = null;
         ticks = 0;
      }
   }

   static void render(class_332 graphics) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 != null && client.field_1687 != null && client.field_1687 == level) {
         if (RvSteal.coordinatesHud) {
            graphics.method_25303(
               client.field_1772,
               coordinates,
               HudLayout.x("Coordinates", graphics.method_51421(), 8),
               HudLayout.y("Coordinates", graphics.method_51443(), 35),
               -1
            );
         }

         if (RvSteal.healthHud) {
            graphics.method_25303(
               client.field_1772, health, HudLayout.x("Health", graphics.method_51421(), 8), HudLayout.y("Health", graphics.method_51443(), 55), -29537
            );
         }

         if (RvSteal.armorHud) {
            int x = HudLayout.x("Armor", graphics.method_51421(), Math.max(0, graphics.method_51421() - 90));
            int y = HudLayout.y("Armor", graphics.method_51443(), 8);

            for (HudCache.Armor item : armor) {
               graphics.method_51427(item.stack(), x, y);
               graphics.method_25303(client.field_1772, item.durability(), x + 19, y + 4, -1);
               y += 20;
            }
         }

         if (RvSteal.betterNear) {
            int x = HudLayout.x("Better Near", graphics.method_51421(), 8);
            int y = HudLayout.y("Better Near", graphics.method_51443(), 140);
            if (y > graphics.method_51443() - 24) {
               return;
            }

            graphics.method_25303(client.field_1772, "Better Near", x, y, -37852);
            y += 12;

            for (String line : nearby) {
               if (y > graphics.method_51443() - 40) {
                  break;
               }

               graphics.method_25303(client.field_1772, line, x, y, -1);
               y += 10;
            }
         }
      }
   }

   private record Armor(class_1799 stack, String durability) {
   }
}
