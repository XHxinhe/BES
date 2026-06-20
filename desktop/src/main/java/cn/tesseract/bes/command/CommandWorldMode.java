package cn.tesseract.bes.command;

import cn.tesseract.bes.Config;
import cn.tesseract.bes.Main;
import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.CommandBase;
import net.minecraft.bes.ICommand;
import net.minecraft.bes.ICommandSender;

import java.util.List;

public class CommandWorldMode extends CommandBase {
    @Override
    public String getCommandName() {
        return "worldmode";
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

        if ("survival".equalsIgnoreCase(args[0]) || "normal".equalsIgnoreCase(args[0]) || "生存".equals(args[0])) {
            setMode("survival");
            send(sender, "§a[世界模式] 已切换为生存。");
            return;
        }

        if ("hardcore".equalsIgnoreCase(args[0]) || "extreme".equalsIgnoreCase(args[0]) || "极限".equals(args[0])) {
            setMode("hardcore");
            send(sender, "§c[世界模式] 已切换为极限。");
            return;
        }

        send(sender, "§c用法: /worldmode <survival|hardcore|status>");
    }

    private static void setMode(String mode) {
        if (Main.server != null) {
            Main.server.setWorldMode(mode);
        } else {
            Main.config.worldMode = mode;
            Main.config.normalize();
        }
        Config.save(Main.config);
    }

    private static void sendStatus(ICommandSender sender) {
        send(sender, "hardcore".equals(Main.config.worldMode) ? "§c[世界模式] 当前为极限。" : "§a[世界模式] 当前为生存。");
    }

    private static void send(ICommandSender sender, String message) {
        sender.sendChatToPlayer(new ChatMessageComponent().addText(message));
    }

    @Override
    public List<String> obf1_a(String[] args) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "survival", "hardcore", "status");
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
