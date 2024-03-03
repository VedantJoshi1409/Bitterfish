import java.io.*;
import java.net.*;

public class Server {
    static int server = 0, client = 0, draw = 0;

    public static void matchMaking(int thinkTime, int amountOfGames) {
        try {
            ServerSocket serverSocket = new ServerSocket(1409);
            boolean serverPlayer = true;
            for (int i = 0; i < amountOfGames; i++) {
                System.out.printf("Server wins: %d\tClient wins: %d\t Draws: %d\n", server, client, draw);
                RepetitionTable.positionHistory.clear();
                Main.fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -");
                Main.arrayToBitBoard();
                Main.pieceBoardUpdate();
                int theoryLimit = (int)(Math.random()*10)+2;
                Main.endGame = false;
                Main.player = true;
                Main.movesSoFar.clear();
                Main.inTheory = theoryLimit != 0;
                int counter = 0;
                while (Main.currentBoard[18] == 0) {
                    if (!Main.endGame) {
                        Main.endGame = Main.checkEndGame();
                    }
                    if (RepetitionTable.addToHistory(Main.currentBoard[14], RepetitionTable.historyFlag)) {
                        draw++;
                        System.out.println("\n\n\nDraw By Repetition");
                        break;
                    }
                    if (counter < theoryLimit) {
                        counter++;
                        try {
                            Thread.sleep(thinkTime);
                        } catch (InterruptedException ignored) {
                        }
                        if (counter >= theoryLimit) {
                            Main.inTheory = false;
                        }
                    }
                    if (Main.currentBoard[15] != 0 && Main.currentBoard[16] != 0) {
                        Main.movesSoFar.add(Main.findMove(Main.currentBoard[15], Main.currentBoard[16]));
                    }
                    if (serverPlayer) {
                        Main.currentBoard = Engine.computerMove(thinkTime, Main.player, Main.currentBoard);

                    } else {
                        Socket socket = serverSocket.accept();
                        DataPackage dataPackage = new DataPackage(Main.currentBoard, Main.player, thinkTime, Main.movesSoFar, Main.inTheory);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(dataPackage);

                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Main.currentBoard = (Long[]) ois.readObject();
                        ois.close();
                        oos.close();
                        socket.close();
                    }
                    Main.longBoardUpdate(true, Main.currentBoard);
                    Main.board = Main.bitsToBoard();
                    Main.frame.repaint();
                    Main.player = !Main.player;
                    serverPlayer = !serverPlayer;
                }
                if (Main.currentBoard[18] == 1) {
                    if (serverPlayer) {
                        server++;
                    } else {
                        client++;
                    }
                    System.out.println("\n\n\nCheckmate");
                } else if (Main.currentBoard[18] == 2) {
                    draw++;
                    System.out.println("\n\n\nStalemate");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
