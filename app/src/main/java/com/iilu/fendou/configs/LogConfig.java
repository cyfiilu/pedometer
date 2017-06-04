package com.iilu.fendou.configs;

import android.os.Environment;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 日志级别优先度从高到低：OFF(关闭), FATAL(致命), ERROR(错误), WARN(警告), INFO(信息), DEBUG(调试), ALL(打开所有的日志)
 * Log4j建议只使用FATAL, ERROR, WARN, INFO, DEBUG这五个级别
 */
public class LogConfig {

    /**
     * 输出格式：
     * [%-d{yyyy-MM-dd HH:mm:ss}] [Level: %-5p] [Class: %c.%M(%F:%L)] - Msg: %m%n
     *
     * 输出格式解释：
     * "-"号指定左对齐
     * %d{yyyy-MM-dd HH:mm:ss}：时间，大括号内是时间格式
     * %p：日志级别，这里%-5p是指定的5个字符的日志名称，为的是格式整齐
     * %c：全类名
     * %M：调用的方法名称
     * %F:%L：类名输出格式解释：:行号（在控制台可以追踪代码）
     * %m：日志信息
     * %n：换行
     *
     * %l: 输出日志事件的发生位置，相当于%C.%M(%F:%L)的组合
     * 输出的信息大概如下：
     * [时间{时间格式}] [5个字符的等级名称] [信息所在的class.method(className：lineNumber)] 输出信息 换行
     */
    private static final String FILE_PATTERN = "%d{yyyy-MM-dd HH:mm:ss}  %p/%t  %l  %m%n";

    /** 日志文件路径地址：SD卡下myc文件夹log文件夹的test文件 */
    public static String fileName = Environment.getExternalStorageDirectory() + File.separator + "fendou" + File.separator + "test.log";

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;

    private static final int MAX_BACKUP_SIZE = 2;

    public static void configure() {
        final LogConfigurator logConfigurator = new LogConfigurator();
        // 设置文件名
        logConfigurator.setFileName(fileName);
        // 设置root日志输出级别，默认为DEBUG
        logConfigurator.setRootLevel(Level.DEBUG);
        // 设置日志输出级别
        logConfigurator.setLevel("org.apache", Level.INFO);
        // 设置 输出到日志文件的文字格式，默认 %d %-5p [%c{2}]-[%L] %m%n
        logConfigurator.setFilePattern(FILE_PATTERN);
        // 设置输出到控制台的文字格式，默认%m%n
        // logConfigurator.setLogCatPattern("%m%n");
        // 设置总文件大小
        logConfigurator.setMaxFileSize(MAX_FILE_SIZE);
        // 设置最大产生的文件个数
        logConfigurator.setMaxBackupSize(MAX_BACKUP_SIZE);
        // 设置所有消息是否被立刻输出，默认为true，false 不输出
        logConfigurator.setImmediateFlush(true);
        // 是否本地控制台打印输出，默认为true，false不输出
        logConfigurator.setUseLogCatAppender(true);
        // 设置是否启用文件附加，默认为true，false为覆盖文件
        logConfigurator.setUseFileAppender(true);
        // 设置是否重置配置文件，默认为true
        logConfigurator.setResetConfiguration(true);
        // 是否显示内部初始化日志，默认为false
        logConfigurator.setInternalDebugging(false);

        logConfigurator.configure();
    }
}
