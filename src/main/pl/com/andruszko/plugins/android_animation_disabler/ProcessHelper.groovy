package pl.com.andruszko.plugins.android_animation_disabler

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

/* Helper to log from gradle tasks */

class ProcessHelper {
    static int runWithLogger(String cmd, Logger logger, String outputTag, String errorTag) {
        logger.info("INVOKING COMMAND: " + cmd)
        Process process = cmd.execute();
        LoggerGobbler outputGobbler = new LoggerGobbler(process.getInputStream(), outputTag, logger, LogLevel.ERROR);
        LoggerGobbler errorGobbler = new LoggerGobbler(process.getErrorStream(), errorTag, logger, LogLevel.ERROR);
        return gobbleProcess(process, outputGobbler, errorGobbler);
    }


    private
    static int gobbleProcess(Process process, AbstractStreamGobbler outputGobbler, AbstractStreamGobbler errorGobbler) {
        if (outputGobbler != null) {
            outputGobbler.start();
        }
        if (errorGobbler != null) {
            errorGobbler.start();
        }
        int resCode = process.waitFor();
        if (outputGobbler != null) {
            outputGobbler.join();
        }
        if (errorGobbler != null) {
            errorGobbler.join();
        }
        return resCode;
    }
}
