import java.util.ArrayList;

public class Evaluation {
    public static final int[] earlyPawnScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            -5, -5, 10, 25, 25, 10, -5, -5,
            -10, -10, 0, 20, 20, 0, -10, -10,
            5, -5, 5, -5, -5, 5, -5, 5,
            10, 10, 5, -20, -20, 5, 10, 10,
            0, 0, 0, 0, 0, 0, 0, 0};

    public static final int[] earlyRookScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            5, 10, 10, 10, 10, 10, 10, 5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            -5, 0, 0, 0, 0, 0, 0, -5,
            1, 0, 0, 5, 5, 0, 0, 1
    };

    public static final int[] earlyKnightScores = {
            -50, -40, -30, -30, -30, -30, -40, -50,
            -40, -20, 0, 0, 0, 0, -20, -40,
            -30, 0, 10, 15, 15, 10, 0, -30,
            -30, 5, 15, 20, 20, 15, 5, -30,
            -30, 0, 15, 20, 20, 15, 0, -30,
            -30, 5, 10, 15, 15, 10, 5, -30,
            -40, -20, 0, 5, 5, 0, -20, -40,
            -50, -40, -30, -30, -30, -30, -40, -50,};

    public static final int[] earlyBishopScores = {
            -20, -10, -10, -10, -10, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 0, 5, 10, 10, 5, 0, -10,
            -10, 5, 5, 10, 10, 5, 5, -10,
            -10, 0, 10, 10, 10, 10, 0, -10,
            -10, 10, 10, 10, 10, 10, 10, -10,
            -10, 5, 0, 0, 0, 0, 5, -10,
            -20, -10, -15, -10, -10, -15, -10, -20,
    };

    public static final int[] earlyWhiteQueenScore = {
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -10, 0, 5, 5, 5, 5, 0, -10,
            -5, 0, 5, 5, 5, 5, 0, -5,
            0, 0, 5, 5, 5, 5, 0, -5,
            -10, 5, 5, 5, 5, 5, 0, -10,
            -10, 0, 5, 0, 0, 0, 0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20
    };

    public static final int[] earlyBlackQueenScore = {
            -20, -10, -10, -5, -5, -10, -10, -20,
            -10, 0, 5, 0, 0, 0, 0, -10,
            -10, 5, 5, 5, 5, 5, 0, -10,
            0, 0, 5, 5, 5, 5, 0, -5,
            -5, 0, 5, 5, 5, 5, 0, -5,
            -10, 0, 5, 5, 5, 5, 0, -10,
            -10, 0, 0, 0, 0, 0, 0, -10,
            -20, -10, -10, -5, -5, -10, -10, -20
    };

    public static final int[] earlyKingScores = {
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -30, -40, -40, -50, -50, -40, -40, -30,
            -20, -30, -30, -40, -40, -30, -30, -20,
            -10, -20, -20, -20, -20, -20, -20, -10,
            20, 20, -20, -20, -20, -20, 20, 20,
            20, 30, -10, -10, -10, -10, 30, 20
    };

    public static final int[] endPawnScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            100, 100, 100, 100, 100, 100, 100, 100,
            70, 70, 70, 70, 70, 70, 70, 70,
            50, 50, 50, 50, 50, 50, 50, 50,
            30, 30, 30, 30, 30, 30, 30, 30,
            20, 20, 20, 20, 20, 20, 20, 20,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    public static final int[] endKnightScores = {
            -50, -40, 0, 0, 0, 0, -40, -50,
            -40, 0, 0, 0, 0, 0, 0, -40,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            -40, 0, 0, 0, 0, 0, 0, -40,
            -50, -40, 0, 0, 0, 0, -40, -50,};

    public static final int[] endKingScores = {
            20, 20, 20, 20, 20, 20, 20, 20,
            20, 20, 20, 20, 20, 20, 20, 20,
            10, 20, 20, 20, 20, 20, 20, 10,
            10, 20, 20, 20, 20, 20, 20, 10,
            10, 20, 20, 20, 20, 20, 20, 10,
            10, 20, 20, 20, 20, 20, 20, 10,
            10, 10, 10, 10, 10, 10, 10, 10,
            10, 10, 10, 10, 10, 10, 10, 10};


    public static double evaluation(Long[] board, boolean player) {
        long mate = board[18];
        if (mate != 0) {
            if (mate == 1) {
                return 999999999;
            } else {
                return 0;
            }
        }
        boolean endGame;
        long wk, bk, castle = board[13], castleState = board[22];
        int material, pieceScore = 0, eval, miscellaneousScore = 0;
        ArrayList<Long> wp, wr, wn, wb, wq, bp, br, bn, bb, bq;
        int wpAmount, wrAmount, wnAmount, wbAmount, wqAmount, bpAmount, brAmount, bnAmount, bbAmount, bqAmount, currentPiece, castleScore = 0, wkSquare, bkSquare, wMat, bMat;
        wp = MoveGeneration.separateBits(board[0]);
        wr = MoveGeneration.separateBits(board[1]);
        wn = MoveGeneration.separateBits(board[2]);
        wb = MoveGeneration.separateBits(board[3]);
        wq = MoveGeneration.separateBits(board[4]);
        wk = board[5];
        bp = MoveGeneration.separateBits(board[6]);
        br = MoveGeneration.separateBits(board[7]);
        bn = MoveGeneration.separateBits(board[8]);
        bb = MoveGeneration.separateBits(board[9]);
        bq = MoveGeneration.separateBits(board[10]);
        bk = board[11];
        wpAmount = wp.size();
        wrAmount = wr.size();
        wnAmount = wn.size();
        wbAmount = wb.size();
        wqAmount = wq.size();
        bpAmount = bp.size();
        brAmount = br.size();
        bnAmount = bn.size();
        bbAmount = bb.size();
        bqAmount = bq.size();
        wMat = wpAmount * 99 + wrAmount * 500 + wnAmount * 300 + wbAmount * 310 + wqAmount * 900;
        bMat = bpAmount * 99 + brAmount * 500 + bnAmount * 300 + bbAmount * 310 + bqAmount * 900;
        material = wMat - bMat;
        if (Main.endGame) {
            endGame = true;
        } else {
            endGame = ((wrAmount + wnAmount + wbAmount + (wqAmount * 2)) < 3) && (brAmount + bnAmount + bbAmount + (bqAmount * 2)) < 3;
        }
        if (endGame) {
            for (Long temp : wp) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += endPawnScores[currentPiece];

            }
            for (Long temp : bp) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= endPawnScores[63 - currentPiece];
            }
            for (Long temp : wn) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += endKnightScores[currentPiece];
            }
            for (Long temp : bn) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= endKnightScores[63 - currentPiece];
            }
            wkSquare = MoveGeneration.bitsToMove(wk);
            pieceScore += endKingScores[wkSquare];
            bkSquare = MoveGeneration.bitsToMove(bk);
            pieceScore -= endKingScores[63 - bkSquare];
            if (player && material>0) {
                miscellaneousScore += endKingCornerEval(wkSquare, bkSquare);
            } else if (!player && material < 0) {
                miscellaneousScore -= endKingCornerEval(bkSquare, wkSquare);
            }
        } else {
            for (Long temp : wp) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += earlyPawnScores[currentPiece];
            }
            for (Long temp : bp) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= earlyPawnScores[63 - currentPiece];
            }
            for (Long temp : wr) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += earlyRookScores[currentPiece];
            }
            for (Long temp : br) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= earlyRookScores[63 - currentPiece];
            }
            for (Long temp : wn) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += earlyKnightScores[currentPiece];
            }
            for (Long temp : bn) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= earlyKnightScores[63 - currentPiece];
            }
            for (Long temp : wb) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += earlyBishopScores[currentPiece];
            }
            for (Long temp : bb) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= earlyBishopScores[63 - currentPiece];
            }
            for (Long temp : wq) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore += earlyWhiteQueenScore[currentPiece];
            }
            for (Long temp : bq) {
                currentPiece = MoveGeneration.bitsToMove(temp);
                pieceScore -= earlyBlackQueenScore[currentPiece];
            }
            currentPiece = MoveGeneration.bitsToMove(wk);
            pieceScore += earlyKingScores[currentPiece];
            currentPiece = MoveGeneration.bitsToMove(bk);
            pieceScore -= earlyKingScores[63 - currentPiece];
            castleScore += ((castle >> 1 & 1) * 10 + (castle & 1) * 10 - (castle >> 3 & 1) * 10 - (castle >> 2 & 1) * 10);
            castleScore += ((castleState >> 1 & 1) * 20 - (castleState & 1) * 20);
        }
        if (player) {
            miscellaneousScore += simplifyScore(material, wMat, bMat);
            eval = material + pieceScore + castleScore + miscellaneousScore;
            return eval;
        } else {
            miscellaneousScore += simplifyScore(material, wMat, bMat);
            eval = material + pieceScore + castleScore + miscellaneousScore;
            return -eval;
        }
    }

    public static int countBits(long bits) {
        int counter = 0;
        for (int i = 0; i < 64; i++) {
            if ((bits >> i & 1) == 1) {
                counter++;
            }
        }
        return counter;
    }

    public static int endKingCornerEval(int pk, int ok) {
        int pRank, pFile, oRank, oFile, eval = 0, distBetweenKing;
        pRank = pk/8;
        pFile = pk%8;
        oRank = ok/8;
        oFile = ok%8;
        eval += (Math.max(3-oRank, oRank-4));
        eval += (Math.max(3-oFile, oFile-4));
        distBetweenKing = Math.abs(pRank-oRank) + Math.abs(pFile-oFile);
        eval += (8-distBetweenKing);
        return eval*10;
    }

    public static int simplifyScore(int material, double whiteMaterial, double blackMaterial) {
        double totalMaterial = whiteMaterial/1000 + blackMaterial/1000;
        double multiplier = (4850 - totalMaterial) /1000;
        if (material == 0) {
            return 0;
        } else if (material > 0) {
            return (int) -((totalMaterial)*multiplier);
        } else {
            return (int) ((totalMaterial)*multiplier);
        }
    }
}