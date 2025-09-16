/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package hu.geri.commands;

import hu.geri.CocoFFA;
import hu.geri.commands.annotations.ArenaNames;
import hu.geri.commands.annotations.EnabledArenas;
import hu.geri.commands.annotations.LocationTypes;
import hu.geri.commands.annotations.OnlinePlayers;
import hu.geri.commands.annotations.RunningArenas;
import hu.geri.commands.subcommands.Create;
import hu.geri.commands.subcommands.Delete;
import hu.geri.commands.subcommands.Editor;
import hu.geri.commands.subcommands.GiveStarter;
import hu.geri.commands.subcommands.Help;
import hu.geri.commands.subcommands.Join;
import hu.geri.commands.subcommands.Leave;
import hu.geri.commands.subcommands.LeaveSpectate;
import hu.geri.commands.subcommands.List;
import hu.geri.commands.subcommands.Reload;
import hu.geri.commands.subcommands.ResetWins;
import hu.geri.commands.subcommands.Setup;
import hu.geri.commands.subcommands.Spectate;
import hu.geri.commands.subcommands.Start;
import hu.geri.commands.subcommands.StartRandom;
import hu.geri.commands.subcommands.Stop;
import hu.geri.commands.subcommands.StopAll;
import hu.geri.commands.subcommands.Wins;
import hu.geri.libs.revxrsal.commands.annotation.Optional;
import hu.geri.libs.revxrsal.commands.annotation.Subcommand;
import hu.geri.libs.revxrsal.commands.bukkit.annotation.CommandPermission;
import hu.geri.libs.revxrsal.commands.orphan.OrphanCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FFACommands
implements OrphanCommand {
    private final CocoFFA plugin;

    public FFACommands(CocoFFA plugin) {
        this.plugin = plugin;
    }

    @Subcommand(value={"help"})
    public void help(CommandSender sender) {
        Help.INSTANCE.execute(sender);
    }

    @Subcommand(value={"create"})
    @CommandPermission(value="cocoffa.admin.create")
    public void create(CommandSender sender, String arena) {
        Create.INSTANCE.execute(sender, arena);
    }

    @Subcommand(value={"delete"})
    @CommandPermission(value="cocoffa.admin.delete")
    public void delete(CommandSender sender, @ArenaNames String arena) {
        Delete.INSTANCE.execute(sender, arena);
    }

    @Subcommand(value={"join"})
    @CommandPermission(value="cocoffa.player")
    public void join(Player player, @RunningArenas String arena) {
        Join.INSTANCE.execute(player, arena);
    }

    @Subcommand(value={"leave"})
    @CommandPermission(value="cocoffa.player")
    public void leave(Player player) {
        Leave.INSTANCE.execute(player);
    }

    @Subcommand(value={"start"})
    @CommandPermission(value="cocoffa.admin.start")
    public void start(CommandSender sender, @ArenaNames String arena) {
        Start.INSTANCE.execute(sender, arena);
    }

    @Subcommand(value={"stop"})
    @CommandPermission(value="cocoffa.admin.stop")
    public void stop(CommandSender sender, @RunningArenas String arena) {
        Stop.INSTANCE.execute(sender, arena);
    }

    @Subcommand(value={"stopall"})
    @CommandPermission(value="cocoffa.admin.stopall")
    public void stopall(CommandSender sender) {
        StopAll.INSTANCE.execute(sender);
    }

    @Subcommand(value={"list"})
    @CommandPermission(value="cocoffa.admin.list")
    public void list(CommandSender sender) {
        List.INSTANCE.execute(sender);
    }

    @Subcommand(value={"reload"})
    @CommandPermission(value="cocoffa.admin.reload")
    public void reload(CommandSender sender) {
        Reload.INSTANCE.execute(sender);
    }

    @Subcommand(value={"givestarter"})
    @CommandPermission(value="cocoffa.admin.givestarter")
    public void givestarter(CommandSender sender, @EnabledArenas String arena, @OnlinePlayers String player, @Optional Integer amount) {
        GiveStarter.INSTANCE.execute(sender, arena, player, amount);
    }

    @Subcommand(value={"resetwins"})
    @CommandPermission(value="cocoffa.admin.resetwins")
    public void resetwins(CommandSender sender, @OnlinePlayers String player) {
        ResetWins.INSTANCE.execute(sender, player);
    }

    @Subcommand(value={"wins"})
    @CommandPermission(value="cocoffa.player")
    public void wins(CommandSender sender, @Optional @OnlinePlayers String player) {
        Wins.INSTANCE.execute(sender, player);
    }

    @Subcommand(value={"setup"})
    @CommandPermission(value="cocoffa.admin.setup")
    public void setup(Player player, @ArenaNames String arena, @LocationTypes String locationType) {
        Setup.INSTANCE.execute(player, arena, locationType);
    }

    @Subcommand(value={"editor"})
    @CommandPermission(value="cocoffa.admin.editor")
    public void editor(Player player, @ArenaNames String arena) {
        Editor.INSTANCE.execute(player, arena);
    }

    @Subcommand(value={"startrandom"})
    @CommandPermission(value="cocoffa.admin.startrandom")
    public void startrandom(CommandSender sender) {
        StartRandom.INSTANCE.execute(sender);
    }

    @Subcommand(value={"spectate"})
    @CommandPermission(value="cocoffa.player")
    public void spectate(Player player, @ArenaNames String arena) {
        Spectate.INSTANCE.execute(player, arena);
    }

    @Subcommand(value={"leavespectate"})
    @CommandPermission(value="cocoffa.player")
    public void leavespectate(Player player) {
        LeaveSpectate.INSTANCE.execute(player);
    }
}

