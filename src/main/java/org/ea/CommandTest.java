package org.ea;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CommandTest {
    public static void main(String[] args) {
        ProcessBuilder builder = new ProcessBuilder( "cmd.exe" );
        Process p = null;
        try {
            p = builder.start();

            //get stdin of shell
            BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

            // execute the desired command (here: ls) n times
            int n = 10;
            for (int i = 0; i < n; i++) {
                p_stdin.write("EXIT /B 11");
                p_stdin.newLine();
                p_stdin.write("IF %ERRORLEVEL% NEQ 0 (EXIT /B %ERRORLEVEL%)");
                p_stdin.newLine();
                p_stdin.flush();
            }

            p.waitFor(1, TimeUnit.MINUTES);
            System.out.println("Exit value: " + p.exitValue());

            p_stdin.write("exit");
            p_stdin.newLine();
            p_stdin.flush();

            // write stdout of shell (=output of all commands)
            Scanner s = new Scanner(p.getInputStream());
            while (s.hasNext()) {
                System.out.println(s.next());
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
