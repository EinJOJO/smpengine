package it.einjojo.smpengine.command.admin;

import it.einjojo.smpengine.SMPEnginePlugin;
import it.einjojo.smpengine.command.Command;
import it.einjojo.smpengine.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MaintenanceSubCommand implements Command {

    private final SMPEnginePlugin plugin;
    boolean enabled;

    public MaintenanceSubCommand(SMPEnginePlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!plugin.getMaintenanceConfig().isEnabled()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission(plugin.getMaintenanceConfig().getBypassPermission())) {
                    player.kick(MessageUtil.format(plugin.getMaintenanceConfig().getKickMessage(), plugin.getPrimaryColor(), plugin.getPrefix()));
                }
            }
            Bukkit.broadcast(plugin.getMessage("command.maintenance.enabled"));
            plugin.getMaintenanceConfig().setEnabled(true);

        } else {
            plugin.getMaintenanceConfig().setEnabled(false);
            Bukkit.broadcast(plugin.getMessage("command.maintenance.disabled"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        return "admin.maintenance";
    }

    @Override
    public String getDescription() {
        return "activate maintenance";
    }

    @Override
    public String getCommand() {
        return "maintenance";
    }
}
