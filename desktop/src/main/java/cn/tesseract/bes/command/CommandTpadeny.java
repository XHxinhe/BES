package cn.tesseract.bes.command;

import net.minecraft.bes.*;

import java.util.List;

public class CommandTpadeny extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpadeny";
    }

    public List<String> getCommandAliases() {
        return List.of("tpano");
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
        String tpaRequest = HomeData.getTpaRequest(commandSenderAsPlayer.username);
        if (tpaRequest == null) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[TPA] 没有待处理的传送申请"));
        } else {
            HomeData.clearTpaRequest(commandSenderAsPlayer.username);
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[TPA] 已拒绝 " + tpaRequest + " 的申请"));
        }
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