package cn.tesseract.bes;

import cn.tesseract.bes.server.BEServer;
import cn.tesseract.bes.server.ServerConfig;
import net.minecraft.bes.*;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Main {
    public static final Minecraft dummyMc = new Minecraft();
    public static final File mcDataDir = new File(System.getProperty("user.dir"));
    public static ServerConfig config = Config.loadOrCreate();
    public static BEServer server;
    public static boolean started;

    static {
        dummyMc.obf1_O = mcDataDir;
        dummyMc.obf1_W = true;
        dummyMc.obf1_Y = new DummyResourceManager();
        dummyMc.obf1_ab = new LanguageManager(dummyMc.aQ, "zh_CN");
        StatList.nopInit();
    }

    public static void main(String[] var0) {
        startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                server.stopServer();
            }
        }));
    }

    public static void startServer() {
        server = new BEServer(config.seed, config.ip, config.port);
        server.obf1_E.start();
        server.setMOTD(config.motd);
    }

    public static class DummyResourceManager implements ReloadableResourceManager {
        public void reloadResources(List<ResourcePack> list) {
        }

        public void registerReloadListener(ResourceManagerReloadListener resourceManagerReloadListener) {
        }

        public Set<String> getResourceDomains() {
            return Collections.emptySet();
        }

        public Resource getResource(ResourceLocation resourceLocation) {
            return new DummyResource(resourceLocation);
        }

        public List<Resource> getAllResources(ResourceLocation resourceLocation) {
            return Collections.emptyList();
        }
    }

    public static class DummyResource implements Resource {
        final ResourceLocation location;

        public DummyResource(ResourceLocation location) {
            this.location = location;
        }

        public InputStream getInputStream() {
            return Main.class.getResourceAsStream("/assets/" + location.resourceDomain + "/" + location.resourcePath);
        }

        public boolean hasMetadata() {
            return false;
        }

        public k getMetadata(String s) {
            return null;
        }
    }
}
