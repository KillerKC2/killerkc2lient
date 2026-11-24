package ru.rvsteal;

import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;

final class HudEditorScreen extends class_437 {
   private final class_437 parent;
   private String dragging;
   private int dx;
   private int dy;

   HudEditorScreen(class_437 parent) {
      super(class_2561.method_43470("Перетащи элементы HUD"));
      this.parent = parent;
   }

   static int defaultX(String name, int w) {
      return switch (name) {
         case "Staffs" -> Math.max(8, w - 188);
         case "Target" -> w / 2 + 20;
         case "Armor" -> Math.max(0, w - 90);
         default -> 8;
      };
   }

   static int defaultY(String name, int h) {
      return switch (name) {
         case "Watermark", "Armor" -> 8;
         case "Coordinates" -> 35;
         case "Health" -> 55;
         case "Hotkeys" -> 80;
         case "Potions" -> 110;
         case "Better Near" -> 140;
         case "Target" -> h / 2 + 20;
         case "Block ESP" -> h - 60;
         default -> 72;
      };
   }

   protected void method_25426() {
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Сброс HUD"), b -> HudLayout.reset())
            .method_46434(this.field_22789 / 2 - 105, 4, 100, 20)
            .method_46431()
      );
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Готово"), b -> this.method_25419()).method_46434(this.field_22789 / 2 + 5, 4, 100, 20).method_46431()
      );
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -2146364644);

      for (String name : HudLayout.NAMES) {
         int x = HudLayout.x(name, this.field_22789, defaultX(name, this.field_22789));
         int y = HudLayout.y(name, this.field_22790, defaultY(name, this.field_22790));
         Ui.round(g, x, y, 90, 18, 4, -802607045);
         g.method_25303(this.field_22793, name, x + 4, y + 5, -1910022);
      }

      super.method_25394(g, mx, my, delta);
   }

   public boolean method_25402(class_11909 e, boolean dbl) {
      if (super.method_25402(e, dbl)) {
         return true;
      }

      if (e.method_74245() != 0) {
         return false;
      }

      for (int i = HudLayout.NAMES.length - 1; i >= 0; i--) {
         String name = HudLayout.NAMES[i];
         int x = HudLayout.x(name, this.field_22789, defaultX(name, this.field_22789));
         int y = HudLayout.y(name, this.field_22790, defaultY(name, this.field_22790));
         if (e.comp_4798() >= x && e.comp_4798() < x + 90 && e.comp_4799() >= y && e.comp_4799() < y + 18) {
            this.dragging = name;
            this.dx = (int)e.comp_4798() - x;
            this.dy = (int)e.comp_4799() - y;
            return true;
         }
      }

      return false;
   }

   public boolean method_25403(class_11909 e, double x, double y) {
      if (this.dragging == null) {
         return false;
      }

      HudLayout.move(
         this.dragging,
         Math.clamp((int)e.comp_4798() - this.dx, 0, Math.max(0, this.field_22789 - 90)),
         Math.clamp((int)e.comp_4799() - this.dy, 0, Math.max(0, this.field_22790 - 18)),
         this.field_22789,
         this.field_22790
      );
      return true;
   }

   public boolean method_25406(class_11909 e) {
      if (this.dragging != null) {
         this.dragging = null;
         RvSteal.save();
         return true;
      } else {
         return super.method_25406(e);
      }
   }

   public void method_25419() {
      RvSteal.save();
      this.field_22787.method_1507(this.parent);
   }

   public boolean method_25421() {
      return false;
   }
}
