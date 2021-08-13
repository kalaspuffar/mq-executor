package org.ea;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class CommandFileTest {
    @Test
    public void echoCommand() throws Exception {
        CommandFile cf = new CommandFile(new File("commands/echo.cmd"));
        Assert.assertEquals("echo \"[message]\"\n", cf.getCommand());
        Assert.assertEquals("Echo is a command to type text on the screen.\n" +
                "\n" +
                "[message]   Message you want to display on screen or returned in the response.\n", cf.getDescription());
    }
}
