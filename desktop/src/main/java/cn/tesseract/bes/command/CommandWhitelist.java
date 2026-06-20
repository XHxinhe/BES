package cn.tesseract.bes.command;

import cn.tesseract.bes.Config;
import cn.tesseract.bes.Main;
import cn.tesseract.bes.server.BESPlayerList;
import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.CommandBase;
import net.minecraft.bes.ICommand;
import net.minecraft.bes.ICommandSender;
import net.minecraft.bes.WrongUsageException;
import net.minecraft.bes.server.MinecraftServer;

import java.util.List;

public class CommandWhitelist extends CommandBase {
    @Override
    public String getCommandName() {
        return "whitelist";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(3, getCommandName());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1 || "status".equalsIgnoreCase(args[0])) {
            sendStatus(sender);
            return;
        }

        BESPlayerList players = (BESPlayerList) MinecraftServer.getServer().getConfigurationManager();
        String action = args[0].toLowerCase();
        if ("on".equals(action) || "enable".equals(action)) {
            Main.config.whitelistEnabled = true;
            players.whiteListEnforced = true;
            Config.save(Main.config);
            send(sender, "§a[白名单] 已开启。");
            return;
        }

        if ("off".equals(action) || "disable".equals(action)) {
            Main.config.whitelistEnabled = false;
            players.whiteListEnforced = false;
            Config.save(Main.config);
            send(sender, "§c[白名单] 已关闭。");
            return;
        }

        if ("reload".equals(action)) {
            players.loadWhiteList();
            send(sender, "§a[白名单] 已重新读取 white-list.txt。");
            return;
        }

        if ("list".equals(action)) {
            send(sender, "§e[白名单] 当前名单: " + CommandBase.joinNiceString(players.obf1_e().toArray()));
            return;
        }

        if ("add".equals(action)) {
            requireName(args);
            players.addToWhiteList(args[1]);
            send(sender, "§a[白名单] 已添加 " + args[1] + "。");
            return;
        }

        if ("remove".equals(action)) {
            requireName(args);
            players.removeFromWhitelist(args[1]);
            send(sender, "§c[白名单] 已移除 " + args[1] + "。");
            return;
        }

        throw new WrongUsageException("用法: /whitelist <on|off|status|list|add|remove|reload>", new Object[0]);
    }

    private void sendStatus(ICommandSender sender) {
        send(sender, Main.config.whitelistEnabled ? "§a[白名单] 当前已开启。" : "§c[白名单] 当前已关闭。");
    }

    private static void requireName(String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("用法: /whitelist <add|remove> <玩家名>", new Object[0]);
        }
    }

    private static void send(ICommandSender sender, String message) {
        sender.sendChatToPlayer(new ChatMessageComponent().addText(message));
    }

    @Override
    public List<String> obf1_a(String[] args) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "on", "off", "status", "list", "add", "remove", "reload");
        }
        if (args.length == 2 && "remove".equalsIgnoreCase(args[0])) {
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().obf1_e());
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1 && args.length > 0 && ("add".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0]));
    }

    @Override
    public int compareTo(ICommand command) {
        return getCommandName().compareTo(command.getCommandName());
    }
}
