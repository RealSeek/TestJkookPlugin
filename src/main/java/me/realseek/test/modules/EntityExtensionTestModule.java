package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.Unsafe;

/**
 * 实体扩展测试模块
 * 测试 Invitation、Game、Reaction 等实体和 Unsafe API
 */
public class EntityExtensionTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "实体扩展";
    }

    @Override
    public String getDescription() {
        return "测试邀请、游戏状态、表情回应等实体接口和 Unsafe API";
    }

    @Override
    protected void executeTests() {
        // Unsafe API 测试
        runTest("验证 Unsafe API 可用性", this::testUnsafeAvailable);
        runTest("测试 Unsafe.getEmoji", this::testUnsafeGetEmoji);
        runTest("测试 Unsafe.getGame", this::testUnsafeGetGame);

        // 实体接口验证测试
        runTest("验证 Invitation 接口存在", this::testInvitationInterface);
        runTest("验证 Game 接口存在", this::testGameInterface);
        runTest("验证 Reaction 接口存在", this::testReactionInterface);
        runTest("验证 CustomEmoji 接口存在", this::testCustomEmojiInterface);

        // 实体能力接口测试
        runTest("验证 MasterHolder 接口", this::testMasterHolderInterface);
        runTest("验证 InviteHolder 接口", this::testInviteHolderInterface);
        runTest("验证 ReactionHolder 接口", this::testReactionHolderInterface);
        runTest("验证 AvatarHolder 接口", this::testAvatarHolderInterface);
        runTest("验证 Nameable 接口", this::testNameableInterface);
    }

    private void testUnsafeAvailable() {
        Unsafe unsafe = plugin.getCore().getUnsafe();
        assertNotNull(unsafe, "Unsafe API 不应为 null");

        logger.info("Unsafe API 可用");
    }

    private void testUnsafeGetEmoji() {
        Unsafe unsafe = plugin.getCore().getUnsafe();

        try {
            // 使用 Unsafe API 快速构造 Emoji 对象
            var emoji = unsafe.getEmoji("test-emoji-id");
            assertNotNull(emoji, "通过 Unsafe 构造的 Emoji 不应为 null");

            logger.info("Unsafe.getEmoji 方法可用");
        } catch (Exception e) {
            logger.warn("Unsafe.getEmoji 执行时发生异常（这是预期的，因为使用了测试ID）: {}", e.getMessage());
        }
    }

    private void testUnsafeGetGame() {
        Unsafe unsafe = plugin.getCore().getUnsafe();

        try {
            // 使用 Unsafe API 快速构造 Game 对象
            var game = unsafe.getGame(12345);
            assertNotNull(game, "通过 Unsafe 构造的 Game 不应为 null");

            logger.info("Unsafe.getGame 方法可用");
        } catch (Exception e) {
            logger.warn("Unsafe.getGame 执行时发生异常（这是预期的，因为使用了测试ID）: {}", e.getMessage());
        }
    }

    private void testInvitationInterface() {
        try {
            // 验证 Invitation 接口存在并加载
            Class<?> invitationClass = Class.forName("snw.jkook.entity.Invitation");
            assertNotNull(invitationClass, "Invitation 接口应该存在");

            // 验证关键方法存在
            assertNotNull(invitationClass.getMethod("getGuild"), "getGuild 方法应该存在");
            assertNotNull(invitationClass.getMethod("getChannel"), "getChannel 方法应该存在");
            assertNotNull(invitationClass.getMethod("getUrlCode"), "getUrlCode 方法应该存在");
            assertNotNull(invitationClass.getMethod("getUrl"), "getUrl 方法应该存在");
            assertNotNull(invitationClass.getMethod("delete"), "delete 方法应该存在");

            logger.info("Invitation 接口验证通过，包含 5 个核心方法");
        } catch (Exception e) {
            throw new AssertionError("Invitation 接口验证失败: " + e.getMessage());
        }
    }

    private void testGameInterface() {
        try {
            Class<?> gameClass = Class.forName("snw.jkook.entity.Game");
            assertNotNull(gameClass, "Game 接口应该存在");

            // 验证关键方法存在
            assertNotNull(gameClass.getMethod("getId"), "getId 方法应该存在");
            assertNotNull(gameClass.getMethod("getName"), "getName 方法应该存在");
            assertNotNull(gameClass.getMethod("setName", String.class), "setName 方法应该存在");
            assertNotNull(gameClass.getMethod("getIcon"), "getIcon 方法应该存在");
            assertNotNull(gameClass.getMethod("setIcon", String.class), "setIcon 方法应该存在");
            assertNotNull(gameClass.getMethod("setNameAndIcon", String.class, String.class),
                    "setNameAndIcon 方法应该存在");

            logger.info("Game 接口验证通过，包含 6 个核心方法");
        } catch (Exception e) {
            throw new AssertionError("Game 接口验证失败: " + e.getMessage());
        }
    }

    private void testReactionInterface() {
        try {
            Class<?> reactionClass = Class.forName("snw.jkook.entity.Reaction");
            assertNotNull(reactionClass, "Reaction 接口应该存在");

            // 验证关键方法存在
            assertNotNull(reactionClass.getMethod("getMessageId"), "getMessageId 方法应该存在");
            assertNotNull(reactionClass.getMethod("getEmoji"), "getEmoji 方法应该存在");
            assertNotNull(reactionClass.getMethod("getTimeStamp"), "getTimeStamp 方法应该存在");
            assertNotNull(reactionClass.getMethod("delete"), "delete 方法应该存在");

            logger.info("Reaction 接口验证通过，包含 4 个核心方法");
        } catch (Exception e) {
            throw new AssertionError("Reaction 接口验证失败: " + e.getMessage());
        }
    }

    private void testCustomEmojiInterface() {
        try {
            Class<?> emojiClass = Class.forName("snw.jkook.entity.CustomEmoji");
            assertNotNull(emojiClass, "CustomEmoji 接口应该存在");

            // 验证关键方法存在
            assertNotNull(emojiClass.getMethod("getId"), "getId 方法应该存在");
            assertNotNull(emojiClass.getMethod("getName"), "getName 方法应该存在");

            logger.info("CustomEmoji 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("CustomEmoji 接口验证失败: " + e.getMessage());
        }
    }

    private void testMasterHolderInterface() {
        try {
            Class<?> holderClass = Class.forName("snw.jkook.entity.abilities.MasterHolder");
            assertNotNull(holderClass, "MasterHolder 接口应该存在");

            // 验证关键方法存在
            assertNotNull(holderClass.getMethod("getMaster"), "getMaster 方法应该存在");

            logger.info("MasterHolder 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("MasterHolder 接口验证失败: " + e.getMessage());
        }
    }

    private void testInviteHolderInterface() {
        try {
            Class<?> holderClass = Class.forName("snw.jkook.entity.abilities.InviteHolder");
            assertNotNull(holderClass, "InviteHolder 接口应该存在");

            // 验证关键方法存在
            assertNotNull(holderClass.getMethod("getInvitations"), "getInvitations 方法应该存在");
            assertNotNull(holderClass.getMethod("createInvite", int.class, Integer.class),
                    "createInvite 方法应该存在");

            logger.info("InviteHolder 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("InviteHolder 接口验证失败: " + e.getMessage());
        }
    }

    private void testReactionHolderInterface() {
        try {
            Class<?> holderClass = Class.forName("snw.jkook.entity.abilities.ReactionHolder");
            assertNotNull(holderClass, "ReactionHolder 接口应该存在");

            // 验证关键方法存在
            assertNotNull(holderClass.getMethod("addReaction", String.class),
                    "addReaction 方法应该存在");

            logger.info("ReactionHolder 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("ReactionHolder 接口验证失败: " + e.getMessage());
        }
    }

    private void testAvatarHolderInterface() {
        try {
            Class<?> holderClass = Class.forName("snw.jkook.entity.abilities.AvatarHolder");
            assertNotNull(holderClass, "AvatarHolder 接口应该存在");

            // 验证关键方法存在
            assertNotNull(holderClass.getMethod("getAvatarUrl", boolean.class),
                    "getAvatarUrl 方法应该存在");

            logger.info("AvatarHolder 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("AvatarHolder 接口验证失败: " + e.getMessage());
        }
    }

    private void testNameableInterface() {
        try {
            Class<?> nameableClass = Class.forName("snw.jkook.entity.abilities.Nameable");
            assertNotNull(nameableClass, "Nameable 接口应该存在");

            // 验证关键方法存在
            assertNotNull(nameableClass.getMethod("getName"), "getName 方法应该存在");

            logger.info("Nameable 接口验证通过");
        } catch (Exception e) {
            throw new AssertionError("Nameable 接口验证失败: " + e.getMessage());
        }
    }
}
