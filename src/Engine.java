import java.util.ArrayList;
import java.util.Arrays;

public class Engine {
    public static PrincipleVariation pv;
    //public static Ponder ponder;

    public static Long[] computerMove(int thinkTime, boolean player, Long[] board) {
        if (Main.inTheory) {
            ArrayList<String> options = OpeningFinder.openingMove(Main.movesSoFar);
            if (options.size() == 0) {
                Main.inTheory = false;
                //System.out.println();
            } else {
                //System.out.println("Opening options: " + options);
                ArrayList<Long[]> moveList = MoveGeneration.possibleMoves(player, board);
                Long[] tempBoard;
                String move = options.get((int) (Math.random() * options.size()));
                Main.makeMove(move, board);
                for (Long[] currentBoard : moveList) {
                    tempBoard = currentBoard.clone();
                    for (int i = 12; i < board.length; i++) {
                        board[i] = 0L;
                        tempBoard[i] = 0L;
                    }
                    if (Arrays.equals(tempBoard, board)) {
                        board = currentBoard;
                        break;
                    }
                }
                return board;
            }
        }
        Long[][] moves;
        Long[] chosenMove;
        long[][] currentPv = null, tempPv;
        long[] pvMove = new long[2];
        long start, end;
        int startIndex = 1, moveNum;
        /*if (ponder != null) {
            if (ponder.finalPv != null) {
                if ((ponder.finalPv[0][0] == board[15]) && ponder.finalPv[0][1] == board[16]) {
                    tempPv = Arrays.copyOfRange(ponder.finalPv, 1, ponder.finalPv.length);
                    if (tempPv.length > 1) {
                        currentPv = tempPv;
                        startIndex = currentPv.length;
                        if (Ponder.timeSpent > thinkTime) {
                            Main.makeMove(MoveGeneration.moveToStringMove(currentPv[0][0]) + MoveGeneration.moveToStringMove(currentPv[0][1]), board);
                            ArrayList<Long[]> moveList = MoveGeneration.possibleMoves(player, board);
                            Long[] tempBoard;
                            for (Long[] currentBoard : moveList) {
                                tempBoard = currentBoard.clone();
                                for (int i = 12; i < board.length; i++) {
                                    board[i] = 0L;
                                    tempBoard[i] = 0L;
                                }
                                if (Arrays.equals(tempBoard, board)) {
                                    board = currentBoard;
                                    break;
                                }
                                return board;
                            }
                        }
                    }
                }
            }
            ponder.interrupt();
            //System.out.println();
        }
        Ponder.kill = true;*/
        //System.out.println("Engine: ");
        start = System.currentTimeMillis();
        for (int i = startIndex; ; i++) {
            pv = new PrincipleVariation(i);
            if (currentPv != null && currentPv.length > 0) {
                pvMove = currentPv[0];
            } else {
                pvMove[0] = -1;
                pvMove[1] = -1;
            }
            RepetitionTable.refreshTables();
            moves = MoveReorder.moveReorder(MoveGeneration.possibleMoves(player, board), pvMove);
            moveNum = ((int) Engine.alphaBeta(i, i, board, player, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, currentPv));
            chosenMove = moves[moveNum];
            if (moves.length == 1 || pv.mate()) break;
            end = System.currentTimeMillis();
            //System.out.println("Depth: " + i + "  " + pv.getPrincipleVariation());
            currentPv = pv.moves.clone();
            if ((end - start) > thinkTime) break;
        }
        //System.out.println("Chosen Move: " + pv.getMove() + "\n");
        //Might be broken. Not sure
        /*Ponder.kill = false;
        ponder = new Ponder(pv.moves, chosenMove, !player);
        ponder.start();*/
        return chosenMove;
    }

    public static double alphaBeta(int depth, int originalDepth, Long[] board, boolean player, double alpha, double beta, long[][] currentPv) {
        int index = TranspositionTable.getIndex(board[14]), pvIndex = originalDepth - depth;
        double eval = TranspositionTable.getEval(index, board[14], depth, alpha, beta);
        if ((eval != TranspositionTable.noValue) && (depth != originalDepth)) {
            return eval;
        }
        if (depth == 0 || board[18] != 0) {
            eval = quiescence(0, player, board, alpha, beta);
            //Bugs out code because of quiescence. Not sure why
            //TranspositionTable.writeTable(index, board[14], depth, eval, TranspositionTable.flagExact);
            return eval;
        }
        boolean foundPV = false, clearTable = false, repetitionFlag, alphaIsRepetition = false;
        long[] currentPvMove = new long[2];
        long currentKey = 0;
        int bestMove = -1, flag = TranspositionTable.flagAlpha;
        Long[] currentBoard;
        if (currentPv != null) {
            if ((pvIndex) < currentPv.length) {
                currentPvMove = currentPv[pvIndex];
            } else {
                currentPvMove[0] = -1;
                currentPvMove[1] = -1;
            }
        }
        ArrayList<Long[]> moves = MoveGeneration.possibleMoves(player, board);
        Long[][] reorderedMoves = MoveReorder.moveReorder(moves, currentPvMove);
        for (int i = 0; i < reorderedMoves.length; i++) {
            repetitionFlag = false;
            currentBoard = reorderedMoves[i];
            if (RepetitionTable.addToHistory(currentBoard[14], RepetitionTable.treeFlag)) {
                eval = 0;
                repetitionFlag = true;
            }
            if (!repetitionFlag) {
                if (foundPV) {
                    eval = -alphaBeta(depth - 1, originalDepth, currentBoard, !player, -alpha - 1, -alpha, currentPv);
                    if ((eval > alpha) && (eval < beta))
                        eval = -alphaBeta(depth - 1, originalDepth, currentBoard, !player, -beta, -alpha, currentPv);
                } else {
                    eval = -alphaBeta(depth - 1, originalDepth, currentBoard, !player, -beta, -alpha, currentPv);
                }
            }
            RepetitionTable.removeFromHistory(currentBoard[14]);
            if (eval >= beta) {
                if (!repetitionFlag) {
                    index = TranspositionTable.getIndex(currentBoard[14]);
                    TranspositionTable.writeTable(index, currentBoard[14], depth, beta, TranspositionTable.flagBeta);
                }
                if (depth != originalDepth) {
                    return beta;
                } else {
                    if ((currentBoard[17] & 1L << 1) != 0) {
                        TranspositionTable.transpositionTable.clear();
                    }
                    if (player) {
                        Main.eval = beta;
                    } else {
                        Main.eval = -beta;
                    }
                    return i;
                }
            }
            if (eval > alpha) {
                alphaIsRepetition = repetitionFlag;
                currentKey = currentBoard[14];
                flag = TranspositionTable.flagExact;
                alpha = eval;
                bestMove = i;
                clearTable = (currentBoard[17] & 1L << 1) != 0;
                foundPV = true;
                pv.moves[pvIndex][0] = currentBoard[15];
                pv.moves[pvIndex][1] = currentBoard[16];
            }
        }
        if (currentKey != 0 && !alphaIsRepetition) {
            index = TranspositionTable.getIndex(currentKey);
            TranspositionTable.writeTable(index, board[14], depth, alpha, flag);
        }
        if (depth != originalDepth) {
            return alpha;
        } else {
            if (clearTable) {
                TranspositionTable.transpositionTable.clear();
            }
            if (player) {
                Main.eval = alpha;
            } else {
                Main.eval = -alpha;
            }
            return bestMove;
        }
    }

    public static double quiescence(int depth, boolean player, Long[] board, double alpha, double beta) {
        double score = Evaluation.evaluation(board, player), eval;
        if (depth >= 5) {
            return score;
        }
        if (score >= beta) {
            return beta;
        }
        if (score > alpha) {
            alpha = score;
        }
        int counter = 0;
        ArrayList<Long[]> possibleMoves = MoveGeneration.possibleMoves(player, board);
        //Long[][] reorderedMoves = MoveReorder.moveReorder(possibleMoves);
        for (Long[] currentBoard : possibleMoves) {
            if ((currentBoard[17] & 1) == 1 || (possibleMoves.size() == 1 && currentBoard[18] == 0)) {
                eval = -quiescence(depth + 1, !player, currentBoard, -beta, -alpha);
                if (eval >= beta) {
                    return beta;
                }
                if (eval > alpha) {
                    alpha = eval;
                }
                counter++;
            }
        }
        if (counter == 0) {
            return Evaluation.evaluation(board, player);
        }
        return alpha;
    }
}
