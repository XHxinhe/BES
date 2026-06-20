package cn.tesseract.bes.server;

import cn.tesseract.bes.Main;
import net.minecraft.bes.NBTTagCompound;
import net.minecraft.bes.ServerConfigurationManager;
import net.minecraft.bes.ServerPlayer;
import net.minecraft.bes.server.MinecraftServer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BESPlayerList extends ServerConfigurationManager {
    private static final Path WHITELIST_FILE = Paths.get("white-list.txt");
    public NBTTagCompound hostPlayerData;

    public BESPlayerList(BEServer var1) {
        super(var1);
        this.maxPlayers = Main.config.maxPlayers;
        this.viewDistance = Main.config.viewDistance;
        this.whiteListEnforced = Main.config.whitelistEnabled;
        var1.getLogAgent().obf1_a("Using max players " + this.maxPlayers + ", view distance " + this.viewDistance, new Object[0]);
        loadWhiteList();
    }

    public void writePlayerData(ServerPlayer var1) {
        if (var1.username.equals(super.getServerInstance().serverOwner)) {
            this.hostPlayerData = new NBTTagCompound();
            var1.writeToNBT(this.hostPlayerData);
        }
        super.writePlayerData(var1);
    }

    public String allowUserToConnect(SocketAddress var1, String var2) {
        this.whiteListEnforced = Main.config.whitelistEnabled;
        return var2.equalsIgnoreCase(super.getServerInstance().serverOwner) ? "That name is already taken." : super.allowUserToConnect(var1, var2);
    }

    public void addToWhiteList(String username) {
        super.addToWhiteList(normalizeName(username));
        saveWhiteList();
    }

    public void removeFromWhitelist(String username) {
        super.removeFromWhitelist(normalizeName(username));
        saveWhiteList();
    }

    public void loadWhiteList() {
        this.obf1_f.clear();
        if (!Files.exists(WHITELIST_FILE)) {
            saveWhiteList();
            return;
        }

        try {
            for (String line : Files.readAllLines(WHITELIST_FILE, StandardCharsets.UTF_8)) {
                String username = normalizeName(line);
                if (!username.isEmpty() && !username.startsWith("#")) {
                    super.addToWhiteList(username);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public NBTTagCompound getHostPlayerData() {
        return this.hostPlayerData;
    }

    public MinecraftServer getServerInstance() {
        return super.getServerInstance();
    }

    private void saveWhiteList() {
        try {
            Files.write(WHITELIST_FILE, this.obf1_f, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private static String normalizeName(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }
}
