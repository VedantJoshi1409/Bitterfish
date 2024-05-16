import java.util.ArrayList;

public class MoveList {
    static final long startSquare = 63L;
    static final long endSquare = 4032L;
    static final long piece = 28672L;
    static final long promotedPiece = 229376L;
    static final long captureFlag = 262144L;
    static final long doublePushFlag = 524288L;
    static final long enPassantFlag = 1048576L;
    static final long castleFlag = 2097152L;

    //promotion finals
    static final long rookPromotion = 32768L;
    static final long knightPromotion = 65536L;
    static final long bishopPromotion = 98304L;
    static final long queenPromotion = 131072L;

    /*(Victims) Pawn Knight Bishop   Rook  Queen   King
(Attackers)
    Pawn     105    305    305    405    505    605
    Knight   103    303    303    403    503    603
    Bishop   103    303    303    403    503    603
    Rook     102    302    302    402    502    602
    Queen    101    301    301    401    501    601
    King     100    300    300    400    500    600 */

    //[attacker][victim]
    static final int[][] attackScores = {
            {105, 305, 305, 405, 505, 605},
            {103, 303, 303, 403, 503, 603},
            {103, 303, 303, 403, 503, 603},
            {102, 302, 302, 402, 502, 602},
            {101, 301, 301, 401, 501, 601},
            {100, 300, 300, 400, 500, 600}
    };

    long[] moves;
    int count; //amount of moves;

/*
                             move encoding
    00 0000 0000 0000 0011 1111    start square
    00 0000 0000 1111 1100 0000    end square
    00 0000 0111 0000 0000 0000    piece (0 - pawn, 1 - rook, 2 - knight, 3 - bishop, 4 - queen, 5 - king)
    00 0011 1000 0000 0000 0000    promoted piece
    00 0100 0000 0000 0000 0000    capture flag
    00 1000 0000 0000 0000 0000    double push flag
    01 0000 0000 0000 0000 0000    enPassant flag
    10 0000 0000 0000 0000 0000    castling flag
*/

    MoveList() {
        moves = new long[218]; //max amount of moves per position
        count = 0;
    }

    void addMove(int startSquare, int endSquare, int piece, int capture, int doublePush, int enPassant, int castle) {
        long move = startSquare;
        move |= ((long) endSquare << 6);
        move |= ((long) piece) << 12;
        move |= ((long) capture) << 18;
        move |= ((long) doublePush) << 19;
        move |= ((long) enPassant) << 20;
        move |= ((long) castle) << 21;
        moves[count] = move;
        count++;
    }

    void addPromotionMoves(int startSquare, int endSquare, int capture) {
        long move = startSquare;
        move |= ((long) endSquare << 6);
        move |= ((long) capture) << 18;

        move |= rookPromotion;
        moves[count] = move;
        count++;
        move &= ~rookPromotion;
        move |= knightPromotion;
        moves[count] = move;
        count++;
        move &= ~knightPromotion;
        move |= bishopPromotion;
        moves[count] = move;
        count++;
        move &= ~bishopPromotion;
        move |= queenPromotion;
        moves[count] = move;
        count++;
    }

    void addMove(long move) {
        moves[count] = move;
        count++;
    }

    void reorder(Board board, long pvMove) {
        long[][] moveAndScore = new long[count][2];
        long move;
        int attacker;
        int victim = 0;
        int capture;
        int score;

        for (int i = 0; i < count; i++) {
            score = 0;
            move = moves[i];

            attacker = getPiece(move);
            capture = getCaptureFlag(moves[i]);
            if (capture == 1) {
                long endSquareBit = 1L << getEndSquare(move);

                if ((board.ePawn & endSquareBit) != 0) {
                    victim = 0;
                } else if ((board.eRook & endSquareBit) != 0) {
                    victim = 1;
                } else if ((board.eKnight & endSquareBit) != 0) {
                    victim = 2;
                } else if ((board.eBishop & endSquareBit) != 0) {
                    victim = 3;
                } else if ((board.eQueen & endSquareBit) != 0) {
                    victim = 4;
                }
                score += attackScores[attacker][victim];
            } /*else {
                if (attacker == 5 && getCastleFlag(move) == 0) {
                    score -= 250;
                } //if king move
            }*/ //doesnt seem to make a difference

            if (move == pvMove) {
                score += 999999999;
            }

            moveAndScore[i][0] = move;
            moveAndScore[i][1] = score;
        }
        quickSort(moveAndScore);

        for (int i = 0; i < count; i++) {
            moves[i] = moveAndScore[i][0];
        }
    }

    void copyPV(MoveList moveList) {
        moves = new long[218];
        count = 0;
        for (int i = 0; i < moveList.moves.length; i++) {
            if (moveList.moves[i] != 0) {
                addMove(moveList.moves[i]);
            } else {
                break;
            }
        }
    }

    static String toStringPv(long[][] pv) {
        String output = "";
        for (int i = 0; i < pv[0].length; i++) {
            if (pv[0][i] == 0) {
                break;
            }
            output += i + 1 + ". " + MoveList.toStringMove(pv[0][i]) + " ";
        }
        return output;
    }

    static int getStartSquare(long move) {
        return (int) (move & startSquare);
    }

    static int getEndSquare(long move) {
        return (int) ((move & endSquare) >> 6);
    }

    static int getPiece(long move) {
        return (int) ((move & piece) >> 12);
    }

    static int getPromotePiece(long move) {
        return (int) ((move & promotedPiece) >> 15);
    }

    static int getCaptureFlag(long move) {
        return (int) ((move & captureFlag) >> 18);
    }

    static int getDoublePushFlag(long move) {
        return (int) ((move & doublePushFlag) >> 19);
    }

    static int getEnPassantFlag(long move) {
        return (int) ((move & enPassantFlag) >> 20);
    }

    static int getCastleFlag(long move) {
        return (int) ((move & castleFlag) >> 21);
    }

    private static void swap(long[][] arr, int i, int j) {
        long[] temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static int partition(long[][] arr, int low, int high) {
        long[] pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j <= high - 1; j++) {
            if (arr[j][1] > pivot[1]) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    private static void quickSort(long[][] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(long[][] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    public static String toStringMove(long move) {
        return BitMethods.moveToStringMove(getStartSquare(move)) + BitMethods.moveToStringMove(getEndSquare(move));
    }

    public String toString() {
        String output = "";
        long move;
        for (int i = 0; i < count; i++) {
            move = moves[i];
            output += String.format("Move %d:\nStart Square: %s\nEnd Square: %s\nPiece: %d\nPromoted Piece: %d\nCapture Flag: %d\nDouble Push Flag: %d\nEnPassant Flag: %d\nCastle Flag: %d\n\n", i, BitMethods.moveToStringMove(getStartSquare(move)), BitMethods.moveToStringMove(getEndSquare(move)), getPiece(move), getPromotePiece(move), getCaptureFlag(move), getDoublePushFlag(move), getEnPassantFlag(move), getCastleFlag(move));
        }
        return output;
    }
}
