package dev.aaron.systemmetrics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CopyOnWriteArraySet;

public class SystemMetricsBroadcastingThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemMetricsBroadcastingThread.class);
    private final SystemMetricsService systemMetricsService;
    private final CopyOnWriteArraySet<WebSocketSession> sessions;
    private final Gson gson;

    public SystemMetricsBroadcastingThread(SystemMetricsService systemMetricsService, CopyOnWriteArraySet<WebSocketSession> sessions) {
        this.systemMetricsService = systemMetricsService;
        this.gson = new Gson();
        this.sessions = sessions;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                double cpuUsage = this.systemMetricsService.getCpuUsage();
                long totalMemory = this.systemMetricsService.getTotalMemory();
                long availableMemory = this.systemMetricsService.getAvailableMemory();
                double memoryUsage = this.systemMetricsService.getMemoryUsage();
                double gpuUsage = this.systemMetricsService.getGpuUsage();

                Metrics metrics = new Metrics(cpuUsage, totalMemory, availableMemory, memoryUsage, gpuUsage);
                String json = this.gson.toJson(metrics);

                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(json));
                    }
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Set the interrupt flag again
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
