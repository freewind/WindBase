package controllers;

import play.mvc.Http;
import play.mvc.WebSocketController;

/**
 * User: freewind
 * Date: 13-3-2
 * Time: 下午6:47
 */
public class WebSocket extends WebSocketController {

    public static void xxx() {
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
