package ru.rvsteal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675.class_307;
import org.slf4j.LoggerFactory;

public final class RvSteal implements ClientModInitializer {
   private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("rvsteal.properties");
   private static boolean hidden;
   private static boolean f7Held;
   private static int f7Presses;
   static boolean betterNear;
   static boolean armorHud;
   static boolean coordinatesHud;
   static boolean attackPlayers;
   static boolean attackMobs;
   static boolean autoSprint;
   static boolean autoRespawn;
   public static boolean playerEsp;
   public static boolean mobEsp;
   static boolean playerAura;
   static boolean mobAura;
   static boolean autoTool;
   static boolean bunnyHop;
   static boolean bright;
   static boolean healthHud;

   public static boolean active() {
      return AccessKey.unlocked() && !hidden;
   }

   static void hide() {
      hidden = true;
      f7Presses = 0;
      ClientModules.suspend(class_310.method_1551());
      class_310.method_1551().method_1507(null);
   }

   public static boolean nightVisionEnabled() {
      return active() && ModuleSettings.nightVision;
   }

   public static boolean itemPhysicsEnabled() {
      return active() && ModuleSettings.itemPhysics;
   }

   public static boolean noHurtCamEnabled() {
      return active() && ModuleSettings.noHurtCam;
   }

   public void onInitializeClient() {
      load();
      class_11900 category = class_11900.method_74698(class_2960.method_60655("rvsteal", "main"));
      class_304 key = KeyBindingHelper.registerKeyBinding(new class_304("key.rvsteal.open", class_307.field_1668, 344, category));
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         boolean f7 = class_3675.method_15987(client.method_22683(), 296);
         if (hidden && f7 && !f7Held && ++f7Presses >= 5) {
            hidden = false;
            f7Presses = 0;
         }

         f7Held = f7;

         while (key.method_1436()) {
            if (!hidden && client.field_1755 == null) {
               client.method_1507((class_437)(AccessKey.unlocked() ? new RvScreen() : new ActivationScreen()));
            }
         }

         if (active()) {
            ScreenHud.tick(client);
            ModuleSettings.tickBinds(client);
            UserScripts.tick(client);
            EspOverlay.tick(client);
            PearlTrails.tick(client);
            ClientModules.tick(client);
            WorldVisuals.tick(client);
            HudCache.tick(client);
         }
      });
      HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, class_2960.method_60655("rvsteal", "hud"), (graphics, delta) -> {
         if (active()) {
            ScreenHud.render(graphics);
            HudCache.render(graphics);
            EspOverlay.render(graphics, delta.method_60637(false));
            PearlTrails.render(graphics, delta.method_60637(false));
         }
      });
   }

   private static void load() {
      Properties properties = new Properties();
      if (Files.exists(CONFIG)) {
         try (InputStream in = Files.newInputStream(CONFIG)) {
            properties.load(in);
            AccessKey.load(properties);
            apply(properties);
         } catch (IOException e) {
            LoggerFactory.getLogger("rvsteal").warn("Could not read configuration", e);
         }
      }
   }

   static void apply(Properties properties) {
      ModuleSettings.load(properties);
      HudLayout.load(properties);
      betterNear = Boolean.parseBoolean(properties.getProperty("betterNear", "false"));
      armorHud = Boolean.parseBoolean(properties.getProperty("armorHud", "false"));
      coordinatesHud = Boolean.parseBoolean(properties.getProperty("coordinatesHud", "false"));
      autoRespawn = Boolean.parseBoolean(properties.getProperty("autoRespawn", "false"));
      autoSprint = Boolean.parseBoolean(properties.getProperty("autoSprint", "false"));
      attackMobs = Boolean.parseBoolean(properties.getProperty("attackMobs", "false"));
      attackPlayers = Boolean.parseBoolean(properties.getProperty("attackPlayers", "false"));
      playerEsp = Boolean.parseBoolean(properties.getProperty("playerEsp", "false"));
      mobEsp = Boolean.parseBoolean(properties.getProperty("mobEsp", "false"));
      playerAura = Boolean.parseBoolean(properties.getProperty("playerAura", "false"));
      mobAura = Boolean.parseBoolean(properties.getProperty("mobAura", "false"));
      autoTool = Boolean.parseBoolean(properties.getProperty("autoTool", "false"));
      bunnyHop = Boolean.parseBoolean(properties.getProperty("bunnyHop", "false"));
      bright = Boolean.parseBoolean(properties.getProperty("bright", "false"));
      healthHud = Boolean.parseBoolean(properties.getProperty("healthHud", "false"));
      EspOverlay.resetBlocks();
   }

   static Properties snapshot() {
      Properties properties = new Properties();
      properties.setProperty("betterNear", Boolean.toString(betterNear));
      properties.setProperty("armorHud", Boolean.toString(armorHud));
      properties.setProperty("coordinatesHud", Boolean.toString(coordinatesHud));
      properties.setProperty("autoRespawn", Boolean.toString(autoRespawn));
      properties.setProperty("autoSprint", Boolean.toString(autoSprint));
      properties.setProperty("attackMobs", Boolean.toString(attackMobs));
      properties.setProperty("attackPlayers", Boolean.toString(attackPlayers));
      properties.setProperty("playerEsp", Boolean.toString(playerEsp));
      properties.setProperty("mobEsp", Boolean.toString(mobEsp));
      properties.setProperty("playerAura", Boolean.toString(playerAura));
      properties.setProperty("mobAura", Boolean.toString(mobAura));
      properties.setProperty("autoTool", Boolean.toString(autoTool));
      properties.setProperty("bunnyHop", Boolean.toString(bunnyHop));
      properties.setProperty("bright", Boolean.toString(bright));
      properties.setProperty("healthHud", Boolean.toString(healthHud));
      ModuleSettings.store(properties);
      HudLayout.store(properties);
      return properties;
   }

   static void save() {
      Properties properties = snapshot();
      AccessKey.store(properties);

      try {
         Files.createDirectories(CONFIG.getParent());

         try (OutputStream out = Files.newOutputStream(CONFIG)) {
            properties.store(out, "KillerKC2 client");
         }
      } catch (IOException e) {
         LoggerFactory.getLogger("rvsteal").warn("Could not save configuration", e);
      }
   }
}
