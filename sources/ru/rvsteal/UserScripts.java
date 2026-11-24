package ru.rvsteal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_746;

final class UserScripts {
   static final int MAX_SCRIPTS = 8;
   static final int MAX_CODE = 4096;
   static final LinkedHashMap<String, UserScripts.Script> scripts = new LinkedHashMap<>();
   private static final Map<String, LuaRuntime> runtimes = new HashMap<>();
   private static final LuaRuntime.Game GAME = new LuaRuntime.Game() {
      @Override
      public double health() {
         class_746 p = class_310.method_1551().field_1724;
         return p == null ? 0.0 : p.method_6032();
      }

      @Override
      public int hunger() {
         class_746 p = class_310.method_1551().field_1724;
         return p == null ? 0 : p.method_7344().method_7586();
      }

      @Override
      public boolean module(String name, boolean enabled) {
         for (RvScreen.Module m : RvScreen.baseModules()) {
            if (m.name().equalsIgnoreCase(name)) {
               if (m.get().getAsBoolean() != enabled) {
                  m.set().accept(enabled);
                  RvSteal.save();
               }

               return true;
            }
         }

         return false;
      }

      @Override
      public void message(String text) {
         class_746 p = class_310.method_1551().field_1724;
         if (p != null) {
            p.method_7353(class_2561.method_43470(text.substring(0, Math.min(text.length(), 160))), false);
         }
      }
   };

   static void create(String name, String code) {
      name = name.trim();
      if (name.isEmpty() || name.length() > 32 || name.contains(".") || name.contains("\n") || name.contains("\r")) {
         throw new IllegalArgumentException("Название: 1–32 символа без точки");
      }

      if (code.isBlank() || code.length() > 4096) {
         throw new IllegalArgumentException("Код: 1–4096 символов");
      }

      if (scripts.size() >= 8 && !scripts.containsKey(name)) {
         throw new IllegalArgumentException("Не более 8 скриптов");
      }

      String checked = name;
      if (RvScreen.baseModules().stream().anyMatch(m -> m.name().equalsIgnoreCase(checked))
         || CustomFunctions.functions.keySet().stream().anyMatch(n -> n.equalsIgnoreCase(checked))
         || Set.of("Configs", "HUD editor", "Function builder", "Script editor").stream().anyMatch(n -> n.equalsIgnoreCase(checked))) {
         throw new IllegalArgumentException("Название занято");
      }

      if (scripts.keySet().stream().anyMatch(n -> n.equalsIgnoreCase(checked) && !n.equals(checked))) {
         throw new IllegalArgumentException("Название занято");
      }

      Properties sizeCheck = new Properties();

      for (UserScripts.Script item : scripts.values()) {
         if (!item.name().equals(name)) {
            sizeCheck.setProperty(item.name(), item.code());
         }
      }

      sizeCheck.setProperty(name, code);

      try {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         sizeCheck.store(bytes, null);
         if (bytes.size() > 22000) {
            throw new IllegalArgumentException("Скрипты слишком большие для обмена конфигом");
         }
      } catch (IOException e) {
         throw new IllegalArgumentException("Ошибка проверки размера", e);
      }

      try {
         new LuaRuntime(code, GAME);
      } catch (RuntimeException | StackOverflowError e) {
         throw new IllegalArgumentException("Ошибка Lua: " + e.getMessage());
      }

      scripts.put(name, new UserScripts.Script(name, code, false));
      runtimes.remove(name);
   }

   static void delete(String name) {
      scripts.remove(name);
      runtimes.remove(name);
      ModuleSettings.binds.remove(name);
   }

   static List<RvScreen.Module> modules() {
      ArrayList<RvScreen.Module> result = new ArrayList<>();

      for (UserScripts.Script script : scripts.values()) {
         result.add(new RvScreen.Module(script.name(), "Lua скрипт", () -> scripts.get(script.name()).enabled(), enabled -> {
            UserScripts.Script current = scripts.get(script.name());
            if (enabled) {
               runtimes.put(current.name(), new LuaRuntime(current.code(), GAME));
            } else {
               runtimes.remove(current.name());
            }

            scripts.put(current.name(), new UserScripts.Script(current.name(), current.code(), enabled));
         }));
      }

      return result;
   }

   static void tick(class_310 client) {
      if (client.field_1724 != null && client.field_1687 != null) {
         for (UserScripts.Script entry : new ArrayList<>(scripts.values())) {
            if (entry.enabled()) {
               try {
                  runtimes.get(entry.name()).tick();
               } catch (RuntimeException | StackOverflowError e) {
                  scripts.put(entry.name(), new UserScripts.Script(entry.name(), entry.code(), false));
                  runtimes.remove(entry.name());
                  String error = Objects.toString(e.getMessage(), "ошибка");
                  client.field_1724
                     .method_7353(class_2561.method_43470("Lua " + entry.name() + ": " + error.substring(0, Math.min(120, error.length()))), false);
                  RvSteal.save();
               }
            }
         }
      }
   }

   static void store(Properties p) {
      int i = 0;

      for (UserScripts.Script s : scripts.values()) {
         String k = "script." + i++ + ".";
         p.setProperty(k + "name", s.name());
         p.setProperty(k + "code", s.code());
      }
   }

   static void load(Properties p) {
      scripts.clear();
      runtimes.clear();

      for (int i = 0; i < 8; i++) {
         String k = "script." + i + ".";
         if (p.containsKey(k + "name")) {
            try {
               create(p.getProperty(k + "name"), p.getProperty(k + "code", ""));
            } catch (RuntimeException var4) {
            }
         }
      }
   }

   record Script(String name, String code, boolean enabled) {
   }
}
