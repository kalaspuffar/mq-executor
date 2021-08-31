package org.ea;

public abstract class Reporter {
    public String softwareName;
    public String softwareVersion;
    public String workerName;

    public Reporter() {}

    public void init(String softwareName, String softwareVersion, String workerName) {
        this.softwareName = softwareName;
        this.softwareVersion = softwareVersion;
        this.workerName = workerName;
    }

    public abstract void reportActivity() throws Exception;
    public abstract int getPriority();
}
