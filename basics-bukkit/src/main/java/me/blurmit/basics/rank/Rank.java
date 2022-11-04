package me.blurmit.basics.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.permissions.Permission;

import java.util.HashSet;
import java.util.Set;

public class Rank {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String displayName;
    @Getter
    @Setter
    private String prefix;
    @Getter
    @Setter
    private String suffix;
    @Getter
    @Setter
    private String color;
    @Getter
    @Setter
    private long priority;
    @Getter
    @Setter
    private Set<Permission> permissions;
    @Getter
    @Setter
    private boolean isDefault;

    public Rank(String name) {
        this(name, 0);
    }

    public Rank(String name, long priority) {
        this(name, priority, false);
    }

    public Rank(String name, long priority, boolean isDefault) {
        this(name, priority, isDefault, new HashSet<>());
    }

    public Rank(String name, long priority, boolean isDefault, Set<Permission> permissions) {
        this(name, name, "", priority, isDefault, permissions, "", "");
    }

    public Rank(String name, String displayName, String color, long priority, boolean isDefault, Set<Permission> permissions, String prefix, String suffix) {
        this.name = name;
        this.priority = priority;
        this.isDefault = isDefault;
        this.permissions = permissions;
        this.prefix = prefix;
        this.suffix = suffix;
        this.displayName = displayName;
        this.color = color;
    }

}
