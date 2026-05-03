package cn.tesseract.bes.command;

import net.minecraft.bes.*;

import java.util.List;

public class CommandBack extends CommandBase {
    @Override
    public String getCommandName() {
        return "back";
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
        double[] lastPosition = HomeData.getLastPosition(commandSenderAsPlayer.username);
        if (lastPosition == null) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[Back] 没有可以返回的位置"));
            return;
        }
        HomeData.setLastPosition(commandSenderAsPlayer.username, commandSenderAsPlayer.posX, commandSenderAsPlayer.posY, commandSenderAsPlayer.posZ, commandSenderAsPlayer.dimension);
        int i = (int) lastPosition[3];
        if (commandSenderAsPlayer.dimension != i) {
            commandSenderAsPlayer.travelToDimension(i);
        }
        commandSenderAsPlayer.obf1_k(lastPosition[0], lastPosition[1], lastPosition[2]);
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§a[Back] 已返回上一个位置！"));
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