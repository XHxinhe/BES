package cn.tesseract.bes.command;

import cn.tesseract.bes.Config;
import cn.tesseract.bes.Main;
import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.CommandBase;
import net.minecraft.bes.ICommand;
import net.minecraft.bes.ICommandSender;

import java.util.List;

public class CommandTeamDamage extends CommandBase {
    @Override
    public String getCommandName() {
        return "teamdamage";
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
            Main.config.teamDamageEnabled = true;
            Config.save(Main.config);
            send(sender, "§a[队伤] 已开启。");
            return;
        }

        if ("off".equalsIgnoreCase(args[0]) || "disable".equalsIgnoreCase(args[0])) {
            Main.config.teamDamageEnabled = false;
            Config.save(Main.config);
            send(sender, "§c[队伤] 已关闭。");
            return;
        }

        send(sender, "§c用法: /teamdamage <on|off|status>");
    }

    private void sendStatus(ICommandSender sender) {
        send(sender, Main.config.teamDamageEnabled ? "§a[队伤] 当前已开启。" : "§c[队伤] 当前已关闭。");
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
