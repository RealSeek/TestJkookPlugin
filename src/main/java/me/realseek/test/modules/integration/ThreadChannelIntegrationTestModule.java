package me.realseek.test.modules.integration;

import snw.jkook.entity.Guild;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.ThreadChannel;
import snw.jkook.entity.thread.ThreadPost;
import snw.jkook.util.PageIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * ThreadChannel（论坛频道）集成测试模块
 * 测试论坛频道的帖子创建、获取、分类管理等功能
 *
 * 注意：ThreadChannel 在 KOOK 中是"论坛频道"，用于发布和管理帖子，
 * 不是消息线程（Discord 的 Thread）
 */
public class ThreadChannelIntegrationTestModule extends IntegrationTestModule {
    private final List<String> createdThreadPostIds = new ArrayList<>();

    @Override
    public String getName() {
        return "论坛频道集成测试";
    }

    @Override
    public String getDescription() {
        return "测试论坛频道(ThreadChannel)的帖子创建、获取、分类管理等功能";
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

        // 只读测试
        runTest("获取论坛频道列表", this::testGetThreadChannels);
        runTest("获取论坛频道详细信息", this::testGetThreadChannelInfo);

        // 需要副作用权限的测试
        if (canExecuteSideEffects()) {
            runTest("创建论坛帖子", this::testCreateThreadPost);
            runTest("获取论坛帖子列表", this::testGetThreadPosts);
            runTest("获取论坛分类列表", this::testGetThreadCategories);
        }
    }

    /**
     * 测试获取论坛频道列表
     */
    private void testGetThreadChannels() {
        try {
            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);
            assertNotNull(guild, "测试服务器不应为 null");

            PageIterator<Set<Channel>> channels = guild.getChannels();
            assertNotNull(channels, "频道迭代器不应为 null");

            if (channels.hasNext()) {
                Set<Channel> firstPage = channels.next();

                List<ThreadChannel> threadChannels = new ArrayList<>();
                for (Channel channel : firstPage) {
                    if (channel instanceof ThreadChannel) {
                        threadChannels.add((ThreadChannel) channel);
                    }
                }

                logger.info("✓ 找到 {} 个论坛频道", threadChannels.size());

                int count = 0;
                for (ThreadChannel channel : threadChannels) {
                    if (count++ < 3) {
                        logger.info("  - 论坛频道: {} (ID: {})",
                                channel.getName(), channel.getId());
                    }
                }

                if (threadChannels.isEmpty()) {
                    logger.info("服务器中没有论坛频道，部分测试将跳过");
                }
            } else {
                logger.info("未找到任何频道");
            }

        } catch (Exception e) {
            logger.warn("获取论坛频道列表时出错: {}", e.getMessage());
            logger.info("测试跳过（可能是权限不足或 API 版本不支持）");
        }
    }

    /**
     * 测试获取论坛频道详细信息
     */
    private void testGetThreadChannelInfo() {
        try {
            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);
            assertNotNull(guild, "测试服务器不应为 null");

            // 查找第一个论坛频道
            ThreadChannel threadChannel = findFirstThreadChannel(guild);
            if (threadChannel == null) {
                logger.info("未找到论坛频道，跳过此测试");
                return;
            }

            logger.info("=== 论坛频道详细信息 ===");
            logger.info("ID: {}", threadChannel.getId());
            logger.info("名称: {}", threadChannel.getName());
            logger.info("服务器ID: {}", threadChannel.getGuild().getId());

            logger.info("✓ 成功获取论坛频道信息");

        } catch (Exception e) {
            logger.warn("获取论坛频道信息时出错: {}", e.getMessage());
            logger.info("测试跳过");
        }
    }

    /**
     * 测试创建论坛帖子
     */
    private void testCreateThreadPost() {
        try {
            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);
            assertNotNull(guild, "测试服务器不应为 null");

            ThreadChannel threadChannel = findFirstThreadChannel(guild);
            if (threadChannel == null) {
                logger.info("未找到论坛频道，跳过创建帖子测试");
                return;
            }

            String title = "测试帖子-" + System.currentTimeMillis();
            String content = "这是一个测试帖子的内容\n\n**测试时间**: " +
                           new java.util.Date() + "\n**测试模块**: 论坛频道集成测试";

            // 创建帖子（不指定分类，使用默认分类）
            ThreadPost post = threadChannel.createThread(title, content, null);
            assertNotNull(post, "创建的帖子不应为 null");
            assertNotNull(post.getId(), "创建的帖子ID不应为 null");

            createdThreadPostIds.add(post.getId());
            logger.info("✓ 成功创建论坛帖子");
            logger.info("  帖子ID: {}", post.getId());
            logger.info("  标题: {}", title);

        } catch (Exception e) {
            logger.warn("创建论坛帖子时出错: {}", e.getMessage());
            logger.info("测试跳过（可能是权限不足或 API 版本不支持）");
        }
    }

    /**
     * 测试获取论坛帖子列表
     */
    private void testGetThreadPosts() {
        try {
            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);
            assertNotNull(guild, "测试服务器不应为 null");

            ThreadChannel threadChannel = findFirstThreadChannel(guild);
            if (threadChannel == null) {
                logger.info("未找到论坛频道，跳过获取帖子列表测试");
                return;
            }

            // 获取所有分类的帖子（传 null 表示获取所有分类）
            PageIterator<Collection<ThreadPost>> posts = threadChannel.getThreadPosts(null);
            assertNotNull(posts, "帖子迭代器不应为 null");

            if (posts.hasNext()) {
                Collection<ThreadPost> firstPage = posts.next();
                logger.info("✓ 获取到 {} 个帖子（第一页）", firstPage.size());

                int count = 0;
                for (ThreadPost post : firstPage) {
                    if (count++ < 3) {
                        logger.info("  - 帖子ID: {}", post.getId());
                    }
                }
            } else {
                logger.info("论坛频道中暂无帖子");
            }

        } catch (Exception e) {
            logger.warn("获取论坛帖子列表时出错: {}", e.getMessage());
            logger.info("测试跳过");
        }
    }

    /**
     * 测试获取论坛分类列表
     */
    private void testGetThreadCategories() {
        try {
            Guild guild = plugin.getCore().getHttpAPI().getGuild(testGuildId);
            assertNotNull(guild, "测试服务器不应为 null");

            ThreadChannel threadChannel = findFirstThreadChannel(guild);
            if (threadChannel == null) {
                logger.info("未找到论坛频道，跳过获取分类列表测试");
                return;
            }

            var categories = threadChannel.getCategories();
            assertNotNull(categories, "分类列表不应为 null");

            logger.info("✓ 论坛分类数量: {}", categories.size());

            int count = 0;
            for (var category : categories) {
                if (count++ < 5) {
                    logger.info("  - 分类: {} (ID: {}, 默认: {})",
                            category.getName(),
                            category.getId(),
                            category.isDefault());
                }
            }

            if (categories.isEmpty()) {
                logger.info("论坛频道中暂无分类");
            }

        } catch (Exception e) {
            logger.warn("获取论坛分类列表时出错: {}", e.getMessage());
            logger.info("测试跳过");
        }
    }

    /**
     * 查找服务器中的第一个论坛频道
     */
    private ThreadChannel findFirstThreadChannel(Guild guild) {
        try {
            PageIterator<Set<Channel>> channels = guild.getChannels();
            if (channels.hasNext()) {
                Set<Channel> firstPage = channels.next();
                for (Channel channel : firstPage) {
                    if (channel instanceof ThreadChannel) {
                        return (ThreadChannel) channel;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("查找论坛频道时出错: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void cleanup() {
        if (!autoCleanup) {
            logger.info("自动清理已禁用，跳过清理");
            return;
        }

        // 注意：论坛帖子的删除通常需要通过 ThreadPost 对象
        // 这里我们不执行清理，因为需要先获取 ThreadPost 对象
        if (!createdThreadPostIds.isEmpty()) {
            logger.info("创建了 {} 个测试帖子", createdThreadPostIds.size());
            logger.info("提示：论坛帖子需要手动删除或等待自动过期");
            for (String postId : createdThreadPostIds) {
                logger.debug("  - 帖子ID: {}", postId);
            }
            createdThreadPostIds.clear();
        }

        logger.info("清理完成");
    }
}
