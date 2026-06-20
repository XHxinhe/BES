package cn.tesseract.bes.server;

import cn.tesseract.bes.Main;
import net.minecraft.bes.*;
import net.minecraft.bes.server.MinecraftServer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BEServer extends MinecraftServer {
    private final List<ServerCommand> pendingCommandList;
    public agD theWorldSettings;
    public Ov obf1_F;
    public BESListenThread theServerListeningThread;
    public Ov obf1_M;

    public BEServer(long seed, String ip, int port) {
        super(Main.mcDataDir);
        this.pendingCommandList = Collections.synchronizedList(new ArrayList<>());
        this.obf1_F = new Ow("Minecraft-Server", " [SERVER]", new File(Main.mcDataDir, "log/output-server.log").getAbsolutePath());
        this.obf1_M = new Ow("Suspicious-Log", null, new File(Main.mcDataDir, "log/suspicious.log").getAbsolutePath());
        Minecraft.obf1_c = this.obf1_F;
        Minecraft.obf1_d = this.obf1_F;
        setServerOwner("Tesseract");
        setFolderName("world");
        setWorldName("world");
        setDemo(false);
        canCreateBonusChest(false);
        setBuildLimit(256);
        setConfigurationManager(new BESPlayerList(this));
        this.theWorldSettings = new agD(seed, EnumGameType.SURVIVAL, true, false, WorldType.parseWorldType("largeBiomes"), false);
        this.theWorldSettings.obf1_c = getConfiguredDifficulty();
        try {
            this.theServerListeningThread = new BESListenThread(this, InetAddress.getByName(ip), port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAllWorlds(String var1, String var2, long var3, WorldType var5, String var6) {
        convertMapIfNeeded(var1);
        this.worldServers = new WorldServer[4];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler var7 = getActiveAnvilConverter().getSaveLoader(var1, true);
        for (int var8 = 0; var8 < this.worldServers.length; var8++) {
            byte var4 = (byte) obf1_f(var8);
            if (var8 == 0) {
                if (isDemo()) {
                    this.worldServers[var8] = new ahA(this, var7, var2, var4, this.theProfiler, this.obf1_F);
                } else {
                    this.worldServers[var8] = new WorldServer(this, var7, var2, var4, this.theWorldSettings, this.theProfiler, this.obf1_F);
                }
            } else {
                this.worldServers[var8] = new agC(this, var7, var2, var4, this.theWorldSettings, this.worldServers[0], this.theProfiler, this.obf1_F);
            }
            this.worldServers[var8].addWorldAccess(new ags(this, this.worldServers[var8]));
            xi.obf1_a(this.worldServers[var8]);
            getConfigurationManager().setPlayerManager(this.worldServers);
        }
        initialWorldChunkLoad();
    }

    public boolean startServer() {
        BEServerCommandThread var1 = new BEServerCommandThread(this);
        var1.setDaemon(true);
        var1.start();
        this.obf1_F.obf1_a("Starting Break Everything Server", new Object[0]);
        obf1_d(false);
        obf1_e(true);
        obf1_f(true);
        obf1_g(true);
        setAllowFlight(true);
        this.obf1_F.obf1_a("Generating keypair", new Object[0]);
        setKeyPair(CryptManager.createNewKeyPair());
        loadAllWorlds(getFolderName(), getWorldName(), this.theWorldSettings.obf1_a, this.theWorldSettings.obf1_f, this.theWorldSettings.obf1_h);
        Sc.obf1_a();
        Main.started = true;
        getLogAgent().obf1_a("Done!", new Object[0]);
        return true;
    }

    public void tick() {
        super.tick();
        if (this.tickCounter % 1000 == 0) {
            getConfigurationManager().loadWhiteList();
            if (getConfigurationManager().getCurrentPlayerCount() == 0) {
                System.gc();
            }
        }
    }

    public void obf1_ab() {
        if (!obf1_D) {
            this.obf1_F.obf1_a("Saving all players and worlds...", new Object[0]);
            getConfigurationManager().saveAllPlayerData();
            obf1_a(false);
            Xe.obf1_a(this);
        }
    }

    public boolean canStructuresSpawn() {
        return true;
    }

    public EnumGameType getGameType() {
        return this.theWorldSettings.obf1_b;
    }

    public afZ obf1_f() {
        return getConfiguredDifficulty();
    }

    public boolean isHardcore() {
        return this.theWorldSettings.obf1_e;
    }

    public File getDataDirectory() {
        return Main.mcDataDir;
    }

    public boolean isDedicatedServer() {
        return true;
    }

    public void finalTick(CrashReport var1) {
        while (isServerRunning()) {
            executePendingCommands();
            try {
                Thread.sleep(10L);
            } catch (InterruptedException var3) {
                var3.printStackTrace();
            }
        }
    }

    public CrashReport addServerInfoToCrashReport(CrashReport var1) {
        return super.addServerInfoToCrashReport(var1);
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper var1) {
        super.addServerStatsToSnooper(var1);
    }

    public boolean isSnooperEnabled() {
        return false;
    }

    public int obf1_a(EnumGameType var1, boolean var2, int var3) {
        return 0;
    }

    public String getMOTD() {
        return this.motd;
    }

    public Ov getLogAgent() {
        return this.obf1_F;
    }

    public Ov obf1_R() {
        return this.obf1_M;
    }

    public void stopServer() {
        super.stopServer();
        this.obf1_h = true;
        getLogAgent().obf1_a("Stopped!");
    }

    public void updateTimeLightAndEntities() {
        super.updateTimeLightAndEntities();
        executePendingCommands();
    }

    public void addPendingCommand(String par1Str, ICommandSender par2ICommandSender, boolean permission_override) {
        this.pendingCommandList.add(new ServerCommand(par1Str, par2ICommandSender, permission_override));
    }

    public void executePendingCommands() {
        while (!this.pendingCommandList.isEmpty()) {
            ServerCommand var1 = this.pendingCommandList.remove(0);
            this.commandManager.obf1_a(var1.sender, var1.command, var1.permission_override);
        }
    }

    public boolean isServerStopped() {
        return this.obf1_h;
    }

    public boolean isCommandBlockEnabled() {
        return true;
    }

    public boolean obf1_h() {
        return this.theWorldSettings.obf1_i;
    }

    public boolean obf1_i() {
        return this.theWorldSettings.obf1_j;
    }

    public NetworkListenThread getNetworkThread() {
        return this.theServerListeningThread;
    }

    public boolean obf1_V() {
        return false;
    }

    private static afZ getConfiguredDifficulty() {
        return "hard".equals(Main.config.difficulty) ? afZ.obf1_b : afZ.obf1_a;
    }
}
