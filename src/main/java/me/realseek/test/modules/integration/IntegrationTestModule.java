package me.realseek.test.modules.integration;

import me.realseek.test.BaseTestModule;
import snw.jkook.config.file.FileConfiguration;

/**
 * 集成测试模块基类
 * 所有需要真实环境的测试模块都应继承此类
 */
public abstract class IntegrationTestModule extends BaseTestModule {
    protected String testGuildId;
    protected String testTextChannelId;
    protected String testVoiceChannelId;
    protected String testUserId;
    protected boolean allowSideEffects;
    protected boolean autoCleanup;
    protected boolean integrationEnabled;

    @Override
    public void initialize(snw.jkook.plugin.Plugin plugin) {
        super.initialize(plugin);
        loadIntegrationConfig();
    }

    /**
     * 加载集成测试配置
     */
    private void loadIntegrationConfig() {
        FileConfiguration config = plugin.getConfig();

        integrationEnabled = config.getBoolean("integration-test.enabled", false);
        testGuildId = config.getString("integration-test.test-guild-id", "");
        testTextChannelId = config.getString("integration-test.test-text-channel-id", "");
        testVoiceChannelId = config.getString("integration-test.test-voice-channel-id", "");
        testUserId = config.getString("integration-test.test-user-id", "");
        allowSideEffects = config.getBoolean("integration-test.allow-side-effects", false);
        autoCleanup = config.getBoolean("integration-test.auto-cleanup", true);

        if (integrationEnabled) {
            logger.info("集成测试已启用");
            logger.info("测试服务器 ID: {}", testGuildId.isEmpty() ? "未配置" : testGuildId);
            logger.info("测试频道 ID: {}", testTextChannelId.isEmpty() ? "未配置" : testTextChannelId);
            logger.info("允许副作用: {}", allowSideEffects);
            logger.info("自动清理: {}", autoCleanup);
        }
    }

    /**
     * 检查集成测试是否已启用
     */
    protected boolean isIntegrationEnabled() {
        return integrationEnabled;
    }

    /**
     * 检查必需的配置是否已设置
     */
    protected boolean checkRequiredConfig() {
        if (testGuildId == null || testGuildId.isEmpty()) {
            logger.warn("测试服务器 ID 未配置，跳过集成测试");
            return false;
        }
        return true;
    }

    /**
     * 检查是否允许执行有副作用的操作
     */
    protected boolean canExecuteSideEffects() {
        if (!allowSideEffects) {
            logger.info("未启用副作用操作，跳过此测试");
            return false;
        }
        return true;
    }

    @Override
    public boolean isAutoRun() {
        // 集成测试默认不自动运行，需要手动触发
        return false;
    }
}
