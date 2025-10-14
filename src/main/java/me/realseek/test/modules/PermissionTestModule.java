package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.Permission;
import snw.jkook.permissions.PermissionDefault;
import snw.jkook.permissions.PermissionNode;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限系统测试模块
 * 测试 JKook 的权限枚举、权限计算和权限节点系统
 */
public class PermissionTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "权限系统";
    }

    @Override
    public String getDescription() {
        return "测试权限枚举值、权限计算逻辑和权限节点系统";
    }

    @Override
    protected void executeTests() {
        // 权限枚举测试
        runTest("验证核心权限枚举存在", this::testPermissionEnumExists);
        runTest("测试 ADMIN 权限值", this::testAdminPermission);
        runTest("测试权限值计算", this::testPermissionValueCalculation);

        // 权限计算测试
        runTest("测试 hasPermission 方法", this::testHasPermission);
        runTest("测试 ADMIN 权限包含所有权限", this::testAdminIncludesAll);
        runTest("测试 sum 方法（单个权限）", this::testSumSinglePermission);
        runTest("测试 sum 方法（多个权限）", this::testSumMultiplePermissions);
        runTest("测试 sum 方法（累加权限）", this::testSumAddPermissions);
        runTest("测试 removeFrom 方法", this::testRemoveFromPermissions);
        runTest("测试 isIncludedIn 方法", this::testIsIncludedIn);

        // 权限节点测试
        runTest("创建基本权限节点", this::testCreatePermissionNode);
        runTest("测试权限节点描述", this::testPermissionNodeDescription);
        runTest("测试权限节点默认值", this::testPermissionNodeDefault);
        runTest("测试权限节点子权限", this::testPermissionNodeChildren);
        runTest("测试 PermissionDefault 枚举", this::testPermissionDefaultEnum);
    }

    private void testPermissionEnumExists() {
        // 验证核心权限枚举存在
        assertNotNull(Permission.ADMIN, "ADMIN 权限不应为 null");
        assertNotNull(Permission.OPERATOR, "OPERATOR 权限不应为 null");
        assertNotNull(Permission.KICK, "KICK 权限不应为 null");
        assertNotNull(Permission.BAN, "BAN 权限不应为 null");
        assertNotNull(Permission.SEND_MESSAGE, "SEND_MESSAGE 权限不应为 null");
        assertNotNull(Permission.MESSAGE_MANAGE, "MESSAGE_MANAGE 权限不应为 null");
        assertNotNull(Permission.CHANNEL_MANAGE, "CHANNEL_MANAGE 权限不应为 null");
        assertNotNull(Permission.ROLE_MANAGE, "ROLE_MANAGE 权限不应为 null");

        logger.info("已验证 8 个核心权限枚举");
    }

    private void testAdminPermission() {
        assertEquals(1, Permission.ADMIN.getValue(), "ADMIN 权限值应该为 1");
        logger.info("ADMIN 权限值: {}", Permission.ADMIN.getValue());
    }

    private void testPermissionValueCalculation() {
        // 测试不同权限的值
        assertEquals(1, Permission.ADMIN.getValue(), "ADMIN 值应为 1");
        assertEquals(2, Permission.OPERATOR.getValue(), "OPERATOR 值应为 2");
        assertEquals(4, Permission.ADMIN_LOG.getValue(), "ADMIN_LOG 值应为 4");
        assertEquals(8, Permission.INVITE.getValue(), "INVITE 值应为 8");
        assertEquals(64, Permission.KICK.getValue(), "KICK 值应为 64");
        assertEquals(128, Permission.BAN.getValue(), "BAN 值应为 128");

        logger.info("权限值计算正确（2的幂次方）");
    }

    private void testHasPermission() {
        // 测试单个权限检查
        int kickPerm = Permission.KICK.getValue();
        assertTrue(Permission.hasPermission(Permission.KICK, kickPerm),
                "应该包含 KICK 权限");
        assertFalse(Permission.hasPermission(Permission.BAN, kickPerm),
                "不应该包含 BAN 权限");

        logger.info("hasPermission 方法工作正常");
    }

    private void testAdminIncludesAll() {
        // ADMIN 权限应该包含所有其他权限
        int adminPerm = Permission.ADMIN.getValue();

        assertTrue(Permission.hasPermission(Permission.KICK, adminPerm),
                "ADMIN 应包含 KICK 权限");
        assertTrue(Permission.hasPermission(Permission.BAN, adminPerm),
                "ADMIN 应包含 BAN 权限");
        assertTrue(Permission.hasPermission(Permission.SEND_MESSAGE, adminPerm),
                "ADMIN 应包含 SEND_MESSAGE 权限");
        assertTrue(Permission.hasPermission(Permission.CHANNEL_MANAGE, adminPerm),
                "ADMIN 应包含 CHANNEL_MANAGE 权限");

        logger.info("ADMIN 权限正确包含所有其他权限");
    }

    private void testSumSinglePermission() {
        int result = Permission.sum(Permission.KICK);
        assertEquals(Permission.KICK.getValue(), result,
                "单个权限求和应该等于该权限值");

        logger.info("单个权限求和: {}", result);
    }

    private void testSumMultiplePermissions() {
        // 测试多个权限求和
        int result = Permission.sum(Permission.KICK, Permission.BAN);
        int expected = Permission.KICK.getValue() + Permission.BAN.getValue();
        assertEquals(expected, result, "多个权限求和应该等于各权限值之和");

        // 验证结果包含这两个权限
        assertTrue(Permission.hasPermission(Permission.KICK, result),
                "结果应包含 KICK 权限");
        assertTrue(Permission.hasPermission(Permission.BAN, result),
                "结果应包含 BAN 权限");

        logger.info("多个权限求和: {} (KICK={}, BAN={})",
                result, Permission.KICK.getValue(), Permission.BAN.getValue());
    }

    private void testSumAddPermissions() {
        // 测试在现有权限基础上累加
        int base = Permission.KICK.getValue();
        int result = Permission.sum(base, Permission.BAN, Permission.SEND_MESSAGE);

        assertTrue(Permission.hasPermission(Permission.KICK, result),
                "结果应包含基础权限 KICK");
        assertTrue(Permission.hasPermission(Permission.BAN, result),
                "结果应包含新增权限 BAN");
        assertTrue(Permission.hasPermission(Permission.SEND_MESSAGE, result),
                "结果应包含新增权限 SEND_MESSAGE");

        logger.info("累加权限计算正确: {}", result);
    }

    private void testRemoveFromPermissions() {
        // 创建包含多个权限的值
        int combined = Permission.sum(Permission.KICK, Permission.BAN, Permission.SEND_MESSAGE);

        // 移除 BAN 权限
        int result = Permission.removeFrom(combined, Permission.BAN);

        assertTrue(Permission.hasPermission(Permission.KICK, result),
                "KICK 权限应该保留");
        assertFalse(Permission.hasPermission(Permission.BAN, result),
                "BAN 权限应该被移除");
        assertTrue(Permission.hasPermission(Permission.SEND_MESSAGE, result),
                "SEND_MESSAGE 权限应该保留");

        logger.info("权限移除功能正常：从 {} 移除 BAN 后得到 {}", combined, result);
    }

    private void testIsIncludedIn() {
        int permissions = Permission.sum(Permission.KICK, Permission.BAN);

        assertTrue(Permission.KICK.isIncludedIn(permissions),
                "KICK 应该包含在权限集合中");
        assertTrue(Permission.BAN.isIncludedIn(permissions),
                "BAN 应该包含在权限集合中");
        assertFalse(Permission.SEND_MESSAGE.isIncludedIn(permissions),
                "SEND_MESSAGE 不应该包含在权限集合中");

        logger.info("isIncludedIn 方法工作正常");
    }

    private void testCreatePermissionNode() {
        PermissionNode node = new PermissionNode("test.permission");

        assertNotNull(node, "权限节点不应为 null");
        assertEquals("test.permission", node.getName(), "权限节点名称应该匹配");

        logger.info("成功创建权限节点: {}", node.getName());
    }

    private void testPermissionNodeDescription() {
        String desc = "This is a test permission";
        PermissionNode node = new PermissionNode("test.permission", desc);

        assertEquals(desc, node.getDescription(), "权限节点描述应该匹配");

        logger.info("权限节点描述设置正确");
    }

    private void testPermissionNodeDefault() {
        PermissionNode node1 = new PermissionNode("test.permission1");
        assertEquals(PermissionDefault.FALSE, node1.getDefault(),
                "默认权限默认值应该为 FALSE");

        PermissionNode node2 = new PermissionNode("test.permission2", PermissionDefault.TRUE);
        assertEquals(PermissionDefault.TRUE, node2.getDefault(),
                "自定义默认值应该被正确设置");

        // 测试修改默认值
        node2.setDefault(PermissionDefault.FALSE);
        assertEquals(PermissionDefault.FALSE, node2.getDefault(),
                "修改后的默认值应该被保存");

        logger.info("权限节点默认值功能正常");
    }

    private void testPermissionNodeChildren() {
        Map<String, Boolean> children = new HashMap<>();
        children.put("test.child1", true);
        children.put("test.child2", false);

        PermissionNode node = new PermissionNode("test.parent", children);

        Map<String, Boolean> retrievedChildren = node.getChildren();
        assertNotNull(retrievedChildren, "子权限映射不应为 null");
        assertEquals(2, retrievedChildren.size(), "应该有 2 个子权限");
        assertTrue(retrievedChildren.containsKey("test.child1"), "应该包含 child1");
        assertTrue(retrievedChildren.containsKey("test.child2"), "应该包含 child2");
        assertEquals(true, retrievedChildren.get("test.child1"), "child1 应该为 true");
        assertEquals(false, retrievedChildren.get("test.child2"), "child2 应该为 false");

        logger.info("权限节点子权限功能正常，包含 {} 个子权限", retrievedChildren.size());
    }

    private void testPermissionDefaultEnum() {
        // 测试所有 PermissionDefault 枚举值
        assertNotNull(PermissionDefault.TRUE, "TRUE 不应为 null");
        assertNotNull(PermissionDefault.FALSE, "FALSE 不应为 null");

        // 测试通过名称获取
        assertEquals(PermissionDefault.TRUE,
                PermissionDefault.getByName("true"),
                "通过名称获取 TRUE 应该成功");
        assertEquals(PermissionDefault.FALSE,
                PermissionDefault.getByName("false"),
                "通过名称获取 FALSE 应该成功");

        // 测试 getValue 方法
        assertTrue(PermissionDefault.TRUE.getValue(), "TRUE.getValue() 应该返回 true");
        assertFalse(PermissionDefault.FALSE.getValue(), "FALSE.getValue() 应该返回 false");

        logger.info("PermissionDefault 枚举功能正常");
    }
}
