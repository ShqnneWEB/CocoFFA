/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.response;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.response.ResponseHandler;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ClassResponseHandlerFactory<A extends CommandActor, T>
implements ResponseHandler.Factory<A> {
    private final Class<?> type;
    private final ResponseHandler<A, T> responseHandler;
    private final boolean allowSubclasses;

    public ClassResponseHandlerFactory(Class<?> type, ResponseHandler<A, T> responseHandler, boolean allowSubclasses) {
        this.type = Classes.wrap(type);
        this.responseHandler = responseHandler;
        this.allowSubclasses = allowSubclasses;
    }

    @Override
    public <L> ResponseHandler<A, L> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<A> lamp) {
        Class<?> pType = Classes.wrap(Classes.getRawType(parameterType));
        if (this.allowSubclasses && this.type.isAssignableFrom(pType)) {
            return this.responseHandler;
        }
        if (this.type == pType) {
            return this.responseHandler;
        }
        return null;
    }

    public Class<?> type() {
        return this.type;
    }

    public ResponseHandler<A, T> responseHandler() {
        return this.responseHandler;
    }

    public boolean allowSubclasses() {
        return this.allowSubclasses;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ClassResponseHandlerFactory that = (ClassResponseHandlerFactory)obj;
        return Objects.equals(this.type, that.type) && Objects.equals(this.responseHandler, that.responseHandler) && this.allowSubclasses == that.allowSubclasses;
    }

    public int hashCode() {
        return Objects.hash(this.type, this.responseHandler, this.allowSubclasses);
    }

    public String toString() {
        return "ClassResponseHandlerFactory[type=" + this.type + ", responseHandler=" + this.responseHandler + ", allowSubclasses=" + this.allowSubclasses + ']';
    }
}

