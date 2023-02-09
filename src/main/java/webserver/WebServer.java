package webserver;

import lombok.extern.slf4j.Slf4j;
import repository.MemorySessionRepository;
import repository.MemoryUserRepository;
import webserver.controller.GlobalController;
import webserver.controller.RequestMappingHandler;
import webserver.service.SessionService;
import webserver.service.UserService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class WebServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int THREAD_NUMS = 50;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUMS);

    public static void main(String[] args) throws Exception {

        // 포트 설정
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버 객체 생성
        UserService userService = new UserService(new MemoryUserRepository());
        SessionService sessionService = new SessionService(new MemorySessionRepository());
        GlobalController globalController = new GlobalController(userService, sessionService);
        RequestMappingHandler requestMappingHandler = new RequestMappingHandler(globalController);


        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            log.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                doTask(connection, requestMappingHandler);
            }
        }
    }

    private static void doTask(Socket connection, RequestMappingHandler requestMappingHandler) {
        threadPool.submit(() -> {
            RequestHandler requestHandler = new RequestHandler(connection, requestMappingHandler);
            requestHandler.run();
        });
    }
}
