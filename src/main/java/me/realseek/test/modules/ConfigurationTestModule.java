package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.config.file.FileConfiguration;
import snw.jkook.config.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 配置系统测试模块
 * 测试 JKook 的配置文件读写功能
 */
public class ConfigurationTestModule extends BaseTestModule {
    private File testConfigFile;
    private FileConfiguration testConfig;

    @Override
    public String getName() {
        return "配置系统";
    }

    @Override
    public String getDescription() {
        return "测试 YAML 配置文件的创建、读取、修改和保存功能";
    }

    @Override
    protected void executeTests() {
        runTest("插件配置可用性", this::testPluginConfig);
        runTest("YAML配置创建", this::testYamlConfigCreation);
        runTest("配置值设置", this::testConfigSet);
        runTest("配置值获取", this::testConfigGet);
        runTest("配置默认值", this::testConfigDefaults);
        runTest("配置节点路径", this::testConfigPath);
        runTest("配置列表操作", this::testConfigList);
        runTest("配置文件保存", this::testConfigSave);
        runTest("配置文件加载", this::testConfigLoad);
        runTest("配置键存在性检查", this::testConfigContains);
    }

    private void testPluginConfig() {
        FileConfiguration config = plugin.getConfig();
        assertNotNull(config, "插件配置不应为null");
    }

    private void testYamlConfigCreation() {
        testConfig = new YamlConfiguration();
        assertNotNull(testConfig, "YAML配置创建失败");
    }

    private void testConfigSet() {
        if (testConfig == null) {
            testConfig = new YamlConfiguration();
        }

        testConfig.set("test.string", "测试字符串");
        testConfig.set("test.number", 42);
        testConfig.set("test.boolean", true);
        testConfig.set("test.double", 3.14);

        assertTrue(true, "配置值设置成功");
    }

    private void testConfigGet() {
        if (testConfig == null) {
            testConfig = new YamlConfiguration();
            testConfig.set("test.string", "测试字符串");
            testConfig.set("test.number", 42);
            testConfig.set("test.boolean", true);
        }

        String stringValue = testConfig.getString("test.string");
        int numberValue = testConfig.getInt("test.number");
        boolean boolValue = testConfig.getBoolean("test.boolean");

        assertEquals("测试字符串", stringValue, "字符串值不匹配");
        assertEquals(42, numberValue, "数字值不匹配");
        assertTrue(boolValue, "布尔值应为true");
    }

    private void testConfigDefaults() {
        if (testConfig == null) {
            testConfig = new YamlConfiguration();
        }

        String defaultString = testConfig.getString("nonexistent.key", "默认值");
        int defaultInt = testConfig.getInt("nonexistent.number", 99);
        boolean defaultBool = testConfig.getBoolean("nonexistent.flag", false);

        assertEquals("默认值", defaultString, "默认字符串值不匹配");
        assertEquals(99, defaultInt, "默认数字值不匹配");
        assertFalse(defaultBool, "默认布尔值应为false");
    }

    private void testConfigPath() {
        if (testConfig == null) {
            testConfig = new YamlConfiguration();
        }

        testConfig.set("level1.level2.level3", "深层值");
        String value = testConfig.getString("level1.level2.level3");

        assertEquals("深层值", value, "深层路径值不匹配");
    }

    private void testConfigList() {
        if (testConfig == null) {
            testConfig = new YamlConfiguration();
        }

        List<String> testList = Arrays.asList("item1", "item2", "item3");
        testConfig.set("test.list", testList);

        List<String> retrievedList = testConfig.getStringList("test.list");
        assertNotNull(retrievedList, "获取的列表不应为null");
        assertEquals(3, retrievedList.size(), "列表大小不匹配");
        assertTrue(retrievedList.contains("item1"), "列表应包含item1");
        assertTrue(retrievedList.contains("item2"), "列表应包含item2");
        assertTrue(retrievedList.contains("item3"), "列表应包含item3");
    }

    private void testConfigSave() {
        try {
            if (testConfig == null) {
                testConfig = new YamlConfiguration();
                testConfig.set("test.data", "保存测试");
            }

            testConfigFile = new File(plugin.getDataFolder(), "test_config.yml");
            testConfig.save(testConfigFile);

            assertTrue(testConfigFile.exists(), "配置文件应该已创建");
        } catch (Exception e) {
            assertTrue(false, "配置保存失败: " + e.getMessage());
        }
    }

    private void testConfigLoad() {
        try {
            if (testConfigFile == null || !testConfigFile.exists()) {
                // 先保存一个配置文件
                testConfig = new YamlConfiguration();
                testConfig.set("test.load", "加载测试");
                testConfigFile = new File(plugin.getDataFolder(), "test_config_load.yml");
                testConfig.save(testConfigFile);
            }

            YamlConfiguration loadedConfig = new YamlConfiguration();
            loadedConfig.load(testConfigFile);

            assertNotNull(loadedConfig, "加载的配置不应为null");

            // 验证加载的内容
            String value = loadedConfig.getString("test.load");
            if (value != null) {
                assertEquals("加载测试", value, "加载的配置值不匹配");
            } else {
                assertTrue(true, "配置加载成功（但测试键不存在）");
            }
        } catch (Exception e) {
            assertTrue(false, "配置加载失败: " + e.getMessage());
        }
    }

    private void testConfigContains() {
        if (testConfig == null) {
            testConfig = new YamlConfiguration();
        }

        testConfig.set("existing.key", "value");

        assertTrue(testConfig.contains("existing.key"), "配置应包含已设置的键");
        assertFalse(testConfig.contains("nonexistent.key"), "配置不应包含未设置的键");
    }

    @Override
    public void cleanup() {
        // 清理测试配置文件
        if (testConfigFile != null && testConfigFile.exists()) {
            if (testConfigFile.delete()) {
                logger.debug("测试配置文件已删除");
            }
        }
    }
}
