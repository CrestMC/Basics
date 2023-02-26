package me.blurmit.basics.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public abstract class Menu {

    private final Set<MenuButton> buttons;
    private Inventory inventory;
    private long page;

    public Menu() {
        this.buttons = new HashSet<>();
        this.inventory = null;
        this.page = 0;
    }

    public abstract String getName();

    public abstract int getSlots();

    public abstract void callButton(InventoryClickEvent event);

    public abstract MenuType getType();

    public void addButton(MenuButton button) {
        this.buttons.add(button);
    }

    public Set<MenuButton> getButtons() {
        return this.buttons;
    }

    public MenuButton getButtonByID(UUID uuid) {
        return this.buttons.stream().filter(button -> button.getID().equals(uuid)).findFirst().orElse(null);
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory != null ? inventory : getType().getInventory(this);
    }

    public boolean isFull(Inventory inventory) {
        return Arrays.stream(inventory.getContents()).filter(Objects::nonNull).count() == inventory.getSize();
    }

}
