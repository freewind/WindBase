package jobs;

import org.apache.commons.io.IOUtils;
import play.Play;
import play.jobs.Job;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Member: Freewind
 * Date: 13-2-2
 * Time: 下午9:35
 */
//@OnApplicationStart
public class LiveReloadJob extends Job {

    @Override
    public void doJob() throws Exception {
        new Thread() {
            @Override
            public void run() {
                ServerSocket server = null;
                try {
                    server = new ServerSocket(35729);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                while (true) {
                    Socket socket = null;
                    Socket client = null;
                    try {
                        socket = server.accept();

                        System.out.println("********** new livereload client");
                        InputStream clientInput = socket.getInputStream();
                        OutputStream clientOutput = socket.getOutputStream();

                        String playPort = Play.configuration.getProperty("http.port");
                        if (isBlank(playPort)) {
                            playPort = "9000";
                        }
                        client = new Socket("localhost", Integer.parseInt(playPort));
                        InputStream serverInput = client.getInputStream();
                        OutputStream serverOutput = client.getOutputStream();

                        while (true) {
                            trans("browser to server", clientInput, serverOutput);
                            trans("server to browser", serverInput, clientOutput);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(client);
                        IOUtils.closeQuietly(socket);
                    }
                }
            }
        }.start();


    }

    private static void trans(String msg, InputStream input, OutputStream output) throws IOException {
        int count = input.available();
        if (count > 0) {
            byte[] bs = new byte[1024];
            input.read(bs, 0, count);
            System.out.println("^^^^ [" + msg + "]tran: " + new String(bs, 0, count));
            output.write(bs, 0, count);
        }
    }


}
