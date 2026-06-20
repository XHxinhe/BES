package cn.tesseract.bes.command;

import cn.tesseract.bes.Config;
import cn.tesseract.bes.Main;
import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.CommandBase;
import net.minecraft.bes.ICommand;
import net.minecraft.bes.ICommandSender;

import java.util.List;

public class CommandTeleportCommands extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpcmd";
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
            Main.config.teleportCommandsEnabled = true;
            Config.save(Main.config);
            sender.sendChatToPlayer(new ChatMessageComponent().addText("§a[传送] 传送类指令已开启。"));
            return;
        }

        if ("off".equalsIgnoreCase(args[0]) || "disable".equalsIgnoreCase(args[0])) {
            Main.config.teleportCommandsEnabled = false;
            Config.save(Main.config);
            sender.sendChatToPlayer(new ChatMessageComponent().addText("§c[传送] 传送类指令已关闭。"));
            return;
        }

        sender.sendChatToPlayer(new ChatMessageComponent().addText("§c用法: /tpcmd <on|off|status>"));
    }

    private void sendStatus(ICommandSender sender) {
        sender.sendChatToPlayer(new ChatMessageComponent().addText(Main.config.teleportCommandsEnabled
                ? "§a[传送] 传送类指令当前已开启。"
                : "§c[传送] 传送类指令当前已关闭。"));
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
