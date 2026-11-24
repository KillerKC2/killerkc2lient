package ru.rvsteal;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_7529;

final class ScriptEditorScreen extends class_437 {
   private final class_437 parent;
   private String name = "";
   private String code = "-- Выполняется каждый тик\nif health() < 10 then\n  module(\"Night Vision\", true)\nend";
   private String status = "";

   ScriptEditorScreen(class_437 parent) {
      super(class_2561.method_43470("Редактор Lua"));
      this.parent = parent;
   }

   ScriptEditorScreen(class_437 parent, String name) {
      this(parent);
      UserScripts.Script s = UserScripts.scripts.get(name);
      if (s != null) {
         this.name = name;
         this.code = s.code();
      }
   }

   protected void method_25426() {
      int w = Math.min(340, this.field_22789 - 16);
      int x = (this.field_22789 - w) / 2;
      int y = 6;
      class_342 n = new class_342(this.field_22793, x, y + 18, w, 20, class_2561.method_43470("Название"));
      n.method_1880(32);
      n.method_1852(this.name);
      n.method_47404(class_2561.method_43470("Название функции"));
      n.method_1863(v -> this.name = v);
      this.method_37063(n);
      int editorHeight = Math.max(24, this.field_22790 - 131);
      class_7529 editor = class_7529.method_71507()
         .method_71508(x)
         .method_71512(y + 52)
         .method_71513(true)
         .method_71515(true)
         .method_71510(class_2561.method_43470("Код Lua"))
         .method_71509(this.field_22793, w, editorHeight, class_2561.method_43470("Код Lua"));
      editor.method_44402(4096);
      editor.method_44400(this.code);
      editor.method_44401(v -> this.code = v);
      this.method_37063(editor);
      int row = y + 56 + editorHeight;
      int half = (w - 4) / 2;
      this.method_37063(new PanelButton("Сохранить", x, row, half, b -> {
         try {
            UserScripts.create(this.name, this.code);
            RvSteal.save();
            this.status = "Сохранено в Custom (выключено)";
         } catch (RuntimeException e) {
            this.status = e.getMessage();
         }
      }));
      this.method_37063(new PanelButton("Удалить", x + half + 4, row, half, b -> {
         UserScripts.delete(this.name.trim());
         RvSteal.save();
         this.status = "Удалено";
      }));
      this.method_37063(new PanelButton("Назад", x, row + 24, w, b -> this.method_25419()));
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      int w = Math.min(340, this.field_22789 - 16);
      int x = (this.field_22789 - w) / 2;
      g.method_25294(0, 0, this.field_22789, this.field_22790, -266789079);
      g.method_27535(this.field_22793, this.field_22785, x, 8, -2111489);
      g.method_25303(this.field_22793, "Lua: health(), hunger(), module(name, bool), message(text)", x, 44, -5661251);
      g.method_25303(this.field_22793, this.field_22793.method_27523(this.status, w), x, this.field_22790 - 16, -2111489);
      super.method_25394(g, mx, my, delta);
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }

   public boolean method_25421() {
      return false;
   }
}
