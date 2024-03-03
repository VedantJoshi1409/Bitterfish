import java.io.*;
import java.net.Socket;

public class Client {
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public Client(int port) {
        try {
            socket = new Socket("localhost", port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("V3");

            ois = new ObjectInputStream(socket.getInputStream());
            ZobristPackage zobristPackage = (ZobristPackage) ois.readObject();
            zobristPackage.implementValues();
            System.out.println("Zobrist Package Received!");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void initMatch() {
        int thinkTime = -1;
        Package dataPackage;
        Board board;

        try {
            thinkTime = ois.readInt();
            System.out.println("Think time: " + thinkTime);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                dataPackage = (Package) ois.readObject();
                Repetition.positionHistory = dataPackage.positionHistory;
                board = dataPackage.board;

                board = Engine.engineMove(board, thinkTime);
                Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

                dataPackage = new Package(board, Repetition.positionHistory);
                oos.writeObject(dataPackage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
