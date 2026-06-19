package cn.tesseract.bes.command;

import net.minecraft.bes.*;
import net.minecraft.bes.server.MinecraftServer;

import java.util.List;

public class CommandTpaccept extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpaccept";
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
            return;
        }
        HomeData.clearTpaRequest(commandSenderAsPlayer.username);
        ServerPlayer playerForUsername = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(tpaRequest);
        if (playerForUsername == null) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[TPA] 申请人已离线"));
            return;
        }
        HomeData.setLastPosition(tpaRequest, playerForUsername.posX, playerForUsername.posY, playerForUsername.posZ, playerForUsername.dimension);
        if (playerForUsername.dimension != commandSenderAsPlayer.dimension) {
            playerForUsername.travelToDimension(commandSenderAsPlayer.dimension);
        }
        playerForUsername.obf1_k(commandSenderAsPlayer.posX, commandSenderAsPlayer.posY, commandSenderAsPlayer.posZ);
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§a[TPA] 已接受 " + tpaRequest + " 的申请"));
        playerForUsername.sendChatToPlayer(new ChatMessageComponent().addText("§a[TPA] 已传送到 " + commandSenderAsPlayer.username + " 的位置！"));
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