import java.util.ArrayList;
import java.util.Arrays;

public class Ponder extends Thread {
    public static boolean kill = false;
    public static long timeSpent;
    public long[][] finalPv;
    public long[][] startPv;
    public Long[] board;
    public boolean player;

    public Ponder(long[][] currentPv, Long[] theBoard, boolean side) {
        startPv = Arrays.copyOfRange(currentPv, 1, currentPv.length);
        board = theBoard;
        player = side;
    }

    public void run() {
        //System.out.println("Pondering: ");
        long start = System.currentTimeMillis(), end;
        for (int i = startPv.length; ; i++) {
            if (kill) break;
            Engine.pv = new PrincipleVariation(i);
            try {
                alphaBeta(i, i, board, player, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, startPv);
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
            end = System.currentTimeMillis();
            timeSpent = start-end;
            finalPv = Engine.pv.moves.clone();
            if (Engine.pv.mate()) break;
            //System.out.println("Ponder Depth: " + i + "  " + Engine.pv.getPrincipleVariation());
        }
    }

    public static double alphaBeta(int depth, int originalDepth, Long[] board, boolean player, double alpha, double beta, long[][] currentPv) {
        if (kill) return 0;
        //THIS BROKE SOMETHING
        /*if (RepetitionTable.addToHistory(board[14], RepetitionTable.treeFlag)) {
            return 0;
        }*/
        int index = TranspositionTable.getIndex(board[14]), pvIndex = originalDepth-depth;
        double eval = TranspositionTable.getEval(index, board[14], depth, alpha, beta);
        if ((eval != TranspositionTable.noValue) && (depth != originalDepth)) {
            return eval;
        }
        if (depth == 0 || board[18] != 0) {
            //eval = Evaluation.evaluation(board, !player);
            eval = quiescence(0, player, board, alpha, beta);
            //Bugs out code because of quiescence. Not sure why
            //TranspositionTable.writeTable(index, board[14], depth, eval, TranspositionTable.flagExact);
            return eval;
        }
        boolean foundPV = false, clearTable = false;
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
            currentBoard = reorderedMoves[i];
            if (foundPV) {
                eval = -alphaBeta(depth - 1, originalDepth, currentBoard, !player, -alpha - 1, -alpha, currentPv);
                if ((eval > alpha) && (eval < beta))
                    eval = -alphaBeta(depth - 1, originalDepth, currentBoard, !player, -beta, -alpha, currentPv);
            } else {
                eval = -alphaBeta(depth - 1, originalDepth, currentBoard, !player, -beta, -alpha, currentPv);
            }
            if (eval >= beta) {
                index = TranspositionTable.getIndex(currentBoard[14]);
                TranspositionTable.writeTable(index, currentBoard[14], depth, beta, TranspositionTable.flagBeta);
                if (depth != originalDepth) {
                    return beta;
                } else {
                    if ((currentBoard[17] & 1L << 1) != 0) {
                        TranspositionTable.transpositionTable.clear();
                    }
                    return i;
                }
            }
            if (eval > alpha) {
                currentKey = currentBoard[14];
                flag = TranspositionTable.flagExact;
                alpha = eval;
                bestMove = i;
                clearTable = (currentBoard[17] & 1L << 1) != 0;
                foundPV = true;
                Engine.pv.moves[pvIndex][0] = currentBoard[15];
                Engine.pv.moves[pvIndex][1] = currentBoard[16];
            }
        }
        if (currentKey != 0) {
            index = TranspositionTable.getIndex(currentKey);
        }
        TranspositionTable.writeTable(index, board[14], depth, alpha, flag);
        if (depth != originalDepth) {
            return alpha;
        } else {
            if (clearTable) {
                TranspositionTable.transpositionTable.clear();
            }
            return bestMove;
        }
    }

    public static double quiescence(int depth, boolean player, Long[] board, double alpha, double beta) {
        if (kill) return 0;
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
