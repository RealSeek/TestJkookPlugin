package me.realseek.test.modules.integration;

import snw.jkook.HttpAPI;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.entity.channel.VoiceChannel;
import snw.jkook.util.PageIterator;

import java.util.Collection;
import java.util.Set;

/**
 * HttpAPI 集成测试模块
 * 测试 HttpAPI 的真实请求功能
 */
public class HttpApiIntegrationTestModule extends IntegrationTestModule {

    @Override
    public String getName() {
        return "HttpAPI 集成测试";
    }

    @Override
    public String getDescription() {
        return "测试 HttpAPI 的真实请求功能，包括获取服务器、用户、频道等";
    }

    @Override
    protected void executeTests() {
        if (!isIntegrationEnabled()) {
            logger.warn("集成测试未启用，请在 config.yml 中设置 integration-test.enabled=true");
            return;
        }

        if (!checkRequiredConfig()) {
            return;
        }

        HttpAPI api = plugin.getCore().getHttpAPI();
        assertNotNull(api, "HttpAPI 不应为 null");

        runTest("获取当前用户（Bot）信息", () -> testGetBotUser(api));
        runTest("获取已加入的服务器列表", () -> testGetJoinedGuilds(api));
        runTest("根据 ID 获取服务器", () -> testGetGuild(api));
        runTest("根据 ID 获取文本频道", () -> testGetTextChannel(api));

        if (testVoiceChannelId != null && !testVoiceChannelId.isEmpty()) {
            runTest("根据 ID 获取语音频道", () -> testGetVoiceChannel(api));
        }

        if (testUserId != null && !testUserId.isEmpty()) {
            runTest("根据 ID 获取用户", () -> testGetUser(api));
        }

        runTest("获取已加入的语音频道列表", () -> testGetJoinedVoiceChannels(api));
    }

    private void testGetBotUser(HttpAPI api) {
        User botUser = plugin.getCore().getUser();
        assertNotNull(botUser, "Bot 用户不应为 null");
        assertNotNull(botUser.getId(), "Bot 用户 ID 不应为 null");
        assertNotNull(botUser.getName(), "Bot 用户名称不应为 null");

        logger.info("Bot 用户信息: ID={}, 名称={}, 是否机器人={}",
                botUser.getId(), botUser.getName(), botUser.isBot());

        assertTrue(botUser.isBot(), "当前用户应该是机器人");
    }

    private void testGetJoinedGuilds(HttpAPI api) {
        PageIterator<Collection<Guild>> guilds = api.getJoinedGuilds();
        assertNotNull(guilds, "服务器列表不应为 null");

        if (guilds.hasNext()) {
            Collection<Guild> firstPage = guilds.next();
            assertNotNull(firstPage, "第一页服务器列表不应为 null");

            logger.info("已加入 {} 个服务器（第一页）", firstPage.size());

            for (Guild guild : firstPage) {
                logger.info("  - 服务器: {} (ID: {})", guild.getName(), guild.getId());
            }

            assertTrue(firstPage.size() > 0, "至少应该加入了一个服务器");
        }
    }

    private void testGetGuild(HttpAPI api) {
        Guild guild = api.getGuild(testGuildId);
        assertNotNull(guild, "测试服务器不应为 null");
        assertEquals(testGuildId, guild.getId(), "服务器 ID 应该匹配");
        assertNotNull(guild.getName(), "服务器名称不应为 null");

        logger.info("测试服务器信息: ID={}, 名称={}, 在线人数={}, 总人数={}",
                guild.getId(), guild.getName(), guild.getOnlineUserCount(), guild.getUserCount());

        assertTrue(guild.getUserCount() > 0, "服务器应该至少有一个成员");
    }

    private void testGetTextChannel(HttpAPI api) {
        TextChannel channel = api.getTextChannel(testTextChannelId);
        assertNotNull(channel, "测试文本频道不应为 null");
        assertEquals(testTextChannelId, channel.getId(), "频道 ID 应该匹配");
        assertNotNull(channel.getName(), "频道名称不应为 null");

        logger.info("测试文本频道信息: ID={}, 名称={}",
                channel.getId(), channel.getName());
    }

    private void testGetVoiceChannel(HttpAPI api) {
        VoiceChannel channel = api.getVoiceChannel(testVoiceChannelId);
        assertNotNull(channel, "测试语音频道不应为 null");
        assertEquals(testVoiceChannelId, channel.getId(), "频道 ID 应该匹配");
        assertNotNull(channel.getName(), "频道名称不应为 null");

        logger.info("测试语音频道信息: ID={}, 名称={}",
                channel.getId(), channel.getName());
    }

    private void testGetUser(HttpAPI api) {
        User user = api.getUser(testUserId);
        assertNotNull(user, "测试用户不应为 null");
        assertEquals(testUserId, user.getId(), "用户 ID 应该匹配");
        assertNotNull(user.getName(), "用户名称不应为 null");

        logger.info("测试用户信息: ID={}, 名称={}, 是否机器人={}, 是否 VIP={}",
                user.getId(), user.getName(), user.isBot(), user.isVip());
    }

    private void testGetJoinedVoiceChannels(HttpAPI api) {
        PageIterator<Collection<VoiceChannel>> channels = api.getJoinedVoiceChannels();
        assertNotNull(channels, "已加入的语音频道列表不应为 null");

        if (channels.hasNext()) {
            Collection<VoiceChannel> firstPage = channels.next();
            logger.info("Bot 已加入 {} 个语音频道（第一页）", firstPage.size());

            for (VoiceChannel channel : firstPage) {
                logger.info("  - 语音频道: {} (ID: {})", channel.getName(), channel.getId());
            }
        } else {
            logger.info("Bot 当前未加入任何语音频道");
        }
    }
}
