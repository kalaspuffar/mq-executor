package org.ea;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class MQExecutor {
    private static final Logger logger = Logger.getLogger(MQExecutor.class);

    public static void main(String[] args) {
        final String PATTERN = "%d [%p|%c|%C{1}] %m%n";

        ConsoleAppender console = new ConsoleAppender();
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.INFO);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);

        try {
            if (!new File("config.json").exists()) {
                InputStream initialStream = MQExecutor.class.getResourceAsStream("/config.json");

                byte[] buffer = new byte[initialStream.available()];
                initialStream.read(buffer);
                OutputStream outStream = new FileOutputStream("config.json");
                outStream.write(buffer);
                System.exit(-1);
            }
            JSONObject config = (JSONObject) JSONValue.parse(new FileReader("config.json"));

            List<Address> rabbitHosts = new ArrayList<>();
            for (String host : ((String)config.get(ConfigConst.HOSTS)).split(",")) {
                rabbitHosts.add(new Address(host.trim(), ((Long) config.get(ConfigConst.PORT)).intValue()));
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHosts.get(0).getHost());
            factory.setUsername((String) config.get(ConfigConst.USERNAME));
            factory.setPassword((String) config.get(ConfigConst.PASSWORD));
            factory.setVirtualHost((String) config.get(ConfigConst.VHOST));
            factory.setPort(((Long) config.get(ConfigConst.PORT)).intValue());

            Connection connection = factory.newConnection(rabbitHosts);
            final Channel channel = connection.createChannel();
            channel.basicQos(1);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            final MQExecutorConsumer mqExecutorConsumer = new MQExecutorConsumer(channel, config);

            channel.basicConsume(
                    (String) config.get(ConfigConst.REQUEST_QUEUE_NAME),
                    false,
                    (String) config.get(ConfigConst.WORKER_NAME),
                    mqExecutorConsumer
            );

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Starting to shutdown.");
                try {
                    channel.close();
                    connection.close();
                } catch (Exception e) {}

                int timeout = 300; // Timeout is 5 minutes.

                while (timeout > 0 && mqExecutorConsumer.isWorking()) {
                    System.out.println("busy");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {}
                    timeout--;
                }

                System.out.println("Shutdown.");
            }));

            Thread connectionThread = null;
            for(Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().contains("AMQP Connection")) {
                    connectionThread = t;
                }
            }

            ServiceLoader<Reporter> reporters = ServiceLoader.load(Reporter.class);
            Reporter reporter = null;
            int highestPrio = -1;
            for (Reporter r : reporters) {
                if (highestPrio < r.getPriority()) {
                    reporter = r;
                    highestPrio = r.getPriority();
                }
            }

            if (reporter == null) {
                throw new Exception("No reporter present");
            }
            reporter.init(
                MQExecutor.class.getName(),
                MQExecutor.class.getPackage().getImplementationVersion(),
                (String)config.get(ConfigConst.WORKER_NAME)
            );

            while(true) {
                System.out.println("Are you still there?");
                if (!connectionThread.isAlive()) {
                    break;
                }
                System.out.println("I'm still alive...");
                reporter.reportActivity();
                Thread.sleep(5 * 60000);
            }
        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
        }
    }
}
