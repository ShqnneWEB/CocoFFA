/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.EmptyCoroutineContext
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 */
package hu.geri.libs.revxrsal.commands.ktx;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.LampBuilderVisitor;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ContextParameter;
import hu.geri.libs.revxrsal.commands.parameter.ParameterTypes;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0002\t\nB\t\b\u0002\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00020\bH\u0016\u00a8\u0006\u000b"}, d2={"Lhu/geri/libs/revxrsal/commands/ktx/SuspendFunctionsSupport;", "Lhu/geri/libs/revxrsal/commands/LampBuilderVisitor;", "Lhu/geri/libs/revxrsal/commands/command/CommandActor;", "<init>", "()V", "visit", "", "builder", "Lhu/geri/libs/revxrsal/commands/Lamp$Builder;", "ContinuationResolver", "BasicContinuation", "common"})
public final class SuspendFunctionsSupport
implements LampBuilderVisitor<CommandActor> {
    @NotNull
    public static final SuspendFunctionsSupport INSTANCE = new SuspendFunctionsSupport();

    private SuspendFunctionsSupport() {
    }

    @Override
    public void visit(@NotNull Lamp.Builder<CommandActor> builder) {
        Intrinsics.checkNotNullParameter(builder, (String)"builder");
        ParameterTypes.Builder<CommandActor> builder2 = builder.parameterTypes();
        ContinuationResolver continuationResolver = ContinuationResolver.INSTANCE;
        Intrinsics.checkNotNull((Object)continuationResolver, (String)"null cannot be cast to non-null type revxrsal.commands.parameter.ContextParameter<revxrsal.commands.command.CommandActor, kotlin.coroutines.Continuation<*>>");
        builder2.addContextParameterLast(Continuation.class, continuationResolver);
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u001b\u0012\u0012\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u001b\u0010\r\u001a\u00020\u00062\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00028\u00000\u000fH\u0016\u00a2\u0006\u0002\u0010\u0010R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u00020\n8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0011"}, d2={"Lhu/geri/libs/revxrsal/commands/ktx/SuspendFunctionsSupport$BasicContinuation;", "T", "Lkotlin/coroutines/Continuation;", "handleException", "Lkotlin/Function1;", "", "", "<init>", "(Lkotlin/jvm/functions/Function1;)V", "context", "Lkotlin/coroutines/EmptyCoroutineContext;", "getContext", "()Lkotlin/coroutines/EmptyCoroutineContext;", "resumeWith", "result", "Lkotlin/Result;", "(Ljava/lang/Object;)V", "common"})
    private static final class BasicContinuation<T>
    implements Continuation<T> {
        @NotNull
        private final Function1<Throwable, Unit> handleException;

        public BasicContinuation(@NotNull Function1<? super Throwable, Unit> handleException) {
            Intrinsics.checkNotNullParameter(handleException, (String)"handleException");
            this.handleException = handleException;
        }

        @NotNull
        public EmptyCoroutineContext getContext() {
            return EmptyCoroutineContext.INSTANCE;
        }

        public void resumeWith(@NotNull Object result) {
            Object object = result;
            Throwable throwable = Result.exceptionOrNull-impl((Object)object);
            if (throwable == null) {
                Object it = object;
                boolean bl = false;
            } else {
                Throwable it = throwable;
                boolean bl = false;
                this.handleException.invoke((Object)it);
            }
        }
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c2\u0002\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J$\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u000bH\u0016\u00a8\u0006\f"}, d2={"Lhu/geri/libs/revxrsal/commands/ktx/SuspendFunctionsSupport$ContinuationResolver;", "Lhu/geri/libs/revxrsal/commands/parameter/ContextParameter;", "Lhu/geri/libs/revxrsal/commands/command/CommandActor;", "Lkotlin/coroutines/Continuation;", "", "<init>", "()V", "resolve", "parameter", "Lhu/geri/libs/revxrsal/commands/command/CommandParameter;", "context", "Lhu/geri/libs/revxrsal/commands/node/ExecutionContext;", "common"})
    private static final class ContinuationResolver
    implements ContextParameter<CommandActor, Continuation<? super Object>> {
        @NotNull
        public static final ContinuationResolver INSTANCE = new ContinuationResolver();

        private ContinuationResolver() {
        }

        @Override
        @NotNull
        public Continuation<Object> resolve(@NotNull CommandParameter parameter, @NotNull ExecutionContext<CommandActor> context) {
            Intrinsics.checkNotNullParameter((Object)parameter, (String)"parameter");
            Intrinsics.checkNotNullParameter(context, (String)"context");
            return new BasicContinuation((Function1<? super Throwable, Unit>)((Function1)arg_0 -> ContinuationResolver.resolve$lambda$0(context, arg_0)));
        }

        private static final Unit resolve$lambda$0(ExecutionContext $context, Throwable it) {
            Intrinsics.checkNotNullParameter((Object)$context, (String)"$context");
            Intrinsics.checkNotNullParameter((Object)it, (String)"it");
            $context.lamp().handleException(it, ErrorContext.executingFunction($context));
            return Unit.INSTANCE;
        }
    }
}

