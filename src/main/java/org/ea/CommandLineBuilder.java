package org.ea;

import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineBuilder {
    private Pattern argumentMatcher = Pattern.compile("\\[[a-z_]+\\]");
    private Map<String, CommandFile> commands = new HashMap<>();
    private JSONObject message;
    private String resultCommand;
    private StringBuilder errorMessage;
    private StringBuilder headerConfig;

    public CommandLineBuilder() throws Exception {
        File dir = new File("commands");
        for (File f : dir.listFiles()) {
            commands.put(
                f.getName().substring(0, f.getName().length() - 4),
                new CommandFile(f)
            );
        }
    }

    public void setMessage(JSONObject message) {
        this.message = message;
    }

    public boolean build() throws Exception {
        String cmd = (String)message.get(MessageConst.COMMAND);
        if (!commands.containsKey(cmd)) {
            errorMessage = new StringBuilder();
            errorMessage.append("Command '");
            errorMessage.append(cmd);
            errorMessage.append("' missing\n\n");
            errorMessage.append("Available commands:\n");
            for (CommandFile cf : commands.values()) {
                errorMessage.append(cf.getShortDescription());
            }
            return false;
        }

        CommandFile cf = commands.get(cmd);
        headerConfig = cf.getHeaderConfig();
        resultCommand = cf.getCommand();
        Matcher m = argumentMatcher.matcher(resultCommand);
        while (m.find()) {
            String replace = m.group(0);
            String arg = replace.substring(1, replace.length() - 1);

            if(!message.containsKey(arg)) {
                errorMessage = new StringBuilder();
                errorMessage.append("Argument '");
                errorMessage.append(arg);
                errorMessage.append("' missing\n\n");
                errorMessage.append(cf.getDescription());
                return false;
            }

            if (message.get(arg) instanceof String) {
                resultCommand = resultCommand.replace(replace, (String)message.get(arg));
            } else if (message.get(arg) instanceof Integer) {
                resultCommand = resultCommand.replace(replace, Integer.toString((Integer) message.get(arg)));
            } else if (message.get(arg) instanceof Long) {
                resultCommand = resultCommand.replace(replace, Long.toString((Long) message.get(arg)));
            } else if (message.get(arg) instanceof Float) {
                resultCommand = resultCommand.replace(replace, Double.toString((Float) message.get(arg)));
            } else if (message.get(arg) instanceof Double) {
                resultCommand = resultCommand.replace(replace, Double.toString((Double) message.get(arg)));
            } else if (message.get(arg) instanceof Boolean) {
                resultCommand = resultCommand.replace(replace, Boolean.toString((Boolean) message.get(arg)));
            }
        }
        return true;
    }

    public String getCommand() {
        return resultCommand;
    }

    public String getHeaderConfig() {
        return headerConfig.toString();
    }

    public String getErrorMessage() {
        return errorMessage.toString();
    }
}
