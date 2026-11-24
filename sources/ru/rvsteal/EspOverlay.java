package ru.rvsteal;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4184;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9304;
import net.minecraft.class_9334;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import ru.rvsteal.mixin.GameRendererAccess;

final class EspOverlay {
   private static final class_1304[] SLOTS = new class_1304[]{
      class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166, class_1304.field_6173, class_1304.field_6171
   };
   private static final List<EspOverlay.EntityInfo> entities = new ArrayList<>();
   private static final Set<class_2338> found = new HashSet<>();
   private static Set<class_2248> selected = Set.of();
   private static Object level;
   private static class_2338 origin;
   private static int cursor;
   private static int ticks;
   private static int scanRange;
   private static int scanPause;
   private static List<class_2338> scanOffsets = List.of();

   static void resetBlocks() {
      found.clear();
      origin = null;
      cursor = 0;
      scanPause = 0;
      selected = Set.of();
   }

   static void tick(class_310 c) {
      if (c.field_1687 != level) {
         level = c.field_1687;
         entities.clear();
         resetBlocks();
         ticks = 0;
      }

      if (c.field_1724 != null && c.field_1687 != null) {
         if (ticks++ % 5 == 0) {
            entities.clear();
            if (RvSteal.playerEsp || RvSteal.mobEsp) {
               for (class_1297 raw : c.field_1687.method_18112()) {
                  if (raw instanceof class_1309 e && e != c.field_1724 && e.method_5805()) {
                     ModuleSettings.Esp settings = e instanceof class_1657 ? ModuleSettings.players : ModuleSettings.mobs;
                     if ((RvSteal.playerEsp && e instanceof class_1657 || RvSteal.mobEsp && e instanceof class_1569)
                        && !(e.method_5858(c.field_1724) > settings.range * settings.range)) {
                        ArrayList<EspOverlay.ItemInfo> gear = new ArrayList<>();
                        if (settings.gear || settings.enchants) {
                           for (class_1304 slot : SLOTS) {
                              class_1799 stack = e.method_6118(slot);
                              if (!stack.method_7960()) {
                                 ArrayList<String> enchants = new ArrayList<>();
                                 if (settings.enchants) {
                                    class_9304 component = (class_9304)stack.method_58694(class_9334.field_49633);
                                    if (component != null) {
                                       for (Entry<class_6880<class_1887>> entry : component.method_57539()) {
                                          enchants.add(class_1887.method_8179((class_6880)entry.getKey(), entry.getIntValue()).getString());
                                       }
                                    }
                                 }

                                 gear.add(new EspOverlay.ItemInfo(stack.method_7972(), List.copyOf(enchants)));
                              }
                           }
                        }

                        entities.add(
                           new EspOverlay.EntityInfo(
                              e, e.method_5477().getString(), String.format(Locale.ROOT, "HP %.1f / %.1f", e.method_6032(), e.method_6063()), gear
                           )
                        );
                     }
                  }
               }
            }
         }

         if (!ModuleSettings.blockEsp) {
            if (origin != null) {
               resetBlocks();
            }
         } else {
            if (selected.isEmpty()) {
               HashSet<class_2248> blocks = new HashSet<>();

               for (String part : ModuleSettings.blocks.split(",")) {
                  class_2960 id = class_2960.method_12829(part.trim());
                  if (id != null && class_7923.field_41175.method_10250(id)) {
                     blocks.add((class_2248)class_7923.field_41175.method_63535(id));
                  }
               }

               selected = blocks;
            }

            int r = ModuleSettings.blockRange;
            if (r != scanRange || scanOffsets.isEmpty()) {
               scanRange = r;
               ArrayList<class_2338> offsets = new ArrayList<>();

               for (int yy = -r; yy <= r; yy++) {
                  for (int zz = -r; zz <= r; zz++) {
                     for (int xx = -r; xx <= r; xx++) {
                        if (xx * xx + yy * yy + zz * zz <= r * r) {
                           offsets.add(new class_2338(xx, yy, zz));
                        }
                     }
                  }
               }

               offsets.sort(Comparator.comparingDouble(posx -> posx.method_10262(class_2338.field_10980)));
               scanOffsets = offsets;
               origin = null;
               cursor = 0;
               found.clear();
            }

            if (origin == null || origin.method_10262(c.field_1724.method_24515()) > 16.0) {
               origin = c.field_1724.method_24515();
               cursor = 0;
               scanPause = 0;
               found.clear();
            }

            if (ticks % 5 == 0) {
               found.removeIf(
                  posx -> !c.field_1687.method_22340(posx)
                     || posx.method_10262(c.field_1724.method_24515()) > r * r
                     || !selected.contains(c.field_1687.method_8320(posx).method_26204())
               );
            }

            if (scanPause-- <= 0) {
               for (int budget = 0; budget < 4096; budget++) {
                  if (cursor >= scanOffsets.size()) {
                     cursor = 0;
                     scanPause = 20;
                     break;
                  }

                  class_2338 pos = origin.method_10081((class_2382)scanOffsets.get(cursor++));
                  if (c.field_1687.method_22340(pos) && selected.contains(c.field_1687.method_8320(pos).method_26204()) && found.size() < 512) {
                     found.add(pos);
                  }
               }
            }
         }
      } else {
         entities.clear();
         resetBlocks();
      }
   }

   static void render(class_332 g, float delta) {
      class_310 c = class_310.method_1551();
      if (c.field_1724 != null && c.field_1687 != null && c.field_1687 == level && !c.field_1690.field_1842) {
         if (RvSteal.playerEsp || RvSteal.mobEsp || ModuleSettings.blockEsp || ModuleSettings.itemEsp) {
            class_4184 camera = c.field_1773.method_19418();
            float fov = ((GameRendererAccess)c.field_1773).killerkc2$getFov(camera, delta, true);
            Matrix4f projection = c.field_1773.method_22973(fov).rotate(new Quaternionf(camera.method_23767()).conjugate());
            class_243 cameraPos = camera.method_71156();
            int count = 0;

            for (EspOverlay.EntityInfo info : entities) {
               class_1309 e = info.entity();
               if (!e.method_31481() && e.method_5805()) {
                  boolean player = e instanceof class_1657;
                  if (player ? RvSteal.playerEsp : RvSteal.mobEsp) {
                     ModuleSettings.Esp settings = player ? ModuleSettings.players : ModuleSettings.mobs;
                     if (!(e.method_5858(c.field_1724) > settings.range * settings.range)) {
                        class_243 offset = e.method_30950(delta).method_1020(e.method_73189());
                        int[] b = EspProjection.bounds(
                           e.method_5829().method_997(offset).method_989(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350),
                           projection,
                           g.method_51421(),
                           g.method_51443()
                        );
                        if (b != null) {
                           int color = 0xFF000000 | settings.color;
                           int pad = settings.padding;
                           b[0] = Math.max(0, b[0] - pad);
                           b[1] = Math.max(0, b[1] - pad);
                           b[2] = Math.min(g.method_51421(), b[2] + pad);
                           b[3] = Math.min(g.method_51443(), b[3] + pad);
                           if (settings.fill > 0) {
                              g.method_25294(b[0], b[1], b[2], b[3], settings.fill << 24 | settings.color);
                           }

                           if (settings.box) {
                              if (settings.shadow) {
                                 frame(g, b, 0xFF000000 | settings.outlineColor, settings.thickness + 2, settings.corners);
                              }

                              frame(g, b, color, settings.thickness, settings.corners);
                           }

                           if (settings.healthBar) {
                              int barX = Math.max(0, b[0] - 5);
                              int height = Math.max(1, b[3] - b[1]);
                              int hpHeight = Math.round(height * Math.clamp(e.method_6032() / Math.max(1.0F, e.method_6063()), 0.0F, 1.0F));
                              g.method_25294(barX, b[1], barX + 3, b[3], -15395555);
                              g.method_25294(barX, b[3] - hpHeight, barX + 2, b[3], -8655460);
                           }

                           if (settings.tracers) {
                              line(g, g.method_51421() / 2, g.method_51443(), (b[0] + b[2]) / 2, b[3], color);
                           }

                           float scale = settings.scale / 100.0F;
                           g.method_51448().pushMatrix();
                           g.method_51448().scale(scale, scale);
                           int labelWidth = 0;
                           if (settings.name) {
                              labelWidth = Math.max(labelWidth, c.field_1772.method_1727(info.name()));
                           }

                           if (settings.hp) {
                              labelWidth = Math.max(labelWidth, c.field_1772.method_1727(info.hp()));
                           }

                           int x = Math.max(
                              1,
                              Math.min(
                                 Math.round((b[0] + b[2]) / (2.0F * scale)) - labelWidth / 2, Math.round(g.method_51421() / scale) - Math.max(110, labelWidth)
                              )
                           );
                           int lines = (settings.name ? 1 : 0) + (settings.hp ? 1 : 0) + (settings.distance ? 1 : 0);
                           int y = Math.max(1, Math.round(b[1] / scale) - lines * 10 - 2);
                           int textColor = 0xFF000000 | settings.textColor;
                           if (settings.name) {
                              label(g, info.name(), x, y, textColor, settings.background);
                              y += 10;
                           }

                           if (settings.hp) {
                              label(g, info.hp(), x, y, textColor, settings.background);
                              y += 10;
                           }

                           if (settings.distance) {
                              label(g, Math.round(e.method_5739(c.field_1724)) + " m", x, y, textColor, settings.background);
                           }

                           y = Math.round(b[3] / scale) + 2;
                           if (settings.gear) {
                              int ix = x;

                              for (EspOverlay.ItemInfo item : info.items()) {
                                 g.method_51427(item.stack(), ix, y);
                                 ix += 18;
                              }

                              if (!info.items().isEmpty()) {
                                 y += 18;
                              }
                           }

                           if (settings.enchants) {
                              for (EspOverlay.ItemInfo item : info.items()) {
                                 if (!item.enchants().isEmpty()) {
                                    label(g, item.stack().method_7964().getString(), x, y, textColor, settings.background);
                                    y += 10;

                                    for (String enchant : item.enchants()) {
                                       if (y > g.method_51443() / scale - 10.0F) {
                                          break;
                                       }

                                       label(g, enchant, x, y, textColor, settings.background);
                                       y += 10;
                                    }
                                 }
                              }
                           }

                           g.method_51448().popMatrix();
                           if (++count >= 64) {
                              break;
                           }
                        }
                     }
                  }
               }
            }

            if (ModuleSettings.blockEsp) {
               for (class_2338 pos : found) {
                  int[] b = EspProjection.bounds(
                     new class_238(pos).method_989(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350),
                     projection,
                     g.method_51421(),
                     g.method_51443()
                  );
                  if (b != null) {
                     frame(g, b, 0xFF000000 | ModuleSettings.blockColor, 1, false);
                  }
               }

               label(
                  g,
                  "Block ESP: "
                     + found.size()
                     + "/512 | "
                     + ModuleSettings.blockRange
                     + " m | "
                     + (selected.isEmpty() ? "проверь ID блоков" : "поиск " + (scanOffsets.isEmpty() ? 0 : cursor * 100 / scanOffsets.size()) + "%"),
                  HudLayout.x("Block ESP", g.method_51421(), 6),
                  HudLayout.y("Block ESP", g.method_51443(), g.method_51443() - 60),
                  0xFF000000 | ModuleSettings.blockColor,
                  true
               );
            }

            if (ModuleSettings.itemEsp) {
               int items = 0;

               for (class_1297 raw : c.field_1687.method_18112()) {
                  if (raw instanceof class_1542 item && !item.method_31481() && !(item.method_5858(c.field_1724) > 4096.0)) {
                     class_243 offset = item.method_30950(delta).method_1020(item.method_73189());
                     int[] b = EspProjection.bounds(
                        item.method_5829().method_997(offset).method_989(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350),
                        projection,
                        g.method_51421(),
                        g.method_51443()
                     );
                     if (b != null) {
                        frame(g, b, -601210, 1, true);
                        label(
                           g,
                           item.method_6983().method_7964().getString() + " x" + item.method_6983().method_7947(),
                           b[0],
                           Math.max(1, b[1] - 11),
                           -601210,
                           true
                        );
                        if (++items >= 128) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void label(class_332 g, String text, int x, int y, int color, boolean background) {
      class_327 font = class_310.method_1551().field_1772;
      float scale = g.method_51448().m00();
      String clipped = font.method_27523(text, Math.max(1, Math.round(g.method_51421() / scale) - x));
      if (background) {
         g.method_25294(x - 1, y - 1, x + font.method_1727(clipped) + 1, y + 9, -1508830436);
      }

      g.method_25303(font, clipped, x, y, color);
   }

   private static void frame(class_332 g, int[] b, int color, int thickness, boolean corners) {
      int t = Math.min(thickness, Math.max(1, Math.min(b[2] - b[0] + 1, b[3] - b[1] + 1)));
      int length = corners ? Math.max(t, (b[2] - b[0]) / 4) : b[2] - b[0] + 1;
      int vertical = corners ? Math.max(t, (b[3] - b[1]) / 4) : b[3] - b[1] + 1;

      for (int side = 0; side < 2; side++) {
         int yy = side == 0 ? b[1] : b[3] - t + 1;
         g.method_25294(b[0], yy, b[0] + length, yy + t, color);
         g.method_25294(b[2] - length + 1, yy, b[2] + 1, yy + t, color);
         int xx = side == 0 ? b[0] : b[2] - t + 1;
         g.method_25294(xx, b[1], xx + t, b[1] + vertical, color);
         g.method_25294(xx, b[3] - vertical + 1, xx + t, b[3] + 1, color);
      }
   }

   private static void line(class_332 g, int x, int y, int endX, int endY, int color) {
      int length = (int)Math.ceil(Math.hypot(endX - x, endY - y));
      if (length != 0) {
         g.method_51448().pushMatrix();
         g.method_51448().translate(x, y);
         g.method_51448().rotate((float)Math.atan2(endY - y, endX - x));
         g.method_25294(0, 0, length, 1, color);
         g.method_51448().popMatrix();
      }
   }

   private record EntityInfo(class_1309 entity, String name, String hp, List<EspOverlay.ItemInfo> items) {
   }

   private record ItemInfo(class_1799 stack, List<String> enchants) {
   }
}
