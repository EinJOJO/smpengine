package it.einjojo.smpengine.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Fun implements Listener {


    public Fun(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


}
