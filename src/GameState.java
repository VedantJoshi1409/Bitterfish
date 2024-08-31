import java.io.*;
import java.util.Hashtable;
import java.util.Map;

public class GameState {
    FixedCapacityHashMap<Integer, HashEntry> ttable;
    Hashtable<Integer, Integer> repetitionTable;
    Board board;

    public GameState(Board board) {
        this.board = board;
        ttable = (FixedCapacityHashMap<Integer, HashEntry>) TTable.table.clone();
        repetitionTable = (Hashtable<Integer, Integer>) Repetition.positionHistory.clone();
    }

    public GameState(String path) {
        if (path.isEmpty()) {
            loadState("");
        } else {
            loadState(path);
        }
    }

    public GameState() {
    }

    public void saveState(String path) {
        if (path.isEmpty()) {
            path = "Games/state.txt";
        }
        try {
            String temp;
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write("Board");
            out.newLine();
            out.write(board.boardToFen());
            out.newLine();
            out.write("" + board.pastMove);
            out.newLine();
            out.write("" + board.zobristKey);
            out.newLine();

            out.write("Zobrist");
            out.newLine();
            for (long[] pieceKeyArray : Zobrist.pieceKeys) {
                temp = "";
                for (long key : pieceKeyArray) {
                    temp += key + " ";
                }
                out.write(temp);
                out.newLine();
            }
            temp = "";
            for (long key : Zobrist.enPassantKeys) {
                temp += key + " ";
            }
            out.write(temp);
            out.newLine();
            temp = "";
            for (long key : Zobrist.castleKeys) {
                temp += key + " ";
            }
            out.write(temp);
            out.newLine();
            out.write("" + Zobrist.sideKey);
            out.newLine();

            if (board.zobristKey != Zobrist.generateKey(board)) {
                System.out.println("ZOBRIST KEY ERROR");
            }

            out.write("Repetition");
            out.newLine();
            for (Map.Entry<Integer, Integer> entry : repetitionTable.entrySet()) {
                out.write(entry.getKey() + " " + entry.getValue());
                out.newLine();
            }

            out.write("Transposition");
            out.newLine();
            for (Map.Entry<Integer, HashEntry> entry : ttable.entrySet()) {
                HashEntry value = entry.getValue();
                out.write(entry.getKey() + " " + value.key + " " + value.depth + " " + value.flag + " " + value.value + " " + value.bestMove);
                out.newLine();
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadState(String path) {
        if (path.isEmpty()) {
            path = "Games/state.txt";
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String lineIn = in.readLine();
            lineIn = in.readLine();
            board = new Board(lineIn);
            lineIn = in.readLine();
            board.pastMove = Long.parseLong(lineIn);
            lineIn = in.readLine();
            board.zobristKey = Long.parseLong(lineIn);

            String[] keys;
            lineIn = in.readLine();
            for (int i = 0; i < 12; i++) {
                lineIn = in.readLine();
                keys = lineIn.split(" ");
                for (int j = 0; j < 64; j++) {
                    Zobrist.pieceKeys[i][j] = Long.parseLong(keys[j]);
                }
            }

            lineIn = in.readLine();
            keys = lineIn.split(" ");
            for (int i = 0; i < 64; i++) {
                Zobrist.enPassantKeys[i] = Long.parseLong(keys[i]);
            }

            lineIn = in.readLine();
            keys = lineIn.split(" ");
            for (int i = 0; i < 16; i++) {
                Zobrist.castleKeys[i] = Long.parseLong(keys[i]);
            }

            lineIn = in.readLine();
            Zobrist.sideKey = Long.parseLong(lineIn);

            lineIn = in.readLine();
            lineIn = in.readLine();
            String[] values;
            Repetition.positionHistory.clear();
            while (!lineIn.equals("Transposition")) {
                values = lineIn.split(" ");
                Repetition.positionHistory.put(Long.parseLong(values[0]), Integer.parseInt(values[1]));
                lineIn = in.readLine();
            }

            TTable.table.clear();
            HashEntry entry;
            if (lineIn.equals("Transposition")) {
                lineIn = in.readLine();
            }
            while (lineIn != null) {
                values = lineIn.split(" ");
                entry = new HashEntry(Long.parseLong(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]), Double.parseDouble(values[4]), Long.parseLong(values[5]));
                TTable.table.put(Integer.parseInt(values[0]), entry);
                lineIn = in.readLine();
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadZobrist(String path) {
        if (path.isEmpty()) {
            path = "Games/state.txt";
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String lineIn = in.readLine();
            lineIn = in.readLine();
            lineIn = in.readLine();
            lineIn = in.readLine();

            String[] keys;
            lineIn = in.readLine();
            for (int i = 0; i < 12; i++) {
                lineIn = in.readLine();
                keys = lineIn.split(" ");
                for (int j = 0; j < 64; j++) {
                    Zobrist.pieceKeys[i][j] = Long.parseLong(keys[j]);
                }
            }

            lineIn = in.readLine();
            keys = lineIn.split(" ");
            for (int i = 0; i < 64; i++) {
                Zobrist.enPassantKeys[i] = Long.parseLong(keys[i]);
            }

            lineIn = in.readLine();
            keys = lineIn.split(" ");
            for (int i = 0; i < 16; i++) {
                Zobrist.castleKeys[i] = Long.parseLong(keys[i]);
            }

            lineIn = in.readLine();
            Zobrist.sideKey = Long.parseLong(lineIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTransposition(String path) {
        if (path.isEmpty()) {
            path = "Games/state.txt";
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String lineIn = in.readLine();
            while (!lineIn.equals("Transposition")) {
                lineIn = in.readLine();
            }
            lineIn = in.readLine();
            String[] values;
            HashEntry entry;
            while (lineIn != null) {
                values = lineIn.split(" ");
                entry = new HashEntry(Long.parseLong(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]), Double.parseDouble(values[4]), Long.parseLong(values[5]));
                TTable.table.put(Integer.parseInt(values[0]), entry);
                lineIn = in.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
