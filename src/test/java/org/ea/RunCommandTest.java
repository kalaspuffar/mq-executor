package org.ea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class RunCommandTest {
    public static void main(String[] args) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("/bin/sh");
        builder.redirectErrorStream(true);
        File workLog = new File("work.log");
        System.out.println("Worklog located at: " + workLog.getAbsolutePath());
        builder.redirectOutput(workLog);
        Process process = builder.start();

        BufferedWriter cmdLine = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        writeCmd(cmdLine, "echo Hello");
        writeCmd(cmdLine, "sleep 10");
        writeCmd(cmdLine, "exit 0");

        if (process.waitFor(5, TimeUnit.SECONDS) == false) {
            process.destroyForcibly();
        }

        StringBuilder stdOutAndErr = new StringBuilder();
        printStream(stdOutAndErr, new FileInputStream(workLog));
        System.out.println(stdOutAndErr.toString());
        System.out.println(process.exitValue());
    }

    private static void writeCmd(BufferedWriter cmdLine, String cmd) throws Exception {
        cmdLine.write(cmd);
        cmdLine.newLine();
        cmdLine.flush();
    }

    private static void printStream(StringBuilder response, InputStream inputStream) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
    }

}
