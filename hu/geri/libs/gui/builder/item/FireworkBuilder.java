/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.FireworkEffect
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.FireworkEffectMeta
 *  org.bukkit.inventory.meta.FireworkMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package hu.geri.libs.gui.builder.item;

import hu.geri.libs.gui.builder.item.BaseItemBuilder;
import hu.geri.libs.gui.components.exception.GuiException;
import java.util.Arrays;
import java.util.List;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class FireworkBuilder
extends BaseItemBuilder<FireworkBuilder> {
    private static final Material STAR = Material.FIREWORK_STAR;
    private static final Material ROCKET = Material.FIREWORK_ROCKET;

    FireworkBuilder(@NotNull ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != STAR && itemStack.getType() != ROCKET) {
            throw new GuiException("FireworkBuilder requires the material to be a FIREWORK_STAR/FIREWORK_ROCKET!");
        }
    }

    @NotNull
    @Contract(value="_ -> this")
    public FireworkBuilder effect(@NotNull FireworkEffect ... effects) {
        return this.effect(Arrays.asList(effects));
    }

    @NotNull
    @Contract(value="_ -> this")
    public FireworkBuilder effect(@NotNull List<FireworkEffect> effects) {
        if (effects.isEmpty()) {
            return this;
        }
        if (this.getItemStack().getType() == STAR) {
            FireworkEffectMeta effectMeta = (FireworkEffectMeta)this.getMeta();
            effectMeta.setEffect(effects.get(0));
            this.setMeta((ItemMeta)effectMeta);
            return this;
        }
        FireworkMeta fireworkMeta = (FireworkMeta)this.getMeta();
        fireworkMeta.addEffects(effects);
        this.setMeta((ItemMeta)fireworkMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public FireworkBuilder power(int power) {
        if (this.getItemStack().getType() == ROCKET) {
            FireworkMeta fireworkMeta = (FireworkMeta)this.getMeta();
            fireworkMeta.setPower(power);
            this.setMeta((ItemMeta)fireworkMeta);
        }
        return this;
    }
}

