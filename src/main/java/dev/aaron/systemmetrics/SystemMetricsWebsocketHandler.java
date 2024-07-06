package dev.aaron.systemmetrics;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArraySet;

public class SystemMetricsWebsocketHandler extends TextWebSocketHandler {
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final SystemMetricsService systemMetricsService;
    private SystemMetricsBroadcastingThread systemMetricsBroadcastingThread;

    public SystemMetricsWebsocketHandler(SystemMetricsService systemMetricsService) {
        this.systemMetricsService = systemMetricsService;
        this.startBroadcasting();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    private void startBroadcasting() {
        this.systemMetricsBroadcastingThread = new SystemMetricsBroadcastingThread(this.systemMetricsService, sessions);
        this.systemMetricsBroadcastingThread.start();
    }

}
