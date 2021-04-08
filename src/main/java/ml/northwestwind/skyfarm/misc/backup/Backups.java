package ml.northwestwind.skyfarm.misc.backup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

import java.io.File;
import java.util.*;

public class Backups {
    public static final Backups INSTANCE = new Backups();
    public final List<Backup> backups = new ArrayList<>();
    public File backupsFolder;
    public BackupStatus doingBackup = BackupStatus.NONE;
    public boolean printFiles = false;
    public int totalFiles = 0;

    public void init() {
        backupsFolder = new File("./skyfarm/backups");

        try {
            backupsFolder = backupsFolder.getCanonicalFile();
        } catch (Exception ignored) { }

        doingBackup = BackupStatus.NONE;
        backups.clear();

        File file = new File(backupsFolder, "backups.json");
        JsonElement element = BackupUtils.readJson(file);
        if (element != null && element.isJsonArray()) try {
            for (JsonElement e : element.getAsJsonArray()) {
                JsonObject json = e.getAsJsonObject();
                if (!json.has("size"))
                    json.addProperty("size", BackupUtils.getSize(new File(backupsFolder, json.get("file").getAsString())));
                backups.add(new Backup(json));
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public boolean run(MinecraftServer server) {
        if (doingBackup.isRunningOrDone()) return false;
        for (ServerWorld world : server.getAllLevels()) if (world != null) world.noSave = true;
        doingBackup = BackupStatus.RUNNING;
        server.getPlayerList().saveAll();

        new Thread(() -> {
            try {
                try {
                    createBackup(server);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            doingBackup = BackupStatus.DONE;
        }).start();

        return true;
    }

    private void createBackup(MinecraftServer server) {
        File src = server.getWorldPath(FolderName.ROOT).toFile();

        try {
            src = src.getCanonicalFile();
        } catch (Exception ignored) {
        }

        Calendar time = Calendar.getInstance();
        File dstFile;
        boolean success = false;
        StringBuilder out = new StringBuilder();
        out.append(System.currentTimeMillis());

        long fileSize = 0L;

        try {
            LinkedHashMap<File, String> fileMap = new LinkedHashMap<>();
            for (File file : BackupUtils.listTree(src)) {
                if (file.getName().equals("session.lock") || !file.canRead()) continue;
                String filePath = file.getAbsolutePath();
                fileMap.put(file, src.getName() + File.separator + filePath.substring(src.getAbsolutePath().length() + 1));
            }

            for (Map.Entry<File, String> entry : fileMap.entrySet()) fileSize += BackupUtils.getSize(entry.getKey());

            if (!backups.isEmpty()) backups.sort(null);
            totalFiles = fileMap.size();
            printFiles = true;
            dstFile = new File(backupsFolder, out.toString());
            dstFile.mkdirs();

            for (Map.Entry<File, String> entry : fileMap.entrySet()) {
                try {
                    File file = entry.getKey();
                    File dst1 = new File(dstFile, entry.getValue());
                    BackupUtils.copyFile(file, dst1);
                } catch (Exception ignored) { }
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        printFiles = false;

        Backup backup = new Backup(time.getTimeInMillis(), out.toString().replace('\\', '/'), getLastIndex() + 1, success, fileSize);
        backups.add(backup);

        JsonArray array = new JsonArray();

        for (Backup backup1 : backups) {
            array.add(backup1.toJsonObject());
        }

        BackupUtils.toJson(new File(server.getServerDirectory(), "local/ftbutilities/backups.json"), array, true);
    }

    private int getLastIndex() {
        int i = 0;

        for (Backup b : backups) {
            i = Math.max(i, b.index);
        }

        return i;
    }
}
