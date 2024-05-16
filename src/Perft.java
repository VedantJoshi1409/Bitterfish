import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Perft {
    final static int random = -1;
    static String[][] perfts = new String[130][2]; //1 is the results and 2 is just the fen

    static void loadPerft() {
        String lineIn;
        boolean exit;
        try {
            BufferedReader in = new BufferedReader(new FileReader("Games/perft tests.txt"));
            for (int i = 0; i < perfts.length; i++) {
                lineIn = in.readLine();
                exit = false;
                for (int j = 0; j < lineIn.length() && !exit; j++) {
                    if (lineIn.charAt(j) == ';') {
                        perfts[i][0] = lineIn.substring(j + 1) + " ";
                        perfts[i][1] = lineIn.substring(0, j - 5);
                        exit = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    static void perftTest(int line) {
        String fen;
        String result;
        int depth;
        int amount;
        int methodResult;
        if (line == random) {
            line = (int) (Math.random() * perfts.length);
            System.out.println("Line: " + (line + 1));
        } else {
            line--;
        }
        result = perfts[line][0];
        fen = perfts[line][1];
        System.out.println(fen);

        Board board = new Board(fen);
        String[] results = result.split(";");
        for (String s : results) {
            int length = s.length();
            depth = Integer.parseInt(s.substring(1, 2));
            amount = Integer.parseInt(s.substring(3, length - 1));
            long start = System.currentTimeMillis();
            methodResult = recursivePerftTest(board, depth - 1, false);
            long end = System.currentTimeMillis();
            System.out.printf("Depth: %d, Nodes: %d, Correct amount: %d, Time taken: %dms\n\n", depth, methodResult, amount, end - start);
        }
    }

    static void perftTest(String fen, int depth) {
        int methodResult;
        System.out.println(fen);
        Board board = new Board(fen);
        for (int i = 0; i < depth; i++) {
            long start = System.currentTimeMillis();
            methodResult = recursivePerftTest(board, i, true);
            long end = System.currentTimeMillis();
            System.out.printf("Depth: %d, Nodes: %d, Time taken: %dms\n\n", i + 1, methodResult, end - start);
        }
    }

    static void perftTest(Board board, int depth) {
        int methodResult;
        for (int i = 0; i < depth; i++) {
            long start = System.currentTimeMillis();
            methodResult = recursivePerftTest(board, i, true);
            long end = System.currentTimeMillis();
            System.out.printf("Depth: %d, Nodes: %d, Time taken: %dms\n\n", i + 1, methodResult, end - start);
        }
    }

    static void perftTest() {
        String fen;
        String result;
        String s;
        int depth;
        int amount;
        int methodResult;

        long start = System.currentTimeMillis();
        for (int i = 0; i < perfts.length; i++) {
            result = perfts[i][0];
            fen = perfts[i][1];
            Board board = new Board(fen);
            String[] results = result.split(";");
            for (int j = 0; j < results.length - 1; j++) {
                s = results[j];
                int length = s.length();
                depth = Integer.parseInt(s.substring(1, 2));
                amount = Integer.parseInt(s.substring(3, length - 1));
                methodResult = recursivePerftTest(board, depth - 1, false);
                if (methodResult != amount) {
                    System.out.println(fen + "\tLine: " + (i + 1));
                    System.out.printf("Depth: %d, Nodes: %d, Correct amount: %d\n\n", depth, methodResult, amount);
                    break;
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Completed in " + (end - start) + "ms");
    }

    private static int recursivePerftTest(Board board, int depth, boolean print) {
        if (print) {
            return recursivePerftTest(board, depth, depth);
        } else {
            return recursivePerftTestNoPrint(board, depth);
        }
    }

    private static int recursivePerftTest(Board board, int depth, int originalDepth) {
        MoveList moveList = MoveGeneration.getMoves(board);
        long[] moves = moveList.moves;
        if (depth == 0) {
            for (int i = 0; i < moveList.count; i++) {
                if (depth == originalDepth) {
                    int start = MoveList.getStartSquare(moves[i]);
                    int end = MoveList.getEndSquare(moves[i]);
                    int promotion = MoveList.getPromotePiece(moves[i]);
                    String promotePiece = "";
                    if (promotion != 0) {
                        switch (promotion) {
                            case 1 -> promotePiece = "r";
                            case 2 -> promotePiece = "n";
                            case 3 -> promotePiece = "b";
                            case 4 -> promotePiece = "q";
                        }
                    }
                    System.out.printf("%s%s: %d\n", BitMethods.moveToStringMove(start), BitMethods.moveToStringMove(end) + promotePiece, 1);
                }
            }
            return moveList.count;
        }
        int count = 0;
        int movesAmount;
        for (int i = 0; i < moveList.count; i++) {
            Board newBoard = new Board(board);
            newBoard.makeMove(moveList.moves[i]);
            movesAmount = recursivePerftTest(newBoard, depth - 1, originalDepth);
            count += movesAmount;
            if (depth == originalDepth) {
                int start = MoveList.getStartSquare(moves[i]);
                int end = MoveList.getEndSquare(moves[i]);
                int promotion = MoveList.getPromotePiece(moves[i]);
                String promotePiece = "";
                if (promotion != 0) {
                    switch (promotion) {
                        case 1 -> promotePiece = "r";
                        case 2 -> promotePiece = "n";
                        case 3 -> promotePiece = "b";
                        case 4 -> promotePiece = "q";
                    }
                }
                System.out.printf("%s%s: %d\n", BitMethods.moveToStringMove(start), BitMethods.moveToStringMove(end) + promotePiece, movesAmount);
            }
        }

        return count;
    }

    private static int recursivePerftTestNoPrint(Board board, int depth) {
        MoveList moveList = MoveGeneration.getMoves(board);
        if (depth == 0) {
            return moveList.count;
        }
        int count = 0;
        int movesAmount;
        for (int i = 0; i < moveList.count; i++) {
            Board newBoard = new Board(board);
            newBoard.makeMove(moveList.moves[i]);
            movesAmount = recursivePerftTestNoPrint(newBoard, depth - 1);
            count += movesAmount;
        }

        return count;
    }

    static void zobristPerftTest() {
        String fen;
        for (int i = 0; i < perfts.length; i++) {
            fen = perfts[i][1];
            Board board = new Board(fen);
            ArrayList<Board[]> arrayList = new ArrayList<>();
            zobristPerftTest(arrayList, board, 4);
            if (arrayList.size() > 0) {
                System.out.println(fen + ": " + arrayList.size());
            }
        }
    }

    static ArrayList<Board[]> zobristPerftTest(int depth) {
        String fen = perfts[(int) (Math.random() * perfts.length)][1];
        System.out.println(fen);
        Board board = new Board(fen);
        ArrayList<Board[]> arrayList = new ArrayList<>();
        zobristPerftTest(arrayList, board, depth);
        return arrayList;
    }

    static ArrayList<Board[]> zobristPerftTest(String fen, int depth) {
        Board board = new Board(fen);
        ArrayList<Board[]> arrayList = new ArrayList<>();
        zobristPerftTest(arrayList, board, depth);
        return arrayList;
    }

    static ArrayList<Board[]> zobristPerftTest(Board board, int depth) {
        ArrayList<Board[]> arrayList = new ArrayList<>();
        zobristPerftTest(arrayList, board, depth);
        return arrayList;
    }

    static void zobristPerftTest(ArrayList<Board[]> arrayList, Board board, int depth) {
        if (depth > 0) {
            MoveList moveList = MoveGeneration.getMoves(board);
            long zobristKey;
            for (int i = 0; i < moveList.count; i++) {
                Board newBoard = new Board(board);
                newBoard.makeMove(moveList.moves[i]);
                zobristKey = Zobrist.generateKey(newBoard);
                if (zobristKey != newBoard.zobristKey) {
                    arrayList.add(new Board[]{board, newBoard});
                } else {
                    zobristPerftTest(arrayList, newBoard, depth - 1);
                }
            }
        }
    }

    static void attackMaskPerftTest() {
        String fen;
        for (int i = 0; i < perfts.length; i++) {
            fen = perfts[i][1];
            Board board = new Board(fen);
            ArrayList<Board[]> arrayList = new ArrayList<>();
            attackMaskPerftTest(arrayList, board, 4);
            if (arrayList.size() > 0) {
                System.out.println(fen + ": " + arrayList.size());
            }
        }
    }

    static ArrayList<Board[]> attackMaskPerftTest(String fen, int depth) {
        Board board = new Board(fen);
        ArrayList<Board[]> arrayList = new ArrayList<>();
        attackMaskPerftTest(arrayList, board, depth);
        return arrayList;
    }

    static ArrayList<Board[]> attackMaskPerftTest(int depth) {
        String fen = perfts[(int) (Math.random() * perfts.length)][1];
        System.out.println(fen);
        Board board = new Board(fen);
        ArrayList<Board[]> arrayList = new ArrayList<>();
        attackMaskPerftTest(arrayList, board, depth);
        return arrayList;
    }

    static void attackMaskPerftTest(ArrayList<Board[]> arrayList, Board board, int depth) {
        if (depth > 0) {
            MoveList moveList = MoveGeneration.getMoves(board);
            for (int i = 0; i < moveList.count; i++) {
                Board newBoard = new Board(board);
                newBoard.makeMove(moveList.moves[i]);
                if (!newBoard.checkAttackMasks()) {
                    arrayList.add(new Board[]{board, newBoard});
                } else {
                    attackMaskPerftTest(arrayList, newBoard, depth - 1);
                }
            }
        }
    }
}
