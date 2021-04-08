package ml.northwestwind.skyfarm.misc.backup;

public enum BackupStatus {
    NONE,
    RUNNING,
    DONE;

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isDone() {
        return this == DONE;
    }

    public boolean isRunningOrDone() {
        return isRunning() || isDone();
    }
}