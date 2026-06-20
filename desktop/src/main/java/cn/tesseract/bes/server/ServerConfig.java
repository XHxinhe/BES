package cn.tesseract.bes.server;

import java.util.ArrayList;
import java.util.Random;

public class ServerConfig {
    public String ip = "0.0.0.0";
    public int port = 25565;
    public long seed = new Random().nextLong();
    public boolean teleportCommandsEnabled = false;
    public boolean whitelistEnabled = false;
    public boolean onlineMode = false;
    public boolean teamDamageEnabled = false;
    public String difficulty = "easy";
    public String worldMode = "survival";
    public String motd = "打破一切！服务器官方群：1085649633";
    public boolean showServerListTime = true;
    public int maxPlayers = 50;
    public int viewDistance = 3;
    public boolean forceGcWhenEmpty = false;

    public void normalize() {
        if (port < 1 || port > 65535) {
            port = 25565;
        }
        if (maxPlayers < 1) {
            maxPlayers = 1;
        } else if (maxPlayers > 50) {
            maxPlayers = 50;
        }
        if (viewDistance < 3) {
            viewDistance = 3;
        } else if (viewDistance > 10) {
            viewDistance = 10;
        }
        if (difficulty == null) {
            difficulty = "easy";
        }

        String difficultyValue = difficulty.trim().toLowerCase();
        if ("hard".equals(difficultyValue) || "difficult".equals(difficultyValue) || "3".equals(difficultyValue) || "困难".equals(difficultyValue)) {
            difficulty = "hard";
        } else {
            difficulty = "easy";
        }

        if (worldMode == null) {
            worldMode = "survival";
        }

        String modeValue = worldMode.trim().toLowerCase();
        if ("hardcore".equals(modeValue) || "extreme".equals(modeValue) || "极限".equals(modeValue)) {
            worldMode = "hardcore";
        } else {
            worldMode = "survival";
        }
    }
}
