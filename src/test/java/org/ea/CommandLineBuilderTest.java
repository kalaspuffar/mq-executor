package org.ea;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandLineBuilderTest {


    @Test
    public void errorCommand() throws Exception {
        JSONObject message = new JSONObject();
        message.put(MessageConst.COMMAND, "MISSING!");
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder();
        commandLineBuilder.setMessage(message);

        Assert.assertFalse(commandLineBuilder.build());
        Assert.assertEquals(
            "Get the appropriate error message",
            "Command 'MISSING!' missing\n" +
                    "\n" +
                    "Available commands:\n" +
                    "echo - Command to echo messages\n",
            commandLineBuilder.getErrorMessage()
        );
    }

    @Test
    public void errorParam() throws Exception {
        JSONObject message = new JSONObject();
        message.put(MessageConst.COMMAND, "echo");
        CommandLineBuilder commandLineBuilder = new CommandLineBuilder();
        commandLineBuilder.setMessage(message);

        Assert.assertFalse(commandLineBuilder.build());
        Assert.assertEquals(
                "Get the appropriate error message",
                "Argument 'message' missing\n" +
                        "\n" +
                        "Echo is a command to type text on the screen.\n" +
                        "\n" +
                        "[message]   Message you want to display on screen or returned in the response.\n",
                commandLineBuilder.getErrorMessage()
        );
    }

    @Test
    public void echoCommand() throws Exception {
        JSONObject message = new JSONObject();
        message.put(MessageConst.COMMAND, "echo");
        message.put("message", "Hello world");

        CommandLineBuilder commandLineBuilder = new CommandLineBuilder();
        commandLineBuilder.setMessage(message);

        Assert.assertTrue(
            "Should be able to build command",
            commandLineBuilder.build()
        );
        Assert.assertEquals(
            "Testing echo hello world",
            "echo \"Hello world\"\n",
                commandLineBuilder.getCommand());
    }
}
