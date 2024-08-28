import java.io.*;
import java.net.Socket;

public class Client {
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    UCI uci;
    String previousPos;

    public Client(int port) {
        try {
            socket = new Socket("localhost", port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("V10");

            ois = new ObjectInputStream(socket.getInputStream());
            uci = new UCI();
            uci.uciCommand((String) ois.readObject());
            uci.uciCommand((String) ois.readObject());
            uci.uciCommand((String) ois.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void initMatch() {

        try {
            while (true) {
                String command = (String) ois.readObject();
                uci.uciCommand(command);

                if (!command.equals("ucinewgame")) {
                    previousPos = command;
                    String go = (String) ois.readObject();
                    oos.writeObject(uci.goClient(go));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
