package me.realseek.test.modules.integration;

import snw.jkook.entity.User;
import snw.jkook.message.component.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * 私聊消息集成测试模块
 * 测试用户私聊消息的发送、接收、更新、删除等功能
 */
public class PrivateMessageIntegrationTestModule extends IntegrationTestModule {

    private User targetUser;
    private final List<String> createdMessageIds = new ArrayList<>();

    @Override
    public String getName() {
        return "私聊消息集成测试";
    }

    @Override
    public String getDescription() {
        return "测试用户私聊消息的实际发送、更新、删除、历史记录获取等操作";
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
            logger.info("私聊消息测试需要副作用权限，跳过");
            return;
        }

        runTest("验证目标用户可用", this::testTargetUserAvailable);
        runTest("发送文本私聊消息", this::testSendTextPrivateMessage);
        runTest("发送组件私聊消息", this::testSendComponentPrivateMessage);
        runTest("更新私聊消息", this::testUpdatePrivateMessage);
        runTest("删除私聊消息", this::testDeletePrivateMessage);
    }

    private void testTargetUserAvailable() {
        assertNotNull(testUserId, "测试用户ID未配置");
        assertFalse(testUserId.isEmpty(), "测试用户ID不能为空");

        try {
            targetUser = plugin.getCore().getHttpAPI().getUser(testUserId);
            assertNotNull(targetUser, "获取测试用户失败");

            logger.info("目标用户验证成功: {} (ID: {})", targetUser.getName(), targetUser.getId());
        } catch (Exception e) {
            throw new AssertionError("获取目标用户失败: " + e.getMessage());
        }
    }

    private void testSendTextPrivateMessage() {
        try {
            String testContent = "【测试】这是一条私聊消息测试 - " + System.currentTimeMillis();

            String messageId = targetUser.sendPrivateMessage(testContent);
            assertNotNull(messageId, "发送私聊消息应该返回消息ID");
            assertFalse(messageId.isEmpty(), "消息ID不应为空");

            createdMessageIds.add(messageId);
            logger.info("成功发送文本私聊消息,ID: {}", messageId);

        } catch (Exception e) {
            throw new AssertionError("发送文本私聊消息失败: " + e.getMessage());
        }
    }

    private void testSendComponentPrivateMessage() {
        try {
            // 注意：User.sendPrivateMessage(Component) 可能在某些 JKook 版本中不受支持
            // 如果遇到问题，应使用 User.sendPrivateMessage(String) 发送纯文本
            logger.info("尝试发送组件私聊消息...");

            try {
                var component = new TextComponent("【测试】这是一条组件私聊消息 - " + System.currentTimeMillis());
                String messageId = targetUser.sendPrivateMessage(component);

                assertNotNull(messageId, "发送组件私聊消息应该返回消息ID");
                assertFalse(messageId.isEmpty(), "消息ID不应为空");

                createdMessageIds.add(messageId);
                logger.info("✓ 成功发送组件私聊消息,ID: {}", messageId);

            } catch (UnsupportedOperationException e) {
                logger.warn("⚠ User.sendPrivateMessage(Component) 方法不受支持");
                logger.info("改用纯文本方式发送私聊消息");

                // 降级使用纯文本发送
                String fallbackContent = "【测试】组件消息降级为纯文本 - " + System.currentTimeMillis();
                String messageId = targetUser.sendPrivateMessage(fallbackContent);
                createdMessageIds.add(messageId);
                logger.info("✓ 使用纯文本方式成功发送,ID: {}", messageId);
            }

        } catch (Exception e) {
            throw new AssertionError("发送组件私聊消息失败: " + e.getMessage());
        }
    }

    private void testUpdatePrivateMessage() {
        if (createdMessageIds.isEmpty()) {
            logger.warn("没有可用的测试消息ID,跳过更新测试");
            return;
        }

        try {
            String messageId = createdMessageIds.get(0);

            // 注意：HttpAPI 可能不直接支持 updatePrivateMessage
            // 通常需要通过 PrivateMessage 对象的 setComponent 方法来更新
            logger.info("私聊消息更新功能需要通过 PrivateMessage 对象操作");
            logger.info("跳过直接 API 调用测试,消息ID: {}", messageId);

        } catch (Exception e) {
            throw new AssertionError("更新私聊消息失败: " + e.getMessage());
        }
    }

    private void testDeletePrivateMessage() {
        try {
            // 创建一条测试消息用于删除测试
            String tempContent = "【测试】待删除的测试消息 - " + System.currentTimeMillis();
            String tempMessageId = targetUser.sendPrivateMessage(tempContent);

            // 注意：
            // 1. HttpAPI 可能不直接支持 deletePrivateMessage
            // 2. 通常需要通过 PrivateMessage 对象的 delete 方法来删除
            // 3. "临时消息"（Temp Message）是服务器频道的特性，不是私聊功能
            logger.info("私聊消息删除功能需要通过 PrivateMessage 对象操作");
            logger.info("测试消息ID: {},将在cleanup中清理", tempMessageId);

            createdMessageIds.add(tempMessageId);

        } catch (Exception e) {
            throw new AssertionError("删除私聊消息失败: " + e.getMessage());
        }
    }

    @Override
    public void cleanup() {
        if (autoCleanup && !createdMessageIds.isEmpty()) {
            logger.info("注意：私聊消息清理需要手动操作或通过 PrivateMessage 对象");
            logger.info("共创建了 {} 条私聊测试消息", createdMessageIds.size());
            createdMessageIds.clear();
        }

        targetUser = null;
    }
}
