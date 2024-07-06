package dev.aaron.systemmetrics;

import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

@Service
public class SystemMetricsService {
    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final CentralProcessor processor;
    private final GpuUsageService gpuUsageService;
    private long[] prevTicks;

    public SystemMetricsService(GpuUsageService gpuUsageService) {
        this.gpuUsageService = gpuUsageService;
        this.systemInfo = new SystemInfo();
        this.hardware = this.systemInfo.getHardware();
        this.prevTicks = new long[CentralProcessor.TickType.values().length];
        this.processor = hardware.getProcessor();
    }

    public double getCpuUsage() {
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100.0;
        prevTicks = processor.getSystemCpuLoadTicks();
        return cpuLoad;
    }

    public double getMemoryUsage() {
        long totalMemory = this.hardware.getMemory().getTotal();
        long availableMemory = this.hardware.getMemory().getAvailable();
        double usedMemory = (double) (totalMemory - availableMemory);
        return (usedMemory / totalMemory) * 100;
    }

    public long getTotalMemory() {
        return this.hardware.getMemory().getTotal();
    }

    public long getAvailableMemory() {
        return this.hardware.getMemory().getAvailable();
    }

    public double getGpuUsage() {
        return this.gpuUsageService.getGpuUsage();
    }
}
