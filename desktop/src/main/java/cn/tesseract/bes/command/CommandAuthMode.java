package cn.tesseract.bes.command;

import cn.tesseract.bes.Config;
import cn.tesseract.bes.Main;
import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.CommandBase;
import net.minecraft.bes.ICommand;
import net.minecraft.bes.ICommandSender;
import net.minecraft.bes.server.MinecraftServer;

import java.util.List;

public class CommandAuthMode extends CommandBase {
    @Override
    public String getCommandName() {
        return "authmode";
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

        if ("on".equalsIgnoreCase(args[0]) || "enable".equalsIgnoreCase(args[0])) {
            setOnlineMode(true);
            send(sender, "§a[正版验证] 已开启。");
            return;
        }

        if ("off".equalsIgnoreCase(args[0]) || "disable".equalsIgnoreCase(args[0])) {
            setOnlineMode(false);
            send(sender, "§c[正版验证] 已关闭。");
            return;
        }

        send(sender, "§c用法: /authmode <on|off|status>");
    }

    private static void setOnlineMode(boolean enabled) {
        Main.config.onlineMode = enabled;
        MinecraftServer.getServer().obf1_d(enabled);
        Config.save(Main.config);
    }

    private void sendStatus(ICommandSender sender) {
        send(sender, Main.config.onlineMode ? "§a[正版验证] 当前已开启。" : "§c[正版验证] 当前已关闭。");
    }

    private static void send(ICommandSender sender, String message) {
        sender.sendChatToPlayer(new ChatMessageComponent().addText(message));
    }

    @Override
    public List<String> obf1_a(String[] args) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "on", "off", "status");
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand command) {
        return getCommandName().compareTo(command.getCommandName());
    }
}
