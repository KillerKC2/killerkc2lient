package ru.rvsteal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

final class CustomFunctions {
   static final LinkedHashMap<String, CustomFunctions.Function> functions = new LinkedHashMap<>();
   private static final Map<String, String> ALIASES = Map.ofEntries(
      Map.entry("ночное зрение", "Night Vision"),
      Map.entry("автоспринт", "Auto Sprint"),
      Map.entry("координаты", "XYZ / FPS"),
      Map.entry("броня", "Armor HUD"),
      Map.entry("игроки есп", "Player ESP"),
      Map.entry("мобы есп", "Mob ESP"),
      Map.entry("блоки есп", "Block ESP"),
      Map.entry("следы бросков", "Throw Trails"),
      Map.entry("физика предметов", "Item Physics"),
      Map.entry("частицы", "Ambient Particles"),
      Map.entry("тема мира", "World Theme"),
      Map.entry("автоинструмент", "Auto Tool")
   );

   static String supported() {
      return String.join("; ", new TreeSet<>(ALIASES.keySet()));
   }

   static CustomFunctions.Function parse(String name, String text) {
      name = name.trim();
      if (!name.isEmpty() && name.length() <= 32 && !name.contains(".") && !name.contains("\n")) {
         if (Set.of("Configs", "HUD editor", "Function builder", "Script editor").contains(name)) {
            throw new IllegalArgumentException("Название занято");
         }

         ArrayList<String> actions = new ArrayList<>();

         for (String part : text.split(";", -1)) {
            String action = part.trim().toLowerCase(Locale.ROOT).replaceFirst("^(включить|переключать|управлять)\\s+", "");
            String canonical = ALIASES.get(action);
            if (canonical == null) {
               canonical = ALIASES.values().stream().filter(v -> v.equalsIgnoreCase(action)).findFirst().orElse(null);
            }

            if (canonical == null) {
               throw new IllegalArgumentException("Неизвестное действие: " + part.trim());
            }

            if (!actions.contains(canonical)) {
               actions.add(canonical);
            }
         }

         if (!actions.isEmpty() && actions.size() <= 12) {
            return new CustomFunctions.Function(name, List.copyOf(actions), false);
         } else {
            throw new IllegalArgumentException("Нужно от 1 до 12 действий");
         }
      } else {
         throw new IllegalArgumentException("Название: 1–32 символа без точки");
      }
   }

   static void create(String name, String text) {
      CustomFunctions.Function f = parse(name, text);
      if (functions.size() >= 24 && !functions.containsKey(f.name())) {
         throw new IllegalArgumentException("Не более 24 функций");
      }

      if (!RvScreen.baseModules().stream().anyMatch(m -> m.name().equalsIgnoreCase(f.name()))
         && !UserScripts.scripts.keySet().stream().anyMatch(n -> n.equalsIgnoreCase(f.name()))) {
         functions.put(f.name(), f);
      } else {
         throw new IllegalArgumentException("Название занято модулем");
      }
   }

   static List<RvScreen.Module> modules() {
      ArrayList<RvScreen.Module> list = new ArrayList<>();

      for (CustomFunctions.Function f : functions.values()) {
         list.add(new RvScreen.Module(f.name(), String.join("; ", f.actions()), () -> functions.get(f.name()).enabled(), enabled -> {
            functions.put(f.name(), new CustomFunctions.Function(f.name(), f.actions(), enabled));

            for (RvScreen.Module m : RvScreen.baseModules()) {
               if (f.actions().contains(m.name())) {
                  m.set().accept(enabled);
               }
            }
         }));
      }

      return list;
   }

   static void store(Properties p) {
      int i = 0;

      for (CustomFunctions.Function f : functions.values()) {
         String k = "custom." + i++ + ".";
         p.setProperty(k + "name", f.name());
         p.setProperty(k + "actions", String.join(";", f.actions()));
         p.setProperty(k + "enabled", f.enabled() + "");
      }
   }

   static void load(Properties p) {
      functions.clear();

      for (int i = 0; i < 24; i++) {
         String k = "custom." + i + ".";
         if (p.containsKey(k + "name")) {
            try {
               CustomFunctions.Function f = parse(p.getProperty(k + "name"), p.getProperty(k + "actions", ""));
               if (!RvScreen.baseModules().stream().anyMatch(m -> m.name().equalsIgnoreCase(f.name()))) {
                  functions.put(f.name(), new CustomFunctions.Function(f.name(), f.actions(), Boolean.parseBoolean(p.getProperty(k + "enabled", "false"))));
               }
            } catch (IllegalArgumentException var4) {
            }
         }
      }
   }

   record Function(String name, List<String> actions, boolean enabled) {
   }
}
