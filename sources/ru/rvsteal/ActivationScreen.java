package ru.rvsteal;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;

final class ActivationScreen extends class_437 {
   private class_342 input;
   private String error = "";

   ActivationScreen() {
      super(class_2561.method_43470("KillerKC2 client"));
   }

   protected void method_25426() {
      int x = (this.field_22789 - 240) / 2;
      int y = this.field_22790 / 2;
      this.input = new class_342(this.field_22793, x, y - 10, 240, 20, class_2561.method_43470("Ключ активации"));
      this.input.method_1880(128);
      this.input.method_47404(class_2561.method_43470("Ключ активации"));
      this.method_37063(this.input);
      this.method_48265(this.input);
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Активировать"), button -> {
         if (AccessKey.activate(this.input.method_1882().trim())) {
            RvSteal.save();
            this.field_22787.method_1507(new RvScreen());
         } else {
            this.error = "Неверный ключ";
         }
      }).method_46434(x, y + 18, 240, 20).method_46431());
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -451931111);
      int x = (this.field_22789 - 240) / 2;
      int y = this.field_22790 / 2;
      g.method_25303(this.field_22793, "KILLERKC2 CLIENT / ACTIVATION", x, y - 48, -2897665);
      g.method_25303(this.field_22793, "Введите ключ для доступа к модулям", x, y - 30, -6709848);
      super.method_25394(g, mx, my, delta);
      g.method_25303(this.field_22793, this.error, x, y + 48, -34160);
   }

   public boolean method_25421() {
      return false;
   }
}
