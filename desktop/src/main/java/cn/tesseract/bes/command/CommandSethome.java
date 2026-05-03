package cn.tesseract.bes.command;

import net.minecraft.bes.*;

import java.util.List;

public class CommandSethome extends CommandBase {
    @Override
    public String getCommandName() {
        return "sethome";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strArr) {
        ServerPlayer commandSenderAsPlayer = CommandBase.getCommandSenderAsPlayer(iCommandSender);
        String str = (strArr.length < 1 || strArr[0].isEmpty()) ? HomeData.DEFAULT_HOME : strArr[0];
        boolean zHasHome = HomeData.hasHome(commandSenderAsPlayer.username, str);
        HomeData.setHome(commandSenderAsPlayer.username, str, commandSenderAsPlayer.posX, commandSenderAsPlayer.posY, commandSenderAsPlayer.posZ, commandSenderAsPlayer.dimension);
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText((zHasHome ? "§e已更新" : "§a已保存") + " §f" + str + "§7: §f" + ((int) commandSenderAsPlayer.posX) + " " + ((int) commandSenderAsPlayer.posY) + " " + ((int) commandSenderAsPlayer.posZ)));
    }

    @Override 
    public List<String> obf1_a(String[] strArr) {
        return null;
    }

    @Override 
    public boolean isUsernameIndex(String[] strArr, int i) {
        return false;
    }

    @Override
    public int compareTo(ICommand iCommand) {
        return getCommandName().compareTo(iCommand.getCommandName());
    }
}