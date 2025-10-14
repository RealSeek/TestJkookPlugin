package me.realseek.test;

/**
 * 测试结果类，记录单个测试用例的执行结果
 */
public class TestResult {
    private final String testName;
    private final String category;
    private final boolean passed;
    private final String message;
    private final long executionTime;
    private final Throwable exception;

    public TestResult(String testName, String category, boolean passed, String message, long executionTime) {
        this(testName, category, passed, message, executionTime, null);
    }

    public TestResult(String testName, String category, boolean passed, String message, long executionTime, Throwable exception) {
        this.testName = testName;
        this.category = category;
        this.passed = passed;
        this.message = message;
        this.executionTime = executionTime;
        this.exception = exception;
    }

    public String getTestName() {
        return testName;
    }

    public String getCategory() {
        return category;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(category).append("] ");
        sb.append(passed ? "✓" : "✗").append(" ");
        sb.append(testName);
        sb.append(" (").append(executionTime).append("ms)");
        if (message != null && !message.isEmpty()) {
            sb.append(" - ").append(message);
        }
        if (exception != null) {
            sb.append("\n  异常: ").append(exception.getClass().getSimpleName());
            sb.append(": ").append(exception.getMessage());
        }
        return sb.toString();
    }
}
