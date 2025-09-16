/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package hu.geri.managers;

import hu.geri.CocoFFA;
import hu.geri.arena.Arena;
import hu.geri.arena.EffectPhase;
import hu.geri.libs.universalScheduler.scheduling.tasks.MyScheduledTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectManager {
    private final CocoFFA plugin;
    private final Map<Arena, MyScheduledTask> effectTasks;

    public EffectManager(CocoFFA plugin) {
        this.plugin = plugin;
        this.effectTasks = new HashMap<Arena, MyScheduledTask>();
    }

    public void applyDefaultEffects(Player player, Arena arena) {
        List<String> defaultEffects = arena.getDefaultEffects();
        if (defaultEffects == null || defaultEffects.isEmpty()) {
            return;
        }
        for (String effectString : defaultEffects) {
            PotionEffect effect = this.parseEffect(effectString);
            if (effect == null) continue;
            player.addPotionEffect(effect);
        }
    }

    public void startEffectSystem(Arena arena) {
        if (arena.getEffectPhases().isEmpty()) {
            return;
        }
        this.stopEffectSystem(arena);
        for (EffectPhase phase : arena.getEffectPhases().values()) {
            phase.setApplied(false);
        }
        long gameStartTime = System.currentTimeMillis();
        MyScheduledTask task = this.plugin.getUniversalScheduler().runTaskTimer(() -> {
            int elapsed = (int)((System.currentTimeMillis() - gameStartTime) / 1000L);
            for (Map.Entry<Integer, EffectPhase> entry : arena.getEffectPhases().entrySet()) {
                EffectPhase phase = entry.getValue();
                if (phase.getStartTime() != elapsed || phase.isApplied()) continue;
                this.applyPhaseEffects(arena, phase);
                phase.setApplied(true);
            }
        }, 0L, 20L);
        this.effectTasks.put(arena, task);
    }

    private void applyPhaseEffects(Arena arena, EffectPhase phase) {
        if (!phase.getEffects().isEmpty()) {
            String effectNames = this.getEffectNamesString(phase.getEffects());
            String effectMessage = this.plugin.getLocaleManager().getMessage("arena.effect-broadcast").replace("%effect%", effectNames);
            for (Player player : arena.getPlayers()) {
                player.sendMessage(effectMessage);
            }
        }
        for (Player player : arena.getPlayers()) {
            this.plugin.getUniversalScheduler().runTask(player.getLocation(), () -> {
                for (String effectString : phase.getEffects()) {
                    PotionEffect effect = this.parseEffect(effectString);
                    if (effect == null) continue;
                    player.addPotionEffect(effect);
                }
            });
        }
    }

    public void stopEffectSystem(Arena arena) {
        MyScheduledTask task = this.effectTasks.remove(arena);
        if (task != null) {
            task.cancel();
        }
    }

    private PotionEffect parseEffect(String effectString) {
        try {
            String[] parts = effectString.split(";");
            if (parts.length != 3) {
                this.plugin.getLogger().warning("Invalid effect format: " + effectString + " (expected: EFFECT_TYPE;AMPLIFIER;DURATION_MINUTES)");
                return null;
            }
            PotionEffectType effectType = PotionEffectType.getByName((String)parts[0].trim());
            if (effectType == null) {
                this.plugin.getLogger().warning("Unknown effect type: " + parts[0]);
                return null;
            }
            int amplifier = Integer.parseInt(parts[1].trim());
            int durationMinutes = Integer.parseInt(parts[2].trim());
            int durationTicks = durationMinutes * 60 * 20;
            return new PotionEffect(effectType, durationTicks, amplifier);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Error parsing effect: " + effectString + " - " + e.getMessage());
            return null;
        }
    }

    public void removeAllEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    private String getEffectNamesString(List<String> effectStrings) {
        if (effectStrings.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < effectStrings.size(); ++i) {
            String effectString = effectStrings.get(i);
            String[] parts = effectString.split(";");
            if (parts.length < 1) continue;
            String effectName = parts[0].trim();
            Object readableName = effectName.toLowerCase().replace("_", " ");
            readableName = ((String)readableName).substring(0, 1).toUpperCase() + ((String)readableName).substring(1);
            sb.append((String)readableName);
            if (parts.length >= 2) {
                try {
                    int amplifier = Integer.parseInt(parts[1].trim());
                    if (amplifier > 0) {
                        sb.append(" ").append(amplifier + 1);
                    }
                } catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            if (i >= effectStrings.size() - 1) continue;
            sb.append(", ");
        }
        return sb.toString();
    }
}

