package org.ea;

import org.apache.log4j.Logger;

import java.io.File;

public class Util {
    private static final Logger logger = Logger.getLogger(Util.class.getName());

    public static void deleteDirectory(File outputDir) {
        for(File f : outputDir.listFiles()) {
            if(f.isDirectory()) deleteDirectory(f);
            f.delete();
        }
        outputDir.deleteOnExit();
    }

}
