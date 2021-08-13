package org.ea;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MQExecutorConsumer extends DefaultConsumer {
    private static final Logger logger = Logger.getLogger(MQExecutorConsumer.class);

    private final JSONObject config;
    private final Channel channel;
    private int workingUnits = 0;

    public MQExecutorConsumer(Channel channel, JSONObject config) {
        super(channel);
        this.channel = channel;
        this.config = config;
    }

    @Override
    public void handleDelivery(
            String consumerTag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body
    ) throws IOException {
        workingUnits++;

        String message;
        StringBuilder response = new StringBuilder();

        File workDirectory = new File("/tmp/MQExecutor-" + System.currentTimeMillis());


        try {
            workDirectory.mkdir();

            message = new String(body, "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            channel.basicAck(envelope.getDeliveryTag(), false);

            JSONObject jobObject = (JSONObject) JSONValue.parse(message);

            handleMessage(response, workDirectory, jobObject);

        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            response.append(sw);
        } finally {
            channel.basicPublish("", (String) config.get(ConfigConst.RESPONSE_QUEUE_NAME), null, response.toString().getBytes());
            System.out.println(" [x] Sent message:");
            System.out.println(response);

            Util.deleteDirectory(workDirectory);
            workingUnits--;
        }
    }

    private int runCommand(CommandLineBuilder clb, StringBuilder stdOutAndErr, File workDirectory) throws Exception {
        boolean windows = System.getProperty("os.name").startsWith("Windows");

        ProcessBuilder builder = new ProcessBuilder(windows ? "cmd.exe" : "/bin/sh");
        Process process = builder.start();

        BufferedWriter cmdLine = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        String errorHandling = windows ? "IF %ERRORLEVEL% NEQ 0 (EXIT /B %ERRORLEVEL%)" : "[ $? -eq 0 ] || exit $?";

        for (String cmd : clb.getCommand().split("\n")) {
            cmdLine.write(cmd);
            cmdLine.newLine();
            cmdLine.write(errorHandling);
            cmdLine.newLine();
            cmdLine.flush();
        }

        cmdLine.write(windows ? "EXIT /B 0" : "exit 0");
        cmdLine.newLine();
        cmdLine.flush();

        process.waitFor(1, TimeUnit.HOURS);
        printStream(stdOutAndErr, process.getErrorStream());
        printStream(stdOutAndErr, process.getInputStream());
        return process.exitValue();
    }

    protected void handleMessage(StringBuilder response, File workDirectory, JSONObject message) throws Exception {
        int exitValue = 0;
        StringBuilder stdOutAndErr = new StringBuilder();

        CommandLineBuilder clb = new CommandLineBuilder();
        clb.setMessage(message);

        Instant fileStart = Instant.now();
        if (!clb.build()) {
            exitValue = -1;
            stdOutAndErr.append(clb.getErrorMessage());
        } else {
            exitValue = runCommand(clb, stdOutAndErr, workDirectory);
        }

        Duration jobDuration = Duration.between(fileStart, Instant.now());
        String duration = LocalTime.MIDNIGHT.plus(jobDuration).format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        response.append("=====================================================\n");
        response.append("Running version " + MQExecutor.class.getPackage().getImplementationVersion() + "\n");
        response.append("Currently doing work on " + config.get(ConfigConst.WORKER_NAME) + "\n");
        response.append("MESSAGE: " + message.toString() + "\n");
        response.append("EXIT CODE: " + exitValue + "\n");
        response.append("TIME SPENT: " + duration + "\n");
        response.append("=====================================================\n");
        response.append(stdOutAndErr);
    }

    private void printStream(StringBuilder response, InputStream inputStream) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
    }

    public boolean isWorking() {
        return workingUnits > 0;
    }
}
