package zx.ias.util;

import zx.ias.Global;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.*;

/**
 * LogUtil
 */

public class DebugLog {

    private static Logger logger;
    private static String tag;
    private static boolean hasSetFile;

    public static String LOG_FILENAME = "debug_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX) + ".log";

    static {
        logger = Logger.getGlobal();

        /*已经有自定义输出，Logger关闭控制台输出*/
        //禁用父类Handler
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.OFF);
        logger.addHandler(consoleHandler);
    }

    /**
     * 开关：日志输出
     */
    private static boolean DEBUG = Global.LOG_DEBUG;

    /**
     * 日志输出格式
     */
    private static final String FORMAT = "%s[%s|:|%s]";

    /**
     * 标准输出
     *
     * @param message 日志信息
     */
    public static void i(String message) {
        if (DEBUG) {
            tag = invokeSrcAddressTag();
            logger.info(message);
            System.out.println(packLog(message));
        }
    }

    /**
     * 错误输出
     *
     * @param message 日志信息
     */
    public static void e(String message) {
        if (DEBUG) {
            tag = invokeSrcAddressTag();
            logger.severe(message);
            System.err.println(packLog(message));
        }
    }

    /**
     * 包装日志
     *
     * @param msg 信息
     * @return 格式化后的日志
     */
    private static String packLog(String msg) {
        /*让packLog初始化Logger文件输出配置避免产生空日志文件*/
        if (!hasSetFile && DEBUG) {
            /*Logger文件记录*/
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(LOG_FILENAME);
            } catch (IOException ignored) {
            }
            if (fileHandler != null) {
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return String.format(FORMAT,
                                record.getInstant(),
                                record.getLevel(),
                                tag + " " + record.getMessage()) + System.lineSeparator();
                    }
                });
                logger.addHandler(fileHandler);
            }
            hasSetFile = true;
        }
        return String.format(FORMAT, Instant.now(), tag, msg);
    }

    /**
     * 生成调用来源（类加方法名）的Tag
     *
     * @return {@link String}
     */
    private static String invokeSrcAddressTag() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement log = stackTrace[1];
        String tag = null;
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement e = stackTrace[i];
            if (!e.getClassName().equals(log.getClassName())) {
                tag = e.getClassName() + "." + e.getMethodName();
                break;
            }
        }
        if (tag == null) tag = log.getClassName() + "." + log.getMethodName();
        return tag;
    }
}
