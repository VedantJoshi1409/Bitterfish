import java.util.ArrayList;

public class Ponder extends Thread {
    Board board;
    ArrayList<Long> ponderedMoves = new ArrayList<>();
    int maxThinkTime = 5000;

    long[] pvLength = new long[Engine.maxDepth];
    long[][] pvTable = new long[Engine.maxDepth][Engine.maxDepth];
    ArrayList<long[][]> PVs = new ArrayList<>();
    long[][] previousPV = new long[Engine.maxDepth][Engine.maxDepth];

    int totalDepth;
    long startTime;
    long thinkTime;
    double timeOut = 123456789;

    public Ponder(Board board, long[][] pvTable) {
        this.board = board;
        ponderedMoves.add(pvTable[1][1]);

        for (int i = 2; i < pvTable.length; i++) {
            if (pvTable[i][i] == 0) {
                break;
            }
            this.previousPV[i - 2][i - 2] = pvTable[i][i];
        }
    }

    public void run() {
        Board nextBoard;
        nextBoard = new Board(board);
        nextBoard.makeMove(ponderedMoves.get(0));
        engineMove(nextBoard, maxThinkTime);
    }

    public void killThread() {
        thinkTime = -1;
    }

    public void engineMove(Board board, int timeLimit) {
        Board temp;

        startTime = System.currentTimeMillis();
        thinkTime = timeLimit;
        for (int i = 1; i < Engine.maxDepth; i++) { //arbitrary depth limit
            totalDepth = i;

            temp = negaMax(board, i);
            if (temp != null) {
                //endTime = System.currentTimeMillis();
                //System.out.printf("Depth: %-2d Time: %-7s", i, (endTime - startTime + "ms"));

                previousPV = pvTable.clone();
            } else {
                break;
            }
        }
        PVs.add(previousPV);
    }

    public Board negaMax(Board board, int depth) {
        Repetition.refreshTables(); //reset tree table to actual positions

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        int hashFlag = TTable.flagAlpha; //if pv move not found flag this node as alpha

        MoveList moveList = MoveGeneration.getMoves(board);
        pvLength[0] = 0;
        moveList.reorder(board, pvTable[0][0], 0, 0);

        Board bestBoard = null;
        Board nextBoard;
        double eval;
        //boolean foundPV = false;
        boolean repetition, alphaIsARepetition = false;

        for (int i = 0; i < moveList.count; i++) {
            nextBoard = new Board(board);
            nextBoard.makeMove(moveList.moves[i]);
            /*if (foundPV) {
                eval = -negaMax(nextBoard, depth - 1, -alpha - 1, -alpha);
                if ((eval > alpha) && (eval < beta)) { //if move searched after pv found is better than pv then have to full search move
                    eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
                }
            } else {
                eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
            }*/ //move ordering not good enough so this just causes time loss

            repetition = Repetition.addToHistory(nextBoard.zobristKey, Repetition.treeFlag);
            if (repetition) {
                eval = 0;
            } else {
                eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
            }
            Repetition.removeFromHistory(nextBoard.zobristKey);

            //System.out.printf("Move: %s, Eval: %.2f\n",MoveList.toStringMove(moveList.moves[i]), eval);

            if (System.currentTimeMillis() - startTime > thinkTime) { //if timelimit reached
                return null;
            }
            if (eval > alpha) {
                alpha = eval;
                alphaIsARepetition = repetition;
                bestBoard = nextBoard;
                //foundPV = true;
                hashFlag = TTable.flagExact;

                pvTable[0][0] = moveList.moves[i];
                for (int j = 1; j < pvLength[1]; j++) {
                    pvTable[0][j] = pvTable[1][j];
                }
                pvLength[0] = pvLength[1];
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag, 0);
        }
        return bestBoard;
    }

    public double negaMax(Board board, int depth, double alpha, double beta) {
        int hashFlag = TTable.flagAlpha;
        double eval = TTable.getValue(board.zobristKey, depth, alpha, beta).eval;
        if (eval != TTable.noValue) { //if this position is already evaluated with this depth
            return eval;
        }

        if (depth == 0) {
            int mate = MoveGeneration.mateCheck(board);
            if (mate == 0) {
                eval = quiescence(board, alpha, beta);
                //TTable.writeValue(board.zobristKey, depth, eval, TTable.flagExact); //this is breaking something
                return eval;
            } else if (mate == 1) {
                //TTable.writeValue(board.zobristKey, depth, -999999999 - depth, TTable.flagExact);
                return -999999999 - depth;
            } else {
                //TTable.writeValue(board.zobristKey, depth, 0, TTable.flagExact);
                return 0;
            }
        }

        MoveList moveList = MoveGeneration.getMoves(board);
        if (moveList.count == 0) {
            if ((board.fKing & board.eAttackMask) != 0) {
                return -999999999 - depth;
            } else {
                return 0;
            }
        }

        Board nextBoard;
        //boolean foundPV = false;
        boolean repetition, alphaIsARepetition = false;

        int pvIndex = totalDepth - depth;
        pvLength[pvIndex] = pvIndex;
        moveList.reorder(board, pvTable[pvIndex][pvIndex], pvIndex, 0);

        for (int i = 0; i < moveList.count; i++) {
            if (System.currentTimeMillis() - startTime > thinkTime) { //if time limit reached
                return timeOut;
            }

            nextBoard = new Board(board);
            nextBoard.makeMove(moveList.moves[i]);


            /*if (foundPV) {
                eval = -negaMax(nextBoard, depth - 1, -alpha - 1, -alpha);
                if ((eval > alpha) && (eval < beta)) { //if move searched after pv found is better than pv then have to full search move
                    eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
                }
            } else {
                eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
            }*/
            repetition = Repetition.addToHistory(nextBoard.zobristKey, Repetition.treeFlag);
            if (repetition) {
                eval = 0;
            } else {
                eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
            }
            Repetition.removeFromHistory(nextBoard.zobristKey); //once all searches completed with added repetition count, can remove the count

            if (System.currentTimeMillis() - startTime > thinkTime) { //if time limit reached
                return timeOut;
            }

            if (eval >= beta) {
                if (!repetition) {
                    TTable.writeValue(board.zobristKey, depth, beta, TTable.flagBeta, 0);
                } //if repetition occurs, this value is not trustworthy

                return beta;
            }
            if (eval > alpha) {
                hashFlag = TTable.flagExact;
                alpha = eval;
                //foundPV = true;

                alphaIsARepetition = repetition; //if eval is 0 because of repetition and alpha < 0, alpha cannot be trusted

                pvTable[pvIndex][pvIndex] = moveList.moves[i];
                for (int j = pvIndex + 1; j < pvLength[pvIndex + 1]; j++) {
                    pvTable[pvIndex][j] = pvTable[pvIndex + 1][j];
                }
                pvLength[pvIndex] = pvLength[pvIndex + 1];
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag, 0);
        }
        return alpha;
    }

    public double quiescence(Board board, double alpha, double beta) {
        double eval = Evaluation.evaluation(board);
        if (eval >= beta) {
            return beta;
        }
        if (alpha < eval) {
            alpha = eval;
        }

        MoveList moveList = MoveGeneration.getMoves(board);
        Board nextBoard;
        moveList.reorder(board, 0, 0, 0);

        for (int i = 0; i < moveList.count; i++) {
            if (MoveList.getCaptureFlag(moveList.moves[i]) == 1) {
                nextBoard = new Board(board);
                nextBoard.makeMove(moveList.moves[i]);
                eval = -quiescence(nextBoard, -beta, -alpha);

                if (eval >= beta) {
                    return beta;
                }
                if (eval > alpha) {
                    alpha = eval;
                }
            }
        }
        return alpha;
    }
}
