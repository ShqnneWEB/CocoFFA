/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package hu.geri.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {
    private static final String DELIMITER = ";";
    private static final int EXPECTED_PARTS = 6;

    public static String serialize(Location location) {
        if (location == null || location.getWorld() == null) {
            return "";
        }
        return location.getWorld().getName() + DELIMITER + location.getX() + DELIMITER + location.getY() + DELIMITER + location.getZ() + DELIMITER + location.getYaw() + DELIMITER + location.getPitch();
    }

    public static Location deserialize(String locationString) {
        if (locationString == null || locationString.trim().isEmpty()) {
            return null;
        }
        String[] parts = locationString.split(DELIMITER);
        if (parts.length != 6) {
            return null;
        }
        try {
            World world = Bukkit.getWorld((String)parts[0]);
            if (world == null) {
                return null;
            }
            return new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isValid(String locationString) {
        return LocationSerializer.deserialize(locationString) != null;
    }
}

