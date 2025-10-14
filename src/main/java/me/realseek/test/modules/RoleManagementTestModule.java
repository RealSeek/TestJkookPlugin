package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;

/**
 * Role 管理测试模块
 * 测试角色的属性、权限检查、管理操作等功能
 */
public class RoleManagementTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "Role管理";
    }

    @Override
    public String getDescription() {
        return "测试 Role 实体的管理功能，包括权限、颜色、位置、属性设置等";
    }

    @Override
    protected void executeTests() {
        runTest("验证 Role 接口存在", this::testRoleInterfaceExists);
        runTest("测试 Role 基本属性方法", this::testRoleBasicMethods);
        runTest("测试 Role 权限相关方法", this::testRolePermissionMethods);
        runTest("测试 Role 属性设置方法", this::testRolePropertySetters);
        runTest("测试 Role 状态检查方法", this::testRoleStatusMethods);
        runTest("验证 Role 继承关系", this::testRoleInheritance);
        runTest("测试 Role 事件类型", this::testRoleEventTypes);
    }

    private void testRoleInterfaceExists() {
        try {
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");
            assertNotNull(roleClass, "Role 接口应该存在");
            assertTrue(roleClass.isInterface(), "Role 应该是接口");

            logger.info("Role 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("Role 接口验证失败: " + e.getMessage());
        }
    }

    private void testRoleBasicMethods() {
        try {
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");

            // 验证基本方法
            assertNotNull(roleClass.getMethod("getGuild"), "getGuild 方法应该存在");
            assertNotNull(roleClass.getMethod("getId"), "getId 方法应该存在");
            assertNotNull(roleClass.getMethod("getName"), "getName 方法应该存在");
            assertNotNull(roleClass.getMethod("getColor"), "getColor 方法应该存在");
            assertNotNull(roleClass.getMethod("getPosition"), "getPosition 方法应该存在");

            logger.info("Role 基本属性方法验证通过（5个方法）");
        } catch (Exception e) {
            throw new AssertionError("Role 基本方法验证失败: " + e.getMessage());
        }
    }

    private void testRolePermissionMethods() {
        try {
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");
            Class<?> permissionClass = Class.forName("snw.jkook.Permission");

            // 验证权限相关方法
            assertNotNull(roleClass.getMethod("isPermissionSet", permissionClass),
                    "isPermissionSet 方法应该存在");
            assertNotNull(roleClass.getMethod("setPermissions", int.class),
                    "setPermissions 方法应该存在");

            logger.info("Role 权限方法验证通过（2个方法）");
        } catch (Exception e) {
            throw new AssertionError("Role 权限方法验证失败: " + e.getMessage());
        }
    }

    private void testRolePropertySetters() {
        try {
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");

            // 验证属性设置方法
            assertNotNull(roleClass.getMethod("setMentionable", boolean.class),
                    "setMentionable 方法应该存在");
            assertNotNull(roleClass.getMethod("setHoist", boolean.class),
                    "setHoist 方法应该存在");
            assertNotNull(roleClass.getMethod("delete"), "delete 方法应该存在");

            logger.info("Role 属性设置方法验证通过（3个方法）");
        } catch (Exception e) {
            throw new AssertionError("Role 属性设置方法验证失败: " + e.getMessage());
        }
    }

    private void testRoleStatusMethods() {
        try {
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");

            // 验证状态检查方法
            assertNotNull(roleClass.getMethod("isMentionable"), "isMentionable 方法应该存在");
            assertNotNull(roleClass.getMethod("isHoist"), "isHoist 方法应该存在");

            logger.info("Role 状态检查方法验证通过（2个方法）");
        } catch (Exception e) {
            throw new AssertionError("Role 状态方法验证失败: " + e.getMessage());
        }
    }

    private void testRoleInheritance() {
        try {
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");
            Class<?> nameableClass = Class.forName("snw.jkook.entity.abilities.Nameable");

            // 验证 Role 继承了 Nameable
            assertTrue(nameableClass.isAssignableFrom(roleClass),
                    "Role 应该继承 Nameable 接口");

            logger.info("Role 继承关系验证通过");
        } catch (Exception e) {
            throw new AssertionError("Role 继承关系验证失败: " + e.getMessage());
        }
    }

    private void testRoleEventTypes() {
        try {
            // 验证 Role 相关事件类型
            Class<?> roleEventClass = Class.forName("snw.jkook.event.role.RoleEvent");
            assertNotNull(roleEventClass, "RoleEvent 类应该存在");

            Class<?> roleCreateEventClass = Class.forName("snw.jkook.event.role.RoleCreateEvent");
            assertNotNull(roleCreateEventClass, "RoleCreateEvent 类应该存在");

            Class<?> roleDeleteEventClass = Class.forName("snw.jkook.event.role.RoleDeleteEvent");
            assertNotNull(roleDeleteEventClass, "RoleDeleteEvent 类应该存在");

            Class<?> roleInfoUpdateEventClass = Class.forName("snw.jkook.event.role.RoleInfoUpdateEvent");
            assertNotNull(roleInfoUpdateEventClass, "RoleInfoUpdateEvent 类应该存在");

            // 验证事件继承关系
            assertTrue(roleEventClass.isAssignableFrom(roleCreateEventClass),
                    "RoleCreateEvent 应该继承自 RoleEvent");
            assertTrue(roleEventClass.isAssignableFrom(roleDeleteEventClass),
                    "RoleDeleteEvent 应该继承自 RoleEvent");
            assertTrue(roleEventClass.isAssignableFrom(roleInfoUpdateEventClass),
                    "RoleInfoUpdateEvent 应该继承自 RoleEvent");

            logger.info("Role 事件类型验证通过（3个事件 + 基类）");
        } catch (Exception e) {
            throw new AssertionError("Role 事件类型验证失败: " + e.getMessage());
        }
    }
}
