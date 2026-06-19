package cn.tesseract.bes.hook;

import cn.tesseract.asm.Hook;
import cn.tesseract.asm.ReturnCondition;
import net.minecraft.LM;
import net.minecraft.bes.CommandException;
import net.minecraft.bes.CommandHandler;
import net.minecraft.bes.CommandTime;
import net.minecraft.bes.CommandServerOp;
import net.minecraft.bes.EntityPlayer;
import net.minecraft.bes.EnumGameType;
import net.minecraft.bes.ICommandSender;
import net.minecraft.bes.Minecraft;
import net.minecraft.bes.Packet70GameEvent;
import net.minecraft.bes.RA;
import net.minecraft.bes.ServerPlayer;
import net.minecraft.bes.WorldType;
import net.minecraft.bes.World;
import net.minecraft.bes.WorldServer;
import net.minecraft.bes.WrongUsageException;
import net.minecraft.bes.server.MinecraftServer;

public class DevCreativeHook {
    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean obf1_b(Minecraft minecraft) {
        return true;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean isCreative(EnumGameType gameType) {
        return gameType == EnumGameType.CREATIVE;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean isSurvivalOrAdventure(EnumGameType gameType) {
        return gameType == EnumGameType.SURVIVAL || gameType == EnumGameType.ADVENTURE;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static EnumGameType getByID(EnumGameType gameType, int id) {
        for (EnumGameType value : EnumGameType.values()) {
            if (value.id == id) {
                return value;
            }
        }
        return EnumGameType.SURVIVAL;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static EnumGameType getByName(EnumGameType gameType, String name) {
        for (EnumGameType value : EnumGameType.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return EnumGameType.SURVIVAL;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void a(LM manager, EnumGameType gameType) {
        manager.c = gameType;
        gameType.configurePlayerCapabilities(manager.b.capabilities);
        manager.b.sendPlayerAbilities();
        syncClientGameMode(manager.b, gameType);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static EnumGameType a(LM manager) {
        return manager.c;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean b(LM manager) {
        return manager.c.isCreative();
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void b(LM manager, EnumGameType gameType) {
        if (manager.c == EnumGameType.NOT_SET) {
            manager.c = gameType;
        }
        manager.a(manager.c);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean obf1_a(CommandHandler handler, EntityPlayer player) {
        if (player == null || player.username == null) {
            return false;
        }
        return hasPrivilegedCommandAccess(player.username);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static boolean canCommandSenderUseCommand(ServerPlayer player, int level, String command) {
        if ("seed".equals(command) && !player.mcServer.isDedicatedServer()) {
            return true;
        }
        if ("tell".equals(command) || "help".equals(command) || "me".equals(command) || "kill".equals(command)) {
            return true;
        }
        return hasPrivilegedCommandAccess(player.username);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void processCommand(CommandServerOp command, ICommandSender sender, String[] args) {
        if (args.length != 1 || args[0].length() <= 0) {
            throw new WrongUsageException("commands.op.usage");
        }
        if (!sender.canCommandSenderUseCommand(3, "op")) {
            throw new CommandException("commands.generic.permission");
        }
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getConfigurationManager() == null) {
            throw new CommandException("commands.generic.exception");
        }
        server.getConfigurationManager().obf1_e.add(args[0].toLowerCase());
        command.notifyAdmins(sender, "commands.op.success", args[0]);
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void processCommand(CommandTime command, ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("commands.time.usage");
        }

        if ("set".equals(args[0])) {
            int targetTime;
            if ("day".equals(args[1])) {
                targetTime = 6000;
            } else if ("night".equals(args[1])) {
                targetTime = 20000;
            } else {
                targetTime = command.parseIntBounded(sender, args[1], 0, 24000);
            }

            MinecraftServer server = MinecraftServer.getServer();
            if (server == null || server.worldServers == null) {
                throw new CommandException("commands.generic.exception");
            }

            for (WorldServer world : server.worldServers) {
                if (world == null) {
                    continue;
                }
                long currentTime = world.obf1_t();
                long dayStart = World.obf1_c(World.obf1_e(currentTime));
                int dayTime = (int) (currentTime - dayStart);
                long newTime = targetTime >= dayTime
                        ? dayStart + targetTime
                        : World.obf1_d(World.obf1_e(currentTime)) + targetTime;
                world.obf1_a(newTime, true);
            }
            command.notifyAdmins(sender, "commands.time.set", args[1]);
            return;
        }

        if ("add".equals(args[0])) {
            int amount = command.parseIntWithMin(sender, args[1], 0);
            MinecraftServer server = MinecraftServer.getServer();
            if (server == null || server.worldServers == null) {
                throw new CommandException("commands.generic.exception");
            }

            for (WorldServer world : server.worldServers) {
                if (world != null) {
                    world.obf1_a(amount, true);
                }
            }
            command.notifyAdmins(sender, "commands.time.added", args[1]);
            return;
        }

        throw new WrongUsageException("commands.time.usage");
    }

    private static boolean hasPrivilegedCommandAccess(String username) {
        if (username == null) {
            return false;
        }
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return false;
        }
        if (server.serverOwner != null && server.serverOwner.equalsIgnoreCase(username)) {
            return true;
        }
        return server.getConfigurationManager() != null
                && server.getConfigurationManager().obf1_e.contains(username.toLowerCase());
    }

    private static void syncClientGameMode(ServerPlayer player, EnumGameType gameType) {
        if (player == null || player.playerNetServerHandler == null || player.worldObj == null || gameType == null) {
            return;
        }
        player.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(3, gameType.id));
        WorldType worldType = player.worldObj.worldInfo.obf1_b.obf1_c;
        player.playerNetServerHandler.sendPacketToPlayer(new RA(
                player.dimension,
                player.worldObj.obf1_E(),
                worldType,
                player.worldObj.getActualHeight(),
                gameType,
                player.worldObj.worldInfo.obf1_b.obf1_D,
                player.worldObj.obf1_t()
        ));
        player.playerNetServerHandler.setPlayerLocation(
                player.posX,
                player.posY,
                player.posZ,
                player.rotationYaw,
                player.rotationPitch
        );
        player.sendPlayerAbilities();
        player.updateHeldItem();
    }
}
