package org.ea;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MessageTest {

    @Test
    public void testDirectoryMessage() throws Exception {
        JSONObject config = new JSONObject();
        config.put(ConfigConst.WORKER_NAME, "Test worker");

        MQExecutorConsumer consumer = new MQExecutorConsumer(null, config);
        StringBuilder response = new StringBuilder();

        File workDirectory = new File("/tmp/MQTest-" + System.currentTimeMillis());
        workDirectory.mkdir();
        JSONObject message = new JSONObject();
        message.put("command", "list_directory");
        message.put("directory", workDirectory.getAbsolutePath());
        consumer.handleMessage(response, workDirectory, message);
        Util.deleteDirectory(workDirectory);

        Assert.assertTrue("Exit code should be 0 for this command.", response.toString().contains("EXIT CODE: 0"));
    }
}
