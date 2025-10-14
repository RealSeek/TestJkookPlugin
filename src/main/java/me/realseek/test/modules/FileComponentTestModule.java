package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.message.component.FileComponent;

/**
 * 文件组件测试模块
 * 测试 FileComponent 和文件上传相关功能
 */
public class FileComponentTestModule extends BaseTestModule {

    @Override
    public String getName() {
        return "文件组件";
    }

    @Override
    public String getDescription() {
        return "测试 FileComponent 文件组件的创建和属性";
    }

    @Override
    protected void executeTests() {
        runTest("创建图片文件组件", this::testCreateImageFile);
        runTest("创建普通文件组件", this::testCreateNormalFile);
        runTest("验证文件组件类型", this::testFileComponentType);
        runTest("验证文件组件 URL", this::testFileComponentUrl);
        runTest("验证文件组件名称", this::testFileComponentName);
        runTest("验证文件组件大小", this::testFileComponentSize);
    }

    private void testCreateImageFile() {
        String imageUrl = "https://img.kookapp.cn/assets/2021-01/test-image.png";
        FileComponent file = new FileComponent(imageUrl, "test-image.png", 102400, FileComponent.Type.IMAGE);

        assertNotNull(file, "FileComponent 不应为 null");
        assertEquals(imageUrl, file.getUrl(), "URL 应该匹配");
        assertEquals(FileComponent.Type.IMAGE, file.getType(), "类型应该为 IMAGE");

        logger.info("成功创建图片文件组件");
    }

    private void testCreateNormalFile() {
        String fileUrl = "https://img.kookapp.cn/assets/2021-01/test-file.pdf";
        FileComponent file = new FileComponent(fileUrl, "test-file.pdf", 512000, FileComponent.Type.FILE);

        assertNotNull(file, "FileComponent 不应为 null");
        assertEquals(fileUrl, file.getUrl(), "URL 应该匹配");
        assertEquals(FileComponent.Type.FILE, file.getType(), "类型应该为 FILE");

        logger.info("成功创建普通文件组件");
    }

    private void testFileComponentType() {
        String url = "https://img.kookapp.cn/assets/test.jpg";
        FileComponent file = new FileComponent(url, "test.jpg", 204800, FileComponent.Type.IMAGE);

        // FileComponent 继承自 BaseComponent
        assertNotNull(file, "文件组件不应为 null");
        assertTrue(file instanceof snw.jkook.message.component.BaseComponent,
                "FileComponent 应该是 BaseComponent 的实例");

        // 测试 Type 枚举
        assertNotNull(FileComponent.Type.FILE, "FILE 类型不应为 null");
        assertNotNull(FileComponent.Type.AUDIO, "AUDIO 类型不应为 null");
        assertNotNull(FileComponent.Type.VIDEO, "VIDEO 类型不应为 null");
        assertNotNull(FileComponent.Type.IMAGE, "IMAGE 类型不应为 null");

        logger.info("FileComponent 类型验证正确");
    }

    private void testFileComponentUrl() {
        String url1 = "https://img.kookapp.cn/assets/image.png";
        String url2 = "https://cdn.example.com/file.zip";

        FileComponent file1 = new FileComponent(url1, "image.png", 100, FileComponent.Type.IMAGE);
        FileComponent file2 = new FileComponent(url2, "file.zip", 200, FileComponent.Type.FILE);

        assertEquals(url1, file1.getUrl(), "第一个文件 URL 应该匹配");
        assertEquals(url2, file2.getUrl(), "第二个文件 URL 应该匹配");
        assertFalse(file1.getUrl().equals(file2.getUrl()), "不同文件的 URL 不应相同");

        logger.info("文件组件 URL 属性正确");
    }

    private void testFileComponentName() {
        String fileName1 = "document.pdf";
        String fileName2 = "image.jpg";

        FileComponent file1 = new FileComponent("https://example.com/doc.pdf", fileName1, 1024, FileComponent.Type.FILE);
        FileComponent file2 = new FileComponent("https://example.com/img.jpg", fileName2, 2048, FileComponent.Type.IMAGE);

        assertEquals(fileName1, file1.getTitle(), "第一个文件名称应该匹配");
        assertEquals(fileName2, file2.getTitle(), "第二个文件名称应该匹配");

        logger.info("文件组件名称属性正确");
    }

    private void testFileComponentSize() {
        int size1 = 102400; // 100 KB
        int size2 = 1048576; // 1 MB
        int size3 = 10485760; // 10 MB

        FileComponent file1 = new FileComponent("https://example.com/small.txt", "small.txt", size1, FileComponent.Type.FILE);
        FileComponent file2 = new FileComponent("https://example.com/medium.zip", "medium.zip", size2, FileComponent.Type.FILE);
        FileComponent file3 = new FileComponent("https://example.com/large.mp4", "large.mp4", size3, FileComponent.Type.VIDEO);

        assertEquals(size1, file1.getSize(), "小文件大小应该匹配");
        assertEquals(size2, file2.getSize(), "中等文件大小应该匹配");
        assertEquals(size3, file3.getSize(), "大文件大小应该匹配");

        assertTrue(file1.getSize() < file2.getSize(), "小文件应该小于中等文件");
        assertTrue(file2.getSize() < file3.getSize(), "中等文件应该小于大文件");

        logger.info("文件组件大小属性正确（100KB, 1MB, 10MB）");
    }
}
