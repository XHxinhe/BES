package cn.tesseract.bes.command;

import net.minecraft.bes.*;

import java.util.List;


public class CommandHome extends CommandBase {
    private static void sendList(ICommandSender iCommandSender, String str, String str2) {
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText(str2));
        StringBuilder sb = new StringBuilder("§7  ");
        List<String> listListHomes = HomeData.listHomes(str);
        for (int i = 0; i < listListHomes.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("§f").append(listListHomes.get(i));
        }
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText(sb.toString()));
    }

    @Override
    public String getCommandName() {
        return HomeData.DEFAULT_HOME;
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
        if (!TeleportCommandAccess.checkEnabled(iCommandSender)) {
            return;
        }
        double[] home;
        String str;
        ServerPlayer commandSenderAsPlayer = CommandBase.getCommandSenderAsPlayer(iCommandSender);
        int iHomeCount = HomeData.homeCount(commandSenderAsPlayer.username);
        if (iHomeCount == 0) {
            iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§c[Home] 尚未设置，请先输入 /sethome [名字]"));
            return;
        }
        if (strArr.length >= 1 && !strArr[0].isEmpty()) {
            str = strArr[0];
            home = HomeData.getHome(commandSenderAsPlayer.username, str);
            if (home == null) {
                sendList(iCommandSender, commandSenderAsPlayer.username, "§c[Home] 未找到名为 §f" + str + "§c 的 home。当前拥有:");
                return;
            }
        } else if (iHomeCount == 1) {
            home = HomeData.getOnlyHome(commandSenderAsPlayer.username);
            str = HomeData.listHomes(commandSenderAsPlayer.username).get(0);
        } else {
            home = HomeData.getHome(commandSenderAsPlayer.username, HomeData.DEFAULT_HOME);
            if (home == null) {
                sendList(iCommandSender, commandSenderAsPlayer.username, "§e[Home] 你有多个 home，请指定名字 §7(/home <名字>)§e:");
                return;
            }
            str = HomeData.DEFAULT_HOME;
        }
        HomeData.setLastPosition(commandSenderAsPlayer.username, commandSenderAsPlayer.posX, commandSenderAsPlayer.posY, commandSenderAsPlayer.posZ, commandSenderAsPlayer.dimension);
        int i = (int) home[3];
        if (commandSenderAsPlayer.dimension != i) {
            commandSenderAsPlayer.travelToDimension(i);
        }
        commandSenderAsPlayer.obf1_k(home[0], home[1], home[2]);
        iCommandSender.sendChatToPlayer(new ChatMessageComponent().addText("§a[Home] 已传送到 §f" + str + "§a！"));
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
