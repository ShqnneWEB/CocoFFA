/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.fastboard.adventure;

import hu.geri.libs.fastboard.FastBoardBase;
import hu.geri.libs.fastboard.FastReflection;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class FastBoard
extends FastBoardBase<Component> {
    private static final MethodHandle COMPONENT_METHOD;
    private static final Object EMPTY_COMPONENT;
    private static final boolean ADVENTURE_SUPPORT;

    public FastBoard(Player player) {
        super(player);
    }

    @Override
    protected void sendLineChange(int score) throws Throwable {
        Component line = (Component)this.getLineByScore(score);
        this.sendTeamPacket(score, FastBoardBase.TeamMode.UPDATE, line, null);
    }

    @Override
    protected Object toMinecraftComponent(Component component) throws Throwable {
        if (component == null) {
            return EMPTY_COMPONENT;
        }
        if (!ADVENTURE_SUPPORT) {
            String legacy = this.serializeLine(component);
            return Array.get(COMPONENT_METHOD.invoke(legacy), 0);
        }
        return COMPONENT_METHOD.invoke(component);
    }

    @Override
    protected String serializeLine(Component value) {
        return LegacyComponentSerializer.legacySection().serialize(value);
    }

    @Override
    protected Component emptyLine() {
        return Component.empty();
    }

    static {
        ADVENTURE_SUPPORT = FastReflection.optionalClass("io.papermc.paper.adventure.PaperAdventure").isPresent();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            if (ADVENTURE_SUPPORT) {
                Class<?> paperAdventure = Class.forName("io.papermc.paper.adventure.PaperAdventure");
                Method method = paperAdventure.getDeclaredMethod("asVanilla", Component.class);
                COMPONENT_METHOD = lookup.unreflect(method);
                EMPTY_COMPONENT = COMPONENT_METHOD.invoke(Component.empty());
            } else {
                Class<?> craftChatMessageClass = FastReflection.obcClass("util.CraftChatMessage");
                COMPONENT_METHOD = lookup.unreflect(craftChatMessageClass.getMethod("fromString", String.class));
                EMPTY_COMPONENT = Array.get(COMPONENT_METHOD.invoke(""), 0);
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }
}

