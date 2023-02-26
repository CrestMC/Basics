package me.blurmit.basics.menu;

import me.blurmit.basics.util.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuButton {

    private String name;
    private Material material;
    private List<String> lore;
    private int slot;
    private UUID uuid;

    public MenuButton(String name) {
        this.name = name;
        this.material = Material.STONE;
        this.lore = new ArrayList<>();
        this.slot = 0;
        this.uuid = UUID.randomUUID();
    }

    public MenuButton setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }

    public MenuButton setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public MenuButton setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getSlot() {
        return this.slot;
    }

    public MenuButton setLore(String... lore) {
        for (int line = 0; line < lore.length; line++) {
            lore[line] = ChatColor.translateAlternateColorCodes('&', lore[line]);
        }

        this.lore = Arrays.asList(lore);
        return this;
    }

    public MenuButton setLore(List<String> lore) {
        this.lore = lore.stream().map(Placeholders::parsePlaceholder).collect(Collectors.toList());

        return this;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public UUID getID() {
        return this.uuid;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


}
