package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import db.Database;
import db.SessionDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.config.AppConfig;
import webserver.handler.RouteGuide;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    // 스레드 풀 생성 (최대 10개 스레드 유지)
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    // 세션 만료 관리용 단일 스케줄러
    private static final ScheduledExecutorService sessionCleaner = Executors.newSingleThreadScheduledExecutor();

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        Database database = AppConfig.getDatabase();
        RouteGuide routeGuide = new RouteGuide(AppConfig.getRouteMappings());

        // [백그라운드 작업] 만료된 세션 청소
        startSessionMaintenance();

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                executorService.execute(new RequestHandler(connection, routeGuide, database));
            }
        } finally {
            executorService.shutdown();
            // 서버 종료 시 스레드 풀 종료
            executorService.shutdown();
        }
    }

    private static void startSessionMaintenance() {
        // 60분마다 SessionDatabase를 순회하며 완료된 Entry 제거
        sessionCleaner.scheduleAtFixedRate(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                int beforeCount = SessionDatabase.getAll().size();

                // 유효하지 않은 만료된 항목 삭제
                SessionDatabase.getAll().entrySet().removeIf(entry ->
                        SessionManager.isExpired(entry.getValue())
                );

                int removedCount = beforeCount - SessionDatabase.getAll().size();
                if (removedCount > 0) {
                    logger.debug("Maintenance: {} expired sessions purged.", removedCount);
                }
            } catch (Exception e) {
                logger.error("Maintenance Error: {}", e.getMessage());
            }
        }, 1, 1, TimeUnit.MINUTES);

        logger.info("Session Maintenance Task initialized.");
    }
}