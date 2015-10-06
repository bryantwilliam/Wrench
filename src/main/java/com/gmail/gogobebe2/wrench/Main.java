package com.gmail.gogobebe2.wrench;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
    private final static ItemStack WRENCH = initWrench();

    private static ItemStack initWrench() {
        ItemStack wrench = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        ItemMeta meta = wrench.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Wrench");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "1 use only");
        lore.add(ChatColor.BLUE + "Choose wisely!");
        meta.setLore(lore);
        wrench.setItemMeta(meta);
        return wrench;
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting up " + this.getName() + ". If you need me to update this plugin, email at gogobebe2@gmail.com");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling " + this.getName() + ". If you need me to update this plugin, email at gogobebe2@gmail.com");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("wrench") || label.equalsIgnoreCase("getwrench")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Error! You need to be a player to use this command.");
                return true;
            }
            Player player = (Player) sender;
            if (player.getInventory().addItem(WRENCH).isEmpty())
                player.sendMessage(ChatColor.GREEN + "A wrench has been added to your inventory");
            else player.sendMessage(ChatColor.RED + "Error, you do not have enough space in your inventory!");
            return true;
        }
        return false;
    }

    @EventHandler
    protected void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item != null && item.getType() == WRENCH.getType() && item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            ItemMeta wrenchMeta =  WRENCH.getItemMeta();
            if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(wrenchMeta.getDisplayName())
                    && itemMeta.hasLore() && itemMeta.getLore().equals(wrenchMeta.getLore())) {
                // Then the item is a wrench.
                Block block = event.getBlock();
                if (block.getType() == Material.MOB_SPAWNER) {
                    ItemStack mobSpawnerItem = new ItemStack(Material.MOB_SPAWNER, 1);
                    ItemMeta meta = mobSpawnerItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.BLUE + ((CreatureSpawner) block.getState()).getSpawnedType().getEntityClass().getName() + " spawner");
                    block.setType(Material.AIR);
                    meta.setLore(lore);
                    mobSpawnerItem.setItemMeta(meta);
                    player.setItemInHand(mobSpawnerItem);
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
                    player.getWorld().playEffect(player.getLocation(), Effect.ITEM_BREAK, null);
                    player.sendMessage(ChatColor.BLUE + "Wrench used!");
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    protected void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.MOB_SPAWNER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                String lore1 = meta.getLore().get(0);
                if (lore1.contains(" spawner")) {
                    String entityName = ChatColor.stripColor(lore1.replace(" spawner", ""));
                    for (EntityType type : EntityType.values()) {
                        if (type.getEntityClass().getName().equals(entityName)) {
                            Block spawner = event.getBlockPlaced();
                            CreatureSpawner state = (CreatureSpawner) spawner.getState();
                            state.setSpawnedType(type);
                            state.update(true);
                        }
                    }
                }
            }
        }
    }
}
