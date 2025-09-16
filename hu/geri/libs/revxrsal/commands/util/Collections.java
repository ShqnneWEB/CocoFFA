/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.CheckReturnValue
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

public final class Collections {
    private Collections() {
        Preconditions.cannotInstantiate(Collections.class);
    }

    @Contract(pure=true)
    @CheckReturnValue
    public static <L, K, V> Map<L, V> mapKeys(Map<K, V> map, Function<K, L> remap) {
        HashMap<L, V> remapped = new HashMap<L, V>();
        for (Map.Entry<K, V> e : map.entrySet()) {
            if (remapped.put(remap.apply(e.getKey()), e.getValue()) == null) continue;
            throw new IllegalStateException("Duplicate key");
        }
        return remapped;
    }

    public static <T> T getOrNull(T[] array, int index) {
        return index >= 0 && index <= Collections.lastIndex(array) ? (T)array[index] : null;
    }

    public static <T> T getOrNull(List<T> list, int index) {
        return index >= 0 && index <= list.size() - 1 ? (T)list.get(index) : null;
    }

    private static int lastIndex(Object[] array) {
        return array.length - 1;
    }

    @Contract(pure=true, value="null, _ -> fail; _, _ -> new")
    @CheckReturnValue
    public static <T> Object[] insertAtBeginning(@NotNull T[] original, @Nullable T item) {
        Preconditions.notNull(original, "original array");
        int newSize = original.length + 1;
        Object[] newArr = new Object[newSize];
        newArr[0] = item;
        System.arraycopy(original, 0, newArr, 1, original.length);
        return newArr;
    }

    @NotNull
    public static <T> T first(@NotNull Iterable<T> iterator, @NotNull Predicate<T> predicate) {
        for (T t : iterator) {
            if (!predicate.test(t)) continue;
            return t;
        }
        throw new IllegalStateException("No element found matching the predicate");
    }

    @Contract(pure=true)
    @CheckReturnValue
    @NotNull
    public static <T> List<T> filter(@NotNull Iterable<T> iterator, @NotNull Predicate<T> predicate) {
        ArrayList<T> list = new ArrayList<T>();
        for (T t : iterator) {
            if (!predicate.test(t)) continue;
            list.add(t);
        }
        return list;
    }

    @Contract(pure=true)
    @CheckReturnValue
    @NotNull
    public static <U, T> List<T> map(@NotNull Iterable<U> iterator, @NotNull Function<U, T> fn) {
        ArrayList<T> list = new ArrayList<T>();
        for (U u : iterator) {
            list.add(fn.apply(u));
        }
        return list;
    }

    @Contract(pure=true)
    @CheckReturnValue
    @NotNull
    public static <U, T> List<T> map(@NotNull U[] iterator, @NotNull Function<U, T> fn) {
        ArrayList<T> list = new ArrayList<T>();
        for (U u : iterator) {
            list.add(fn.apply(u));
        }
        return list;
    }

    public static <T> boolean any(@NotNull Iterable<T> iterator, Predicate<T> predicate) {
        for (T t : iterator) {
            if (!predicate.test(t)) continue;
            return true;
        }
        return false;
    }

    public static <T> int count(@NotNull Iterable<T> iterator, Predicate<T> predicate) {
        int i = 0;
        for (T t : iterator) {
            if (!predicate.test(t)) continue;
            ++i;
        }
        return i;
    }

    @NotNull
    public static <U, T> LinkedList<T> mapToLinkedList(@NotNull Iterable<U> iterator, @NotNull Function<U, T> fn) {
        LinkedList<T> list = new LinkedList<T>();
        for (U u : iterator) {
            list.add(fn.apply(u));
        }
        return list;
    }

    @NotNull
    public static <E> @UnmodifiableView Iterator<E> unmodifiableIterator(@NotNull Iterator<E> iterator) {
        return new UnmodifiableIterator(iterator);
    }

    @Contract(value="_ -> new")
    @CheckReturnValue
    public static <K, V> @Unmodifiable @NotNull Map<K, V> copyMap(@NotNull Map<K, V> map) {
        if (map instanceof LinkedHashMap) {
            return java.util.Collections.unmodifiableMap(new LinkedHashMap<K, V>(map));
        }
        return java.util.Collections.unmodifiableMap(new HashMap<K, V>(map));
    }

    @Contract(value="_ -> new")
    @CheckReturnValue
    public static <T> @Unmodifiable @NotNull List<T> copyList(@NotNull Collection<T> list) {
        return java.util.Collections.unmodifiableList(new ArrayList<T>(list));
    }

    static final class UnmodifiableIterator<E>
    implements Iterator<E> {
        private final Iterator<? extends E> iterator;

        private UnmodifiableIterator(Iterator<? extends E> iterator) {
            this.iterator = iterator;
        }

        public static <E> Iterator<E> create(Iterator<? extends E> iterator) {
            if (iterator == null) {
                throw new NullPointerException("The iterator can not be null.");
            }
            return new UnmodifiableIterator<E>(iterator);
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public E next() {
            return this.iterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Iterator.remove() is disabled.");
        }
    }
}

