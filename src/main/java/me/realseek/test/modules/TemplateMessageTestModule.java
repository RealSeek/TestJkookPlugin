package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;

/**
 * TemplateMessage 测试模块
 * 测试模板消息的创建和功能,包括 Markdown 和 Card 类型
 */
public class TemplateMessageTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "模板消息";
    }

    @Override
    public String getDescription() {
        return "测试 TemplateMessage 的创建、类型和内容方法";
    }

    @Override
    protected void executeTests() {
        runTest("验证 TemplateMessage 类存在", this::testTemplateMessageExists);
        runTest("测试 Markdown 模板消息创建", this::testMarkdownTemplate);
        runTest("测试 Card 模板消息创建", this::testCardTemplate);
        runTest("测试模板消息基本方法", this::testTemplateMessageMethods);
        runTest("验证模板消息继承关系", this::testTemplateMessageInheritance);
    }

    private void testTemplateMessageExists() {
        try {
            Class<?> templateMessageClass = Class.forName("snw.jkook.message.component.TemplateMessage");
            assertNotNull(templateMessageClass, "TemplateMessage 类应该存在");

            logger.info("TemplateMessage 类验证通过");
        } catch (Exception e) {
            throw new AssertionError("TemplateMessage 类验证失败: " + e.getMessage());
        }
    }

    private void testMarkdownTemplate() {
        try {
            Class<?> templateMessageClass = Class.forName("snw.jkook.message.component.TemplateMessage");

            // 验证 TemplateMessage 构造器存在（通常使用 new TemplateMessage(id, type, content)）
            // 类型 9 = Markdown, 类型 10 = Card
            var constructors = templateMessageClass.getConstructors();
            assertTrue(constructors.length > 0, "TemplateMessage 应该有公共构造器");

            logger.info("TemplateMessage 构造器验证通过，共找到 {} 个构造器", constructors.length);
        } catch (Exception e) {
            throw new AssertionError("Markdown 模板消息方法验证失败: " + e.getMessage());
        }
    }

    private void testCardTemplate() {
        try {
            Class<?> templateMessageClass = Class.forName("snw.jkook.message.component.TemplateMessage");

            // 验证 card 静态工厂方法存在
            assertNotNull(templateMessageClass.getMethod("card", long.class, String.class),
                    "card 静态方法应该存在");

            logger.info("Card 模板消息创建方法验证通过");
        } catch (Exception e) {
            throw new AssertionError("Card 模板消息方法验证失败: " + e.getMessage());
        }
    }

    private void testTemplateMessageMethods() {
        try {
            Class<?> templateMessageClass = Class.forName("snw.jkook.message.component.TemplateMessage");

            // 验证基本方法
            assertNotNull(templateMessageClass.getMethod("getId"), "getId 方法应该存在");
            assertNotNull(templateMessageClass.getMethod("getContent"), "getContent 方法应该存在");
            assertNotNull(templateMessageClass.getMethod("getType"), "getType 方法应该存在");

            logger.info("TemplateMessage 基本方法验证通过(3个方法)");
        } catch (Exception e) {
            throw new AssertionError("TemplateMessage 方法验证失败: " + e.getMessage());
        }
    }

    private void testTemplateMessageInheritance() {
        try {
            Class<?> templateMessageClass = Class.forName("snw.jkook.message.component.TemplateMessage");
            Class<?> baseComponentClass = Class.forName("snw.jkook.message.component.BaseComponent");

            // 验证 TemplateMessage 继承 BaseComponent
            assertTrue(baseComponentClass.isAssignableFrom(templateMessageClass),
                    "TemplateMessage 应该继承 BaseComponent");

            logger.info("TemplateMessage 继承关系验证通过 (继承自 BaseComponent)");
        } catch (Exception e) {
            throw new AssertionError("TemplateMessage 继承关系验证失败: " + e.getMessage());
        }
    }
}
