import java.security.SecureRandom;
import java.util.ArrayList;

public class Zobrist {
    public static long[][] pieceKeys = new long[12][64];
    public static long[] enPassantKeys = new long[64];
    public static long[] castleKeys = new long[16];
    public static long sideKey;

    public static long randomLong() {
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }

    public static void initializeRandomKeys() {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 12; j++) {
                pieceKeys[j][i] = randomLong();
            }
            enPassantKeys[i] = randomLong();
        }
        for (int i = 0; i < 16; i++) {
            castleKeys[i] = randomLong();
        }
        sideKey = randomLong();
    }

    public static long generateKey(Long[] pieceSpecificBoard, boolean player) {
        ArrayList<Long> piece;
        long key = 0L, currentBoard, enPassant = pieceSpecificBoard[12], castle = pieceSpecificBoard[13];
        int square;
        for (int i = 0; i < 12; i++) {
            currentBoard = pieceSpecificBoard[i];
            piece = MoveGeneration.separateBits(currentBoard);
            for (long currentPiece : piece) {
                square = MoveGeneration.bitsToMove(currentPiece);
                key ^= pieceKeys[i][square];
            }
        }
        if (enPassant != 0) {
            key ^= enPassantKeys[MoveGeneration.bitsToMove(enPassant)];
        }
        if (player) key ^= sideKey;
        key ^= castleKeys[(int) castle];
        return key;
    }


}