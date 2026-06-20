package cn.tesseract.bes.server;

import java.util.ArrayList;
import java.util.Random;

public class ServerConfig {
    public String ip = "0.0.0.0";
    public int port = 25565;
    public long seed = new Random().nextLong();
    public boolean teleportCommandsEnabled = false;
    public String difficulty = "easy";
    public String motd = "打破一切！服务器官方群：1085649633";

    public void normalize() {
        if (port < 1 || port > 65535) {
            port = 25565;
        }
        if (difficulty == null) {
            difficulty = "easy";
            return;
        }

        String value = difficulty.trim().toLowerCase();
        if ("hard".equals(value) || "difficult".equals(value) || "3".equals(value) || "困难".equals(value)) {
            difficulty = "hard";
        } else {
            difficulty = "easy";
        }
    }
}
