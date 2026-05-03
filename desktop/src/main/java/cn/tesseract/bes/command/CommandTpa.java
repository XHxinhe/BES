package cn.tesseract.bes.command;

import net.minecraft.bes.*;
import net.minecraft.bes.server.MinecraftServer;

import java.util.List;

public class CommandTpa extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpa";
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
        if (strArr.length < 1) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c用法: /tpa <玩家名>"));
            return;
        }
        ServerPlayer commandSenderAsPlayer = CommandBase.getCommandSenderAsPlayer(iCommandSender);
        String str = strArr[0];
        ServerPlayer playerForUsername = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(str);
        if (playerForUsername == null) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[TPA] 找不到玩家: " + str));
            return;
        }
        if (playerForUsername == commandSenderAsPlayer) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[TPA] 不能向自己发送申请"));
            return;
        }
        if (HomeData.isAutoAcceptTpa(playerForUsername.username)) {
            HomeData.setLastPosition(commandSenderAsPlayer.username, commandSenderAsPlayer.posX, commandSenderAsPlayer.posY, commandSenderAsPlayer.posZ, commandSenderAsPlayer.dimension);
            if (commandSenderAsPlayer.dimension != playerForUsername.dimension) {
                commandSenderAsPlayer.travelToDimension(playerForUsername.dimension);
            }
            commandSenderAsPlayer.obf1_k(playerForUsername.posX, playerForUsername.posY, playerForUsername.posZ);
            commandSenderAsPlayer.sendChatToPlayer(new ChatMessageComponent().addText("§a[TPA] " + playerForUsername.username + " 已开启自动接受，已直接传送"));
            playerForUsername.sendChatToPlayer(new ChatMessageComponent().addText("§7[TPA] " + commandSenderAsPlayer.username + " 已传送到你身边"));
            return;
        }
        HomeData.setTpaRequest(playerForUsername.username, commandSenderAsPlayer.username);
        playerForUsername.sendChatToPlayer(new ChatMessageComponent().addText("§e[TPA] " + commandSenderAsPlayer.username + " 申请传送到你身边，输入 §f/tpaccept §e接受或 §f/tpdeny §e拒绝"));
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§e[TPA] 已向 " + playerForUsername.username + " 发送传送申请"));
    }

    @Override 
    public List<String> obf1_a(String[] strArr) {
        if (strArr.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(strArr, MinecraftServer.getServer().getConfigurationManager().obf1_b());
        }
        return null;
    }

    @Override 
    public boolean isUsernameIndex(String[] strArr, int i) {
        return i == 0;
    }

    @Override
    public int compareTo(ICommand iCommand) {
        return getCommandName().compareTo(iCommand.getCommandName());
    }
}