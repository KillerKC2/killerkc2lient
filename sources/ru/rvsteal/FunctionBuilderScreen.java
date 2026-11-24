package ru.rvsteal;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;

final class FunctionBuilderScreen extends class_437 {
   private final class_437 parent;
   private String status = "";
   private String name = "";
   private String actions = "ночное зрение; координаты";

   FunctionBuilderScreen(class_437 parent) {
      super(class_2561.method_43470("Конструктор функций"));
      this.parent = parent;
   }

   protected void method_25426() {
      int x = (this.field_22789 - 300) / 2;
      int y = Math.max(4, (this.field_22790 - 200) / 2);
      class_342 n = new class_342(this.field_22793, x + 10, y + 32, 280, 20, class_2561.method_43470("Название"));
      n.method_1880(32);
      n.method_1852(this.name);
      n.method_47404(class_2561.method_43470("Название функции"));
      n.method_1863(v -> this.name = v);
      this.method_37063(n);
      class_342 a = new class_342(this.field_22793, x + 10, y + 65, 280, 20, class_2561.method_43470("Действия"));
      a.method_1880(1024);
      a.method_1852(this.actions);
      a.method_1863(v -> this.actions = v);
      this.method_37063(a);
      this.method_37063(new PanelButton("Сохранить функцию", x + 10, y + 95, 136, b -> {
         try {
            CustomFunctions.create(this.name, this.actions);
            RvSteal.save();
            this.status = "Сохранено во вкладку Custom";
         } catch (IllegalArgumentException e) {
            this.status = e.getMessage();
         }
      }));
      this.method_37063(new PanelButton("Удалить по имени", x + 154, y + 95, 136, b -> {
         CustomFunctions.functions.remove(this.name.trim());
         RvSteal.save();
         this.status = "Удалено";
      }));
      this.method_37063(new PanelButton("Доступные действия", x + 10, y + 123, 280, b -> this.field_22787.method_1507(new ActionsScreen(this))));
      this.method_37063(new PanelButton("Назад", x + 10, y + 167, 280, b -> this.method_25419()));
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      int x = (this.field_22789 - 300) / 2;
      int y = Math.max(4, (this.field_22790 - 200) / 2);
      g.method_25294(0, 0, this.field_22789, this.field_22790, -1610020080);
      Ui.round(g, x, y, 300, 196, 8, -99016919);
      g.method_27535(this.field_22793, this.field_22785, x + 10, y + 12, -2507265);
      g.method_25303(this.field_22793, "Действия через ;  (тумблер управляет всеми)", x + 10, y + 54, -6906707);
      g.method_25303(this.field_22793, this.field_22793.method_27523(this.status, 280), x + 10, y + 149, -3623443);
      super.method_25394(g, mx, my, delta);
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }

   public boolean method_25421() {
      return false;
   }
}
