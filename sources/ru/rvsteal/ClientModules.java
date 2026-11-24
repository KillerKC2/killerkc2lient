package ru.rvsteal;

import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_418;
import net.minecraft.class_746;

final class ClientModules {
   static class_1309 target;
   private static class_1309 previousAuraTarget;
   private static int deathTicks;
   private static int totemCooldown;
   private static Double previousGamma;

   static void suspend(class_310 client) {
      if (previousGamma != null && ((Double)client.field_1690.method_42473().method_41753()).equals(1.0)) {
         client.field_1690.method_42473().method_41748(previousGamma);
      }

      previousGamma = null;
      target = null;
      previousAuraTarget = null;
   }

   static void tick(class_310 client) {
      target = null;
      if (RvSteal.bright && previousGamma == null) {
         previousGamma = (Double)client.field_1690.method_42473().method_41753();
         client.field_1690.method_42473().method_41748(1.0);
      } else if (!RvSteal.bright && previousGamma != null) {
         if (((Double)client.field_1690.method_42473().method_41753()).equals(1.0)) {
            client.field_1690.method_42473().method_41748(previousGamma);
         }

         previousGamma = null;
      }

      class_746 player = client.field_1724;
      if (player != null && client.field_1687 != null) {
         if (RvSteal.autoRespawn && client.field_1755 instanceof class_418) {
            if (++deathTicks == 20) {
               player.method_7331();
            }
         } else {
            deathTicks = 0;
         }

         if (client.field_1755 == null && player.method_5805() && !player.method_7325()) {
            if (RvSteal.autoSprint
               && player.field_3913.field_54155.comp_3159()
               && !player.field_3913.field_54155.comp_3160()
               && !player.method_5715()
               && !player.method_6115()
               && !player.field_5976
               && !player.method_6059(class_1294.field_5919)
               && (player.method_7344().method_7586() > 6 || player.method_31549().field_7478)) {
               player.method_5728(true);
            }

            if (RvSteal.bunnyHop && player.method_24828() && player.field_3913.field_54155.comp_3159() && !player.method_5715() && !player.method_5765()) {
               player.method_6043();
            }

            if (client.field_1761 != null) {
               if (totemCooldown > 0) {
                  totemCooldown--;
               }

               if (ModuleSettings.autoTotem
                  && totemCooldown == 0
                  && player.field_7512 == player.field_7498
                  && player.field_7498.method_34255().method_7960()
                  && !player.method_6115()
                  && !player.method_6079().method_31574(class_1802.field_8288)) {
                  for (int i = 0; i < 36; i++) {
                     if (player.method_31548().method_5438(i).method_31574(class_1802.field_8288)) {
                        client.field_1761.method_2906(player.field_7498.field_7763, i < 9 ? i + 36 : i, 40, class_1713.field_7791, player);
                        totemCooldown = 10;
                        break;
                     }
                  }
               }

               if (RvSteal.autoTool && client.field_1690.field_1886.method_1434() && client.field_1765 instanceof class_3965 hit && !player.method_6115()) {
                  class_2680 state = client.field_1687.method_8320(hit.method_17777());
                  class_1661 inventory = player.method_31548();
                  int best = inventory.method_67532();
                  float speed = inventory.method_5438(best).method_7924(state);

                  for (int i = 0; i < 9; i++) {
                     float candidate = inventory.method_5438(i).method_7924(state);
                     if (candidate > speed) {
                        speed = candidate;
                        best = i;
                     }
                  }

                  inventory.method_61496(best);
               }

               if (!player.method_6115() && !client.field_1761.method_2923()) {
                  if (client.field_1692 instanceof class_1309 aimed
                     && (RvSteal.attackPlayers && aimed instanceof class_1657 || RvSteal.attackMobs && aimed instanceof class_1569)) {
                     target = aimed;
                  }

                  if (target == null && (RvSteal.playerAura || RvSteal.mobAura)) {
                     double closest = player.method_55755() * player.method_55755();
                     if (previousAuraTarget != null
                        && previousAuraTarget.method_5805()
                        && !previousAuraTarget.method_31481()
                        && player.method_18395(previousAuraTarget)
                        && player.method_6057(previousAuraTarget)
                        && player.method_5858(previousAuraTarget) <= closest
                        && (RvSteal.playerAura && previousAuraTarget instanceof class_1657 || RvSteal.mobAura && previousAuraTarget instanceof class_1569)) {
                        target = previousAuraTarget;
                     }

                     if (target == null) {
                        for (class_1297 entity : client.field_1687.method_18112()) {
                           if (entity instanceof class_1309 candidate
                              && candidate != player
                              && candidate.method_5805()
                              && player.method_18395(candidate)
                              && player.method_6057(candidate)
                              && (RvSteal.playerAura && candidate instanceof class_1657 || RvSteal.mobAura && candidate instanceof class_1569)) {
                              double distance = player.method_5858(candidate);
                              if (distance <= closest) {
                                 closest = distance;
                                 target = candidate;
                              }
                           }
                        }
                     }
                  }

                  previousAuraTarget = target;
                  if (target != null
                     && target != player
                     && target.method_5805()
                     && player.method_18395(target)
                     && player.method_6057(target)
                     && player.method_5858(target) <= player.method_55755() * player.method_55755()) {
                     boolean aura = target instanceof class_1657 ? RvSteal.playerAura : RvSteal.mobAura;
                     boolean legit = target instanceof class_1657 ? ModuleSettings.playerLegit : ModuleSettings.mobLegit;
                     if (aura && legit) {
                        class_243 offset = target.method_33571().method_1020(player.method_33571());
                        float yaw = (float)Math.toDegrees(Math.atan2(offset.field_1350, offset.field_1352)) - 90.0F;
                        float pitch = (float)(
                           -Math.toDegrees(
                              Math.atan2(offset.field_1351, Math.sqrt(offset.field_1352 * offset.field_1352 + offset.field_1350 * offset.field_1350))
                           )
                        );
                        float yawStep = class_3532.method_15393(yaw - player.method_36454());
                        float pitchStep = pitch - player.method_36455();
                        player.method_36456(player.method_36454() + Math.clamp(yawStep * 0.25F, -4.0F, 4.0F));
                        player.method_36457(Math.clamp(player.method_36455() + Math.clamp(pitchStep * 0.25F, -3.0F, 3.0F), -90.0F, 90.0F));
                        if (Math.abs(yawStep) > 12.0F || Math.abs(pitchStep) > 10.0F) {
                           return;
                        }
                     }

                     if (player.method_7261(0.0F) < 1.0F) {
                        return;
                     }

                     client.field_1761.method_2918(player, target);
                     player.method_6104(class_1268.field_5808);
                  }
               }
            }
         }
      } else {
         deathTicks = 0;
         previousAuraTarget = null;
      }
   }
}
