package irc;

import cfg.CFGManager;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;

    public Client() {
        CFGManager cfgManager = new CFGManager();

        try {
            socket = new Socket(cfgManager.getServer(), Integer.parseInt(cfgManager.getPort()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            bw.write("PASS " + cfgManager.getPass() + "\r\n");
            bw.write("NICK " + cfgManager.getNick() + "\r\n");
            bw.write("JOIN " + cfgManager.getChannel() + "\r\n");

            bw.write("CAP REQ :twitch.tv/membership\r\n");
            bw.write("CAP REQ :twitch.tv/tags\r\n");
            bw.write("CAP REQ :twitch.tv/commands\r\n");
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String str) throws IOException {
        bw.write(str);
        bw.flush();
    }

    public String read() throws IOException {
        return br.readLine();
    }

    public boolean isConnected() {
        return (socket != null) && socket.isConnected() && !socket.isClosed();
    }

    public void stop() {
        try {
            bw.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
