package me.realseek;

import me.realseek.test.TestManager;
import snw.jkook.command.JKookCommand;
import snw.jkook.plugin.BasePlugin;

/**
 * KookBC 功能测试插件
 * 用于全面测试 JKook/KookBC 的所有核心功能
 *
 * @author RealSeek
 * @version 1.0.0
 */
public class TestJkookPlugin extends BasePlugin {
    private TestManager testManager;

    @Override
    public void onLoad() {
        getLogger().info("====================================");
        getLogger().info("  KookBC 功能测试插件加载中...");
        getLogger().info("====================================");
    }

    @Override
    public void onEnable() {
        getLogger().info("====================================");
        getLogger().info("  KookBC 功能测试插件启动");
        getLogger().info("  版本: {}", getDescription().getVersion());
        getLogger().info("====================================");

        // 加载配置
        saveDefaultConfig();
        reloadConfig();

        // 初始化测试管理器
        try {
            testManager = new TestManager(this);
            getLogger().info("测试管理器初始化成功");
        } catch (Exception e) {
            getLogger().error("测试管理器初始化失败", e);
            return;
        }

        // 注册命令
        registerCommands();

        // 检查是否启用自动测试
        if (getConfig().getBoolean("auto-test.enabled", false)) {
            long delay = getConfig().getLong("auto-test.delay", 5000);
            getLogger().info("将在 {}ms 后自动执行测试", delay);

            getCore().getScheduler().runTaskLater(this, () -> {
                getLogger().info("开始自动测试...");
                testManager.runAllTests();
            }, delay);
        } else {
            getLogger().info("自动测试已禁用，使用 /kbctest run 命令手动执行测试");
        }

        getLogger().info("插件启动完成！");
    }

    @Override
    public void onDisable() {
        getLogger().info("====================================");
        getLogger().info("  KookBC 功能测试插件���闭中...");
        getLogger().info("====================================");

        // 清理测试资源
        if (testManager != null) {
            testManager.cleanup();
        }

        getLogger().info("插件已关闭");
    }

    /**
     * 注册插件命令
     */
    private void registerCommands() {
        // 主命令
        JKookCommand mainCommand = new JKookCommand("kbctest")
                .setDescription("KookBC 测试插件主命令")
                .setHelpContent(
                        "KookBC 功能测试插件命令:\n" +
                        "/kbctest run - 运行所有测试\n" +
                        "/kbctest run <模块名> - 运行指定模块测试\n" +
                        "/kbctest list - 列出所有测试模块\n" +
                        "/kbctest help - 显示帮助信息"
                )
                .setExecutor((sender, args, message) -> {
                    if (args.length == 0) {
                        getLogger().info("使用 /kbctest help 查看帮助信息");
                        return;
                    }

                    String subCommand = ((String) args[0]).toLowerCase();

                    switch (subCommand) {
                        case "run":
                            if (args.length == 1) {
                                getLogger().info("开始执行所有测试，请查看控制台输出...");
                                getCore().getScheduler().runTask(this, () -> {
                                    testManager.runAllTests();
                                });
                            } else {
                                String moduleName = (String) args[1];
                                getLogger().info("开始执行测试模块: {}", moduleName);
                                getCore().getScheduler().runTask(this, () -> {
                                    testManager.runTest(moduleName);
                                });
                            }
                            break;

                        case "list":
                            getLogger().info("正在列出所有测试模块...");
                            testManager.listModules();
                            break;

                        case "help":
                            getLogger().info("=== KookBC 测试插件帮助 ===");
                            getLogger().info("/kbctest run - 运行所有测试");
                            getLogger().info("/kbctest run <模块> - 运行指定测试");
                            getLogger().info("/kbctest list - 列出测试模块");
                            getLogger().info("/kbctest help - 显示此帮助");
                            break;

                        default:
                            getLogger().warn("未知命令: {}", subCommand);
                            getLogger().info("使用 /kbctest help 查看帮助");
                            break;
                    }
                });

        // 注册命令
        try {
            getCore().getCommandManager().registerCommand(this, mainCommand);
            getLogger().info("命令注册成功: /kbctest");
        } catch (Exception e) {
            getLogger().error("命令注册失败", e);
        }
    }

    /**
     * 获取测试管理器
     */
    public TestManager getTestManager() {
        return testManager;
    }
}
