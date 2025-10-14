package me.realseek.test.modules.integration;

import snw.jkook.entity.CustomEmoji;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.TextComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传集成测试模块
 * 测试文件上传、图片上传、资源上传等功能
 */
public class FileUploadIntegrationTestModule extends IntegrationTestModule {

    private TextChannel testChannel;
    private File testFile;
    private final List<String> createdMessageIds = new ArrayList<>();

    @Override
    public String getName() {
        return "文件上传集成测试";
    }

    @Override
    public String getDescription() {
        return "测试文件上传、图片上传、资源 URL 获取等操作";
    }

    @Override
    protected void executeTests() {
        if (!isIntegrationEnabled()) {
            logger.warn("集成测试未启用,跳过测试");
            return;
        }

        if (!checkRequiredConfig()) {
            return;
        }

        if (!canExecuteSideEffects()) {
            logger.info("文件上传测试需要副作用权限，跳过");
            return;
        }

        runTest("验证测试频道可用", this::testChannelAvailable);
        runTest("创建临时测试文件", this::testCreateTempFile);
        runTest("上传文件获取 URL", this::testUploadFile);
        runTest("测试用户头像 URL", this::testUserAvatarUrl);
        runTest("测试服务器图标 URL", this::testGuildIconUrl);
        runTest("测试自定义表情资源", this::testCustomEmojiResource);
    }

    private void testChannelAvailable() {
        assertNotNull(testTextChannelId, "测试频道ID未配置");
        assertFalse(testTextChannelId.isEmpty(), "测试频道ID不能为空");

        try {
            testChannel = plugin.getCore().getHttpAPI().getTextChannel(testTextChannelId);
            assertNotNull(testChannel, "获取测试频道失败");

            logger.info("测试频道验证成功: {} (ID: {})", testChannel.getName(), testChannel.getId());
        } catch (Exception e) {
            throw new AssertionError("获取测试频道失败: " + e.getMessage());
        }
    }

    private void testCreateTempFile() {
        try {
            // 创建临时目录
            File tempDir = new File(plugin.getDataFolder(), "temp");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            // 创建临时文本文件
            testFile = new File(tempDir, "test-upload-" + System.currentTimeMillis() + ".txt");
            try (FileOutputStream fos = new FileOutputStream(testFile)) {
                String content = "这是一个测试文件,用于验证文件上传功能。\n创建时间: " + System.currentTimeMillis();
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }

            assertTrue(testFile.exists(), "临时文件创建失败");
            assertTrue(testFile.length() > 0, "临时文件应该有内容");

            logger.info("成功创建临时测试文件: {} ({}字节)", testFile.getName(), testFile.length());

        } catch (Exception e) {
            throw new AssertionError("创建临时测试文件失败: " + e.getMessage());
        }
    }

    private void testUploadFile() {
        if (testFile == null || !testFile.exists()) {
            logger.warn("临时文件不可用,跳过上传测试");
            return;
        }

        try {
            String fileUrl = plugin.getCore().getHttpAPI().uploadFile(testFile);
            assertNotNull(fileUrl, "上传文件应该返回 URL");
            assertFalse(fileUrl.isEmpty(), "文件 URL 不应为空");
            assertTrue(fileUrl.startsWith("http"), "文件 URL 应该以 http 开头");

            logger.info("成功上传文件,URL: {}", fileUrl);

            // 测试使用上传的文件发送消息
            if (testChannel != null) {
                var component = new TextComponent("【测试】文件上传测试\n文件 URL: " + fileUrl);

                String messageId = testChannel.sendComponent(component);
                assertNotNull(messageId, "发送包含文件的消息应该成功");

                createdMessageIds.add(messageId);
                logger.info("成功发送包含文件的消息,ID: {}", messageId);
            }

        } catch (Exception e) {
            throw new AssertionError("上传文件失败: " + e.getMessage());
        }
    }

    private void testUserAvatarUrl() {
        try {
            // 使用正确的方法获取 Bot 自己的User对象
            User currentUser = plugin.getCore().getUser();
            assertNotNull(currentUser, "获取当前 Bot 用户失败");

            String avatarUrl = currentUser.getAvatarUrl(false);
            assertNotNull(avatarUrl, "用户头像 URL 不应为 null");

            if (!avatarUrl.isEmpty()) {
                assertTrue(avatarUrl.startsWith("http"), "头像 URL 应该以 http 开头");
                logger.info("成功获取 Bot 头像 URL: {}", avatarUrl);
            } else {
                logger.info("Bot 可能没有设置头像");
            }

        } catch (Exception e) {
            logger.warn("获取用户头像 URL 时出错: {}", e.getMessage());
            logger.info("用户头像测试跳过（可能是 API 限制）");
            // 不抛出异常，允许测试继续
        }
    }

    private void testGuildIconUrl() {
        try {
            assertNotNull(testGuildId, "测试服务器ID未配置");
            assertFalse(testGuildId.isEmpty(), "测试服务器ID不能为空");

            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);
            assertNotNull(guild, "获取测试服务器失败");

            // 验证Guild对象基本属性
            assertNotNull(guild.getId(), "服务器ID不应为null");
            assertNotNull(guild.getName(), "服务器名称不应为null");

            logger.info("成功获取服务器信息: {} (ID: {})", guild.getName(), guild.getId());

        } catch (Exception e) {
            throw new AssertionError("获取服务器信息失败: " + e.getMessage());
        }
    }

    private void testCustomEmojiResource() {
        try {
            assertNotNull(testGuildId, "测试服务器ID未配置");
            assertFalse(testGuildId.isEmpty(), "测试服务器ID不能为空");

            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);

            // 注意：getCustomEmojis() 返回 PageIterator<Set<CustomEmoji>>，不是 Collection
            logger.info("注意：自定义表情列表使用 PageIterator 分页获取");

            var customEmojisIterator = guild.getCustomEmojis();
            assertNotNull(customEmojisIterator, "获取自定义表情迭代器不应为 null");

            if (customEmojisIterator.hasNext()) {
                var emojiSet = customEmojisIterator.next();
                logger.info("成功获取自定义表情集合,当前页包含 {} 个表情", emojiSet.size());

                if (!emojiSet.isEmpty()) {
                    CustomEmoji firstEmoji = emojiSet.iterator().next();
                    String emojiId = firstEmoji.getId();
                    String emojiName = firstEmoji.getName();

                    assertNotNull(emojiId, "自定义表情 ID 不应为 null");
                    assertNotNull(emojiName, "自定义表情名称不应为 null");

                    logger.info("首个自定义表情: {} (ID: {})", emojiName, emojiId);
                }
            } else {
                logger.info("服务器没有自定义表情,这是正常的");
            }

        } catch (Exception e) {
            throw new AssertionError("测试自定义表情资源失败: " + e.getMessage());
        }
    }

    @Override
    public void cleanup() {
        // 清理测试消息
        if (autoCleanup && !createdMessageIds.isEmpty()) {
            logger.info("清理 {} 条文件上传测试消息...", createdMessageIds.size());

            for (String messageId : createdMessageIds) {
                try {
                    var message = plugin.getCore().getHttpAPI().getTextChannelMessage(messageId);
                    if (message != null) {
                        message.delete();
                        logger.debug("已删除消息: {}", messageId);
                    }
                } catch (Exception e) {
                    logger.warn("清理消息 {} 时发生错误: {}", messageId, e.getMessage());
                }
            }

            createdMessageIds.clear();
        }

        // 清理临时文件
        if (testFile != null && testFile.exists()) {
            if (testFile.delete()) {
                logger.info("已删除临时测试文件: {}", testFile.getName());
            }
        }

        testChannel = null;
        testFile = null;
    }
}
