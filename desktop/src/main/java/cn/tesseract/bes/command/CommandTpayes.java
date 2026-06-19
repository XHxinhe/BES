package cn.tesseract.bes.command;

import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.CommandBase;
import net.minecraft.bes.ICommand;
import net.minecraft.bes.ICommandSender;

import java.util.List;

public class CommandTpayes extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpayes";
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
        if (HomeData.toggleAutoAcceptTpa(CommandBase.getCommandSenderAsPlayer(iCommandSender).username)) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§a[TPA] 自动接受已开启 §7他人 /tpa 你时直接传送，再输 /tpayes 关闭"));
        } else {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[TPA] 自动接受已关闭"));
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