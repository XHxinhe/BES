package cn.tesseract.bes.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BEServerCommandThread extends Thread {
    BEServer server;

    BEServerCommandThread(BEServer par1DedicatedServer) {
        this.server = par1DedicatedServer;
    }

    public void run() {
        String var2;
        BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!this.server.isServerStopped() && this.server.isServerRunning() && (var2 = var1.readLine()) != null) {
                this.server.addPendingCommand(var2, this.server, false);
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }
}
