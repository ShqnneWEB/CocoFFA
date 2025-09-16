/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package hu.geri.libs.revxrsal.commands.bukkit.listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.bukkit.actor.ActorFactory;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class AsyncPaperTabListener<A extends BukkitCommandActor>
implements Listener {
    private final Lamp<A> lamp;
    private final ActorFactory<A> actorFactory;

    public AsyncPaperTabListener(Lamp<A> lamp, ActorFactory<A> actorFactory) {
        this.lamp = lamp;
        this.actorFactory = actorFactory;
    }

    @EventHandler(ignoreCancelled=true)
    public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
        String buf = event.getBuffer();
        if (!event.isCommand() && !buf.startsWith("/") || buf.indexOf(32) == -1) {
            return;
        }
        StringStream stream = StringStream.create(buf.startsWith("/") ? buf.substring(1) : buf);
        A actor = this.actorFactory.create(event.getSender(), this.lamp);
        try {
            List<String> complete = this.lamp.autoCompleter().complete(actor, stream);
            if (complete.isEmpty()) {
                return;
            }
            if (complete.size() == 1 && complete.get(0).isEmpty()) {
                complete.set(0, " ");
            }
            for (String s : complete) {
                event.getCompletions().add(s);
            }
            event.setHandled(true);
        } catch (Throwable throwable) {
            // empty catch block
        }
    }
}

