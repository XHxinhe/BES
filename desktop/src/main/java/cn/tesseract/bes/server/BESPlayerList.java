package cn.tesseract.bes.server;

import net.minecraft.bes.NBTTagCompound;
import net.minecraft.bes.ServerConfigurationManager;
import net.minecraft.bes.ServerPlayer;
import net.minecraft.bes.server.MinecraftServer;

import java.net.SocketAddress;

public class BESPlayerList extends ServerConfigurationManager {
    public NBTTagCompound hostPlayerData;

    public BESPlayerList(BEServer var1) {
        super(var1);
        this.viewDistance = 10;
        this.whiteListEnforced = false;
    }

    public void writePlayerData(ServerPlayer var1) {
        if (var1.username.equals(super.getServerInstance().serverOwner)) {
            this.hostPlayerData = new NBTTagCompound();
            var1.writeToNBT(this.hostPlayerData);
        }
        super.writePlayerData(var1);
    }

    public String allowUserToConnect(SocketAddress var1, String var2) {
        return var2.equalsIgnoreCase(super.getServerInstance().serverOwner) ? "That name is already taken." : super.allowUserToConnect(var1, var2);
    }

    public NBTTagCompound getHostPlayerData() {
        return this.hostPlayerData;
    }

    public MinecraftServer getServerInstance() {
        return super.getServerInstance();
    }
}
