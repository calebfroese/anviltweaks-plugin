package com.calebfroese.dockerbungee.commands;



import com.calebfroese.dockerbungee.DockerUtil;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class UnbootCommand extends Command {

    public UnbootCommand() {
        super("unboot");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage(new TextComponent("Try /unboot <server>"));
            return;
        }
        String serverName = strings[0];

        List<String> servers = DockerUtil.getRunningServers();
        if (servers.contains(serverName)) {
            DockerUtil.bootServer(serverName, commandSender);
        } else {
            String serverList = String.join(", ", servers);
            commandSender.sendMessage(
                    new TextComponent("Error: '" + serverName + "' is not running. Running servers: " + serverList));
        }
    }
}
