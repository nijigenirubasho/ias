package zx.ias;

import java.io.File;

/**
 * GlobalConfig
 */

public class Global {

    /**
     * 在控制台和文件上写日志输出
     */
    public static final boolean LOG_DEBUG = new File("debug").exists();

    /**
     * 随机数据生成模式
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean RANDOM_DATA_GENERATE_MODE = new File("data_gen").exists();
}
