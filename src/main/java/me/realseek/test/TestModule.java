package me.realseek.test;

import snw.jkook.plugin.Plugin;

import java.util.List;

/**
 * 测试模块接口，所有测试模块必须实现此接口
 */
public interface TestModule {
    /**
     * 获取模块名称
     */
    String getName();

    /**
     * 获取模块描述
     */
    String getDescription();

    /**
     * 初始化测试模块
     * @param plugin 插件实例
     */
    void initialize(Plugin plugin);

    /**
     * 执行所有测试
     * @return 测试结果列表
     */
    List<TestResult> runTests();

    /**
     * 清理测试资源
     */
    void cleanup();

    /**
     * 是否自动运行
     * @return true表示插件启动时自动运行，false表示需要手动触发
     */
    default boolean isAutoRun() {
        return false;
    }
}
