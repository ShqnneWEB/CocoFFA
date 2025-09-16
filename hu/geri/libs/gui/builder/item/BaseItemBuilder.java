/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.persistence.PersistentDataContainer
 */
package hu.geri.libs.gui.builder.item;

import com.google.common.base.Preconditions;
import hu.geri.libs.gui.components.GuiAction;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.components.util.ItemNbt;
import hu.geri.libs.gui.components.util.Legacy;
import hu.geri.libs.gui.components.util.VersionHelper;
import hu.geri.libs.gui.guis.GuiItem;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {
    private static final EnumSet<Material> LEATHER_ARMOR = EnumSet.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
    private static final Field DISPLAY_NAME_FIELD;
    private static final Field LORE_FIELD;
    private ItemStack itemStack;
    private ItemMeta meta;

    protected BaseItemBuilder(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "Item can't be null!");
        this.itemStack = itemStack;
        this.meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    @NotNull
    protected Object serializeComponent(@NotNull Component component) {
        if (VersionHelper.IS_ITEM_NAME_COMPONENT) {
            return MinecraftComponentSerializer.get().serialize(component);
        }
        return GsonComponentSerializer.gson().serialize(component);
    }

    @NotNull
    protected Component deserializeComponent(@NotNull Object obj) {
        if (VersionHelper.IS_ITEM_NAME_COMPONENT) {
            return MinecraftComponentSerializer.get().deserialize(obj);
        }
        return GsonComponentSerializer.gson().deserialize((String)obj);
    }

    @NotNull
    @Contract(value="_ -> this")
    public B name(@NotNull Component name) {
        if (this.meta == null) {
            return (B)this;
        }
        if (VersionHelper.IS_COMPONENT_LEGACY) {
            this.meta.setDisplayName(Legacy.SERIALIZER.serialize(name));
            return (B)this;
        }
        try {
            DISPLAY_NAME_FIELD.set(this.meta, this.serializeComponent(name));
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B amount(int amount) {
        this.itemStack.setAmount(amount);
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B lore(@Nullable Component @NotNull ... lore) {
        return this.lore(Arrays.asList(lore));
    }

    @NotNull
    @Contract(value="_ -> this")
    public B lore(@NotNull List<@Nullable Component> lore) {
        if (this.meta == null) {
            return (B)this;
        }
        if (VersionHelper.IS_COMPONENT_LEGACY) {
            this.meta.setLore(lore.stream().filter(Objects::nonNull).map(Legacy.SERIALIZER::serialize).collect(Collectors.toList()));
            return (B)this;
        }
        List jsonLore = lore.stream().filter(Objects::nonNull).map(this::serializeComponent).collect(Collectors.toList());
        try {
            LORE_FIELD.set(this.meta, jsonLore);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B lore(@NotNull Consumer<List<@Nullable Component>> lore) {
        ArrayList<Component> components;
        if (this.meta == null) {
            return (B)this;
        }
        if (VersionHelper.IS_COMPONENT_LEGACY) {
            List stringLore = this.meta.getLore();
            components = stringLore == null ? new ArrayList() : stringLore.stream().map(Legacy.SERIALIZER::deserialize).collect(Collectors.toList());
        } else {
            try {
                List jsonLore = (List)LORE_FIELD.get(this.meta);
                components = jsonLore == null ? new ArrayList() : jsonLore.stream().map(this::deserializeComponent).collect(Collectors.toList());
            } catch (IllegalAccessException exception) {
                components = new ArrayList<Component>();
                exception.printStackTrace();
            }
        }
        lore.accept(components);
        return this.lore(components);
    }

    @NotNull
    @Contract(value="_, _, _ -> this")
    public B enchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        this.meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return (B)this;
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public B enchant(@NotNull Enchantment enchantment, int level) {
        return this.enchant(enchantment, level, true);
    }

    @NotNull
    @Contract(value="_ -> this")
    public B enchant(@NotNull Enchantment enchantment) {
        return this.enchant(enchantment, 1, true);
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public B enchant(@NotNull Map<Enchantment, Integer> enchantments, boolean ignoreLevelRestriction) {
        enchantments.forEach((enchantment, level) -> this.enchant((Enchantment)enchantment, (int)level, ignoreLevelRestriction));
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B enchant(@NotNull Map<Enchantment, Integer> enchantments) {
        return this.enchant(enchantments, true);
    }

    @NotNull
    @Contract(value="_ -> this")
    public B disenchant(@NotNull Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B flags(@NotNull ItemFlag ... flags) {
        this.meta.addItemFlags(flags);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B unbreakable() {
        return this.unbreakable(true);
    }

    @NotNull
    @Contract(value="_ -> this")
    public B unbreakable(boolean unbreakable) {
        if (VersionHelper.IS_UNBREAKABLE_LEGACY) {
            return this.setNbt("Unbreakable", unbreakable);
        }
        this.meta.setUnbreakable(unbreakable);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B glow() {
        return this.glow(true);
    }

    @NotNull
    @Contract(value="_ -> this")
    public B glow(boolean glow) {
        if (glow) {
            this.meta.addEnchant(Enchantment.LURE, 1, false);
            this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            return (B)this;
        }
        for (Enchantment enchantment : this.meta.getEnchants().keySet()) {
            this.meta.removeEnchant(enchantment);
        }
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B pdc(@NotNull Consumer<PersistentDataContainer> consumer) {
        consumer.accept(this.meta.getPersistentDataContainer());
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B model(int modelData) {
        if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
            this.meta.setCustomModelData(Integer.valueOf(modelData));
        }
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B color(@NotNull Color color) {
        if (LEATHER_ARMOR.contains(this.itemStack.getType())) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.getMeta();
            leatherArmorMeta.setColor(color);
            this.setMeta((ItemMeta)leatherArmorMeta);
        }
        return (B)this;
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public B setNbt(@NotNull String key, @NotNull String value) {
        this.itemStack.setItemMeta(this.meta);
        this.itemStack = ItemNbt.setString(this.itemStack, key, value);
        this.meta = this.itemStack.getItemMeta();
        return (B)this;
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public B setNbt(@NotNull String key, boolean value) {
        this.itemStack.setItemMeta(this.meta);
        this.itemStack = ItemNbt.setBoolean(this.itemStack, key, value);
        this.meta = this.itemStack.getItemMeta();
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B removeNbt(@NotNull String key) {
        this.itemStack.setItemMeta(this.meta);
        this.itemStack = ItemNbt.removeTag(this.itemStack, key);
        this.meta = this.itemStack.getItemMeta();
        return (B)this;
    }

    @NotNull
    public ItemStack build() {
        this.itemStack.setItemMeta(this.meta);
        return this.itemStack;
    }

    @NotNull
    @Contract(value=" -> new")
    public GuiItem asGuiItem() {
        return new GuiItem(this.build());
    }

    @NotNull
    @Contract(value="_ -> new")
    public GuiItem asGuiItem(@NotNull GuiAction<InventoryClickEvent> action) {
        return new GuiItem(this.build(), action);
    }

    @NotNull
    protected ItemStack getItemStack() {
        return this.itemStack;
    }

    protected void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @NotNull
    protected ItemMeta getMeta() {
        return this.meta;
    }

    protected void setMeta(@NotNull ItemMeta meta) {
        this.meta = meta;
    }

    @Deprecated
    public B setName(@NotNull String name) {
        this.getMeta().setDisplayName(name);
        return (B)this;
    }

    @Deprecated
    public B setAmount(int amount) {
        this.getItemStack().setAmount(amount);
        return (B)this;
    }

    @Deprecated
    public B addLore(@NotNull String ... lore) {
        return this.addLore(Arrays.asList(lore));
    }

    @Deprecated
    public B addLore(@NotNull List<String> lore) {
        List<String> newLore = this.getMeta().hasLore() ? this.getMeta().getLore() : new ArrayList();
        newLore.addAll(lore);
        return this.setLore(newLore);
    }

    @Deprecated
    public B setLore(@NotNull String ... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    @Deprecated
    public B setLore(@NotNull List<String> lore) {
        this.getMeta().setLore(lore);
        return (B)this;
    }

    @Deprecated
    public B addEnchantment(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        this.getMeta().addEnchant(enchantment, level, ignoreLevelRestriction);
        return (B)this;
    }

    @Deprecated
    public B addEnchantment(@NotNull Enchantment enchantment, int level) {
        return this.addEnchantment(enchantment, level, true);
    }

    @Deprecated
    public B addEnchantment(@NotNull Enchantment enchantment) {
        return this.addEnchantment(enchantment, 1, true);
    }

    @Deprecated
    public B removeEnchantment(@NotNull Enchantment enchantment) {
        this.getItemStack().removeEnchantment(enchantment);
        return (B)this;
    }

    @Deprecated
    public B addItemFlags(@NotNull ItemFlag ... flags) {
        this.getMeta().addItemFlags(flags);
        return (B)this;
    }

    @Deprecated
    public B setUnbreakable(boolean unbreakable) {
        return this.unbreakable(unbreakable);
    }

    static {
        try {
            Class<?> metaClass = VersionHelper.craftClass("inventory.CraftMetaItem");
            DISPLAY_NAME_FIELD = metaClass.getDeclaredField("displayName");
            DISPLAY_NAME_FIELD.setAccessible(true);
            LORE_FIELD = metaClass.getDeclaredField("lore");
            LORE_FIELD.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException exception) {
            exception.printStackTrace();
            throw new GuiException("Could not retrieve displayName nor lore field for ItemBuilder.");
        }
    }
}

