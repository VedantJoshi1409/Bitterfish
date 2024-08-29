public class Engine {
    final static int nonEval = 123456789;

    final static int nullMoveReduction = 2;
    static boolean inNullMove;

    final static int fullDepthMoves = 4;
    final static int reductionLimit = 3;

    //static Ponder ponder;
    static int maxDepth = 30;
    static boolean tbMove;

    //static Ponder ponder;
    static long[] pvLength = new long[maxDepth];
    static long[][] pvTable = new long[maxDepth][maxDepth];
    static long[][] previousPV = new long[maxDepth][maxDepth];
    static double evaluation;

    static long[][] killerMoves = new long[2][maxDepth];
    static int[][] historyMoves = new int[6][64];

    static int totalDepth;
    static int nodes;
    static long thinkTime, startTime, endTime;
    static boolean kill;


    public static Board engineMove(Board board, int timeLimit) {
        Board bestBoard = null, temp;
        kill = false;

        startTime = System.currentTimeMillis();
        thinkTime = timeLimit;

        /*if (ponder != null && ponder.ponderedMoves.size() != 0 && ponder.PVs.size() != 0) {
            ponder.killThread();
            for (int i = 0; i < ponder.ponderedMoves.size(); i++) {
                if (1L<<MoveList.getStartSquare(ponder.ponderedMoves.get(i)) == board.startSquare && 1L<<MoveList.getEndSquare(ponder.ponderedMoves.get(i)) == board.endSquare) {
                    previousPV = ponder.PVs.get(i).clone();
                }
            }
        }*/

        tbMove = false;
        for (int i = 1; i < maxDepth && !kill; i++) { //arbitrary depth limit
            inNullMove = false;
            if (i == 1) { //so first move can be made
                thinkTime = Integer.MAX_VALUE;
            } else {
                thinkTime = timeLimit;
            }

            totalDepth = i;
            nodes = 0;

            temp = negaMax(board, i);
            if (temp != null) {
                bestBoard = temp;
                previousPV = pvTable.clone();
                pvTable = new long[maxDepth][maxDepth];

                endTime = System.currentTimeMillis();
                if (Main.uci) {
                    System.out.printf("info depth %d nodes %d time %d pv %s score cp %d hashfull %d\n", i, nodes, (endTime - startTime), MoveList.toStringPvUCI(previousPV), (int) evaluation, (int) TTable.hashfull());
//                    System.out.println(TTable.memoryUsage());
                } else {
                    System.out.printf("Depth: %-2d Time: %-11s Nodes: %,-11d PV: %s\n", i, (endTime - startTime + "ms"), nodes, MoveList.toStringPv(previousPV));
                }
            } else {
                break;
            }
            if (MoveGeneration.getMoves(bestBoard).count == 0 && (bestBoard.fKing & bestBoard.eAttackMask) != 0 || tbMove) {
                break;
            }
        }
        //System.out.println();

        //ponder = new Ponder(bestBoard, previousPV);
        //ponder.start(); until I figure out how to have it ponder top 3 moves I wont use this
        return bestBoard;
    }

    private static Board negaMax(Board board, int depth) {
        Repetition.refreshTables(); //reset tree table to actual positions

        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;
        int hashFlag = TTable.flagAlpha; //if pv move not found flag this node as alpha
        pvLength[0] = 0;

        //No need to check for mate or 50 move rule draw in this "wrapper" class as those checks are made in Main.play()
        Pair pair = TTable.getValue(board.zobristKey, depth, alpha, beta);
        long bestMove = pair.bestMove;

        MoveList moveList = MoveGeneration.getMoves(board);
        if (board.endGame) {
            int[] request = TBProbe.getEval(board);
            if (request.length > 1) {
                int from = request[2];
                int to = request[3];
                int promote = request[4];
                long move = moveList.getMoveFromTB(from, to, promote);

                if (move != -1) {
                    Board output = new Board(board);
                    output.makeMove(move);
                    tbMove = true;
                    return output;
                }
            }
        }
        moveList.reorder(board, previousPV[0][0], 0, bestMove);

        Board bestBoard = null;
        Board nextBoard;
        double eval;
        boolean foundPV = false;
        boolean repetition, alphaIsARepetition = false;

        for (int i = 0; i < moveList.count; i++) {
            nextBoard = new Board(board);
            nextBoard.makeMove(moveList.moves[i]);

            repetition = Repetition.addToHistory(nextBoard.zobristKey, Repetition.treeFlag);
            if (repetition) {
                eval = 0;
            } else {

                if (i == 0 || !foundPV)
                    eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
                else {
                    if (i >= fullDepthMoves && depth >= reductionLimit) {
                        eval = -negaMax(nextBoard, depth - 2, -alpha - 1, -alpha);
                    } else {
                        eval = alpha + 1;
                    }
                    if (eval > alpha) {
                        eval = -negaMax(nextBoard, depth - 1, -alpha - 1, -alpha);
                        if (eval > alpha && eval < beta) {
                            eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
                        }
                    }
                }

            }
            Repetition.removeFromHistory(nextBoard.zobristKey);

            //System.out.printf("Move: %s, Eval: %.2f\n",MoveList.toStringMove(moveList.moves[i]), eval);

            if (System.currentTimeMillis() - startTime > thinkTime) { //if timelimit reached
                return null;
            }
            //beta is currently infinity so nothing is greater than beta
            if (eval > alpha) {
                bestMove = moveList.moves[i];

                alpha = eval;
                alphaIsARepetition = repetition;
                bestBoard = nextBoard;
                foundPV = true;
                hashFlag = TTable.flagExact;

                pvTable[0][0] = moveList.moves[i];
                for (int j = 1; j < pvLength[1]; j++) {
                    pvTable[0][j] = pvTable[1][j];
                }
                pvLength[0] = pvLength[1];
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag, bestMove);
        }
        evaluation = alpha;
        return bestBoard;
    }

    private static double negaMax(Board board, int depth, double alpha, double beta) {
        if (board.endGame) {
            int[] request = TBProbe.getEval(board);
            if (request.length == 1) {
                if (request[0] == TBProbe.TB_RESULT_CHECKMATE) {
                    return -9999999 - depth;
                } else if (request[0] == TBProbe.TB_RESULT_STALEMATE) {
                    return 0;
                }
            } else if (request.length > 1) {
                if (request[0] == TBProbe.TB_LOSS) {
                    return -9999999 - depth;
                } else if (request[0] == TBProbe.TB_DRAW || request[0] == TBProbe.TB_BLESSED_LOSS || request[0] == TBProbe.TB_CURSED_WIN) {
                    return 0;
                } else if (request[0] == TBProbe.TB_WIN) {
                    return 9999999 + depth;
                }
            }
        }


        int pvIndex = totalDepth - depth;
        pvLength[pvIndex] = pvIndex;

        int hashFlag = TTable.flagAlpha;
        Pair pair = TTable.getValue(board.zobristKey, depth, alpha, beta);
        double eval = pair.eval;
        long bestMove = pair.bestMove;

        if (eval != TTable.noValue) { //if this position is already evaluated with this depth
            nodes++;
            return eval;
        }

        if (depth <= 0) {
            nodes++;
            int mate = MoveGeneration.mateCheck(board);
            if (mate == 0) {
                eval = quiescence(board, alpha, beta);
                return eval;
            } else if (mate == 1) {
                return -9999999 - depth;
            } else {
                return 0;
            }
        }

        MoveList moveList = MoveGeneration.getMoves(board);
        if (moveList.count == 0) {
            nodes++;
            if ((board.fKing & board.eAttackMask) != 0) {
                return -9999999 - depth;
            } else {
                return 0;
            }
        }

        if (board.halfMoveClock >= 100) {
            return 0;
        }

        Board nextBoard;

        //NULL MOVE PRUNING
        if (!inNullMove && depth >= nullMoveReduction + 1 && pvIndex >= 2 && !board.enemyKingInCheck && (((board.eKing | board.ePawn) & ~board.eOccupied) != 0)) {
            inNullMove = true;
            nextBoard = new Board(board);
            nextBoard.makeNullMove();
            eval = -negaMax(nextBoard, depth - 1 - nullMoveReduction, -beta, -beta + 1);
            inNullMove = false;
            if (eval >= beta) {
                return beta;
            }
        }

        double staticEval = nonEval;

        //REVERSE FUTILITY PRUNING
        if (depth < 3 && (beta - alpha) <= 1 && !board.enemyKingInCheck && Math.abs(beta - 1) > Integer.MIN_VALUE + 100) {
            staticEval = Evaluation.evaluation(board);
            int evalMargin = 120 * depth;
            if (staticEval - evalMargin >= beta) {
                return staticEval - evalMargin;
            }
        }

        //RAZORING
        if (depth <= 2 && (beta - alpha) <= 1 && !board.enemyKingInCheck) {
            nodes++;
            if (staticEval != nonEval) {
                eval = staticEval + 125;
            } else {
                eval = Evaluation.evaluation(board) + 125;
            }
            double newEval;

            if (eval < beta) {
                if (depth == 1) {
                    newEval = quiescence(board, alpha, beta);
                    return Math.max(newEval, eval);
                }
            }

            eval += 175;

            if (eval < beta) {
                newEval = quiescence(board, alpha, beta);
                if (newEval < beta) {
                    return Math.max(newEval, eval);
                }
            }
            nodes--;
        }

        boolean foundPV = false;
        boolean repetition, alphaIsARepetition = false;

        moveList.reorder(board, previousPV[pvIndex][pvIndex], pvIndex, bestMove);
        for (int i = 0; i < moveList.count; i++) {
            if (System.currentTimeMillis() - startTime > thinkTime) { //if time limit reached
                return nonEval;
            }

            nextBoard = new Board(board);
            nextBoard.makeMove(moveList.moves[i]);

            repetition = Repetition.addToHistory(nextBoard.zobristKey, Repetition.treeFlag);
            if (repetition) {
                eval = 0;
            } else {

                if (i == 0 || !foundPV)
                    eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
                else {
                    if (i >= fullDepthMoves && depth >= reductionLimit) {
                        eval = -negaMax(nextBoard, depth - 2, -alpha - 1, -alpha);
                    } else {
                        eval = alpha + 1;
                    }
                    if (eval > alpha) {
                        eval = -negaMax(nextBoard, depth - 1, -alpha - 1, -alpha);
                        if (eval > alpha && eval < beta) {
                            eval = -negaMax(nextBoard, depth - 1, -beta, -alpha);
                        }
                    }
                }

            }
            Repetition.removeFromHistory(nextBoard.zobristKey); //once all searches completed with added repetition count, can remove the count

            if (System.currentTimeMillis() - startTime > thinkTime) { //if time limit reached
                return nonEval;
            }

            if (eval >= beta) {
                if (MoveList.getCaptureFlag(moveList.moves[i]) == 0) {
                    killerMoves[1][pvIndex] = killerMoves[0][pvIndex];
                    killerMoves[0][pvIndex] = moveList.moves[i];
                }

                if (!repetition) {
                    TTable.writeValue(board.zobristKey, depth, beta, TTable.flagBeta, moveList.moves[i]);
                } //if repetition occurs, this value is not trustworthy

                return beta;
            }
            if (eval > alpha) {
                bestMove = moveList.moves[i];
                if (MoveList.getCaptureFlag(moveList.moves[i]) == 0) {
                    historyMoves[MoveList.getPiece(moveList.moves[i])][MoveList.getEndSquare(moveList.moves[i])] = pvIndex;
                }

                hashFlag = TTable.flagExact;
                alpha = eval;
                foundPV = true;

                alphaIsARepetition = repetition; //if eval is 0 because of repetition and alpha < 0, alpha cannot be trusted
                pvTable[pvIndex][pvIndex] = moveList.moves[i];
                for (int j = pvIndex + 1; j < pvLength[pvIndex + 1]; j++) {
                    pvTable[pvIndex][j] = pvTable[pvIndex + 1][j];
                }
                pvLength[pvIndex] = pvLength[pvIndex + 1];
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag, bestMove);
        }
        return alpha;
    }

    private static double quiescence(Board board, double alpha, double beta) {
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


    public static Board engineMove(int depth, Board board) {
        Board bestBoard = null, temp;
        thinkTime = Integer.MAX_VALUE;
        startTime = System.currentTimeMillis();

        for (int i = 1; i <= depth; i++) {
            inNullMove = false;

            totalDepth = i;
            nodes = 0;

            temp = negaMax(board, i);
            if (temp != null) {
                bestBoard = temp;
                previousPV = pvTable.clone();
                pvTable = new long[maxDepth][maxDepth];

                endTime = System.currentTimeMillis();
                System.out.printf("Depth: %-2d Time: %-11s Nodes: %,-11d PV: %s\n", i, (endTime - startTime + "ms"), nodes, MoveList.toStringPv(previousPV));
            } else {
                break;
            }
            if (MoveGeneration.getMoves(bestBoard).count == 0 && (bestBoard.fKing & bestBoard.eAttackMask) != 0) {
                break;
            }
        }
        System.out.println();
        return bestBoard;
    }

    public static SearchNode getSearchNodes(Board board, int depth) {
        SearchNode parentNode = null;
        thinkTime = Integer.MAX_VALUE;
        startTime = System.currentTimeMillis();

        for (int i = 1; i <= depth; i++) {
            parentNode = new SearchNode(board);

            totalDepth = i;
            nodes = 0;

            negaMax(board, i, parentNode);
            previousPV = pvTable.clone();
            pvTable = new long[maxDepth][maxDepth];

            endTime = System.currentTimeMillis();
            System.out.printf("Depth: %-2d Time: %-11s Nodes: %,-11d PV: %s\n", i, (endTime - startTime + "ms"), nodes, MoveList.toStringPv(previousPV));
        }
        System.out.println();
        return parentNode;
    }

    private static void negaMax(Board board, int depth, SearchNode parentNode) {
        parentNode.eval = negaMax(board, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, parentNode);
    }

    private static double negaMax(Board board, int depth, double alpha, double beta, SearchNode parentNode) {
        int pvIndex = totalDepth - depth;
        pvLength[pvIndex] = pvIndex;

        int hashFlag = TTable.flagAlpha;
        Pair pair = TTable.getValue(board.zobristKey, depth, alpha, beta);
        double eval = pair.eval;
        /*if (eval != TTable.noValue) { //if this position is already evaluated with this depth
            nodes++;
            return eval;
        }*/

        if (depth == 0) {
            nodes++;
            int mate = MoveGeneration.mateCheck(board);
            if (mate == 0) {
                eval = quiescence(board, alpha, beta, parentNode);
                //TTable.writeValue(board.zobristKey, depth, eval, TTable.flagExact); //this is breaking something
                return eval;
            } else if (mate == 1) {
                //TTable.writeValue(board.zobristKey, depth, -9999999 - depth, TTable.flagExact);
                return -9999999 - depth;
            } else {
                //TTable.writeValue(board.zobristKey, depth, 0, TTable.flagExact);
                return 0;
            }
        }

        MoveList moveList = MoveGeneration.getMoves(board);
        if (moveList.count == 0) {
            nodes++;
            if ((board.fKing & board.eAttackMask) != 0) {
                return -9999999 - depth;
            } else {
                return 0;
            }
        }

        Board nextBoard;
        SearchNode currentNode;
        //boolean foundPV = false;
        boolean repetition, alphaIsARepetition = false;

        moveList.reorder(board, previousPV[pvIndex][pvIndex], pvIndex, 0);
        for (int i = 0; i < moveList.count; i++) {
            if (System.currentTimeMillis() - startTime > thinkTime) { //if time limit reached
                return nonEval;
            }

            nextBoard = new Board(board);
            nextBoard.makeMove(moveList.moves[i]);
            currentNode = new SearchNode(nextBoard);


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
                eval = -negaMax(nextBoard, depth - 1, -beta, -alpha, currentNode);
            }
            Repetition.removeFromHistory(nextBoard.zobristKey); //once all searches completed with added repetition count, can remove the count
            currentNode.eval = eval;

            if (System.currentTimeMillis() - startTime > thinkTime) { //if time limit reached
                return nonEval;
            }

            if (eval >= beta) {
                if (!repetition) {
                    TTable.writeValue(board.zobristKey, depth, beta, TTable.flagBeta, 0);
                } //if repetition occurs, this value is not trustworthy
                currentNode.flag = SearchNode.AboveBeta;
                parentNode.addChild(currentNode);
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

                currentNode.flag = SearchNode.NewAlpha;
                parentNode.addChild(currentNode);
            } else {
                currentNode.flag = SearchNode.BelowAlpha;
                parentNode.addChild(currentNode);
            }
        }
        if (!alphaIsARepetition) {
            TTable.writeValue(board.zobristKey, depth, alpha, hashFlag, 0);
        }
        return alpha;
    }

    private static double quiescence(Board board, double alpha, double beta, SearchNode parentNode) {
        double eval = Evaluation.evaluation(board);
        if (eval >= beta) {
            return beta;
        }
        if (alpha < eval) {
            alpha = eval;
        }

        MoveList moveList = MoveGeneration.getMoves(board);
        Board nextBoard;
        SearchNode currentNode;
        moveList.reorder(board, 0, 0, 0);

        for (int i = 0; i < moveList.count; i++) {
            if (MoveList.getCaptureFlag(moveList.moves[i]) == 1) {
                nextBoard = new Board(board);
                nextBoard.makeMove(moveList.moves[i]);
                currentNode = new SearchNode(nextBoard);

                eval = -quiescence(nextBoard, -beta, -alpha, currentNode);
                currentNode.eval = eval;

                if (eval >= beta) {
                    currentNode.flag = SearchNode.AboveBeta;
                    parentNode.addChild(currentNode);
                    return beta;
                }
                if (eval > alpha) {
                    currentNode.flag = SearchNode.NewAlpha;
                    parentNode.addChild(currentNode);
                    alpha = eval;
                } else {
                    currentNode.flag = SearchNode.BelowAlpha;
                    parentNode.addChild(currentNode);
                }
            }
        }
        return alpha;
    }
}
