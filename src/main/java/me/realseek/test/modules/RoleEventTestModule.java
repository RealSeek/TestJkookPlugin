package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.role.RoleCreateEvent;
import snw.jkook.event.role.RoleDeleteEvent;
import snw.jkook.event.role.RoleInfoUpdateEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Role 事件测试模块
 * 测试角色相关事件的监听和处理功能
 */
public class RoleEventTestModule extends BaseTestModule implements Listener {

    private final AtomicBoolean roleCreateEventReceived = new AtomicBoolean(false);
    private final AtomicBoolean roleDeleteEventReceived = new AtomicBoolean(false);
    private final AtomicBoolean roleInfoUpdateEventReceived = new AtomicBoolean(false);
    private CountDownLatch eventLatch;

    @Override
    public String getName() {
        return "Role事件监听";
    }

    @Override
    public String getDescription() {
        return "测试 Role 事件（创建、删除、更新）的监听器注册和事件处理";
    }

    @Override
    protected void executeTests() {
        runTest("验证 RoleEvent 类型存在", this::testRoleEventClassesExist);
        runTest("注册 Role 事件监听器", this::testRegisterRoleEventListeners);
        runTest("验证 RoleCreateEvent 结构", this::testRoleCreateEventStructure);
        runTest("验证 RoleDeleteEvent 结构", this::testRoleDeleteEventStructure);
        runTest("验证 RoleInfoUpdateEvent 结构", this::testRoleInfoUpdateEventStructure);
        runTest("验证事件监听器方法签名", this::testEventHandlerMethods);
    }

    private void testRoleEventClassesExist() {
        try {
            Class<?> roleEventClass = Class.forName("snw.jkook.event.role.RoleEvent");
            assertNotNull(roleEventClass, "RoleEvent 类应该存在");

            Class<?> roleCreateEventClass = Class.forName("snw.jkook.event.role.RoleCreateEvent");
            assertNotNull(roleCreateEventClass, "RoleCreateEvent 类应该存在");

            Class<?> roleDeleteEventClass = Class.forName("snw.jkook.event.role.RoleDeleteEvent");
            assertNotNull(roleDeleteEventClass, "RoleDeleteEvent 类应该存在");

            Class<?> roleInfoUpdateEventClass = Class.forName("snw.jkook.event.role.RoleInfoUpdateEvent");
            assertNotNull(roleInfoUpdateEventClass, "RoleInfoUpdateEvent 类应该存在");

            logger.info("所有 Role 事件类型验证通过(4个事件类)");
        } catch (Exception e) {
            throw new AssertionError("Role 事件类验证失败: " + e.getMessage());
        }
    }

    private void testRegisterRoleEventListeners() {
        try {
            // 注册监听器
            plugin.getCore().getEventManager().registerHandlers(plugin, this);

            logger.info("成功注册 Role 事件监听器");

            // 说明：实际事件触发需要在 Kook 客户端操作
            logger.info("注意：实际事件触发需要手动在服务器中创建/删除/修改角色");

        } catch (Exception e) {
            throw new AssertionError("注册 Role 事件监听器失败: " + e.getMessage());
        }
    }

    private void testRoleCreateEventStructure() {
        try {
            Class<?> eventClass = Class.forName("snw.jkook.event.role.RoleCreateEvent");

            // 验证事件方法
            assertNotNull(eventClass.getMethod("getRole"), "getRole 方法应该存在");

            logger.info("RoleCreateEvent 结构验证通过");
        } catch (Exception e) {
            throw new AssertionError("RoleCreateEvent 结构验证失败: " + e.getMessage());
        }
    }

    private void testRoleDeleteEventStructure() {
        try {
            Class<?> eventClass = Class.forName("snw.jkook.event.role.RoleDeleteEvent");

            // 验证事件方法
            assertNotNull(eventClass.getMethod("getRole"), "getRole 方法应该存在");

            logger.info("RoleDeleteEvent 结构验证通过");
        } catch (Exception e) {
            throw new AssertionError("RoleDeleteEvent 结构验证失败: " + e.getMessage());
        }
    }

    private void testRoleInfoUpdateEventStructure() {
        try {
            Class<?> eventClass = Class.forName("snw.jkook.event.role.RoleInfoUpdateEvent");

            // 验证事件方法
            assertNotNull(eventClass.getMethod("getRole"), "getRole 方法应该存在");

            logger.info("RoleInfoUpdateEvent 结构验证通过");
        } catch (Exception e) {
            throw new AssertionError("RoleInfoUpdateEvent 结构验证失败: " + e.getMessage());
        }
    }

    private void testEventHandlerMethods() {
        try {
            // 验证事件处理方法存在并且有 @EventHandler 注解
            var onRoleCreateMethod = this.getClass().getMethod("onRoleCreate", RoleCreateEvent.class);
            assertNotNull(onRoleCreateMethod.getAnnotation(EventHandler.class),
                    "onRoleCreate 应该有 @EventHandler 注解");

            var onRoleDeleteMethod = this.getClass().getMethod("onRoleDelete", RoleDeleteEvent.class);
            assertNotNull(onRoleDeleteMethod.getAnnotation(EventHandler.class),
                    "onRoleDelete 应该有 @EventHandler 注解");

            var onRoleInfoUpdateMethod = this.getClass().getMethod("onRoleInfoUpdate", RoleInfoUpdateEvent.class);
            assertNotNull(onRoleInfoUpdateMethod.getAnnotation(EventHandler.class),
                    "onRoleInfoUpdate 应该有 @EventHandler 注解");

            logger.info("事件监听器方法签名验证通过(3个处理方法)");
        } catch (Exception e) {
            throw new AssertionError("事件处理方法验证失败: " + e.getMessage());
        }
    }

    // ====== 事件处理器 ======

    @EventHandler
    public void onRoleCreate(RoleCreateEvent event) {
        logger.info("收到 RoleCreateEvent: 角色 {} 已创建 (ID: {})",
                event.getRole().getName(), event.getRole().getId());
        roleCreateEventReceived.set(true);
        if (eventLatch != null) {
            eventLatch.countDown();
        }
    }

    @EventHandler
    public void onRoleDelete(RoleDeleteEvent event) {
        logger.info("收到 RoleDeleteEvent: 角色 {} 已删除 (ID: {})",
                event.getRole().getName(), event.getRole().getId());
        roleDeleteEventReceived.set(true);
        if (eventLatch != null) {
            eventLatch.countDown();
        }
    }

    @EventHandler
    public void onRoleInfoUpdate(RoleInfoUpdateEvent event) {
        logger.info("收到 RoleInfoUpdateEvent: 角色 {} 信息已更新 (ID: {})",
                event.getRole().getName(), event.getRole().getId());
        roleInfoUpdateEventReceived.set(true);
        if (eventLatch != null) {
            eventLatch.countDown();
        }
    }

    /**
     * 等待事件触发（用于集成测试）
     * 注意：此方法需要手动操作才能触发事件
     */
    public boolean waitForEvents(long timeoutSeconds) {
        try {
            eventLatch = new CountDownLatch(3);
            return eventLatch.await(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 检查是否收到了角色创建事件
     */
    public boolean isRoleCreateEventReceived() {
        return roleCreateEventReceived.get();
    }

    /**
     * 检查是否收到了角色删除事件
     */
    public boolean isRoleDeleteEventReceived() {
        return roleDeleteEventReceived.get();
    }

    /**
     * 检查是否收到了角色信息更新事件
     */
    public boolean isRoleInfoUpdateEventReceived() {
        return roleInfoUpdateEventReceived.get();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        // 注销监听器
        try {
            plugin.getCore().getEventManager().unregisterAllHandlers(plugin);
            logger.info("已注销 Role 事件监听器");
        } catch (Exception e) {
            logger.warn("注销 Role 事件监听器失败", e);
        }

        roleCreateEventReceived.set(false);
        roleDeleteEventReceived.set(false);
        roleInfoUpdateEventReceived.set(false);
        eventLatch = null;
    }
}
