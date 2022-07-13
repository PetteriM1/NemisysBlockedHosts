package me.petterim1.nemisysblockedhosts;

import org.itxtech.nemisys.command.Command;
import org.itxtech.nemisys.command.CommandSender;
import org.itxtech.nemisys.event.EventHandler;
import org.itxtech.nemisys.event.EventPriority;
import org.itxtech.nemisys.event.Listener;
import org.itxtech.nemisys.event.player.PlayerAsyncPreLoginEvent;
import org.itxtech.nemisys.event.player.PlayerLoginEvent;
import org.itxtech.nemisys.network.AdvancedSourceInterface;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.plugin.PluginBase;
import org.itxtech.nemisys.utils.Config;

import java.net.InetSocketAddress;
import java.util.List;

public class Main extends PluginBase implements Listener {

    private String kickMessage;
    private List<String> blockedHosts;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("reloadblocked")) {
            reloadConfig();
            loadConfig();
            sender.sendMessage("Config reloaded");
        }
        return true;
    }

    private void loadConfig() {
        saveDefaultConfig();
        Config config = getConfig();
        kickMessage = config.getString("kickMessage");
        blockedHosts = config.getStringList("blockedHosts");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerLoginEvent e) {
        InetSocketAddress address = e.getPlayer().getSocketAddress();
        if (address != null) {
            String hostName = address.getHostName();
            if (hostName != null) {
                for (String blockedPart : blockedHosts) {
                    if (hostName.contains(blockedPart)) {
                        e.setKickMessage(kickMessage);
                        e.setCancelled(true);
                        for (SourceInterface in : getServer().getNetwork().getInterfaces()) {
                            if (in instanceof AdvancedSourceInterface) {
                                ((AdvancedSourceInterface) in).blockAddress(address.getAddress());
                            }
                        }
                        return;
                    }
                }
            }
        }
    }
}
