package dev.aaron.systemmetrics;

public record Metrics(
        double cpuUsage,
        double totalMemory,
        double availableMemory,
        double memoryUsage,
        double gpuUsage
) {
}
