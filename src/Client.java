import java.io.*;
import java.net.*;

public class Client {
    public static void returnMove() {
        try {
            while (true) {
                Socket socket = new Socket("localhost", 1409);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                DataPackage dataPackage = (DataPackage) ois.readObject();
                int thinkTime = dataPackage.getIntValue();
                Long[] receivedBoard = dataPackage.getLongArray();
                boolean player = dataPackage.getBooleanValue();
                Main.movesSoFar = dataPackage.getMovesSoFar();
                Main.inTheory = dataPackage.getTheory();
                Long[] newBoard = Engine.computerMove(thinkTime, player, receivedBoard);
                Main.currentBoard = newBoard;
                Main.longBoardUpdate(true, Main.currentBoard);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(newBoard);
                oos.close();
                ois.close();
                socket.close();
                if (!Main.endGame) {
                    Main.checkEndGame();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

