import TB.Tablebase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TBProbe {
    private static final long TB_MAX_MOVES = (192 + 1);
    private static final long TB_MAX_CAPTURES = 64;
    private static final long TB_MAX_PLY = 256;
    private static final long TB_CASTLING_K = 0x1;     /* White king-side. */
    private static final long TB_CASTLING_Q = 0x2;     /* White queen-side. */
    private static final long TB_CASTLING_k = 0x4;     /* Black king-side. */
    private static final long TB_CASTLING_q = 0x8;     /* Black queen-side. */

    public static final long TB_LOSS = 0;       /* LOSS */
    public static final long TB_BLESSED_LOSS = 1;       /* LOSS but 50-move draw */
    public static final long TB_DRAW = 2;       /* DRAW */
    public static final long TB_CURSED_WIN = 3;       /* WIN but 50-move draw  */
    public static final long TB_WIN = 4;       /* WIN  */

    private static final long TB_PROMOTES_NONE = 0;
    private static final long TB_PROMOTES_QUEEN = 1;
    private static final long TB_PROMOTES_ROOK = 2;
    private static final long TB_PROMOTES_BISHOP = 3;
    private static final long TB_PROMOTES_KNIGHT = 4;

    private static final long TB_RESULT_WDL_MASK = 0x0000000F;
    private static final long TB_RESULT_TO_MASK = 0x000003F0;
    private static final long TB_RESULT_FROM_MASK = 0x0000FC00;
    private static final long TB_RESULT_PROMOTES_MASK = 0x00070000;
    private static final long TB_RESULT_EP_MASK = 0x00080000;
    private static final long TB_RESULT_DTZ_MASK = 0xFFF00000L;
    private static final long TB_RESULT_WDL_SHIFT = 0;
    private static final long TB_RESULT_TO_SHIFT = 4;
    private static final long TB_RESULT_FROM_SHIFT = 10;
    private static final long TB_RESULT_PROMOTES_SHIFT = 16;
    private static final long TB_RESULT_EP_SHIFT = 19;
    private static final long TB_RESULT_DTZ_SHIFT = 20;

    private static final int[] promoteMap = new int[] {
            0, 4, 1, 3, 2
    };

    public static final long TB_RESULT_CHECKMATE = 4;
    public static final long TB_RESULT_STALEMATE = 2;
    public static final long TB_RESULT_FAILED = 0xFFFFFFFFL;

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String base = "https://tablebase.lichess.ovh/standard/mainline?fen=";

    private static boolean initalized = false;

    public static long[] getHttpEval(Board board) {
        StringBuffer response = new StringBuffer();
        try {
            String fen = board.boardToFen();
            fen = fen.replace(' ', '_');
            String url = base + fen;

            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

            // Set request method
            httpClient.setRequestMethod("GET");
            httpClient.setRequestProperty("User-Agent", USER_AGENT);

            // Get response
            int responseCode = httpClient.getResponseCode();
//            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if (response.toString().equals("{\"mainline\":[],\"winner\":null,\"dtz\":0,\"precise_dtz\":0}")) {
                    return new long[]{};
                }
            } else if (responseCode == 429) { //Too many requests
                return new long[]{};
            } else {
                System.out.println("GET request failed. Error Code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Tablebase error: ");
            e.printStackTrace();
        }

        String cutResponse = response.substring(21);
        int shift = 0;
        if (cutResponse.charAt(4) != '"') {
            shift++;
        }
        String move = cutResponse.substring(0, 4 + shift);
        int startIndex = cutResponse.indexOf("precise_dtz") + 13 + shift;
        int endIndex;
        for (int i = startIndex + 1; ; i++) {
            if (!Character.isDigit(cutResponse.charAt(i))) {
                endIndex = i;
                break;
            }
        }
        int dtz = Integer.parseInt(cutResponse.substring(startIndex, endIndex));
        long longMove = MoveGeneration.getMoves(board).getMoveFromString(move);
        int eval;

        if (dtz < 0) {
            eval = 999999999 + dtz;
        } else {
            eval = -100 + dtz;
        }

        return new long[]{longMove, eval};
    }

    public static void init(String path) {
        Tablebase.init(path);
        initalized = true;
    }

    public static int[] getEval(Board board) {
        if (!initalized) {
            return new int[] {(int) TB_RESULT_FAILED};
        }

        long value = getValue(board);

        if (value == TB_RESULT_FAILED || value == TB_RESULT_STALEMATE || value == TB_RESULT_CHECKMATE) {
            return new int[] {(int) value};
        }

        int wdl = (int) ((value & TB_RESULT_WDL_MASK) >> TB_RESULT_WDL_SHIFT);
        int dtz = (int) ((value & TB_RESULT_DTZ_MASK) >> TB_RESULT_DTZ_SHIFT);
        int from = Evaluation.shiftSquares[(int) ((value & TB_RESULT_FROM_MASK) >> TB_RESULT_FROM_SHIFT)];
        int to = Evaluation.shiftSquares[(int) ((value & TB_RESULT_TO_MASK) >> TB_RESULT_TO_SHIFT)];
        int promote = promoteMap[(int) ((value & TB_RESULT_PROMOTES_MASK) >> TB_RESULT_PROMOTES_SHIFT)];
//        int ep = (int) ((value & TB_RESULT_EP_MASK) >> TB_RESULT_EP_SHIFT);
        return new int[]{wdl, dtz, from, to, promote};
    }

    private static long getValue(Board board) {
        long oldWhite, oldBlack, oldKings = board.eKing | board.fKing, oldQueens = board.fQueen | board.eQueen,
                oldRooks = board.fRook | board.eRook,
                oldBishops = board.fBishop | board.eBishop,
                oldKnights = board.fKnight | board.eKnight,
                oldPawns = board.fPawn | board.ePawn,
                previousPawnPush;

        long newWhite = 0, newBlack = 0,
                newKings = 0,
                newQueens = 0,
                newRooks = 0,
                newBishops = 0,
                newKnights = 0,
                newPawns = 0,
                eP = 0;

        int currentSquare;

        if (board.player) {
            oldWhite = board.fOccupied;
            oldBlack = board.eOccupied;
            previousPawnPush = board.previousPawnPush >> 8;
        } else {
            oldWhite = board.eOccupied;
            oldBlack = board.fOccupied;
            previousPawnPush = board.previousPawnPush << 8;
        }

        while (oldWhite != 0) {
            currentSquare = BitMethods.getLS1B(oldWhite);
            oldWhite &= ~(1L << currentSquare);
            newWhite |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldBlack != 0) {
            currentSquare = BitMethods.getLS1B(oldBlack);
            oldBlack &= ~(1L << currentSquare);
            newBlack |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldKings != 0) {
            currentSquare = BitMethods.getLS1B(oldKings);
            oldKings &= ~(1L << currentSquare);
            newKings |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldQueens != 0) {
            currentSquare = BitMethods.getLS1B(oldQueens);
            oldQueens &= ~(1L << currentSquare);
            newQueens |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldRooks != 0) {
            currentSquare = BitMethods.getLS1B(oldRooks);
            oldRooks &= ~(1L << currentSquare);
            newRooks |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldBishops != 0) {
            currentSquare = BitMethods.getLS1B(oldBishops);
            oldBishops &= ~(1L << currentSquare);
            newBishops |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldKnights != 0) {
            currentSquare = BitMethods.getLS1B(oldKnights);
            oldKnights &= ~(1L << currentSquare);
            newKnights |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        while (oldPawns != 0) {
            currentSquare = BitMethods.getLS1B(oldPawns);
            oldPawns &= ~(1L << currentSquare);
            newPawns |= 1L << Evaluation.shiftSquares[currentSquare];
        }

        if (previousPawnPush != 0) {
            eP = Evaluation.shiftSquares[BitMethods.getLS1B(previousPawnPush)];
        }

        return Tablebase.probeDTZ(newWhite, newBlack, newKings, newQueens, newRooks, newBishops, newKnights, newPawns, board.halfMoveClock, board.castleRights, eP, board.player);
    }
}