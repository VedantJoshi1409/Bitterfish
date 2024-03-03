import java.security.SecureRandom;

public class Zobrist {
    public static long[][] pieceKeys = new long[12][64]; //1 is wPawn, 6 is wKing, 7 is bPawn, 12 is bKing
    public static long[] enPassantKeys = new long[64];
    public static long[] castleKeys = new long[16];
    public static long sideKey;

    public static long randomLong() {
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }

    public static void initKeys() {
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

    public static long generateKey(Board board) {
        long key = 0L, enPassant = board.previousPawnPush, castle = board.castleRights;
        int currentPieceSquare;
        long bPawn;
        long wPawn;
        long bRook;
        long wRook;
        long bKnight;
        long wKnight;
        long bBishop;
        long wBishop;
        long bQueen;
        long wQueen;
        long bKing;
        long wKing;
        if (board.player) {
            bPawn = board.ePawn;
            wPawn = board.fPawn;
            bRook = board.eRook;
            wRook = board.fRook;
            bKnight = board.eKnight;
            wKnight = board.fKnight;
            bBishop = board.eBishop;
            wBishop = board.fBishop;
            bQueen = board.eQueen;
            wQueen = board.fQueen;
            bKing = board.eKing;
            wKing = board.fKing;
        } else {
            bPawn = board.fPawn;
            wPawn = board.ePawn;
            bRook = board.fRook;
            wRook = board.eRook;
            bKnight = board.fKnight;
            wKnight = board.eKnight;
            bBishop = board.fBishop;
            wBishop = board.eBishop;
            bQueen = board.fQueen;
            wQueen = board.eQueen;
            bKing = board.fKing;
            wKing = board.eKing;
        }
        while (wPawn != 0) {
            currentPieceSquare = BitMethods.getLS1B(wPawn);
            wPawn &= ~(1L<<currentPieceSquare);
            key ^= pieceKeys[0][currentPieceSquare];
        }

        while (wRook != 0) {
            currentPieceSquare = BitMethods.getLS1B(wRook);
            wRook &= ~(1L<<currentPieceSquare);
            key ^= pieceKeys[1][currentPieceSquare];
        }

        while (wKnight != 0) {
            currentPieceSquare = BitMethods.getLS1B(wKnight);
            wKnight &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[2][currentPieceSquare];
        }

        while (wBishop != 0) {
            currentPieceSquare = BitMethods.getLS1B(wBishop);
            wBishop &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[3][currentPieceSquare];
        }

        while (wQueen != 0) {
            currentPieceSquare = BitMethods.getLS1B(wQueen);
            wQueen &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[4][currentPieceSquare];
        }

        while (wKing != 0) {
            currentPieceSquare = BitMethods.getLS1B(wKing);
            wKing &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[5][currentPieceSquare];
        }

        while (bPawn != 0) {
            currentPieceSquare = BitMethods.getLS1B(bPawn);
            bPawn &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[6][currentPieceSquare];
        }

        while (bRook != 0) {
            currentPieceSquare = BitMethods.getLS1B(bRook);
            bRook &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[7][currentPieceSquare];
        }

        while (bKnight != 0) {
            currentPieceSquare = BitMethods.getLS1B(bKnight);
            bKnight &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[8][currentPieceSquare];
        }

        while (bBishop != 0) {
            currentPieceSquare = BitMethods.getLS1B(bBishop);
            bBishop &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[9][currentPieceSquare];
        }

        while (bQueen != 0) {
            currentPieceSquare = BitMethods.getLS1B(bQueen);
            bQueen &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[10][currentPieceSquare];
        }

        while (bKing != 0) {
            currentPieceSquare = BitMethods.getLS1B(bKing);
            bKing &= ~(1L << currentPieceSquare);
            key ^= pieceKeys[11][currentPieceSquare];
        }

        if (enPassant != 0) {
            key ^= enPassantKeys[BitMethods.getLS1B(enPassant)];
        }
        if (board.player) key ^= sideKey;
        key ^= castleKeys[(int) castle];
        return key;
    }
}