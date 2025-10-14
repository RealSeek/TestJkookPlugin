package me.realseek.test.modules.integration;

import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.TextChannelMessage;
import snw.jkook.message.component.MarkdownComponent;
import snw.jkook.message.component.TextComponent;
import snw.jkook.message.component.card.*;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.module.*;
import snw.jkook.util.PageIterator;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息发送和接收集成测试模块
 * 测试实际的消息发送、接收、更新、删除等功能
 */
public class MessageIntegrationTestModule extends IntegrationTestModule {
    private final List<String> createdMessageIds = new ArrayList<>();

    @Override
    public String getName() {
        return "消息集成测试";
    }

    @Override
    public String getDescription() {
        return "测试消息的实际发送、接收、更新和删除功能";
    }

    @Override
    protected void executeTests() {
        if (!isIntegrationEnabled()) {
            logger.warn("集成测试未启用");
            return;
        }

        if (!checkRequiredConfig()) {
            return;
        }

        if (!canExecuteSideEffects()) {
            logger.info("消息测试需要副作用权限，跳过");
            return;
        }

        TextChannel channel = plugin.getCore().getHttpAPI().getTextChannel(testTextChannelId);
        assertNotNull(channel, "测试频道不应为 null");

        runTest("发送纯文本消息", () -> testSendTextMessage(channel));
        runTest("发送 Markdown 消息", () -> testSendMarkdownMessage(channel));
        runTest("发送卡片消息", () -> testSendCardMessage(channel));
        runTest("获取频道历史消息", () -> testGetChannelMessages(channel));
        runTest("更新消息", () -> testUpdateMessage(channel));
        runTest("删除消息", () -> testDeleteMessage(channel));
    }

    private void testSendTextMessage(TextChannel channel) {
        String content = "【测试】这是一条纯文本测试消息 - " + System.currentTimeMillis();
        String messageId = channel.sendComponent(new TextComponent(content));

        assertNotNull(messageId, "消息 ID 不应为 null");
        assertFalse(messageId.isEmpty(), "消息 ID 不应为空");
        createdMessageIds.add(messageId);

        logger.info("成功发送纯文本消息，ID: {}", messageId);
    }

    private void testSendMarkdownMessage(TextChannel channel) {
        String content = "【测试】**这是粗体** *这是斜体* ~~这是删除线~~ - " + System.currentTimeMillis();
        String messageId = channel.sendComponent(new MarkdownComponent(content));

        assertNotNull(messageId, "消息 ID 不应为 null");
        assertFalse(messageId.isEmpty(), "消息 ID 不应为空");
        createdMessageIds.add(messageId);

        logger.info("成功发送 Markdown 消息，ID: {}", messageId);
    }

    private void testSendCardMessage(TextChannel channel) {
        MultipleCardComponent card = new CardBuilder()
                .setTheme(Theme.INFO)
                .setSize(Size.LG)
                .addModule(new HeaderModule(new PlainTextElement("【测试】卡片消息测试")))
                .addModule(new SectionModule(new MarkdownElement(
                        "**测试时间**: " + System.currentTimeMillis() + "\n" +
                        "**测试模块**: 消息集成测试\n" +
                        "**状态**: 正常运行"
                )))
                .addModule(new ContextModule.Builder()
                        .add(new PlainTextElement("这是一条测试卡片消息"))
                        .build())
                .build();

        String messageId = channel.sendComponent(card);

        assertNotNull(messageId, "消息 ID 不应为 null");
        assertFalse(messageId.isEmpty(), "消息 ID 不应为空");
        createdMessageIds.add(messageId);

        logger.info("成功发送卡片消息，ID: {}", messageId);
    }

    private void testGetChannelMessages(TextChannel channel) {
        PageIterator<Collection<snw.jkook.message.ChannelMessage>> messages =
                channel.getMessages(null, false, "before");

        assertNotNull(messages, "消息迭代器不应为 null");

        if (messages.hasNext()) {
            Collection<snw.jkook.message.ChannelMessage> firstPage = messages.next();
            assertNotNull(firstPage, "第一页消息不应为 null");

            logger.info("获取到 {} 条历史消息", firstPage.size());

            int count = 0;
            for (snw.jkook.message.ChannelMessage msg : firstPage) {
                if (count++ < 3) { // 只打印前3条
                    logger.info("  - 消息 ID: {}, 发送者: {}, 时间: {}",
                            msg.getId(),
                            msg.getSender().getName(),
                            msg.getTimeStamp());
                }
            }

            assertTrue(firstPage.size() > 0, "应该至少有一条历史消息");
        }
    }

    private void testUpdateMessage(TextChannel channel) {
        if (createdMessageIds.isEmpty()) {
            logger.warn("没有可更新的测试消息，跳过此测试");
            return;
        }

        String messageId = createdMessageIds.get(0);
        TextChannelMessage message = plugin.getCore().getHttpAPI().getTextChannelMessage(messageId);

        if (message != null) {
            String newContent = "【测试】这条消息已被更新 - " + System.currentTimeMillis();
            message.setComponent(new TextComponent(newContent));

            logger.info("成功更新消息，ID: {}", messageId);
        } else {
            logger.warn("无法找到要更新的消息，ID: {}", messageId);
        }
    }

    private void testDeleteMessage(TextChannel channel) {
        if (autoCleanup && !createdMessageIds.isEmpty()) {
            String messageId = createdMessageIds.get(createdMessageIds.size() - 1);

            try {
                TextChannelMessage message = plugin.getCore().getHttpAPI().getTextChannelMessage(messageId);
                if (message != null) {
                    message.delete();
                    logger.info("成功删除测试消息，ID: {}", messageId);
                    createdMessageIds.remove(createdMessageIds.size() - 1);
                }
            } catch (Exception e) {
                logger.warn("删除消息时发生错误: {}", e.getMessage());
            }
        } else {
            logger.info("自动清理已禁用或没有可删除的消息，跳过删除测试");
        }
    }

    @Override
    public void cleanup() {
        if (autoCleanup && !createdMessageIds.isEmpty()) {
            logger.info("清理 {} 条测试消息...", createdMessageIds.size());

            for (String messageId : createdMessageIds) {
                try {
                    TextChannelMessage message = plugin.getCore().getHttpAPI().getTextChannelMessage(messageId);
                    if (message != null) {
                        message.delete();
                        logger.debug("已删除消息: {}", messageId);
                    }
                } catch (Exception e) {
                    logger.warn("清理消息 {} 时发生错误: {}", messageId, e.getMessage());
                }
            }

            createdMessageIds.clear();
            logger.info("测试消息清理完成");
        }
    }
}
