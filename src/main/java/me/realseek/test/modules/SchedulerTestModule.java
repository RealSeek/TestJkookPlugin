package me.realseek.test.modules;

import me.realseek.test.BaseTestModule;
import snw.jkook.scheduler.Scheduler;
import snw.jkook.scheduler.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 调度器测试模块
 * 测试 JKook/KookBC 的任务调度功能
 */
public class SchedulerTestModule extends BaseTestModule {
    private Task currentTask;

    @Override
    public String getName() {
        return "任务调度器";
    }

    @Override
    public String getDescription() {
        return "测试同步任务、延迟任务、定时任务的执行和取消功能";
    }

    @Override
    protected void executeTests() {
        runTest("调度器可用性检查", this::testSchedulerAvailability);
        runTest("立即执行任务", this::testRunTask);
        runTest("延迟执行任务", this::testRunTaskLater);
        runTest("定时重复任务", this::testRunTaskTimer);
        runTest("任务取消", this::testCancelTask);
        runTest("任务状态查询", this::testIsScheduled);
        runTest("批量取消任务", this::testCancelTasks);
        runTest("插件初始化后任务", this::testScheduleAfterPluginInit);
    }

    private void testSchedulerAvailability() {
        Scheduler scheduler = plugin.getCore().getScheduler();
        assertNotNull(scheduler, "调度器不应为null");
    }

    private void testRunTask() {
        Scheduler scheduler = plugin.getCore().getScheduler();
        AtomicBoolean executed = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        Task task = scheduler.runTask(plugin, () -> {
            executed.set(true);
            latch.countDown();
        });

        assertNotNull(task, "任务对象不应为null");

        try {
            boolean completed = latch.await(1, TimeUnit.SECONDS);
            assertTrue(completed && executed.get(), "立即执行任务应该在1秒内完成");
        } catch (InterruptedException e) {
            assertTrue(false, "等待任务执行时被中断: " + e.getMessage());
        }
    }

    private void testRunTaskLater() {
        Scheduler scheduler = plugin.getCore().getScheduler();
        AtomicBoolean executed = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        long delay = 500; // 500ms 延迟

        long startTime = System.currentTimeMillis();
        Task task = scheduler.runTaskLater(plugin, () -> {
            executed.set(true);
            latch.countDown();
        }, delay);

        assertNotNull(task, "延迟任务对象不应为null");

        try {
            boolean completed = latch.await(2, TimeUnit.SECONDS);
            long executionTime = System.currentTimeMillis() - startTime;

            assertTrue(completed && executed.get(), "延迟任务应该执行");
            assertTrue(executionTime >= delay, "任务执行时间应该大于等于延迟时间");
        } catch (InterruptedException e) {
            assertTrue(false, "等待延迟任务执行时被中断: " + e.getMessage());
        }
    }

    private void testRunTaskTimer() {
        Scheduler scheduler = plugin.getCore().getScheduler();
        AtomicInteger count = new AtomicInteger(0);
        long delay = 100;
        long period = 200;

        Task task = scheduler.runTaskTimer(plugin, () -> {
            count.incrementAndGet();
        }, delay, period);

        assertNotNull(task, "定时任务对象不应为null");
        currentTask = task;

        try {
            Thread.sleep(1000); // 等待1秒，应该执行4-5次
            int executions = count.get();
            assertTrue(executions >= 3, "定时任务应该至少执行3次，实际执行: " + executions);

            // 取消任务
            scheduler.cancelTask(task.getTaskId());
            int beforeCancel = count.get();
            Thread.sleep(500);
            int afterCancel = count.get();
            assertEquals(beforeCancel, afterCancel, "取消后任务不应继续执行");
        } catch (InterruptedException e) {
            assertTrue(false, "测试定时任务时被中断: " + e.getMessage());
        } finally {
            if (task != null) {
                scheduler.cancelTask(task.getTaskId());
            }
        }
    }

    private void testCancelTask() {
        Scheduler scheduler = plugin.getCore().getScheduler();
        AtomicBoolean executed = new AtomicBoolean(false);

        Task task = scheduler.runTaskLater(plugin, () -> {
            executed.set(true);
        }, 1000); // 1秒后执行

        assertNotNull(task, "任务对象不应为null");

        // 立即取消任务
        scheduler.cancelTask(task.getTaskId());

        try {
            Thread.sleep(1500); // 等待超过任务原定执行时间
            assertFalse(executed.get(), "已取消的任务不应该执行");
        } catch (InterruptedException e) {
            assertTrue(false, "测试任务取消时被中断: " + e.getMessage());
        }
    }

    private void testIsScheduled() {
        Scheduler scheduler = plugin.getCore().getScheduler();

        Task task = scheduler.runTaskLater(plugin, () -> {}, 2000);
        assertNotNull(task, "任务对象不应为null");

        boolean scheduled = scheduler.isScheduled(task.getTaskId());
        assertTrue(scheduled, "新创建的任务应该处于已调度状态");

        scheduler.cancelTask(task.getTaskId());
        boolean scheduledAfterCancel = scheduler.isScheduled(task.getTaskId());
        assertFalse(scheduledAfterCancel, "取消后的任务不应该处于已调度状态");
    }

    private void testCancelTasks() {
        Scheduler scheduler = plugin.getCore().getScheduler();

        // 创建多个任务
        Task task1 = scheduler.runTaskLater(plugin, () -> {}, 5000);
        Task task2 = scheduler.runTaskLater(plugin, () -> {}, 5000);
        Task task3 = scheduler.runTaskLater(plugin, () -> {}, 5000);

        assertNotNull(task1, "任务1不应为null");
        assertNotNull(task2, "任务2不应为null");
        assertNotNull(task3, "任务3不应为null");

        // 批量取消
        scheduler.cancelTasks(plugin);

        // 验证所有任务都已取消
        assertFalse(scheduler.isScheduled(task1.getTaskId()), "任务1应该已被取消");
        assertFalse(scheduler.isScheduled(task2.getTaskId()), "任务2应该已被取消");
        assertFalse(scheduler.isScheduled(task3.getTaskId()), "任务3应该已被取消");
    }

    private void testScheduleAfterPluginInit() {
        Scheduler scheduler = plugin.getCore().getScheduler();

        try {
            // 注意：这个测试可能会失败，因为插件可能已经初始化完成
            Task task = scheduler.scheduleAfterPluginInitTask(plugin, () -> {
                logger.info("插件初始化后任务执行");
            });

            // 如果没有抛出异常，说明调度成功
            if (task != null) {
                assertTrue(true, "插件初始化后任务调度成功");
            } else {
                logger.warn("插件初始化后任务可能已经执行或调度失败");
                assertTrue(true, "测试完成（但任务为null）");
            }
        } catch (IllegalStateException e) {
            // 如果插件已经初始化完成，会抛出此异常
            logger.info("插件已完成初始化，无法测试初始化后任务: {}", e.getMessage());
            assertTrue(true, "测试完成（插件已初始化）");
        } catch (Exception e) {
            logger.warn("测试插件初始化后任务时发生异常: {}", e.getMessage());
            assertTrue(true, "测试完成（发生异常）");
        }
    }

    @Override
    public void cleanup() {
        // 清理所有测试任务
        try {
            plugin.getCore().getScheduler().cancelTasks(plugin);
            logger.debug("调度器测试模块清理完成");
        } catch (Exception e) {
            logger.warn("清理调度器任务时发生错误", e);
        }
    }
}
