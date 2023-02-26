package me.blurmit.basics.punishments.data;

import lombok.Data;
import me.blurmit.basics.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

@Data(staticConstructor = "of")
public class PunishmentData {

    private final PunishmentType type;
    private final UUID target;
    private final UUID moderator;
    private final String reason;

    private final long punishedAt;
    private final long expiresAt;

    public static PunishmentData of(ResultSet results) {
        try {
            UUID target = UUID.fromString(results.getString("target"));
            UUID moderator = UUID.fromString(results.getString("moderator"));
            String reason = results.getString("reason");
            long punishedAt = results.getLong("punished_at");
            long expiresAt = results.getLong("expires_at");

            String typeName = results.getMetaData().getTableName(1).replaceFirst("basics_", "").toUpperCase();
            PunishmentType type = PunishmentType.getByName(typeName);

            return of(type, target, moderator, reason, punishedAt, expiresAt);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Corrupted punishment data detected.", e);
            return null;
        }
    }

    public static PunishmentData of(ConfigurationSection section) {
        try {
            UUID target = UUID.fromString(section.getName());
            UUID moderator = UUID.fromString(section.getString("moderator"));
            String reason = section.getString("reason");
            long punishedAt = section.getLong("punished_at");
            long expiresAt = section.getLong("expires_at");

            String typeName = section.getParent().getName();
            PunishmentType type = PunishmentType.getByName(typeName);

            return of(type, target, moderator, reason, punishedAt, expiresAt);
        } catch (NullPointerException e) {
            Bukkit.getLogger().log(Level.WARNING, "Corrupted punishment data detected.", e);
            return null;
        }
    }

}
