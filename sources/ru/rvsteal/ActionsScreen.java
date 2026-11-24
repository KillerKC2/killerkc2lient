package ru.rvsteal;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

final class ActionsScreen extends class_437 {
   private final class_437 parent;

   ActionsScreen(class_437 parent) {
      super(class_2561.method_43470("Доступные действия"));
      this.parent = parent;
   }

   protected void method_25426() {
      this.method_37063(new PanelButton("Назад", (this.field_22789 - 260) / 2, this.field_22790 - 28, 260, b -> this.method_25419()));
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -266789079);
      int x = (this.field_22789 - 300) / 2;
      int y = 12;
      g.method_27535(this.field_22793, this.field_22785, x, y, -2111489);
      String[] names = CustomFunctions.supported().split("; ");

      for (int i = 0; i < names.length; i++) {
         g.method_25303(this.field_22793, names[i], x + i % 2 * 150, y + 28 + i / 2 * 19, -3818024);
      }

      g.method_25303(this.field_22793, "Например: включить ночное зрение; координаты", x, this.field_22790 - 47, -5989955);
      super.method_25394(g, mx, my, delta);
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }
}
