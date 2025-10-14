package me.realseek.test;

import org.slf4j.Logger;
import snw.jkook.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试模块基类，提供通用的测试执行逻辑
 */
public abstract class BaseTestModule implements TestModule {
    protected Plugin plugin;
    protected Logger logger;
    protected List<TestResult> results;

    @Override
    public void initialize(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.results = new ArrayList<>();
    }

    @Override
    public List<TestResult> runTests() {
        results.clear();
        logger.info("开始执行 {} 测试模块...", getName());
        long startTime = System.currentTimeMillis();

        try {
            executeTests();
        } catch (Exception e) {
            logger.error("测试模块 {} 执行时发生严重错误", getName(), e);
            results.add(new TestResult(
                "模块执行",
                getName(),
                false,
                "模块执行失败: " + e.getMessage(),
                System.currentTimeMillis() - startTime,
                e
            ));
        }

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("{} 测试完成，总耗时: {}ms", getName(), totalTime);

        return results;
    }

    /**
     * 子类实现具体的测试逻辑
     */
    protected abstract void executeTests();

    /**
     * 执行单个测试用例
     */
    protected void runTest(String testName, Runnable test) {
        long startTime = System.currentTimeMillis();
        try {
            test.run();
            long executionTime = System.currentTimeMillis() - startTime;
            results.add(new TestResult(testName, getName(), true, "测试通过", executionTime));
            logger.info("  ✓ {} - {}ms", testName, executionTime);
        } catch (AssertionError e) {
            long executionTime = System.currentTimeMillis() - startTime;
            results.add(new TestResult(testName, getName(), false, e.getMessage(), executionTime, e));
            logger.error("  ✗ {} - 断言失败: {}", testName, e.getMessage());
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            results.add(new TestResult(testName, getName(), false, e.getMessage(), executionTime, e));
            logger.error("  ✗ {} - 异常: {}", testName, e.getMessage(), e);
        }
    }

    /**
     * 断言方法
     */
    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    protected void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    protected void assertNotNull(Object obj, String message) {
        assertTrue(obj != null, message);
    }

    protected void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(message + " [期望: " + expected + ", 实际: " + actual + "]");
        }
    }

    @Override
    public void cleanup() {
        // 默认不需要清理
    }
}
