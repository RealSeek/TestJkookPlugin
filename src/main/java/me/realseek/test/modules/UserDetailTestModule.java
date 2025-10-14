package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.entity.User;

/**
 * User 实体详细功能测试模块
 * 测试用户的高级功能，包括属性、状态、操作方法等
 */
public class UserDetailTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "User详细功能";
    }

    @Override
    public String getDescription() {
        return "测试 User 实体的详细功能，包括昵称、识别码、VIP状态、在线状态等";
    }

    @Override
    protected void executeTests() {
        runTest("验证 User 接口存在", this::testUserInterfaceExists);
        runTest("测试 User 基本属性方法", this::testUserBasicMethods);
        runTest("测试 User 状态检查方法", this::testUserStatusMethods);
        runTest("测试识别码格式化", this::testIdentifyNumberFormatting);
        runTest("测试 User 命令发送者接口", this::testUserCommandSenderInterface);
        runTest("测试 IntimacyInfo 接口", this::testIntimacyInfoInterface);
        runTest("验证私聊消息方法存在", this::testPrivateMessageMethodsExist);
        runTest("验证角色管理方法存在", this::testRoleManagementMethodsExist);
        runTest("验证黑名单方法存在", this::testBlockMethodsExist);
        runTest("验证语音频道方法存在", this::testVoiceChannelMethodsExist);
    }

    private void testUserInterfaceExists() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");
            assertNotNull(userClass, "User 接口应该存在");

            // 验证继承关系
            assertTrue(userClass.isInterface(), "User 应该是接口");

            logger.info("User 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("User 接口验证失败: " + e.getMessage());
        }
    }

    private void testUserBasicMethods() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");

            // 验证基本方法存在
            assertNotNull(userClass.getMethod("getId"), "getId 方法应该存在");
            assertNotNull(userClass.getMethod("getName"), "getName 方法应该存在");
            assertNotNull(userClass.getMethod("getNickName", Class.forName("snw.jkook.entity.Guild")),
                    "getNickName 方法应该存在");
            assertNotNull(userClass.getMethod("getFullName", Class.forName("snw.jkook.entity.Guild")),
                    "getFullName 方法应该存在");
            assertNotNull(userClass.getMethod("getIdentifyNumber"), "getIdentifyNumber 方法应该存在");
            assertNotNull(userClass.getMethod("getIdentifyNumberAsString"),
                    "getIdentifyNumberAsString 方法应该存在");

            logger.info("User 基本属性方法验证通过（6个方法）");
        } catch (Exception e) {
            throw new AssertionError("User 基本方法验证失败: " + e.getMessage());
        }
    }

    private void testUserStatusMethods() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");

            // 验证状态检查方法
            assertNotNull(userClass.getMethod("isVip"), "isVip 方法应该存在");
            assertNotNull(userClass.getMethod("isBot"), "isBot 方法应该存在");
            assertNotNull(userClass.getMethod("isOnline"), "isOnline 方法应该存在");
            assertNotNull(userClass.getMethod("isBanned"), "isBanned 方法应该存在");

            logger.info("User 状态检查方法验证通过（4个方法）");
        } catch (Exception e) {
            throw new AssertionError("User 状态方法验证失败: " + e.getMessage());
        }
    }

    private void testIdentifyNumberFormatting() {
        // 测试识别码格式化逻辑（默认方法）
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");
            var method = userClass.getMethod("getIdentifyNumberAsString");

            // 验证是默认方法
            assertTrue(method.isDefault(), "getIdentifyNumberAsString 应该是默认方法");

            logger.info("识别码格式化方法验证通过");
        } catch (Exception e) {
            throw new AssertionError("识别码格式化验证失败: " + e.getMessage());
        }
    }

    private void testUserCommandSenderInterface() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");
            Class<?> commandSenderClass = Class.forName("snw.jkook.command.CommandSender");

            // 验证 User 继承了 CommandSender
            assertTrue(commandSenderClass.isAssignableFrom(userClass),
                    "User 应该继承 CommandSender 接口");

            logger.info("User CommandSender 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("CommandSender 接口验证失败: " + e.getMessage());
        }
    }

    private void testIntimacyInfoInterface() {
        try {
            Class<?> intimacyInfoClass = Class.forName("snw.jkook.entity.User$IntimacyInfo");
            assertNotNull(intimacyInfoClass, "IntimacyInfo 接口应该存在");

            // 验证 IntimacyInfo 方法
            assertNotNull(intimacyInfoClass.getMethod("getSocialImage"),
                    "getSocialImage 方法应该存在");
            assertNotNull(intimacyInfoClass.getMethod("getSocialInfo"),
                    "getSocialInfo 方法应该存在");
            assertNotNull(intimacyInfoClass.getMethod("getLastRead"),
                    "getLastRead 方法应该存在");
            assertNotNull(intimacyInfoClass.getMethod("getScore"),
                    "getScore 方法应该存在");
            assertNotNull(intimacyInfoClass.getMethod("getSocialImages"),
                    "getSocialImages 方法应该存在");

            // 验证 SocialImage 内部接口
            Class<?> socialImageClass = Class.forName("snw.jkook.entity.User$IntimacyInfo$SocialImage");
            assertNotNull(socialImageClass, "SocialImage 接口应该存在");
            assertNotNull(socialImageClass.getMethod("getId"), "SocialImage.getId 方法应该存在");
            assertNotNull(socialImageClass.getMethod("getUrl"), "SocialImage.getUrl 方法应该存在");

            logger.info("IntimacyInfo 接口验证通过（5个方法 + SocialImage 接口）");
        } catch (Exception e) {
            throw new AssertionError("IntimacyInfo 接口验证失败: " + e.getMessage());
        }
    }

    private void testPrivateMessageMethodsExist() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");

            // 验证私聊消息方法（4个重载）
            assertNotNull(userClass.getMethod("sendPrivateMessage", String.class),
                    "sendPrivateMessage(String) 方法应该存在");
            assertNotNull(userClass.getMethod("sendPrivateMessage", String.class,
                            Class.forName("snw.jkook.message.PrivateMessage")),
                    "sendPrivateMessage(String, PrivateMessage) 方法应该存在");
            assertNotNull(userClass.getMethod("sendPrivateMessage",
                            Class.forName("snw.jkook.message.component.BaseComponent")),
                    "sendPrivateMessage(BaseComponent) 方法应该存在");
            assertNotNull(userClass.getMethod("sendPrivateMessage",
                            Class.forName("snw.jkook.message.component.BaseComponent"),
                            Class.forName("snw.jkook.message.PrivateMessage")),
                    "sendPrivateMessage(BaseComponent, PrivateMessage) 方法应该存在");

            logger.info("私聊消息方法验证通过（4个重载方法）");
        } catch (Exception e) {
            throw new AssertionError("私聊消息方法验证失败: " + e.getMessage());
        }
    }

    private void testRoleManagementMethodsExist() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");
            Class<?> guildClass = Class.forName("snw.jkook.entity.Guild");
            Class<?> roleClass = Class.forName("snw.jkook.entity.Role");

            // 验证角色管理方法
            assertNotNull(userClass.getMethod("getRoles", guildClass),
                    "getRoles 方法应该存在");
            assertNotNull(userClass.getMethod("grantRole", roleClass),
                    "grantRole(Role) 方法应该存在");
            assertNotNull(userClass.getMethod("revokeRole", roleClass),
                    "revokeRole(Role) 方法应该存在");
            assertNotNull(userClass.getMethod("grantRole", guildClass, int.class),
                    "grantRole(Guild, int) 方法应该存在");
            assertNotNull(userClass.getMethod("revokeRole", guildClass, int.class),
                    "revokeRole(Guild, int) 方法应该存在");

            logger.info("角色管理方法验证通过（5个方法）");
        } catch (Exception e) {
            throw new AssertionError("角色管理方法验证失败: " + e.getMessage());
        }
    }

    private void testBlockMethodsExist() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");

            // 验证黑名单方法
            assertNotNull(userClass.getMethod("block"), "block 方法应该存在");
            assertNotNull(userClass.getMethod("unblock"), "unblock 方法应该存在");

            logger.info("黑名单方法验证通过（2个方法）");
        } catch (Exception e) {
            throw new AssertionError("黑名单方法验证失败: " + e.getMessage());
        }
    }

    private void testVoiceChannelMethodsExist() {
        try {
            Class<?> userClass = Class.forName("snw.jkook.entity.User");
            Class<?> guildClass = Class.forName("snw.jkook.entity.Guild");

            // 验证语音频道方法
            assertNotNull(userClass.getMethod("getJoinedVoiceChannel", guildClass),
                    "getJoinedVoiceChannel 方法应该存在");

            // 验证亲密度方法
            assertNotNull(userClass.getMethod("getIntimacy"), "getIntimacy 方法应该存在");
            assertNotNull(userClass.getMethod("getIntimacyInfo"), "getIntimacyInfo 方法应该存在");
            assertNotNull(userClass.getMethod("setIntimacy", int.class), "setIntimacy(int) 方法应该存在");
            assertNotNull(userClass.getMethod("setIntimacy", int.class, String.class, Integer.class),
                    "setIntimacy(int, String, Integer) 方法应该存在");

            logger.info("语音频道和亲密度方法验证通过（5个方法）");
        } catch (Exception e) {
            throw new AssertionError("语音频道方法验证失败: " + e.getMessage());
        }
    }
}
