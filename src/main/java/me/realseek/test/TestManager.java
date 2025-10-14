package me.realseek.test;

import me.realseek.test.modules.*;
import org.slf4j.Logger;
import snw.jkook.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试管理器，负责管理所有测试模块的执行
 */
public class TestManager {
    private final Plugin plugin;
    private final Logger logger;
    private final List<TestModule> modules;
    private final TestReportManager reportManager;

    public TestManager(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.modules = new ArrayList<>();

        File reportDir = new File(plugin.getDataFolder(), "reports");
        this.reportManager = new TestReportManager(logger, reportDir);

        // 注册所有测试模块
        registerModules();
    }

    /**
     * 注册所有测试模块
     */
    private void registerModules() {
        // 基础功能测试（单元测试级别）
        registerModule(new ApiTestModule());
        registerModule(new ConfigurationTestModule());

        // 核心功能测试（单元测试级别）
        registerModule(new EventSystemTestModule());
        registerModule(new CommandSystemTestModule());
        registerModule(new MessageBuilderTestModule());
        registerModule(new SchedulerTestModule());

        // 新增的单元测试模块（第一轮补充）
        registerModule(new PermissionTestModule());
        registerModule(new FileComponentTestModule());
        registerModule(new EntityExtensionTestModule());

        // 新增的单元测试模块（第二轮补充）
        registerModule(new UserDetailTestModule());
        registerModule(new RoleManagementTestModule());
        registerModule(new TemplateMessageTestModule());
        registerModule(new RoleEventTestModule());

        // 集成测试（需要真实环境）
        registerModule(new me.realseek.test.modules.integration.HttpApiIntegrationTestModule());
        registerModule(new me.realseek.test.modules.integration.MessageIntegrationTestModule());
        registerModule(new me.realseek.test.modules.integration.GuildIntegrationTestModule());

        // 新增的集成测试模块（第二轮补充）
        registerModule(new me.realseek.test.modules.integration.PrivateMessageIntegrationTestModule());
        registerModule(new me.realseek.test.modules.integration.FriendSystemIntegrationTestModule());
        registerModule(new me.realseek.test.modules.integration.FileUploadIntegrationTestModule());

        // ThreadChannel 需要 JKook 0.55.0+，暂时禁用
        // registerModule(new me.realseek.test.modules.integration.ThreadChannelIntegrationTestModule());

        long unitTestCount = modules.stream()
                .filter(m -> !(m instanceof me.realseek.test.modules.integration.IntegrationTestModule))
                .count();
        long integrationCount = modules.stream()
                .filter(m -> m instanceof me.realseek.test.modules.integration.IntegrationTestModule)
                .count();

        logger.info("已注册 {} 个测试模块（{} 个单元测试 + {} 个集成测试）",
                modules.size(), unitTestCount, integrationCount);
    }

    /**
     * 注册单个测试模块
     */
    private void registerModule(TestModule module) {
        try {
            module.initialize(plugin);
            modules.add(module);
            logger.debug("已注册测试模块: {}", module.getName());
        } catch (Exception e) {
            logger.error("注册测试模块 {} 失败", module.getName(), e);
        }
    }

    /**
     * 运行所有测试模块
     */
    public void runAllTests() {
        logger.info("====================================");
        logger.info("      开始执行 KookBC 功能测试");
        logger.info("====================================");
        logger.info("测试模块数量: {}", modules.size());
        logger.info("");

        long startTime = System.currentTimeMillis();

        for (TestModule module : modules) {
            try {
                logger.info(">>> 正在测试: {}", module.getName());
                logger.info("    {}", module.getDescription());

                List<TestResult> results = module.runTests();
                reportManager.addModuleResults(module.getName(), results);

                logger.info("");
            } catch (Exception e) {
                logger.error("测试模块 {} 执行失败", module.getName(), e);
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("====================================");
        logger.info("所有测试完成，总耗时: {}ms", totalTime);
        logger.info("====================================");
        logger.info("");

        // 生成测试报告
        reportManager.printSummary();
        reportManager.generateHtmlReport();
        reportManager.generateTextReport();
    }

    /**
     * 运行���定的测试模块
     */
    public void runTest(String moduleName) {
        TestModule targetModule = null;
        for (TestModule module : modules) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                targetModule = module;
                break;
            }
        }

        if (targetModule == null) {
            logger.warn("未找到测试模块: {}", moduleName);
            logger.info("可用的测试模块:");
            for (TestModule module : modules) {
                logger.info("  - {}", module.getName());
            }
            return;
        }

        logger.info("执行测试模块: {}", targetModule.getName());
        List<TestResult> results = targetModule.runTests();

        // 打印结果
        logger.info("测试结果:");
        for (TestResult result : results) {
            logger.info("  {}", result.toString());
        }
    }

    /**
     * 运行自动测试（插件启动时自动运行的测试）
     */
    public void runAutoTests() {
        logger.info("执行自动测试...");

        List<TestModule> autoModules = new ArrayList<>();
        for (TestModule module : modules) {
            if (module.isAutoRun()) {
                autoModules.add(module);
            }
        }

        if (autoModules.isEmpty()) {
            logger.info("没有配置自动运行的测试模块");
            return;
        }

        for (TestModule module : autoModules) {
            logger.info("自动测试: {}", module.getName());
            List<TestResult> results = module.runTests();
            reportManager.addModuleResults(module.getName(), results);
        }
    }

    /**
     * 列出所有测试模块
     */
    public void listModules() {
        logger.info("可用的测试模块:");
        for (TestModule module : modules) {
            logger.info("  [{}] {}", module.getName(), module.getDescription());
        }
    }

    /**
     * 清理所有测试模块
     */
    public void cleanup() {
        logger.info("清理测试资源...");
        for (TestModule module : modules) {
            try {
                module.cleanup();
            } catch (Exception e) {
                logger.error("清理测试模块 {} 时发生错误", module.getName(), e);
            }
        }
    }

    /**
     * 获取所有测试模块
     */
    public List<TestModule> getModules() {
        return new ArrayList<>(modules);
    }

    /**
     * 获取测试报告管理器
     */
    public TestReportManager getReportManager() {
        return reportManager;
    }
}
