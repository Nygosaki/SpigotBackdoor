package me.nygosaki.voidaddons;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Trophy implements CommandExecutor{
    private File jsonFile;
    NamespacedKey key = new NamespacedKey("trophy", "name");

    public Trophy(){
        try {
            jsonFile = new File("plugins/VoidAddons/trophies.json");
            if (!jsonFile.exists()) {
                jsonFile.getParentFile().mkdirs();
                jsonFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("trophies")) {
            Gson gson = new Gson();
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("§e ----- §6Trophy List §e-----\n ");
                if (jsonFile.length() > 0) {
                    FileReader reader = new FileReader("plugins/VoidAddons/trophies.json");
                    Map<String, String> trophiesData = gson.fromJson(reader, HashMap.class);

                    for (Map.Entry<String, String> entry : trophiesData.entrySet()) {
                        String faction = entry.getValue();
                        String trophyName = entry.getKey();
                        sb.append("§6").append(trophyName).append("§e: ").append("§f").append(faction).append("\n");
                    }
                }
                player.sendMessage(sb.toString());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (cmd.getName().equalsIgnoreCase("trophyclaim")) { // Update the faction's trophy
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item != null && !item.getType().isAir()) {
                ItemMeta itemMeta = item.getItemMeta();

                if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    PersistentDataContainer trophyPDC = item.getItemMeta().getPersistentDataContainer();
                    String trophyName = trophyPDC.get(key, PersistentDataType.STRING);

                    String faction = "%clansLite_clanName%";
                    faction = PlaceholderAPI.setPlaceholders(player, faction);

                    try {
                            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                            Map<String, String> trophiesData = new Gson().fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                            reader.close();

                            trophiesData.put(trophyName, faction);

                            FileWriter fileWriter = new FileWriter(jsonFile);
                            fileWriter.write(new Gson().toJson(trophiesData));
                            fileWriter.close();

                            player.sendMessage("Trophy §6" + trophyName + "§r has been acquired by the faction §6" + faction + ".§r");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return true;
                } else {
                    player.sendMessage("The item in your hand is not a tracked item.");
                    return false;
                }
            } else {
                player.sendMessage("Hold a trophy in your main hand to capture it.");
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("trophymanage")) { // Remove the tracker from the item in hand and from all factions
            if (!player.hasPermission("voidaddons.trophy.manage")) { // Check for permission
                player.sendMessage("You do not have permission to use this command.");
                return false;
            }
            if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {

                try {
                    String trophyName = args[1];
                        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                        Map<String, String> trophiesData = new Gson().fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                        reader.close();

                        if (trophiesData.containsKey(trophyName)) {
                            trophiesData.remove(trophyName);

                            FileWriter fileWriter = new FileWriter(jsonFile);
                            fileWriter.write(new Gson().toJson(trophiesData));
                            fileWriter.close();

                            player.sendMessage("Trophy entry deleted successfully.");
                            return true;
                        } else {
                            player.sendMessage("Trophy not found.");
                            return true;
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("create") && args.length >= 2) {

                String trophyName = args[1]; // This is the value provided by the user
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item != null && !item.getType().isAir()) {
                    ItemMeta itemMeta = item.getItemMeta();

                    itemMeta.setDisplayName(ChatColor.DARK_PURPLE + trophyName);

                    itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, trophyName);
                    item.setItemMeta(itemMeta);
                    player.sendMessage("The item in your main hand has become a trophy.");
                    try {
                        Map<String, String> trophiesData;

                        if (jsonFile.length() > 0) {
                            // If the file is not empty, read existing JSON data
                            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                            trophiesData = new Gson().fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
                            reader.close();
                        } else {
                            trophiesData = new HashMap<>();
                        }

                        trophiesData.put(trophyName, "NoneFaction");

                        FileWriter fileWriter = new FileWriter(jsonFile);
                        fileWriter.write(new Gson().toJson(trophiesData));
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    player.sendMessage("Hold an item in your hand to add a tracker.");
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return false;
    }
}
