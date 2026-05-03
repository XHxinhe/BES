package cn.tesseract.bes.server;

import net.minecraft.bes.ICommandSender;

public class ServerCommand {
    public final String command;
    public final ICommandSender sender;
    public boolean permission_override;

    public ServerCommand(String par1Str, ICommandSender par2ICommandSender, boolean permission_override) {
        this.command = par1Str;
        this.sender = par2ICommandSender;
        this.permission_override = permission_override;
    }
}
