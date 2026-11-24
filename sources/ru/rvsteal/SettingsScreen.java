package ru.rvsteal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_7923;
import org.lwjgl.glfw.GLFW;

final class SettingsScreen extends class_437 {
   private final class_437 parent;
   private final RvScreen.Module module;
   private boolean binding;
   private int page;
   private String error = "";
   private final List<Runnable> rows = new ArrayList<>();
   private int x;
   private int y;
   private int row;

   SettingsScreen(class_437 parent, RvScreen.Module module) {
      super(class_2561.method_43470(module.name() + " — настройки"));
      this.parent = parent;
      this.module = module;
   }

   private void button(String label, Runnable action) {
      int index = this.rows.size();
      this.rows.add(() -> this.method_37063(class_4185.method_46430(class_2561.method_43470(label), b -> {
         action.run();
         RvSteal.save();
         this.method_41843();
      }).method_46434(this.x, this.y + 52 + index % 4 * 24, 260, 20).method_46431()));
   }

   private void toggle(String label, BooleanSupplier get, Consumer<Boolean> set) {
      this.button(label + ": " + (get.getAsBoolean() ? "Вкл" : "Выкл"), () -> set.accept(!get.getAsBoolean()));
   }

   private void colorInput(String label, int color, IntConsumer set) {
      int index = this.rows.size();
      this.rows.add(() -> {
         int yy = this.y + 52 + index % 4 * 24;
         class_342 input = new class_342(this.field_22793, this.x, yy, 260, 20, class_2561.method_43470(label + " #RRGGBB"));
         input.method_1880(48);
         input.method_1852(label + ": #" + ModuleSettings.hex(color));
         input.method_1863(value -> {
            String hex = value.substring(value.lastIndexOf(35) + 1).trim();
            if (!hex.matches("[0-9a-fA-F]{6}")) {
               this.error = "Цвет: 6 символов после #";
            } else {
               set.accept(Integer.parseInt(hex, 16));
               this.error = "";
               RvSteal.save();
            }
         });
         this.method_37063(input);
      });
   }

   protected void method_25426() {
      this.x = (this.field_22789 - 260) / 2;
      this.y = Math.max(2, (this.field_22790 - 210) / 2);
      this.row = 0;
      this.rows.clear();
      this.method_37063(class_4185.method_46430(class_2561.method_43470(this.binding ? "Нажми клавишу; Esc — отмена" : "Бинд: " + this.keyName()), b -> {
         this.binding = true;
         this.method_41843();
      }).method_46434(this.x, this.y + 24, 260, 20).method_46431());
      String name = this.module.name();
      if (UserScripts.scripts.containsKey(name)) {
         this.button("Редактировать Lua", () -> this.field_22787.method_1507(new ScriptEditorScreen(this, name)));
      }

      if (CustomFunctions.functions.containsKey(name)) {
         CustomFunctions.Function f = CustomFunctions.functions.get(name);

         for (RvScreen.Module action : RvScreen.baseModules()) {
            if (f.actions().contains(action.name())) {
               this.button("Настройки: " + action.name(), () -> this.field_22787.method_1507(new SettingsScreen(this, action)));
            }
         }
      }

      if (name.equals("Hitboxes")) {
         this.button(
            "Добавить к каждой стороне: " + String.format(Locale.ROOT, "%.1f", ModuleSettings.hitboxSize / 100.0) + " блоков",
            () -> ModuleSettings.hitboxSize = ModuleSettings.hitboxSize >= 300 ? 0 : ModuleSettings.hitboxSize + 10
         );
      }

      if (name.equals("Player Aura") || name.equals("Mob Aura")) {
         boolean player = name.equals("Player Aura");
         boolean legit = player ? ModuleSettings.playerLegit : ModuleSettings.mobLegit;
         this.button("Режим: " + (legit ? "Legit" : "Rage"), () -> {
            if (player) {
               ModuleSettings.playerLegit = !ModuleSettings.playerLegit;
            } else {
               ModuleSettings.mobLegit = !ModuleSettings.mobLegit;
            }
         });
      }

      if (name.equals("Player ESP") || name.equals("Mob ESP")) {
         ModuleSettings.Esp e = name.equals("Player ESP") ? ModuleSettings.players : ModuleSettings.mobs;
         this.toggle("Рамка", () -> e.box, v -> e.box = v);
         this.toggle("Имя", () -> e.name, v -> e.name = v);
         this.toggle("HP", () -> e.hp, v -> e.hp = v);
         this.toggle("Экипировка", () -> e.gear, v -> e.gear = v);
         this.toggle("Чары", () -> e.enchants, v -> e.enchants = v);
         this.button("Дальность: " + e.range, () -> e.range = e.range >= 128 ? 16 : e.range + 16);
         this.colorInput("Рамка", e.color, v -> e.color = v);
         this.colorInput("Текст", e.textColor, v -> e.textColor = v);
         this.colorInput("Обводка", e.outlineColor, v -> e.outlineColor = v);
         this.button("Толщина: " + e.thickness + " px", () -> e.thickness = e.thickness % 4 + 1);
         this.button("Заливка: " + e.fill, () -> e.fill = e.fill >= 150 ? 0 : e.fill + 30);
         this.button("Масштаб текста/вещей: " + e.scale + "%", () -> e.scale = e.scale >= 200 ? 50 : e.scale + 25);
         this.button("Отступ рамки: " + e.padding + " px", () -> e.padding = e.padding >= 12 ? 0 : e.padding + 2);
         this.toggle("Угловая рамка", () -> e.corners, v -> e.corners = v);
         this.toggle("Полоска здоровья", () -> e.healthBar, v -> e.healthBar = v);
         this.toggle("Дистанция", () -> e.distance, v -> e.distance = v);
         this.toggle("Линии к целям", () -> e.tracers, v -> e.tracers = v);
         this.toggle("Внешняя обводка", () -> e.shadow, v -> e.shadow = v);
         this.toggle("Фон подписей", () -> e.background, v -> e.background = v);
      }

      if (name.equals("Block ESP")) {
         this.button("Радиус: " + ModuleSettings.blockRange, () -> {
            ModuleSettings.blockRange = ModuleSettings.blockRange >= 32 ? 8 : ModuleSettings.blockRange + 8;
            EspOverlay.resetBlocks();
         });
         this.button("Сундуки и шалкеры", () -> setBlocks("minecraft:chest,minecraft:trapped_chest,minecraft:ender_chest,minecraft:shulker_box"));
         this.button("Алмазы и древние обломки", () -> setBlocks("minecraft:diamond_ore,minecraft:deepslate_diamond_ore,minecraft:ancient_debris"));
         this.button("Спавнеры", () -> setBlocks("minecraft:spawner,minecraft:trial_spawner"));
         this.colorInput("Цвет блоков", ModuleSettings.blockColor, v -> ModuleSettings.blockColor = v);
         int index = this.rows.size();
         this.rows
            .add(
               () -> {
                  class_342 input = new class_342(
                     this.field_22793, this.x, this.y + 52 + index % 4 * 24, 260, 20, class_2561.method_43470("ID блоков через запятую")
                  );
                  input.method_1880(2048);
                  input.method_1852(ModuleSettings.blocks);
                  input.method_1863(value -> {
                     if (!validBlocks(value)) {
                        this.error = "Неизвестный ID блока";
                     } else {
                        this.error = "";
                        setBlocks(value);
                        RvSteal.save();
                     }
                  });
                  this.method_37063(input);
               }
            );
      }

      if (name.equals("World Theme")) {
         this.button("Тема: Lavender", () -> {
            ModuleSettings.skyColor = 9668058;
            ModuleSettings.fogColor = 12889579;
            ModuleSettings.cloudColor = 16766445;
         });
         this.button("Тема: Sunset", () -> {
            ModuleSettings.skyColor = 12677543;
            ModuleSettings.fogColor = 15971485;
            ModuleSettings.cloudColor = 16766128;
         });
         this.button("Тема: Arctic", () -> {
            ModuleSettings.skyColor = 4361128;
            ModuleSettings.fogColor = 10804456;
            ModuleSettings.cloudColor = 14022143;
         });
         this.colorInput("Небо", ModuleSettings.skyColor, v -> ModuleSettings.skyColor = v);
         this.colorInput("Туман", ModuleSettings.fogColor, v -> ModuleSettings.fogColor = v);
         this.colorInput("Облака", ModuleSettings.cloudColor, v -> ModuleSettings.cloudColor = v);
      }

      if (name.equals("Ambient Particles")) {
         this.button(
            "Частицы: " + new String[]{"End Rod", "Cherry", "Firefly"}[ModuleSettings.particleStyle],
            () -> ModuleSettings.particleStyle = (ModuleSettings.particleStyle + 1) % 3
         );
         this.button("Плотность: " + ModuleSettings.particleDensity, () -> ModuleSettings.particleDensity = ModuleSettings.particleDensity % 5 + 1);
      }

      int pages = Math.max(1, (this.rows.size() + 3) / 4);
      this.page = Math.min(this.page, pages - 1);

      for (int i = this.page * 4; i < Math.min(this.rows.size(), (this.page + 1) * 4); i++) {
         this.rows.get(i).run();
      }

      this.method_37063(class_4185.method_46430(class_2561.method_43470("<"), b -> {
         this.page = Math.max(0, this.page - 1);
         this.method_41843();
      }).method_46434(this.x, this.y + 154, 30, 20).method_46431());
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Назад  " + (this.page + 1) + "/" + pages), b -> this.method_25419())
            .method_46434(this.x + 34, this.y + 154, 192, 20)
            .method_46431()
      );
      this.method_37063(class_4185.method_46430(class_2561.method_43470(">"), b -> {
         this.page = Math.min(pages - 1, this.page + 1);
         this.method_41843();
      }).method_46434(this.x + 230, this.y + 154, 30, 20).method_46431());
   }

   private static boolean validBlocks(String value) {
      if (value.isBlank()) {
         return false;
      }

      for (String part : value.split(",", -1)) {
         class_2960 id = class_2960.method_12829(part.trim());
         if (id == null || !class_7923.field_41175.method_10250(id)) {
            return false;
         }
      }

      return true;
   }

   private static void setBlocks(String value) {
      ModuleSettings.blocks = value;
      EspOverlay.resetBlocks();
   }

   private String keyName() {
      int key = ModuleSettings.binds.getOrDefault(this.module.name(), -1);
      if (key < 0) {
         return "нет";
      }

      String name = GLFW.glfwGetKeyName(key, 0);
      return (name == null ? "KEY " + key : name.toUpperCase(Locale.ROOT)) + " (Del — убрать)";
   }

   public boolean method_25404(class_11908 event) {
      if (!this.binding) {
         return super.method_25404(event);
      }

      int key = event.comp_4795();
      if (key == 256) {
         this.binding = false;
         this.method_41843();
         return true;
      }

      if (key != 261 && key != 259) {
         if (key < 32 || key > 348 || key == 344 || key == 296) {
            return true;
         }

         ModuleSettings.binds.put(this.module.name(), key);
      } else {
         ModuleSettings.binds.remove(this.module.name());
      }

      this.binding = false;
      RvSteal.save();
      this.method_41843();
      return true;
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -401534180);
      g.method_27535(this.field_22793, this.field_22785, this.x, this.y + 7, -2507265);
      super.method_25394(g, mx, my, delta);
      if (!this.error.isEmpty()) {
         g.method_25303(this.field_22793, this.error, this.x, this.y + 182, -34953);
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
