package cn.tesseract.bes.command;

import cn.tesseract.bes.Main;
import net.minecraft.bes.ChatMessageComponent;
import net.minecraft.bes.ICommandSender;

public final class TeleportCommandAccess {
    private TeleportCommandAccess() {
    }

    public static boolean checkEnabled(ICommandSender sender) {
        if (Main.config.teleportCommandsEnabled) {
            return true;
        }
        sender.sendChatToPlayer(new ChatMessageComponent().addText("§c[传送] 传送类指令当前未开启，请联系服务器管理员。"));
        return false;
    }
}
