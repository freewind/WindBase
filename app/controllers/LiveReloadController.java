package controllers;

import livereload.LRWebSocketHandler;
import livereload.Watcher;
import play.mvc.Http;
import play.mvc.WebSocketController;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Member: Freewind
 * Date: 13-2-2
 * Time: 下午11:14
 */
public class LiveReloadController extends WebSocketController {

    public static void handle() throws Exception {
        LRWebSocketHandler handler = new LRWebSocketHandler();
        Path path = FileSystems.getDefault().getPath(".");
        System.out.println("######## path: " + path.toAbsolutePath());

        Watcher watcher = new Watcher(path);
        watcher.listener = handler;
        watcher.start();
        watcher.run();

        while (inbound.isOpen()) {
            Http.WebSocketEvent e = await(inbound.nextEvent());
            if (e instanceof Http.WebSocketFrame) {
                System.out.println("##### get from web socket client: " + e.toString());
            } else if (e instanceof Http.WebSocketClose) {
                System.out.println("###### Live reload client is closed");
            } else {
                System.out.println("##### other event: " + e.toString());
            }
        }
    }


}
