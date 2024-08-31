import TB.Tablebase;

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

        /*newGame();
        long wtime = 1000;
        long winc = 100;
        String[] blocks = testGame.split(" ");
        for (int i = 8; i < blocks.length; i+=2) {
            String temp = "";
            for (int j = 0; j <= i; j++) {
                temp = temp.concat(blocks[j] +  " ");
            }
            long start = System.currentTimeMillis();
            position(temp);


            if (i == 200) {
                GameState state = new GameState(board);
//                state.saveState("");
//                System.out.println("STATE SAVED");
            }

            go(String.format("go wtime %d winc %d", wtime, winc));
            long end = System.currentTimeMillis();
            wtime -= (end - start);
            wtime += winc;
        }
        GameState s = new GameState();
        s.loadZobrist("");
        s.loadTransposition("");
        position(testGame);
        go("go depth 2");*/
    }

    void setoption(String command) {
        String[] commands = command.split(" ");
        if (commands[2].equalsIgnoreCase("nnue")) {
            Main.nnue = Boolean.parseBoolean(commands[5]);
        } else if (commands[2].equalsIgnoreCase("clear")) {
            TTable.clearTables();
        } else if (commands[2].equalsIgnoreCase("tablebase")) {
            Main.tbPath = commands[5];
            Tablebase.init(Main.tbPath);
        } else if (commands[2].equalsIgnoreCase("hashtable")) {
            Main.ttCapacity = Math.max(Math.min(Integer.parseInt(commands[7]), 10000), 16) * 1000000 / 72;
            TTable.init(Main.ttCapacity);
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
            board = new Board(command.substring(13));
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
                /*System.out.println("Move " + (i/2+1) + ": " + moves[i] + ": " + Repetition.getRepetitionAmount(nextBoard.zobristKey, Repetition.historyFlag) + " " + nextBoard.zobristKey + " " + i);
                if (i == 192 || i == 196) {
                    System.out.println(nextBoard);
                }*/
            }

            board = nextBoard;
            System.out.println(board);
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
                movetime =remainingTime / Integer.parseInt(commands[i + 1]);
                break;
            }
            if (commands[i].equals(inc)) {
                incBonus = Integer.parseInt(commands[i + 1]);
            }
            if (commands[i].equals("depth")) {
                System.out.println(getBestMove(Engine.engineMove(Integer.parseInt(commands[i+1]), board)));
                return;
            }
        }
        if (movetime != 250) {
            movetime+=incBonus;
        } else {
            movetime+=incBonus-200;
        }

        Board temp = Engine.engineMove(board, movetime);
        /*if (Repetition.getRepetitionAmount(temp.zobristKey, Repetition.historyFlag) > 0) {
            TTable.clearTables();
        }*/
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
