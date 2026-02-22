package com.seven.deadlysins.utils;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import com.seven.deadlysins.SevenDeadlySins;
import com.seven.deadlysins.registry.CustomEnchant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class VisualUtil {

    private static ParticleNativeAPI api;

    private static ParticleNativeAPI getAPI() {
        if (api == null) {
            api = ParticleNativeCore.loadAPI(SevenDeadlySins.getInstance());
        }
        return api;
    }

    public static void playVisual(Location center, Vector dir, CustomEnchant enchant, double scale) {
        if (center.getWorld() == null) return;
        Collection<Player> players = center.getWorld().getPlayers();
        if (dir == null) dir = new Vector(0, 1, 0);

        String n = enchant.name();
        if (n.equals("BLOOD_EAGLE")) {
            for (double i = -2; i <= 2; i += 0.2) {
                Location loc = center.clone().add(i, Math.abs(i), 0);
                getAPI().LIST_1_8.FLAME.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
                getAPI().LIST_1_8.REDSTONE.packetColored(true, loc, 255, 0, 0).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("BERSERKERS_RAGE")) {
            for (int i = 0; i < 20; i++) {
                Location loc = center.clone().add((Math.random() - 0.5) * 2, Math.random() * 2, (Math.random() - 0.5) * 2);
                getAPI().LIST_1_8.SPELL_WITCH.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("SIEGE_BREAKER")) { // Ice shatter
            for (int i = 0; i < 30; i++) {
                Location loc = center.clone().add((Math.random() - 0.5) * 2, Math.random() * 2, (Math.random() - 0.5) * 2);
                getAPI().LIST_1_8.SNOWBALL.packet(true, loc, 0D, 0D, 0D, 0.1D, 1).sendInRadiusTo(players, 30D);
                getAPI().LIST_1_8.CLOUD.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("HELLFIRE_TREBUCHET")) { // Fire vortex
            for (double y = 0; y < 3; y += 0.2) {
                double r = y * 0.5;
                Location loc = center.clone().add(Math.cos(y * 5) * r, y, Math.sin(y * 5) * r);
                getAPI().LIST_1_8.FLAME.packet(true, loc, 0D, 0D, 0D, 0.05D, 2).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("WARLORDS_CRY")) { // Yellow Dome
            for (int i = 0; i < 30; i++) {
                double a = Math.random() * Math.PI * 2;
                Location loc = center.clone().add(Math.cos(a) * 2, Math.random() * 2, Math.sin(a) * 2);
                getAPI().LIST_1_8.REDSTONE.packetColored(true, loc, 255, 255, 0).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("DUELISTS_SPITE")) { // Black Slash
            Vector right = new Vector(-dir.getZ(), 0, dir.getX()).normalize();
            for (double a = -1; a <= 1; a += 0.1) {
                Location loc = center.clone().add(right.clone().multiply(a)).add(0, 1 + Math.cos(a), 0);
                getAPI().LIST_1_8.SMOKE_LARGE.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
                getAPI().LIST_1_8.SWEEP_ATTACK.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("IMPALER")) { // White spears down
            for (int i = 0; i < 5; i++) {
                Location loc = center.clone().add((Math.random() - 0.5) * 2, 3, (Math.random() - 0.5) * 2);
                for (double y = 0; y < 3; y += 0.5) {
                    getAPI().LIST_1_8.CRIT.packet(true, loc.clone().subtract(0, y, 0), 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
                }
            }
        } else if (n.equals("SCORCHED_EARTH")) { // Lava trail
            getAPI().LIST_1_8.LAVA.packet(true, center, 0D, 0D, 0D, 0.5D, 5).sendInRadiusTo(players, 30D);
            getAPI().LIST_1_8.FLAME.packet(true, center, 0D, 0D, 0D, 0D, 5).sendInRadiusTo(players, 30D);
        } else if (n.equals("VENGEANCE_STRIKE")) { // Red Stitch
            for (double i = 0; i < Math.PI * 2; i += 0.2) {
                Location loc = center.clone().add(Math.cos(i) * 2 * scale, 1, Math.sin(i) * 2 * scale);
                getAPI().LIST_1_8.REDSTONE.packetColored(true, loc, 255, 0, 0).sendInRadiusTo(players, 30D);
                getAPI().LIST_1_8.CRIT_MAGIC.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("MUTILATION")) { // Purple gas
            for (int i = 0; i < 15; i++) {
                Location loc = center.clone().add((Math.random() - 0.5) * 2, Math.random() * 2, (Math.random() - 0.5) * 2);
                getAPI().LIST_1_8.REDSTONE.packetColored(true, loc, 128, 0, 128).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("KINGS_RESURGENCE")) { // Golden pillar
            for (double y = 0; y < 5; y += 0.2) {
                getAPI().LIST_1_8.REDSTONE.packetColored(true, center.clone().add(0, y, 0), 255, 215, 0).sendInRadiusTo(players, 30D);
                getAPI().LIST_1_8.END_ROD.packet(true, center.clone().add(0, y, 0), 0D, 0.1D, 0D, 0.05D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("REGAL_PRESENCE")) { // White ring
            for (double i = 0; i < Math.PI * 2; i += 0.2) {
                Location loc = center.clone().add(Math.cos(i) * 3, 0.5, Math.sin(i) * 3);
                getAPI().LIST_1_8.CLOUD.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("SOLITARY_MONARCH")) { // Cyan glow
            for (int i = 0; i < 15; i++) {
                Location loc = center.clone().add((Math.random() - 0.5) * 2, Math.random() * 2, (Math.random() - 0.5) * 2);
                getAPI().LIST_1_8.REDSTONE.packetColored(true, loc, 0, 255, 255).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("CHAMPIONS_CHALLENGE")) { // Purple beam
            for (double i = 0; i < 5; i += 0.5) {
                Location loc = center.clone().add(dir.clone().multiply(i));
                getAPI().LIST_1_8.REDSTONE.packetColored(true, loc, 128, 0, 255).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("CROWN_OF_THORNS")) { // Red spikes
            for (int i = 0; i < 5; i++) {
                Location base = center.clone().add((Math.random() - 0.5) * 2, 0, (Math.random() - 0.5) * 2);
                for (double y = 0; y < 2; y += 0.2) {
                    getAPI().LIST_1_8.REDSTONE.packetColored(true, base.clone().add(0, y, 0), 255, 0, 0).sendInRadiusTo(players, 30D);
                }
            }
        } else if (n.equals("ARROGANT_PARRY")) { // Ash burst
            getAPI().LIST_1_8.EXPLOSION_LARGE.packet(true, center, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            getAPI().LIST_1_8.SMOKE_NORMAL.packet(true, center, 0D, 0.1D, 0D, 0.1D, 20).sendInRadiusTo(players, 30D);
        } else if (n.equals("SOVEREIGNS_REACH")) { // Extending white slash
            for (double i = 0; i < scale; i += 0.5) {
                Location loc = center.clone().add(dir.clone().multiply(i));
                getAPI().LIST_1_8.SWEEP_ATTACK.packet(true, loc, 0D, 0D, 0D, 0D, 1).sendInRadiusTo(players, 30D);
            }
        } else if (n.equals("GILDED_EXECUTION")) { // Gold explosion
            getAPI().LIST_1_8.BLOCK_CRACK.packet(true, center, 0D, 0D, 0D, 0.1D, 30, org.bukkit.Material.GOLD_BLOCK, (byte)0).sendInRadiusTo(players, 30D);
        } else if (n.equals("UNBOWED")) { // Green slime burst
            getAPI().LIST_1_8.SLIME.packet(true, center, 0D, 0D, 0D, 0.1D, 20).sendInRadiusTo(players, 30D);
        } else if (n.equals("ROYAL_DECREE")) { // Blue electric ring
            for (double i = 0; i < Math.PI * 2; i += 0.2) {
                Location loc = center.clone().add(Math.cos(i) * 5, 0.5, Math.sin(i) * 5);
   
