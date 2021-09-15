package org.ea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CommandFile {
    private final StringBuilder command = new StringBuilder();
    private final StringBuilder description = new StringBuilder();
    private final StringBuilder shortDescription = new StringBuilder();
    private final StringBuilder headerConfig = new StringBuilder();

    private final int TYPE_COMMAND = 1;
    private final int TYPE_DESCRIPTION = 2;
    private final int TYPE_SHORT_DESCRIPTION = 3;
    private final int HEADER_CONFIG = 4;

    public CommandFile(File cmd) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(cmd));

        int type = 0;

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.trim().equals("===COMMAND===")) {
                type = TYPE_COMMAND;
                continue;
            } else if (line.trim().equals("===DESCRIPTION===")) {
                type = TYPE_DESCRIPTION;
                continue;
            } else if (line.trim().equals("===SHORT_DESCRIPTION===")) {
                type = TYPE_SHORT_DESCRIPTION;
                continue;
            } else if (line.trim().equals("===HEADER_CONFIG===")) {
                type = HEADER_CONFIG;
                continue;
            }


            if (type == TYPE_COMMAND) {
                command.append(line);
                command.append("\n");
            } else if (type == TYPE_DESCRIPTION) {
                description.append(line);
                description.append("\n");
            } else if (type == TYPE_SHORT_DESCRIPTION) {
                shortDescription.append(line);
                shortDescription.append("\n");
            } else if (type == HEADER_CONFIG) {
                headerConfig.append(line);
                headerConfig.append("\n");
            }
        }
        bufferedReader.close();
    }

    public String getCommand() {
        return command.toString();
    }

    public String getShortDescription() {
        return shortDescription.toString();
    }

    public String getDescription() {
        return description.toString();
    }

    public StringBuilder getHeaderConfig() {
        return headerConfig;
    }
}
