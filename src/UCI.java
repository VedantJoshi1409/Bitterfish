import java.util.Scanner;

public class UCI {
    Scanner sc;
    Board board;
    int baseMovetime = -1;

    public UCI() {
        sc = new Scanner(System.in);
    }

    void loop() {
        String lineIn = sc.nextLine();
        while (!lineIn.equals("quit")) {
            switch (lineIn.split(" ")[0]) {
                case "setoption":
                    setoption(lineIn);
                    break;
                case "isready":
                    System.out.println("readyok");
                    break;
                case "ucinewgame":
                    newGame();
                    break;
                case "position":
                    position(lineIn);
                    break;
                case "go":
                    go(lineIn);
                    break;
                case "stop":
                    stop();
                    break;
            }

            lineIn = sc.nextLine();
        }
    }

    void setoption(String command) {
        String[] commands = command.split(" ");
        if (commands[2].equalsIgnoreCase("nnue")) {
            Main.nnue = Boolean.parseBoolean(commands[4]);
        } else if (commands[2].equalsIgnoreCase("clear")) {
            TTable.clearTables();
        }
    }

    void newGame() {
        Repetition.clearTables();
        TTable.clearTables();
        board = new Board(PosConstants.startPos);
        Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);
    }

    void position(String command) {
        String pos = command.split(" ")[1];
        if (pos.equals("startpos")) {
            board = new Board(PosConstants.startPos);
        } else {
            board = new Board(command.substring(9));
        }

        Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

        if (command.contains("moves")) {
            String[] moves = command.substring(command.indexOf("moves")).split(" ");
            Board nextBoard = board;

            Repetition.clearTables();
            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            for (int i = 1; i < moves.length; i++) {
                MoveList moveList = MoveGeneration.getMoves(nextBoard);
                nextBoard = new Board(nextBoard);
                nextBoard.makeMove(moveList.getMoveFromString(moves[i]));
                Repetition.addToHistory(nextBoard.zobristKey, Repetition.historyFlag);
            }

            board = nextBoard;
        }
//        System.out.println(board);
    }

    void go(String command) {
        String[] commands = command.split(" ");
        int movetime = 1000;
        int incBonus = 0;
        String side;
        String inc;
        if (board.player) {
            side = "wtime";
            inc = "winc";
        } else {
            side = "btime";
            inc = "binc";
        }
        int remainingTime = baseMovetime;

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals("movetime")) {
                movetime = Integer.parseInt(commands[i + 1]);
                break;
            }
            if (commands[i].equals(side)) {
                remainingTime = Integer.parseInt(commands[i + 1]);
                if (remainingTime > baseMovetime || remainingTime < 5000) {
                    baseMovetime = remainingTime;
                }
                movetime = Math.max(baseMovetime / 50, 250);
            }
            if (commands[i].equals("movestogo")) {
                movetime = Math.max(remainingTime / Integer.parseInt(commands[i + 1]), 250);
                break;
            }
            if (commands[i].equals(inc)) {
                incBonus = Integer.parseInt(commands[i + 1]);
            }
        }
        if (movetime != 250) {
            movetime+=incBonus;
        } else {
            movetime+=incBonus-200;
        }

        Board temp = Engine.engineMove(board, movetime);
        System.out.println(getBestMove(temp));
    }

    String getBestMove(Board board) {
        return ("bestmove " + MoveList.toStringMove(board.pastMove));
    }

    void stop() {
        Engine.kill = true;
    }

    String uciCommand(String command) {
        switch (command.split(" ")[0]) {
            case "isready":
                return "readyok";
            case "ucinewgame":
                newGame();
                break;
            case "position":
                position(command);
                break;
            case "go":
                return goClient(command);
            case "stop":
                stop();
                break;
        }
        return null;
    }

    String goClient(String command) {
        String[] commands = command.split(" ");
        int movetime = 1000;
        boolean setTime = false;
        String side;
        if (board.player) {
            side = "wtime";
        } else {
            side = "btime";
        }

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals("movetime")) {
                movetime = Integer.parseInt(commands[i + 1]);
                setTime = true;
            }
            if (!setTime && commands[i].equals(side)) {
                movetime = Math.max(Integer.parseInt(commands[i + 1]) / 50, 250);
            }
        }

        Board temp = Engine.engineMove(board, movetime);
        return getBestMove(temp);
    }
}
