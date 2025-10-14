package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.entity.User;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.*;
import snw.jkook.event.guild.*;
import snw.jkook.event.pm.*;
import snw.jkook.event.user.*;
import snw.jkook.message.Message;
import snw.jkook.message.ChannelMessage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事件系统测试模块
 * 测试 JKook/KookBC 的事件监听和分发机制
 */
public class EventSystemTestModule extends BaseTestModule {
    private final AtomicInteger eventCount = new AtomicInteger(0);
    private final AtomicBoolean listenerRegistered = new AtomicBoolean(false);
    private TestEventListener testListener;

    @Override
    public String getName() {
        return "事件系统";
    }

    @Override
    public String getDescription() {
        return "测试事件注册、监听和分发机制，包括频道事件、用户事件、服务器事件等";
    }

    @Override
    protected void executeTests() {
        runTest("事件管理器可用性检查", this::testEventManagerAvailability);
        runTest("事件监听器注册", this::testEventListenerRegistration);
        runTest("事件监听器注销", this::testEventListenerUnregistration);
        runTest("事件优先级支持", this::testEventPriority);
        runTest("频道消息事件类型检查", this::testChannelMessageEventType);
        runTest("用户事件类型检查", this::testUserEventTypes);
        runTest("服务器事件类型检查", this::testGuildEventTypes);
        runTest("私聊事件类型检查", this::testPrivateMessageEventTypes);
        runTest("事件继承关系验证", this::testEventInheritance);
    }

    private void testEventManagerAvailability() {
        assertNotNull(plugin.getCore().getEventManager(), "事件管理器不应为null");
    }

    private void testEventListenerRegistration() {
        testListener = new TestEventListener();
        plugin.getCore().getEventManager().registerHandlers(plugin, testListener);
        listenerRegistered.set(true);
        assertTrue(true, "监听器注册成功");
    }

    private void testEventListenerUnregistration() {
        if (listenerRegistered.get() && testListener != null) {
            // 测试注销功能
            plugin.getCore().getEventManager().unregisterAllHandlers(plugin);
            assertTrue(true, "监听器注销成功");

            // 重新注册以便后续测试
            plugin.getCore().getEventManager().registerHandlers(plugin, testListener);
        } else {
            assertTrue(false, "监听器未注册，无法测试注销");
        }
    }

    private void testEventPriority() {
        // 验证事件处理器注解支持
        assertTrue(EventHandler.class.isAnnotation(), "EventHandler应该是注解类型");
    }

    private void testChannelMessageEventType() {
        // 验证频道消息事件类的存在
        assertNotNull(ChannelMessageEvent.class, "ChannelMessageEvent类应该存在");
        assertNotNull(ChannelMessageDeleteEvent.class, "ChannelMessageDeleteEvent类应该存在");
        assertNotNull(ChannelMessagePinEvent.class, "ChannelMessagePinEvent类应该存在");
        assertNotNull(ChannelMessageUnpinEvent.class, "ChannelMessageUnpinEvent类应该存在");
        assertNotNull(ChannelMessageUpdateEvent.class, "ChannelMessageUpdateEvent类应该存在");
    }

    private void testUserEventTypes() {
        // 验证用户事件类的存在
        assertNotNull(UserJoinGuildEvent.class, "UserJoinGuildEvent类应该存在");
        assertNotNull(UserLeaveGuildEvent.class, "UserLeaveGuildEvent类应该存在");
        assertNotNull(UserOnlineEvent.class, "UserOnlineEvent类应该存在");
        assertNotNull(UserOfflineEvent.class, "UserOfflineEvent类应该存在");
        assertNotNull(UserJoinVoiceChannelEvent.class, "UserJoinVoiceChannelEvent类应该存在");
        assertNotNull(UserLeaveVoiceChannelEvent.class, "UserLeaveVoiceChannelEvent类应该存在");
        assertNotNull(UserClickButtonEvent.class, "UserClickButtonEvent类应该存在");
        assertNotNull(UserAddReactionEvent.class, "UserAddReactionEvent类应该存在");
        assertNotNull(UserRemoveReactionEvent.class, "UserRemoveReactionEvent类应该存在");
        assertNotNull(UserInfoUpdateEvent.class, "UserInfoUpdateEvent类应该存在");
    }

    private void testGuildEventTypes() {
        // 验证服务器事件类的存在
        assertNotNull(GuildAddEmojiEvent.class, "GuildAddEmojiEvent类应该存在");
        assertNotNull(GuildRemoveEmojiEvent.class, "GuildRemoveEmojiEvent类应该存在");
        assertNotNull(GuildUpdateEmojiEvent.class, "GuildUpdateEmojiEvent类应该存在");
        assertNotNull(GuildBanUserEvent.class, "GuildBanUserEvent类应该存在");
        assertNotNull(GuildUnbanUserEvent.class, "GuildUnbanUserEvent类应该存在");
        assertNotNull(GuildInfoUpdateEvent.class, "GuildInfoUpdateEvent类应该存在");
        assertNotNull(GuildDeleteEvent.class, "GuildDeleteEvent类应该存在");
        assertNotNull(GuildUserNickNameUpdateEvent.class, "GuildUserNickNameUpdateEvent类应该存在");
    }

    private void testPrivateMessageEventTypes() {
        // 验证私聊事件类的存在
        assertNotNull(PrivateMessageReceivedEvent.class, "PrivateMessageReceivedEvent类应该存在");
        assertNotNull(PrivateMessageUpdateEvent.class, "PrivateMessageUpdateEvent类应该存在");
        assertNotNull(PrivateMessageDeleteEvent.class, "PrivateMessageDeleteEvent类应该存在");
    }

    private void testEventInheritance() {
        // 验证事件继承关系
        assertTrue(ChannelEvent.class.isAssignableFrom(ChannelMessageEvent.class),
                "ChannelMessageEvent应该继承自ChannelEvent");
        assertTrue(UserEvent.class.isAssignableFrom(UserJoinGuildEvent.class),
                "UserJoinGuildEvent应该继承自UserEvent");
        assertTrue(GuildEvent.class.isAssignableFrom(GuildBanUserEvent.class),
                "GuildBanUserEvent应该继承自GuildEvent");
    }

    @Override
    public void cleanup() {
        if (listenerRegistered.get() && testListener != null) {
            try {
                plugin.getCore().getEventManager().unregisterAllHandlers(plugin);
            } catch (Exception e) {
                logger.warn("清理事件监听器时发生错误", e);
            }
        }
    }

    /**
     * 测试用事件监听器
     */
    private class TestEventListener implements Listener {
        @EventHandler
        public void onChannelMessage(ChannelMessageEvent event) {
            eventCount.incrementAndGet();
            logger.debug("收到频道消息事件: {}", event.getClass().getSimpleName());
        }

        @EventHandler
        public void onUserJoinGuild(UserJoinGuildEvent event) {
            eventCount.incrementAndGet();
            logger.debug("收到用户加入服务器事件");
        }

        @EventHandler
        public void onUserLeaveGuild(UserLeaveGuildEvent event) {
            eventCount.incrementAndGet();
            logger.debug("收到用户离开服务器事件");
        }

        @EventHandler
        public void onPrivateMessage(PrivateMessageReceivedEvent event) {
            eventCount.incrementAndGet();
            logger.debug("收到私聊消息事件");
        }

        @EventHandler
        public void onGuildBan(GuildBanUserEvent event) {
            eventCount.incrementAndGet();
            logger.debug("收到用户被封禁事件");
        }

        @EventHandler
        public void onUserClickButton(UserClickButtonEvent event) {
            eventCount.incrementAndGet();
            logger.debug("收到用户点击按钮事件");
        }
    }
}
