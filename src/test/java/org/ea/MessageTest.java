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
    public void testStringMessage() throws Exception {
        JSONObject config = new JSONObject();
        config.put(ConfigConst.WORKER_NAME, "Test worker");
        MQExecutorConsumer consumer = new MQExecutorConsumer(null, config);
        StringBuilder response = new StringBuilder();

        File workDirectory = new File("/tmp/MQTest-" + System.currentTimeMillis());
        workDirectory.mkdir();
        JSONObject message = new JSONObject();
        message.put("command", "echo");
        message.put("message", "test");
        consumer.handleMessage(response, workDirectory, message);
        Util.deleteDirectory(workDirectory);

        String before = response.toString();
        int firstIndex = before.indexOf("=====================================================");
        int index = before.indexOf("=====================================================", firstIndex + "=====================================================".length());
        String text = before.substring(index + "=====================================================".length()).trim();
        Assert.assertTrue("text should match", text.equals("test"));
    }

    @Test
    public void testIntMessage() throws Exception {
        JSONObject config = new JSONObject();
        config.put(ConfigConst.WORKER_NAME, "Test worker");
        MQExecutorConsumer consumer = new MQExecutorConsumer(null, config);
        StringBuilder response = new StringBuilder();

        File workDirectory = new File("/tmp/MQTest-" + System.currentTimeMillis());
        workDirectory.mkdir();
        JSONObject message = new JSONObject();
        message.put("command", "echo");
        message.put("message", 5);
        consumer.handleMessage(response, workDirectory, message);
        Util.deleteDirectory(workDirectory);

        String before = response.toString();
        int firstIndex = before.indexOf("=====================================================");
        int index = before.indexOf("=====================================================", firstIndex + "=====================================================".length());
        String text = before.substring(index + "=====================================================".length()).trim();
        Assert.assertTrue("text should match", text.equals("5"));
    }

    @Test
    public void testFloatMessage() throws Exception {
        JSONObject config = new JSONObject();
        config.put(ConfigConst.WORKER_NAME, "Test worker");
        MQExecutorConsumer consumer = new MQExecutorConsumer(null, config);
        StringBuilder response = new StringBuilder();

        File workDirectory = new File("/tmp/MQTest-" + System.currentTimeMillis());
        workDirectory.mkdir();
        JSONObject message = new JSONObject();
        message.put("command", "echo");
        message.put("message", 3.4);
        consumer.handleMessage(response, workDirectory, message);
        Util.deleteDirectory(workDirectory);

        String before = response.toString();
        int firstIndex = before.indexOf("=====================================================");
        int index = before.indexOf("=====================================================", firstIndex + "=====================================================".length());
        String text = before.substring(index + "=====================================================".length()).trim();
        Assert.assertTrue("text should match", text.equals("3.4"));
    }


    @Test
    public void testBooleanMessage() throws Exception {
        JSONObject config = new JSONObject();
        config.put(ConfigConst.WORKER_NAME, "Test worker");
        MQExecutorConsumer consumer = new MQExecutorConsumer(null, config);
        StringBuilder response = new StringBuilder();

        File workDirectory = new File("/tmp/MQTest-" + System.currentTimeMillis());
        workDirectory.mkdir();
        JSONObject message = new JSONObject();
        message.put("command", "echo");
        message.put("message", true);
        consumer.handleMessage(response, workDirectory, message);
        Util.deleteDirectory(workDirectory);

        String before = response.toString();
        int firstIndex = before.indexOf("=====================================================");
        int index = before.indexOf("=====================================================", firstIndex + "=====================================================".length());
        String text = before.substring(index + "=====================================================".length()).trim();
        Assert.assertTrue("text should match", text.equals("true"));
    }

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
        Assert.assertTrue("Header should have info", response.toString().contains("There is 1 file and 2 dir"));
    }
}
