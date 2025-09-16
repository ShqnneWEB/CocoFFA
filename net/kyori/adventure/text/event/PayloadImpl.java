/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.event;

import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

abstract class PayloadImpl
implements ClickEvent.Payload {
    PayloadImpl() {
    }

    public String toString() {
        return Internals.toString(this);
    }

    static final class CustomImpl
    extends PayloadImpl
    implements ClickEvent.Payload.Custom {
        private final Key key;
        private final BinaryTagHolder nbt;

        CustomImpl(@NotNull Key key, @NotNull BinaryTagHolder nbt) {
            this.key = key;
            this.nbt = nbt;
        }

        @Override
        @NotNull
        public Key key() {
            return this.key;
        }

        @Override
        @NotNull
        public String data() {
            return this.nbt.string();
        }

        @Override
        @NotNull
        public BinaryTagHolder nbt() {
            return this.nbt;
        }

        @Override
        @NotNull
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(ExaminableProperty.of("key", this.key), ExaminableProperty.of("nbt", this.nbt));
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            CustomImpl that = (CustomImpl)other;
            return Objects.equals(this.key, that.key) && Objects.equals(this.nbt, that.nbt);
        }

        public int hashCode() {
            int result = this.key.hashCode();
            result = 31 * result + this.nbt.hashCode();
            return result;
        }
    }

    static final class DialogImpl
    extends PayloadImpl
    implements ClickEvent.Payload.Dialog {
        private final DialogLike dialogLike;

        DialogImpl(@NotNull DialogLike dialogLike) {
            this.dialogLike = dialogLike;
        }

        @Override
        @NotNull
        public DialogLike dialog() {
            return this.dialogLike;
        }

        @Override
        @NotNull
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(ExaminableProperty.of("dialog", this.dialogLike));
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            DialogImpl that = (DialogImpl)other;
            return Objects.equals(this.dialogLike, that.dialogLike);
        }

        public int hashCode() {
            return this.dialogLike.hashCode();
        }
    }

    static final class IntImpl
    extends PayloadImpl
    implements ClickEvent.Payload.Int {
        private final int integer;

        IntImpl(int integer) {
            this.integer = integer;
        }

        @Override
        public int integer() {
            return this.integer;
        }

        @Override
        @NotNull
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(ExaminableProperty.of("integer", this.integer));
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            IntImpl that = (IntImpl)other;
            return Objects.equals(this.integer, that.integer);
        }

        public int hashCode() {
            return this.integer;
        }
    }

    static final class TextImpl
    extends PayloadImpl
    implements ClickEvent.Payload.Text {
        private final String value;

        TextImpl(@NotNull String value) {
            this.value = value;
        }

        @Override
        @NotNull
        public String value() {
            return this.value;
        }

        @Override
        @NotNull
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(ExaminableProperty.of("value", this.value));
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            TextImpl that = (TextImpl)other;
            return Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }
}

