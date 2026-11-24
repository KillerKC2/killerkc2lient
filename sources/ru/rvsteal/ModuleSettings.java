package ru.rvsteal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.class_310;
import net.minecraft.class_3675;

final class ModuleSettings {
   static final ModuleSettings.Esp players = new ModuleSettings.Esp();
   static final ModuleSettings.Esp mobs = new ModuleSettings.Esp();
   static boolean playerLegit = true;
   static boolean mobLegit = true;
   static boolean blockEsp;
   static boolean nightVision;
   static boolean watermark = true;
   static boolean hotkeysHud = true;
   static boolean potionsHud = true;
   static boolean staffHud = true;
   static boolean worldTheme;
   static boolean ambientParticles;
   static boolean itemEsp;
   static boolean autoTotem;
   static boolean noHurtCam;
   static boolean targetHud;
   static boolean hitboxes;
   static boolean pearlTrails;
   static boolean itemPhysics;
   static int hitboxSize = 30;
   static int skyColor = 9668058;
   static int fogColor = 12889579;
   static int cloudColor = 16766445;
   static int particleStyle;
   static int particleDensity = 2;
   static int blockColor = 6677985;
   static int blockRange = 16;
   static String blocks = "minecraft:chest,minecraft:spawner,minecraft:diamond_ore,minecraft:deepslate_diamond_ore";
   static final Map<String, Integer> binds = new HashMap<>();
   private static final Set<Integer> held = new HashSet<>();

   static void load(Properties p) {
      binds.clear();
      CustomFunctions.load(p);
      UserScripts.load(p);
      pearlTrails = Boolean.parseBoolean(p.getProperty("pearlTrails", "false"));
      itemPhysics = Boolean.parseBoolean(p.getProperty("itemPhysics", "false"));
      hitboxes = Boolean.parseBoolean(p.getProperty("hitboxes", "false"));
      hitboxSize = number(p, "hitboxSize", 30, 0, 300);
      targetHud = Boolean.parseBoolean(p.getProperty("targetHud", "false"));
      noHurtCam = Boolean.parseBoolean(p.getProperty("noHurtCam", "false"));
      autoTotem = Boolean.parseBoolean(p.getProperty("autoTotem", "false"));
      worldTheme = Boolean.parseBoolean(p.getProperty("worldTheme", "false"));
      ambientParticles = Boolean.parseBoolean(p.getProperty("ambientParticles", "false"));
      itemEsp = Boolean.parseBoolean(p.getProperty("itemEsp", "false"));
      skyColor = color(p, "skyColor", 9668058);
      fogColor = color(p, "fogColor", 12889579);
      cloudColor = color(p, "cloudColor", 16766445);
      blockColor = color(p, "blockColor", 6677985);
      particleStyle = number(p, "particleStyle", 0, 0, 2);
      particleDensity = number(p, "particleDensity", 2, 1, 5);
      watermark = Boolean.parseBoolean(p.getProperty("watermark", "true"));
      hotkeysHud = Boolean.parseBoolean(p.getProperty("hotkeysHud", "true"));
      potionsHud = Boolean.parseBoolean(p.getProperty("potionsHud", "true"));
      staffHud = Boolean.parseBoolean(p.getProperty("staffHud", "true"));
      playerLegit = Boolean.parseBoolean(p.getProperty("playerLegit", "true"));
      mobLegit = Boolean.parseBoolean(p.getProperty("mobLegit", "true"));
      blockEsp = Boolean.parseBoolean(p.getProperty("blockEsp", "false"));
      nightVision = Boolean.parseBoolean(p.getProperty("nightVision", "false"));
      blockRange = number(p, "blockRange", 16, 8, 32);
      blocks = p.getProperty("blocks", blocks);

      for (Entry<String, ModuleSettings.Esp> entry : Map.of("players", players, "mobs", mobs).entrySet()) {
         String k = entry.getKey();
         ModuleSettings.Esp e = entry.getValue();
         e.box = Boolean.parseBoolean(p.getProperty(k + ".box", "true"));
         e.name = Boolean.parseBoolean(p.getProperty(k + ".name", "true"));
         e.hp = Boolean.parseBoolean(p.getProperty(k + ".hp", "true"));
         e.gear = Boolean.parseBoolean(p.getProperty(k + ".gear", "true"));
         e.enchants = Boolean.parseBoolean(p.getProperty(k + ".enchants", "false"));
         e.range = number(p, k + ".range", 64, 16, 128);
         e.corners = Boolean.parseBoolean(p.getProperty(k + ".corners", "false"));
         e.healthBar = Boolean.parseBoolean(p.getProperty(k + ".healthBar", "true"));
         e.distance = Boolean.parseBoolean(p.getProperty(k + ".distance", "false"));
         e.tracers = Boolean.parseBoolean(p.getProperty(k + ".tracers", "false"));
         e.shadow = Boolean.parseBoolean(p.getProperty(k + ".shadow", "true"));
         e.background = Boolean.parseBoolean(p.getProperty(k + ".background", "true"));
         e.color = color(p, k + ".color", 12946687);
         e.textColor = color(p, k + ".textColor", 16777215);
         e.outlineColor = color(p, k + ".outlineColor", 1052696);
         e.thickness = number(p, k + ".thickness", 1, 1, 4);
         e.fill = number(p, k + ".fill", 0, 0, 150);
         e.scale = number(p, k + ".scale", 100, 50, 200);
         e.padding = number(p, k + ".padding", 0, 0, 12);
      }

      for (String key : p.stringPropertyNames()) {
         if (key.startsWith("bind.")) {
            int value = number(p, key, -1, -1, 348);
            if (value >= 32 && value != 296 && value != 344) {
               binds.put(key.substring(5), value);
            }
         }
      }
   }

   private static int number(Properties p, String key, int fallback, int min, int max) {
      try {
         return Math.clamp(Integer.parseInt(p.getProperty(key, "")), min, max);
      } catch (NumberFormatException ignored) {
         return fallback;
      }
   }

   static int color(Properties p, String key, int fallback) {
      String value = p.getProperty(key, "").replace("#", "");
      return !value.matches("[0-9a-fA-F]{6}") ? fallback : Integer.parseInt(value, 16);
   }

   static String hex(int color) {
      return String.format(Locale.ROOT, "%06X", color & 16777215);
   }

   static void store(Properties p) {
      CustomFunctions.store(p);
      UserScripts.store(p);
      p.setProperty("pearlTrails", pearlTrails + "");
      p.setProperty("itemPhysics", itemPhysics + "");
      p.setProperty("hitboxes", hitboxes + "");
      p.setProperty("hitboxSize", hitboxSize + "");
      p.setProperty("targetHud", targetHud + "");
      p.setProperty("noHurtCam", noHurtCam + "");
      p.setProperty("autoTotem", autoTotem + "");
      p.setProperty("worldTheme", worldTheme + "");
      p.setProperty("ambientParticles", ambientParticles + "");
      p.setProperty("itemEsp", itemEsp + "");
      p.setProperty("skyColor", hex(skyColor));
      p.setProperty("fogColor", hex(fogColor));
      p.setProperty("cloudColor", hex(cloudColor));
      p.setProperty("blockColor", hex(blockColor));
      p.setProperty("particleStyle", particleStyle + "");
      p.setProperty("particleDensity", particleDensity + "");
      p.setProperty("watermark", watermark + "");
      p.setProperty("hotkeysHud", hotkeysHud + "");
      p.setProperty("potionsHud", potionsHud + "");
      p.setProperty("staffHud", staffHud + "");
      p.setProperty("playerLegit", playerLegit + "");
      p.setProperty("mobLegit", mobLegit + "");
      p.setProperty("blockEsp", blockEsp + "");
      p.setProperty("nightVision", nightVision + "");
      p.setProperty("blockRange", blockRange + "");
      p.setProperty("blocks", blocks);

      for (Entry<String, ModuleSettings.Esp> entry : Map.of("players", players, "mobs", mobs).entrySet()) {
         String k = entry.getKey();
         ModuleSettings.Esp e = entry.getValue();
         p.setProperty(k + ".box", e.box + "");
         p.setProperty(k + ".name", e.name + "");
         p.setProperty(k + ".hp", e.hp + "");
         p.setProperty(k + ".gear", e.gear + "");
         p.setProperty(k + ".enchants", e.enchants + "");
         p.setProperty(k + ".range", e.range + "");
         p.setProperty(k + ".corners", e.corners + "");
         p.setProperty(k + ".healthBar", e.healthBar + "");
         p.setProperty(k + ".distance", e.distance + "");
         p.setProperty(k + ".tracers", e.tracers + "");
         p.setProperty(k + ".shadow", e.shadow + "");
         p.setProperty(k + ".background", e.background + "");
         p.setProperty(k + ".color", hex(e.color));
         p.setProperty(k + ".textColor", hex(e.textColor));
         p.setProperty(k + ".outlineColor", hex(e.outlineColor));
         p.setProperty(k + ".thickness", e.thickness + "");
         p.setProperty(k + ".fill", e.fill + "");
         p.setProperty(k + ".scale", e.scale + "");
         p.setProperty(k + ".padding", e.padding + "");
      }

      binds.forEach((key, value) -> p.setProperty("bind." + key, value + ""));
   }

   static void tickBinds(class_310 client) {
      if (binds.isEmpty()) {
         held.clear();
      } else {
         Set<Integer> now = new HashSet<>();

         for (int key : binds.values()) {
            if (class_3675.method_15987(client.method_22683(), key)) {
               now.add(key);
            }
         }

         if (client.field_1755 == null && client.field_1724 != null) {
            boolean changed = false;

            for (RvScreen.Module module : RvScreen.allModules()) {
               int key = binds.getOrDefault(module.name(), -1);
               if (now.contains(key) && !held.contains(key)) {
                  module.set().accept(!module.get().getAsBoolean());
                  changed = true;
               }
            }

            if (changed) {
               RvSteal.save();
            }
         }

         held.clear();
         held.addAll(now);
      }
   }

   static final class Esp {
      boolean box = true;
      boolean name = true;
      boolean hp = true;
      boolean gear = true;
      boolean enchants;
      boolean corners;
      boolean healthBar = true;
      boolean distance;
      boolean tracers;
      boolean shadow = true;
      boolean background = true;
      int color = 12946687;
      int textColor = 16777215;
      int outlineColor = 1052696;
      int thickness = 1;
      int fill = 0;
      int scale = 100;
      int padding = 0;
      int range = 64;
   }
}
