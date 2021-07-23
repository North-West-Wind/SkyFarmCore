package ml.northwestwind.skyfarm.misc.backup;

import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.packet.message.SBackupDonePacket;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Backups {
    public static final Backups INSTANCE = new Backups();
    public File backupsFolder;
    public BackupStatus doingBackup = BackupStatus.NONE;

    public void init() {
        backupsFolder = new File("./skyfarm/backups");

        try {
            backupsFolder = backupsFolder.getCanonicalFile();
        } catch (Exception ignored) {
        }

        doingBackup = BackupStatus.NONE;
    }

    public boolean run(MinecraftServer server) {
        if (doingBackup.isRunning()) return false;
        for (ServerWorld world : server.getAllLevels()) if (world != null) world.noSave = true;
        doingBackup = BackupStatus.RUNNING;
        server.getPlayerList().saveAll();
        server.saveAllChunks(true, true, true);

        new Thread(() -> {
            try {
                try {
                    FileUtils.cleanDirectory(backupsFolder);
                    createBackup(server);
                    for (ServerWorld world : server.getAllLevels()) if (world != null) world.noSave = false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            doingBackup = BackupStatus.DONE;
            LogManager.getLogger().info("Backup done!");
            SkyblockData data = SkyblockData.get(server.overworld());
            data.setInLoop(true);
            data.setDirty();
            SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SBackupDonePacket());
        }).start();

        return true;
    }

    private boolean createBackup(MinecraftServer server) {
        File src = server.getWorldPath(FolderName.ROOT).toFile();

        try {
            src = src.getCanonicalFile();
        } catch (Exception ignored) {
        }

        boolean success = false;

        try {
            LinkedHashMap<File, String> fileMap = new LinkedHashMap<>();
            for (File file : BackupUtils.listTree(src)) {
                if (file.getName().equals("session.lock") || !file.canRead()) continue;
                String filePath = file.getAbsolutePath();
                fileMap.put(file, src.getName() + File.separator + filePath.substring(src.getAbsolutePath().length() + 1));
            }

            for (Map.Entry<File, String> entry : fileMap.entrySet()) {
                try {
                    File file = entry.getKey();
                    File dst1 = new File(backupsFolder, entry.getValue());
                    BackupUtils.copyFile(file, dst1);
                } catch (Exception ignored) {
                }
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return success;
    }

    public boolean restore(MinecraftServer server) {
        server.getPlayerList().saveAll();
        File dst = server.getWorldPath(FolderName.ROOT).toFile();
        File src = backupsFolder;
        if (!src.isDirectory() || src.list().length < 1) return false;

        try {
            src = src.getCanonicalFile();
            dst = dst.getCanonicalFile();
            for (File f : Objects.requireNonNull(dst.listFiles())) {
                f = f.getCanonicalFile();
                if (f.getName().equals("data")) for (File file : Objects.requireNonNull(f.listFiles())) {
                    file = file.getCanonicalFile();
                    if (file.getName().equals("skyfarm.dat")) continue;
                    else if (file.isDirectory()) FileUtils.deleteDirectory(file);
                    else file.delete();
                }
                else if (f.getName().equals("playerdata") || f.getName().equals("advancements") || f.getName().equals("stats") || f.getName().equals("serverconfig") || f.getName().equals("session.lock")) continue;
                else if (f.isDirectory()) FileUtils.deleteDirectory(f);
                else if (!f.getName().equals("level.dat") && !f.getName().equals("level.dat_old")) f.delete();
            }
            src = new File(src.getAbsolutePath() + File.separator + dst.getName());
            src = src.getCanonicalFile();
        } catch (Exception ignored) {
        }

        boolean success = false;

        for (File s : Objects.requireNonNull(src.listFiles())) {
            try {
                s = s.getCanonicalFile();
                if (s.getName().equals("playerdata") || s.getName().equals("serverconfig") || s.getName().equals("advancements") || s.getName().equals("stats")) continue;
                LinkedHashMap<File, String> fileMap = new LinkedHashMap<>();
                if (s.isDirectory()) for (File file : BackupUtils.listTree(s)) {
                    if (file.getName().equals("session.lock") || !file.canRead()) continue;
                    String filePath = file.getAbsolutePath();
                    fileMap.put(file, s.getName() + File.separator + filePath.substring(s.getAbsolutePath().length() + 1));
                } else fileMap.put(s, s.getName());

                for (Map.Entry<File, String> entry : fileMap.entrySet()) {
                    try {
                        File file = entry.getKey();
                        File dst1 = new File(dst, entry.getValue());
                        BackupUtils.copyFile(file, dst1);
                    } catch (Exception ignored) {
                    }
                }
                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            FileUtils.cleanDirectory(src.getParentFile());
        } catch (IOException ignored) { }

        return success;
    }
}
