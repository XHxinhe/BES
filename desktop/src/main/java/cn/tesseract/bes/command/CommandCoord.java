package cn.tesseract.bes.command;

import net.minecraft.bes.*;

import java.util.Collections;
import java.util.List;

public class CommandCoord extends CommandBase {
    @Override
    public String getCommandName() {
        return "c";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("coord");
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
    public void processCommand(ICommandSender sender, String[] strArr) {
        if (sender instanceof ServerPlayer) {
            ChunkCoordinates pos = sender.getPlayerCoordinates();
            sender.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("当前坐标 X: %s Y: %s Z: %s", pos.posX, pos.posY, pos.posZ)));
            return;
        }
        throw new CommandException("commands.listhome.playerOnly");
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
