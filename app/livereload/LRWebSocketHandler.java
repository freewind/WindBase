package livereload;

import play.mvc.Http;

public class LRWebSocketHandler {

    private LRProtocol p = new LRProtocol();

    public LRWebSocketHandler() {
    }

    void notifyChange(String path) throws Exception {
        System.out.println("### notify path: " + path);
        Http.Inbound inbound = Http.Inbound.current();
        if (inbound != null && inbound.isOpen()) {
            String msg = p.reload(path);
            System.out.println("### send: " + msg);
            Http.Outbound.current().send(msg);
        }
    }
}