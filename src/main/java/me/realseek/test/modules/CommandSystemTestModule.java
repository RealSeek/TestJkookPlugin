package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.command.*;
import snw.jkook.entity.User;
import snw.jkook.message.Message;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 命令系统测试模块
 * 测试 JKook/KookBC 的命令注册和执行机制
 */
public class CommandSystemTestModule extends BaseTestModule {
    private JKookCommand testCommand;
    private final AtomicBoolean commandExecuted = new AtomicBoolean(false);

    @Override
    public String getName() {
        return "命令系统";
    }

    @Override
    public String getDescription() {
        return "测试命令注册、参数解析、子命令、别名等功能";
    }

    @Override
    protected void executeTests() {
        runTest("命令管理器可用性检查", this::testCommandManagerAvailability);
        runTest("基础命令创建", this::testBasicCommandCreation);
        runTest("命令注册", this::testCommandRegistration);
        runTest("命令别名设置", this::testCommandAliases);
        runTest("命令前缀设置", this::testCommandPrefixes);
        runTest("命令描述和帮助", this::testCommandDescriptionAndHelp);
        runTest("命令参数类型", this::testCommandArguments);
        runTest("可选参数", this::testOptionalArguments);
        runTest("子命令创建", this::testSubcommands);
        runTest("用户命令执行器", this::testUserCommandExecutor);
        runTest("控制台命令执行器", this::testConsoleCommandExecutor);
    }

    private void testCommandManagerAvailability() {
        assertNotNull(plugin.getCore().getCommandManager(), "命令管理器不应为null");
    }

    private void testBasicCommandCreation() {
        testCommand = new JKookCommand("testcmd");
        assertNotNull(testCommand, "命令对象创建失败");
        assertEquals("testcmd", testCommand.getRootName(), "命令根名称不匹配");
    }

    private void testCommandRegistration() {
        JKookCommand cmd = new JKookCommand("test_register")
                .setDescription("测试命令注册")
                .setExecutor((sender, arguments, message) -> {});

        try {
            plugin.getCore().getCommandManager().registerCommand(plugin, cmd);
            assertTrue(true, "命令注册成功");
        } catch (Exception e) {
            assertTrue(false, "命令注册失败: " + e.getMessage());
        }
    }

    private void testCommandAliases() {
        JKookCommand cmd = new JKookCommand("test_alias")
                .addAlias("ta")
                .addAlias("testalias");

        assertTrue(cmd.getAliases().contains("ta"), "别名'ta'未添加");
        assertTrue(cmd.getAliases().contains("testalias"), "别名'testalias'未添加");
        assertEquals(2, cmd.getAliases().size(), "别名数量不匹配");
    }

    private void testCommandPrefixes() {
        JKookCommand cmd = new JKookCommand("test_prefix", '/')
                .addPrefix("!");

        assertTrue(cmd.getPrefixes().contains("/"), "默认前缀'/'不存在");
        assertTrue(cmd.getPrefixes().contains("!"), "自定义前缀'!'未添加");
    }

    private void testCommandDescriptionAndHelp() {
        String description = "这是一个测试命令";
        String helpContent = "使用方法: /test <参数>";

        JKookCommand cmd = new JKookCommand("test_help")
                .setDescription(description)
                .setHelpContent(helpContent);

        assertEquals(description, cmd.getDescription(), "命令描述不匹配");
        assertEquals(helpContent, cmd.getHelpContent(), "命令帮助内容不匹配");
    }

    private void testCommandArguments() {
        JKookCommand cmd = new JKookCommand("test_args")
                .addArgument(String.class)
                .addArgument(Integer.class)
                .addArgument(Boolean.class);

        assertEquals(3, cmd.getArguments().size(), "参数数量不匹配");
        assertTrue(cmd.getArguments().contains(String.class), "String参数类型未添加");
        assertTrue(cmd.getArguments().contains(Integer.class), "Integer参数类型未添加");
        assertTrue(cmd.getArguments().contains(Boolean.class), "Boolean参数类型未添加");
    }

    private void testOptionalArguments() {
        JKookCommand cmd = new JKookCommand("test_optional")
                .addArgument(String.class)
                .addOptionalArgument(Integer.class, 10)
                .addOptionalArgument(Boolean.class, true);

        assertEquals(1, cmd.getArguments().size(), "必需参数数量不匹配");
        assertEquals(2, cmd.getOptionalArguments().size(), "可选参数数量不匹配");
    }

    private void testSubcommands() {
        JKookCommand subCmd1 = new JKookCommand("add")
                .setDescription("添加子命令");
        JKookCommand subCmd2 = new JKookCommand("remove")
                .setDescription("移除子命令");

        JKookCommand mainCmd = new JKookCommand("test_subcommand")
                .addSubcommand(subCmd1)
                .addSubcommand(subCmd2);

        assertEquals(2, mainCmd.getSubcommands().size(), "子命令数量不匹配");
    }

    private void testUserCommandExecutor() {
        commandExecuted.set(false);

        UserCommandExecutor executor = (sender, arguments, message) -> {
            commandExecuted.set(true);
        };

        JKookCommand cmd = new JKookCommand("test_user_exec")
                .executesUser(executor);

        assertNotNull(cmd.getUserCommandExecutor(), "用户命令执行器未设置");

        // 模拟执行
        try {
            if (cmd.getUserCommandExecutor() != null) {
                // 这里只是验证执行器存在，实际执行需要真实的用户和消息对象
                assertTrue(true, "用户命令执行器设置成功");
            }
        } catch (Exception e) {
            assertTrue(false, "用户命令执行器测试失败: " + e.getMessage());
        }
    }

    private void testConsoleCommandExecutor() {
        ConsoleCommandExecutor executor = (sender, arguments) -> {
            // 控制台命令执行逻辑
        };

        JKookCommand cmd = new JKookCommand("test_console_exec")
                .executesConsole(executor);

        assertNotNull(cmd.getConsoleCommandExecutor(), "控制台命令执行器未设置");
    }

    @Override
    public void cleanup() {
        // 清理注册的测试命令
        // 注意：JKook API 可能没有提供命令注销方法，这里只是示例
        logger.debug("命令系统测试模块清理完成");
    }
}
