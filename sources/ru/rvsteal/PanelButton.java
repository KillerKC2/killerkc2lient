package ru.rvsteal;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_4185.class_4241;

final class PanelButton extends class_4185 {
   PanelButton(String label, int x, int y, int w, class_4241 action) {
      super(x, y, w, 20, class_2561.method_43470(label), action, field_40754);
   }

   protected void method_75752(class_332 g, int mx, int my, float delta) {
      Ui.round(g, this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364(), 5, this.method_25367() ? -10201215 : -13618881);
      class_327 font = class_310.method_1551().field_1772;
      g.method_27534(font, this.method_25369(), this.method_46426() + this.method_25368() / 2, this.method_46427() + 6, this.field_22763 ? -1120006 : -9145988);
   }
}
