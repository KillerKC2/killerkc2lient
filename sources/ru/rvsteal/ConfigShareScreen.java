package ru.rvsteal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;

final class ConfigShareScreen extends class_437 {
   private final class_437 parent;
   private String status = "";
   private String name = "Мой конфиг";
   private final Path directory = FabricLoader.getInstance().getConfigDir().resolve("killerkc2-profiles");
   private List<String> names = List.of();

   ConfigShareScreen(class_437 parent) {
      super(class_2561.method_43470("Конфигурации"));
      this.parent = parent;
   }

   private Path profile() {
      if (!this.name.matches("[\\p{L}\\p{N} _-]{1,32}")) {
         throw new IllegalArgumentException("Имя: буквы, цифры, пробел, _ и -");
      } else {
         return this.directory.resolve(this.name + ".kc2");
      }
   }

   private void action(Runnable work) {
      try {
         work.run();
      } catch (Exception e) {
         this.status = "Ошибка: " + Objects.toString(e.getMessage(), "операция не выполнена");
      }
   }

   private void refresh() {
      try {
         Files.createDirectories(this.directory);

         try (Stream<Path> paths = Files.list(this.directory)) {
            this.names = paths.filter(p -> p.toString().endsWith(".kc2")).map(p -> p.getFileName().toString().replaceFirst("\\.kc2$", "")).sorted().toList();
         }
      } catch (Exception e) {
         this.status = "Не удалось прочитать профили";
      }
   }

   protected void method_25426() {
      this.refresh();
      int x = (this.field_22789 - 300) / 2;
      int y = Math.max(2, (this.field_22790 - 218) / 2);
      class_342 input = new class_342(this.field_22793, x + 12, y + 37, 276, 20, class_2561.method_43470("Имя профиля"));
      input.method_1880(32);
      input.method_1852(this.name);
      input.method_1863(v -> this.name = v);
      this.method_37063(input);
      this.method_37063(new PanelButton("Сохранить", x + 12, y + 64, 134, b -> this.action(() -> {
         try {
            Files.writeString(this.profile(), ConfigCodec.encode(RvSteal.snapshot()), StandardCharsets.UTF_8);
            this.refresh();
            this.status = "Профиль сохранён";
         } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка записи");
         }
      })));
      this.method_37063(new PanelButton("Загрузить", x + 154, y + 64, 134, b -> this.action(() -> {
         try {
            Path p = this.profile();
            if (Files.size(p) > 65536L) {
               throw new IllegalArgumentException("Слишком большой файл");
            }

            RvSteal.apply(ConfigCodec.decode(Files.readString(p)));
            RvSteal.save();
            this.status = "Загружено: " + this.name;
         } catch (IOException e) {
            throw new IllegalArgumentException("Профиль не найден");
         }
      })));
      this.method_37063(new PanelButton("Выбрать следующий", x + 12, y + 90, 134, b -> {
         if (!this.names.isEmpty()) {
            this.name = this.names.get((this.names.indexOf(this.name) + 1) % this.names.size());
            input.method_1852(this.name);
         }
      }));
      this.method_37063(new PanelButton("Удалить", x + 154, y + 90, 134, b -> this.action(() -> {
         try {
            Files.deleteIfExists(this.profile());
            this.refresh();
            this.status = "Удалено";
         } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка удаления");
         }
      })));
      this.method_37063(new PanelButton("Поделиться кодом", x + 12, y + 123, 134, b -> this.action(() -> {
         try {
            this.field_22787.field_1774.method_1455(ConfigCodec.encode(RvSteal.snapshot()));
            this.status = "Код скопирован";
         } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка копирования");
         }
      })));
      this.method_37063(new PanelButton("Импорт из буфера", x + 154, y + 123, 134, b -> this.action(() -> {
         try {
            RvSteal.apply(ConfigCodec.decode(this.field_22787.field_1774.method_1460()));
            RvSteal.save();
            this.status = "Импортировано";
         } catch (Exception e) {
            throw new IllegalArgumentException("Неверный код");
         }
      })));
      this.method_37063(new PanelButton("Назад", x + 12, y + 183, 276, b -> this.method_25419()));
   }

   public void method_25394(class_332 g, int mx, int my, float delta) {
      int x = (this.field_22789 - 300) / 2;
      int y = Math.max(2, (this.field_22790 - 218) / 2);
      g.method_25294(0, 0, this.field_22789, this.field_22790, -1610020080);
      Ui.round(g, x, y, 300, 214, 9, -99016919);
      g.method_27535(this.field_22793, this.field_22785, x + 12, y + 12, -2111489);
      g.method_25303(this.field_22793, "Имя профиля   •   сохранено: " + this.names.size(), x + 12, y + 26, -6906707);
      g.method_25303(this.field_22793, this.field_22793.method_27523(this.status, 276), x + 12, y + 158, -3294737);
      super.method_25394(g, mx, my, delta);
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }

   public boolean method_25421() {
      return false;
   }
}
