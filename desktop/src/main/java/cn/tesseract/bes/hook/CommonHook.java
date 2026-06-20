package cn.tesseract.bes.hook;

import cn.tesseract.asm.Hook;
import cn.tesseract.asm.ReturnCondition;
import cn.tesseract.bes.Main;
import cn.tesseract.bes.command.*;
import net.minecraft.bes.*;
import net.minecraft.bes.server.MinecraftServer;

public class CommonHook {
    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Minecraft getMinecraft(Minecraft c) {
        return Main.dummyMc;
    }

    @Hook
    public static void obf1_a(agq c, Achievement achievement, EntityPlayer entityPlayer) {
        if (!c.obf1_b.obf1_a(achievement) || !entityPlayer.username.equals(c.obf1_b.obf1_K.get(achievement).obf1_a)) {
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("%s 刚刚获得了 %s 成就！", entityPlayer.username, "§a[" + String.valueOf(achievement) + "]§r"));
        }
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE)
    public static boolean c(SaveHandler c, String str) {
        return str.startsWith("sync failed");
    }

    @Hook(targetMethod = "<init>", injector = "exit")
    public static void init(ServerCommandManager c) {
        c.registerCommand(new CommandBack());
        c.registerCommand(new CommandCoord());
        c.registerCommand(new CommandHome());
        c.registerCommand(new CommandSethome());
        c.registerCommand(new CommandTpa());
        c.registerCommand(new CommandTpaccept());
        c.registerCommand(new CommandTpadeny());
        c.registerCommand(new CommandTpayes());
        c.registerCommand(new CommandTeleportCommands());
    }

    @Hook(injector = "exit")
    public static void processCommand(qm c, ICommandSender var1, String[] var2) {
        System.exit(0);
    }
}
