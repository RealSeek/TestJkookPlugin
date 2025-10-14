package me.realseek.test;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 测试报告管理器，负责生成和保存测试报告
 */
public class TestReportManager {
    private final Logger logger;
    private final File reportDir;
    private final Map<String, List<TestResult>> moduleResults;
    private final SimpleDateFormat dateFormat;

    public TestReportManager(Logger logger, File reportDir) {
        this.logger = logger;
        this.reportDir = reportDir;
        this.moduleResults = new LinkedHashMap<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
    }

    /**
     * 添加测试模块的结果
     */
    public void addModuleResults(String moduleName, List<TestResult> results) {
        moduleResults.put(moduleName, results);
    }

    /**
     * 生成并打印测试摘要
     */
    public void printSummary() {
        logger.info("====================================");
        logger.info("         测试执行摘要");
        logger.info("====================================");

        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        long totalTime = 0;

        for (Map.Entry<String, List<TestResult>> entry : moduleResults.entrySet()) {
            String moduleName = entry.getKey();
            List<TestResult> results = entry.getValue();

            int modulePassed = 0;
            int moduleFailed = 0;
            long moduleTime = 0;

            for (TestResult result : results) {
                totalTests++;
                if (result.isPassed()) {
                    passedTests++;
                    modulePassed++;
                } else {
                    failedTests++;
                    moduleFailed++;
                }
                totalTime += result.getExecutionTime();
                moduleTime += result.getExecutionTime();
            }

            logger.info("[{}] 通过: {}, 失败: {}, 耗时: {}ms",
                    moduleName, modulePassed, moduleFailed, moduleTime);
        }

        logger.info("====================================");
        logger.info("总测试数: {}", totalTests);
        logger.info("通过: {} ({}%)", passedTests,
                totalTests > 0 ? String.format("%.2f", passedTests * 100.0 / totalTests) : "0");
        logger.info("失败: {} ({}%)", failedTests,
                totalTests > 0 ? String.format("%.2f", failedTests * 100.0 / totalTests) : "0");
        logger.info("总耗时: {}ms", totalTime);
        logger.info("====================================");
    }

    /**
     * 生成详细的HTML测试报告
     */
    public void generateHtmlReport() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File reportFile = new File(reportDir, "test_report_" + timestamp + ".html");

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='zh-CN'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            writer.println("    <title>KookBC 测试报告</title>");
            writer.println("    <style>");
            writer.println("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }");
            writer.println("        .container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
            writer.println("        h1 { color: #333; border-bottom: 3px solid #4CAF50; padding-bottom: 10px; }");
            writer.println("        h2 { color: #555; margin-top: 30px; }");
            writer.println("        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }");
            writer.println("        .summary-card { padding: 20px; border-radius: 8px; text-align: center; }");
            writer.println("        .summary-card.total { background-color: #2196F3; color: white; }");
            writer.println("        .summary-card.passed { background-color: #4CAF50; color: white; }");
            writer.println("        .summary-card.failed { background-color: #f44336; color: white; }");
            writer.println("        .summary-card.time { background-color: #FF9800; color: white; }");
            writer.println("        .summary-card h3 { margin: 0; font-size: 16px; }");
            writer.println("        .summary-card .value { font-size: 36px; font-weight: bold; margin: 10px 0; }");
            writer.println("        .module { margin: 20px 0; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }");
            writer.println("        .module-header { background-color: #f8f9fa; padding: 15px; font-weight: bold; font-size: 18px; }");
            writer.println("        .test-result { padding: 12px 15px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }");
            writer.println("        .test-result:last-child { border-bottom: none; }");
            writer.println("        .test-result.passed { background-color: #f1f8f4; }");
            writer.println("        .test-result.failed { background-color: #fef5f5; }");
            writer.println("        .test-name { flex: 1; }");
            writer.println("        .test-status { font-weight: bold; margin-right: 15px; }");
            writer.println("        .test-status.passed { color: #4CAF50; }");
            writer.println("        .test-status.failed { color: #f44336; }");
            writer.println("        .test-time { color: #666; font-size: 14px; }");
            writer.println("        .test-message { color: #666; font-size: 14px; margin-top: 5px; }");
            writer.println("        .error-details { background-color: #fff3cd; border: 1px solid #ffc107; border-radius: 4px; padding: 10px; margin-top: 5px; font-family: monospace; font-size: 12px; }");
            writer.println("        .timestamp { text-align: right; color: #999; margin-top: 20px; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("    <div class='container'>");
            writer.println("        <h1>🧪 KookBC 功能测试报告</h1>");

            // 计算统计数据
            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;
            long totalTime = 0;

            for (List<TestResult> results : moduleResults.values()) {
                for (TestResult result : results) {
                    totalTests++;
                    if (result.isPassed()) {
                        passedTests++;
                    } else {
                        failedTests++;
                    }
                    totalTime += result.getExecutionTime();
                }
            }

            // 摘要卡片
            writer.println("        <div class='summary'>");
            writer.println("            <div class='summary-card total'>");
            writer.println("                <h3>总测试数</h3>");
            writer.println("                <div class='value'>" + totalTests + "</div>");
            writer.println("            </div>");
            writer.println("            <div class='summary-card passed'>");
            writer.println("                <h3>通过</h3>");
            writer.println("                <div class='value'>" + passedTests + "</div>");
            if (totalTests > 0) {
                writer.println("                <div>" + String.format("%.1f", passedTests * 100.0 / totalTests) + "%</div>");
            }
            writer.println("            </div>");
            writer.println("            <div class='summary-card failed'>");
            writer.println("                <h3>失败</h3>");
            writer.println("                <div class='value'>" + failedTests + "</div>");
            if (totalTests > 0) {
                writer.println("                <div>" + String.format("%.1f", failedTests * 100.0 / totalTests) + "%</div>");
            }
            writer.println("            </div>");
            writer.println("            <div class='summary-card time'>");
            writer.println("                <h3>总耗时</h3>");
            writer.println("                <div class='value'>" + totalTime + "</div>");
            writer.println("                <div>毫秒</div>");
            writer.println("            </div>");
            writer.println("        </div>");

            // 详细结果
            writer.println("        <h2>📋 详细测试结果</h2>");
            for (Map.Entry<String, List<TestResult>> entry : moduleResults.entrySet()) {
                String moduleName = entry.getKey();
                List<TestResult> results = entry.getValue();

                writer.println("        <div class='module'>");
                writer.println("            <div class='module-header'>" + escapeHtml(moduleName) + "</div>");

                for (TestResult result : results) {
                    String statusClass = result.isPassed() ? "passed" : "failed";
                    String statusText = result.isPassed() ? "✓ 通过" : "✗ 失败";

                    writer.println("            <div class='test-result " + statusClass + "'>");
                    writer.println("                <div class='test-name'>");
                    writer.println("                    <div><strong>" + escapeHtml(result.getTestName()) + "</strong></div>");
                    if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                        writer.println("                    <div class='test-message'>" + escapeHtml(result.getMessage()) + "</div>");
                    }
                    if (!result.isPassed() && result.getException() != null) {
                        writer.println("                    <div class='error-details'>");
                        writer.println("                        " + escapeHtml(result.getException().toString()));
                        writer.println("                    </div>");
                    }
                    writer.println("                </div>");
                    writer.println("                <div class='test-status " + statusClass + "'>" + statusText + "</div>");
                    writer.println("                <div class='test-time'>" + result.getExecutionTime() + "ms</div>");
                    writer.println("            </div>");
                }

                writer.println("        </div>");
            }

            writer.println("        <div class='timestamp'>报告生成时间: " + dateFormat.format(new Date()) + "</div>");
            writer.println("    </div>");
            writer.println("</body>");
            writer.println("</html>");

            logger.info("HTML测试报告已生成: {}", reportFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("生成HTML测试报告失败", e);
        }
    }

    /**
     * 生成文本格式的测试报告
     */
    public void generateTextReport() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File reportFile = new File(reportDir, "test_report_" + timestamp + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            writer.println("====================================");
            writer.println("      KookBC 功能测试报告");
            writer.println("====================================");
            writer.println("生成时间: " + dateFormat.format(new Date()));
            writer.println();

            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;
            long totalTime = 0;

            for (Map.Entry<String, List<TestResult>> entry : moduleResults.entrySet()) {
                String moduleName = entry.getKey();
                List<TestResult> results = entry.getValue();

                writer.println("【" + moduleName + "】");
                writer.println("----------------------------------------");

                for (TestResult result : results) {
                    totalTests++;
                    if (result.isPassed()) {
                        passedTests++;
                    } else {
                        failedTests++;
                    }
                    totalTime += result.getExecutionTime();

                    writer.println(result.toString());
                }

                writer.println();
            }

            writer.println("====================================");
            writer.println("测试统计");
            writer.println("====================================");
            writer.println("总测试数: " + totalTests);
            writer.println("通过: " + passedTests + " (" +
                    (totalTests > 0 ? String.format("%.2f", passedTests * 100.0 / totalTests) : "0") + "%)");
            writer.println("失败: " + failedTests + " (" +
                    (totalTests > 0 ? String.format("%.2f", failedTests * 100.0 / totalTests) : "0") + "%)");
            writer.println("总耗时: " + totalTime + "ms");
            writer.println("====================================");

            logger.info("文本测试报告已生成: {}", reportFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("生成文本测试报告失败", e);
        }
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
