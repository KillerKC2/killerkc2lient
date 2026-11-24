package ru.rvsteal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;

final class RvScreen extends class_437 {
   private int category;
   private int scroll;
   private String query = "";
   private class_342 search;
   private static List<RvScreen.Module> base;

   RvScreen() {
      super(class_2561.method_43470("KillerKC2 client"));
   }

   private List<RvScreen.Module> modules() {
      return (this.query.isBlank() ? categoryModules(this.category) : allModules())
         .stream()
         .filter(m -> m.name().toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT)))
         .toList();
   }

   static List<RvScreen.Module> baseModules() {
      if (base == null) {
         ArrayList<RvScreen.Module> list = new ArrayList<>();

         for (int i = 0; i < 6; i++) {
            list.addAll(categoryModules(i));
         }

         base = List.copyOf(list);
      }

      return base;
   }

   static List<RvScreen.Module> allModules() {
      ArrayList<RvScreen.Module> list = new ArrayList<>(baseModules());
      list.addAll(CustomFunctions.modules());
      list.addAll(UserScripts.modules());
      return list;
   }

   private static List<RvScreen.Module> categoryModules(int category) {
      return switch (category) {
         case 0 -> List.of(
            new RvScreen.Module("Hitboxes", "Увеличение области прицеливания игроков", () -> ModuleSettings.hitboxes, v -> ModuleSettings.hitboxes = v),
            new RvScreen.Module("Auto Totem", "Тотем из инвентаря во вторую руку", () -> ModuleSettings.autoTotem, v -> ModuleSettings.autoTotem = v),
            new RvScreen.Module("Player Aura", "Атака ближайшего игрока", () -> RvSteal.playerAura, v -> RvSteal.playerAura = v),
            new RvScreen.Module("Trigger PvP", "Атака игрока под прицелом", () -> RvSteal.attackPlayers, v -> RvSteal.attackPlayers = v)
         );
         case 1 -> List.of(
            new RvScreen.Module("Mob Aura", "Атака ближайшего монстра", () -> RvSteal.mobAura, v -> RvSteal.mobAura = v),
            new RvScreen.Module("Trigger PvE", "Атака монстра под прицелом", () -> RvSteal.attackMobs, v -> RvSteal.attackMobs = v),
            new RvScreen.Module("Auto Tool", "Выбор инструмента из хотбара", () -> RvSteal.autoTool, v -> RvSteal.autoTool = v),
            new RvScreen.Module("Auto Respawn", "Возрождение после смерти", () -> RvSteal.autoRespawn, v -> RvSteal.autoRespawn = v)
         );
         case 2 -> List.of(
            new RvScreen.Module("Auto Sprint", "Спринт при движении вперёд", () -> RvSteal.autoSprint, v -> RvSteal.autoSprint = v),
            new RvScreen.Module("Bunny Hop", "Прыжки при движении вперёд", () -> RvSteal.bunnyHop, v -> RvSteal.bunnyHop = v)
         );
         case 3 -> List.of(
            new RvScreen.Module("Player ESP", "Подсветка загруженных игроков", () -> RvSteal.playerEsp, v -> RvSteal.playerEsp = v),
            new RvScreen.Module("Mob ESP", "Подсветка враждебных мобов", () -> RvSteal.mobEsp, v -> RvSteal.mobEsp = v),
            new RvScreen.Module("Item ESP", "Название и количество дропа", () -> ModuleSettings.itemEsp, v -> ModuleSettings.itemEsp = v),
            new RvScreen.Module("Block ESP", "Выбранные блоки сквозь стены", () -> ModuleSettings.blockEsp, v -> ModuleSettings.blockEsp = v),
            new RvScreen.Module("Throw Trails", "Следы жемчуга, зелий и других бросков", () -> ModuleSettings.pearlTrails, v -> ModuleSettings.pearlTrails = v)
         );
         case 4 -> List.of(
            new RvScreen.Module("World Theme", "Цвета неба, облаков и тумана", () -> ModuleSettings.worldTheme, v -> ModuleSettings.worldTheme = v),
            new RvScreen.Module("Ambient Particles", "Частицы вокруг игрока", () -> ModuleSettings.ambientParticles, v -> ModuleSettings.ambientParticles = v),
            new RvScreen.Module("Night Vision", "Клиентское ночное зрение", () -> ModuleSettings.nightVision, v -> ModuleSettings.nightVision = v),
            new RvScreen.Module("Brightness", "Яркость игры на максимум", () -> RvSteal.bright, v -> RvSteal.bright = v),
            new RvScreen.Module("No Hurt Cam", "Отключает качание камеры при уроне", () -> ModuleSettings.noHurtCam, v -> ModuleSettings.noHurtCam = v),
            new RvScreen.Module("Item Physics", "Предметы лежат на земле", () -> ModuleSettings.itemPhysics, v -> ModuleSettings.itemPhysics = v)
         );
         case 5 -> List.of(
            new RvScreen.Module("Watermark", "Имя клиента, FPS и ping", () -> ModuleSettings.watermark, v -> ModuleSettings.watermark = v),
            new RvScreen.Module("Hotkeys HUD", "Включённые модули с биндом", () -> ModuleSettings.hotkeysHud, v -> ModuleSettings.hotkeysHud = v),
            new RvScreen.Module("Potions HUD", "Активные эффекты и время", () -> ModuleSettings.potionsHud, v -> ModuleSettings.potionsHud = v),
            new RvScreen.Module("Staffs HUD", "Ранги staff/admin/mod из TAB", () -> ModuleSettings.staffHud, v -> ModuleSettings.staffHud = v),
            new RvScreen.Module("Target HUD", "Здоровье, броня и эффекты цели", () -> ModuleSettings.targetHud, v -> ModuleSettings.targetHud = v),
            new RvScreen.Module("Better Near", "Список игроков рядом", () -> RvSteal.betterNear, v -> RvSteal.betterNear = v),
            new RvScreen.Module("Armor HUD", "Броня и прочность", () -> RvSteal.armorHud, v -> RvSteal.armorHud = v),
            new RvScreen.Module("XYZ / FPS", "Координаты и частота кадров", () -> RvSteal.coordinatesHud, v -> RvSteal.coordinatesHud = v),
            new RvScreen.Module("Health HUD", "Здоровье и голод", () -> RvSteal.healthHud, v -> RvSteal.healthHud = v)
         );
         case 6 -> {
            ArrayList<RvScreen.Module> list = new ArrayList<>(CustomFunctions.modules());
            list.addAll(UserScripts.modules());
            yield list;
         }
         default -> List.of(
            new RvScreen.Module("Configs", "Профили и обмен", () -> false, v -> {}),
            new RvScreen.Module("HUD editor", "Расположение виджетов", () -> false, v -> {}),
            new RvScreen.Module("Function builder", "Создание функций", () -> false, v -> {}),
            new RvScreen.Module("Script editor", "Программирование Lua", () -> false, v -> {})
         );
      };
   }

   private int panelWidth() {
      return Math.min(540, this.field_22789 - 8);
   }

   private int panelHeight() {
      return Math.min(340, this.field_22790 - 8);
   }

   private int left() {
      return (this.field_22789 - this.panelWidth()) / 2;
   }

   private int top() {
      return (this.field_22790 - this.panelHeight()) / 2;
   }

   private int sidebar() {
      return Math.min(108, this.panelWidth() / 3);
   }

   private int columns() {
      return this.panelWidth() >= 400 ? 2 : 1;
   }

   private int cardWidth() {
      return (this.panelWidth() - this.sidebar() - 24) / this.columns() - 4;
   }

   private int visibleRows() {
      return Math.max(1, (this.panelHeight() - 66) / 65);
   }

   private int maxScroll() {
      return Math.max(0, (this.modules().size() + this.columns() - 1) / this.columns() - this.visibleRows());
   }

   protected void method_25426() {
      if (!AccessKey.unlocked()) {
         this.field_22787.method_1507(new ActivationScreen());
      } else {
         this.search = new class_342(
            this.field_22793,
            this.left() + this.sidebar() + 12,
            this.top() + 10,
            this.panelWidth() - this.sidebar() - 24,
            18,
            class_2561.method_43470("Search modules")
         );
         this.search.method_1858(false);
         this.search.method_47404(class_2561.method_43470("Search..."));
         this.search.method_1880(64);
         this.search.method_1852(this.query);
         this.search.method_1863(value -> {
            this.query = value;
            this.scroll = 0;
         });
         this.method_37063(this.search);
      }
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      int x = this.left();
      int y = this.top();
      int w = this.panelWidth();
      int h = this.panelHeight();
      int side = this.sidebar();
      g.method_25294(0, 0, this.field_22789, this.field_22790, 1074334480);
      Ui.round(g, x - 2, y - 2, w + 4, h + 4, 11, 1428890943);
      Ui.round(g, x, y, w, h, 10, -266328532);
      Ui.round(g, x, y, side, h, 10, -267118306);
      g.method_25303(this.field_22793, "K", x + 16, y + 16, -4410369);
      g.method_25303(this.field_22793, "CLIENT", x + 31, y + 16, -2433819);
      String[] names = new String[]{"Combat", "PvE", "Movement", "ESP", "World", "HUD", "Custom", "Tools"};

      for (int i = 0; i < names.length; i++) {
         int ty = y + 47 + i * 18;
         if (this.category == i) {
            Ui.round(g, x + 7, ty - 5, side - 14, 17, 5, -15789031);
         }

         g.method_25303(this.field_22793, names[i], x + 15, ty + 1, this.category == i ? -921094 : -7169370);
      }

      g.method_25303(this.field_22793, "Hide client", x + 12, y + h - 27, -23119);
      g.method_25303(this.field_22793, "5x F7", x + 12, y + h - 14, -7568451);
      Ui.round(g, x + side + 7, y + 6, w - side - 14, 25, 7, -15262429);
      List<RvScreen.Module> list = this.modules();

      for (int row = 0; row < this.visibleRows(); row++) {
         for (int col = 0; col < this.columns(); col++) {
            int index = (this.scroll + row) * this.columns() + col;
            if (index < list.size()) {
               RvScreen.Module m = list.get(index);
               int rx = x + side + 10 + col * (this.cardWidth() + 8);
               int ry = y + 42 + row * 65;
               int cw = this.cardWidth();
               Ui.round(g, rx, ry, cw, 58, 6, -15130843);
               g.method_25303(this.field_22793, this.field_22793.method_27523(m.name(), cw - 12), rx + 6, ry + 6, -4604472);
               boolean tool = this.category == 7 && this.query.isBlank();
               g.method_25303(this.field_22793, tool ? "Открыть" : "Active", rx + 6, ry + 24, -986890);
               if (!tool) {
                  boolean enabled = m.get().getAsBoolean();
                  Ui.round(g, rx + cw - 30, ry + 22, 23, 12, 6, enabled ? -5858319 : -11446174);
                  Ui.round(g, rx + cw - (enabled ? 17 : 28), ry + 23, 10, 10, 5, -1250056);
               }

               g.method_25303(this.field_22793, tool ? "ЛКМ: открыть" : "ПКМ: настройки", rx + 6, ry + 43, -9932930);
            }
         }
      }

      if (list.isEmpty()) {
         g.method_25303(this.field_22793, "Ничего не найдено", x + side + 14, y + 48, -6774868);
      }

      if (this.maxScroll() > 0) {
         g.method_25303(this.field_22793, this.scroll + 1 + " / " + (this.maxScroll() + 1) + "  scroll", x + side + 14, y + h - 12, -9274486);
      }

      super.method_25394(g, mx, my, delta);
   }

   public boolean method_25402(class_11909 event, boolean doubleClick) {
      if (super.method_25402(event, doubleClick)) {
         return true;
      }

      if (event.method_74245() != 0 && event.method_74245() != 1) {
         return false;
      }

      int x = this.left();
      int y = this.top();
      if (event.method_74245() == 0 && event.comp_4798() >= x + 7 && event.comp_4798() < x + this.sidebar() - 7) {
         if (event.comp_4799() >= y + this.panelHeight() - 32 && event.comp_4799() < y + this.panelHeight() - 16) {
            RvSteal.hide();
            return true;
         }

         for (int i = 0; i < 8; i++) {
            if (event.comp_4799() >= y + 42 + i * 18 && event.comp_4799() < y + 59 + i * 18) {
               this.category = i;
               this.scroll = 0;
               this.search.method_1852("");
               return true;
            }
         }
      }

      List<RvScreen.Module> list = this.modules();

      for (int row = 0; row < this.visibleRows(); row++) {
         for (int col = 0; col < this.columns(); col++) {
            int index = (this.scroll + row) * this.columns() + col;
            if (index < list.size()) {
               int rx = x + this.sidebar() + 10 + col * (this.cardWidth() + 8);
               int ry = y + 42 + row * 65;
               if (!(event.comp_4798() < rx) && !(event.comp_4798() >= rx + this.cardWidth()) && !(event.comp_4799() < ry) && !(event.comp_4799() >= ry + 58)) {
                  RvScreen.Module module = list.get(index);
                  if (module.name().equals("Configs")) {
                     this.field_22787.method_1507(new ConfigShareScreen(this));
                     return true;
                  }

                  if (module.name().equals("HUD editor")) {
                     this.field_22787.method_1507(new HudEditorScreen(this));
                     return true;
                  }

                  if (module.name().equals("Function builder")) {
                     this.field_22787.method_1507(new FunctionBuilderScreen(this));
                     return true;
                  }

                  if (module.name().equals("Script editor")) {
                     this.field_22787.method_1507(new ScriptEditorScreen(this));
                     return true;
                  }

                  if (event.method_74245() == 1) {
                     this.field_22787.method_1507(new SettingsScreen(this, module));
                  } else {
                     module.set().accept(!module.get().getAsBoolean());
                     RvSteal.save();
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean method_25401(double x, double y, double horizontal, double vertical) {
      this.scroll = Math.clamp(this.scroll - (int)Math.signum(vertical), 0, this.maxScroll());
      return true;
   }

   public boolean method_25421() {
      return false;
   }

   record Module(String name, String detail, BooleanSupplier get, Consumer<Boolean> set) {
   }
}
