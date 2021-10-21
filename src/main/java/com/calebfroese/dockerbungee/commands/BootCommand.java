package com.calebfroese.dockerbungee.commands;

import com.calebfroese.dockerbungee.DockerUtil;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import static com.calebfroese.dockerbungee.DockerBungeePlugin.plugin;

import java.util.List;

public class BootCommand extends Command {

    public BootCommand() {
        super("boot");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage(new TextComponent("Try /boot <server>"));
            return;
        }
        String serverName = strings[0];

        List<String> servers = plugin.configuration.getServers();
        if (servers.contains(serverName)) {
            DockerUtil.bootServer(serverName, commandSender);
        } else {
            String serverList = String.join(", ", servers);
            commandSender.sendMessage(
                    new TextComponent("Error: '" + serverName + "' is not a known server. Servers: " + serverList));
        }
    }
}
