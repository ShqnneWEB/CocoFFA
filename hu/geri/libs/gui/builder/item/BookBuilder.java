/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.BookMeta
 *  org.bukkit.inventory.meta.BookMeta$Generation
 *  org.bukkit.inventory.meta.ItemMeta
 */
package hu.geri.libs.gui.builder.item;

import hu.geri.libs.gui.builder.item.BaseItemBuilder;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.components.util.Legacy;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BookBuilder
extends BaseItemBuilder<BookBuilder> {
    private static final EnumSet<Material> BOOKS = EnumSet.of(Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);

    BookBuilder(@NotNull ItemStack itemStack) {
        super(itemStack);
        if (!BOOKS.contains(itemStack.getType())) {
            throw new GuiException("BookBuilder requires the material to be a WRITABLE_BOOK/WRITTEN_BOOK!");
        }
    }

    @NotNull
    @Contract(value="_ -> this")
    public BookBuilder author(@Nullable Component author) {
        BookMeta bookMeta = (BookMeta)this.getMeta();
        if (author == null) {
            bookMeta.setAuthor(null);
            this.setMeta((ItemMeta)bookMeta);
            return this;
        }
        bookMeta.setAuthor(Legacy.SERIALIZER.serialize(author));
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public BookBuilder generation(@Nullable BookMeta.Generation generation) {
        BookMeta bookMeta = (BookMeta)this.getMeta();
        bookMeta.setGeneration(generation);
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public BookBuilder page(@NotNull Component ... pages) {
        return this.page(Arrays.asList(pages));
    }

    @NotNull
    @Contract(value="_ -> this")
    public BookBuilder page(@NotNull List<Component> pages) {
        BookMeta bookMeta = (BookMeta)this.getMeta();
        for (Component page : pages) {
            bookMeta.addPage(new String[]{Legacy.SERIALIZER.serialize(page)});
        }
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }

    @NotNull
    @Contract(value="_, _ -> this")
    public BookBuilder page(int page, @NotNull Component data) {
        BookMeta bookMeta = (BookMeta)this.getMeta();
        bookMeta.setPage(page, Legacy.SERIALIZER.serialize(data));
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public BookBuilder title(@Nullable Component title) {
        BookMeta bookMeta = (BookMeta)this.getMeta();
        if (title == null) {
            bookMeta.setTitle(null);
            this.setMeta((ItemMeta)bookMeta);
            return this;
        }
        bookMeta.setTitle(Legacy.SERIALIZER.serialize(title));
        this.setMeta((ItemMeta)bookMeta);
        return this;
    }
}

