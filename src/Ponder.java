public class Ponder extends Thread{
    MoveList previousPV;
    MoveList principleVariation;
    Board board;
    long ponderedMove;

    int totalDepth;
    long startTime;
    long thinkTime;
    double timeOut = 123456789;

    public Ponder(Board board, MoveList principleVariation) {
        this.board = new Board(board);
        this.board.makeMove(principleVariation.moves[1]);
        this.principleVariation = new MoveList();
        ponderedMove = principleVariation.moves[1];

        for (int i = 2; i < principleVariation.count; i++) {
            this.principleVariation.addMove(principleVariation.moves[i]);
        }
    }

    public void run() {
        engineMove(board, Integer.MAX_VALUE);
    }

    public void killThread() {
        thinkTime = -1;
    }

    public void engineMove(Board board, int timeLimit) {
        Board temp;
        principleVariation = new MoveList();
        previousPV = new MoveList();

        startTime = System.currentTimeMillis();
        thinkTime = timeLimit;
        for (int i = 1; ; i++) { //arbitrary depth limit
            totalDepth = i;

            temp = negaMax(board, i);
            if (temp != null) {
                //endTime = System.currentTimeMillis();
                //System.out.printf("Depth: %-2d Time: %-7s", i, (endTime - startTime + "ms"));
                //principleVariation.printPV(totalDepth);

                previousPV.copyPV(principleVariation);
            } else {
                break;
            }
        }
    }

    public Board negaMax(Board board, int depth) {
        Repetition.refreshTables(); //reset tree table to actual positions

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        int hashFlag = TTable.flagAlpha; //if pv move not found flag this node as alpha

        MoveList moveList = MoveGeneration.getMoves(board);
        moveList.reorder(board, principleVariation.moves[0]);

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

            if (eval >= beta) {
                principleVariation.moves[0] = moveList.moves[i];
                if (!repetition) {
                    TTable.writeValue(board.zobristKey, depth, beta, TTable.flagBeta);
                }
                return nextBoard;
            }
            if (eval > alpha) {
                alpha = eval;
                alphaIsARepetition = repetition;
                bestBoard = nextBoard;
                //foundPV = true;
                hashFlag = TTable.flagExact;

                principleVariation.moves[0] = moveList.moves[i];
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag);
        }
        return bestBoard;
    }

    public double negaMax(Board board, int depth, double alpha, double beta) {
        int hashFlag = TTable.flagAlpha;
        double eval = TTable.getValue(board.zobristKey, depth, alpha, beta);
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
        moveList.reorder(board, principleVariation.moves[pvIndex]);

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
                    TTable.writeValue(board.zobristKey, depth, beta, TTable.flagBeta);
                } //if repetition occurs, this value is not trustworthy

                return beta;
            }
            if (eval > alpha) {
                hashFlag = TTable.flagExact;
                alpha = eval;
                //foundPV = true;

                alphaIsARepetition = repetition; //if eval is 0 because of repetition and alpha < 0, alpha cannot be trusted
                principleVariation.moves[pvIndex] = moveList.moves[i];
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag);
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
        moveList.reorder(board, 0);

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
