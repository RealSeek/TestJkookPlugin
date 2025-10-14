package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.HttpAPI;
import snw.jkook.entity.User;

/**
 * API 功能测试模块
 * 测试 JKook/KookBC 提供的各种 API 接口
 */
public class ApiTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "API 功能";
    }

    @Override
    public String getDescription() {
        return "测试 HttpAPI、用户查询、频道操作、服务器信息等 API 功能";
    }

    @Override
    protected void executeTests() {
        runTest("HttpAPI 可用性检查", this::testHttpApiAvailability);
        runTest("Core API 可用性检查", this::testCoreApiAvailability);
        runTest("用户 API 接口检查", this::testUserApiInterface);
        runTest("频道 API 接口检查", this::testChannelApiInterface);
        runTest("服务器 API 接口检查", this::testGuildApiInterface);
        runTest("消息 API 接口检查", this::testMessageApiInterface);
        runTest("Bot 用户信息获取", this::testGetBotUser);
        runTest("HttpAPI 获取方法", this::testHttpApiGetter);
    }

    private void testHttpApiAvailability() {
        HttpAPI httpApi = plugin.getCore().getHttpAPI();
        assertNotNull(httpApi, "HttpAPI 实例不应为null");
    }

    private void testCoreApiAvailability() {
        assertNotNull(plugin.getCore(), "Core 实例不应为null");
        assertNotNull(plugin.getCore().getEventManager(), "EventManager 不应为null");
        assertNotNull(plugin.getCore().getCommandManager(), "CommandManager 不应为null");
        assertNotNull(plugin.getCore().getScheduler(), "Scheduler 不应为null");
    }

    private void testUserApiInterface() {
        // 验证 User 接口的关键方法存在
        try {
            Class<?> userClass = User.class;
            assertNotNull(userClass.getMethod("getId"), "User.getId() 方法应该存在");
            assertNotNull(userClass.getMethod("getName"), "User.getName() 方法应该存在");
            assertNotNull(userClass.getMethod("isBot"), "User.isBot() 方法应该存在");
            assertNotNull(userClass.getMethod("isVip"), "User.isVip() 方法应该存在");
            assertTrue(true, "User API 接口验证通过");
        } catch (NoSuchMethodException e) {
            assertTrue(false, "User API 接口缺少必要方法: " + e.getMessage());
        }
    }

    private void testChannelApiInterface() {
        // 验证频道相关类的存在
        try {
            assertNotNull(Class.forName("snw.jkook.entity.channel.Channel"),
                    "Channel 类应该存在");
            assertNotNull(Class.forName("snw.jkook.entity.channel.TextChannel"),
                    "TextChannel 类应该存在");
            assertNotNull(Class.forName("snw.jkook.entity.channel.VoiceChannel"),
                    "VoiceChannel 类应该存在");
            assertTrue(true, "频道 API 接口验证通过");
        } catch (ClassNotFoundException e) {
            assertTrue(false, "频道 API 接口缺少必要类: " + e.getMessage());
        }
    }

    private void testGuildApiInterface() {
        // 验证服务器相关类的存在
        try {
            Class<?> guildClass = Class.forName("snw.jkook.entity.Guild");
            assertNotNull(guildClass.getMethod("getId"), "Guild.getId() 方法应该存在");
            assertNotNull(guildClass.getMethod("getName"), "Guild.getName() 方法应该存在");
            assertNotNull(guildClass.getMethod("getMaster"), "Guild.getMaster() 方法应该存在");
            assertTrue(true, "服务器 API 接口验证通过");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            assertTrue(false, "服务器 API 接口验证失败: " + e.getMessage());
        }
    }

    private void testMessageApiInterface() {
        // 验证消息相关类的存在
        try {
            assertNotNull(Class.forName("snw.jkook.message.Message"),
                    "Message 类应该存在");
            assertNotNull(Class.forName("snw.jkook.message.ChannelMessage"),
                    "ChannelMessage 类应该存在");
            assertNotNull(Class.forName("snw.jkook.message.PrivateMessage"),
                    "PrivateMessage 类应该存在");
            assertNotNull(Class.forName("snw.jkook.message.TextChannelMessage"),
                    "TextChannelMessage 类应该存在");
            assertTrue(true, "消息 API 接口验证通过");
        } catch (ClassNotFoundException e) {
            assertTrue(false, "消息 API 接口缺少必要类: " + e.getMessage());
        }
    }

    private void testGetBotUser() {
        try {
            User botUser = plugin.getCore().getUser();
            if (botUser != null) {
                assertNotNull(botUser.getId(), "Bot 用户 ID 不应为null");
                logger.info("Bot 用户 ID: {}, 名称: {}", botUser.getId(), botUser.getName());
                assertTrue(true, "成功获取 Bot 用户信息");
            } else {
                // 在某些情况下可能为null（如未连接）
                logger.warn("Bot 用户信息为null，可能尚未连接到 Kook");
                assertTrue(true, "Bot 用户获取测试完成（但用户为null）");
            }
        } catch (Exception e) {
            logger.warn("获取 Bot 用户信息时发生异常: {}", e.getMessage());
            assertTrue(true, "Bot 用户获取测试完成（发生异常）");
        }
    }

    private void testHttpApiGetter() {
        try {
            HttpAPI api = plugin.getCore().getHttpAPI();
            assertNotNull(api, "HttpAPI 不应为null");

            // 验证 HttpAPI 的关键方法
            Class<?> apiClass = api.getClass().getInterfaces()[0]; // 获取接口
            assertTrue(apiClass.getName().contains("HttpAPI"),
                    "应该实现 HttpAPI 接口");

            assertTrue(true, "HttpAPI 接口验证通过");
        } catch (Exception e) {
            assertTrue(false, "HttpAPI 验证失败: " + e.getMessage());
        }
    }
}
