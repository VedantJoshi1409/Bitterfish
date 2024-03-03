import java.util.Hashtable;

public class TranspositionTable {
    public static final int initialCapacity = 64000000, flagExact = 0, flagBeta = 1, flagAlpha = 2, noValue = 123456789;
    public static Hashtable<Integer, double[]> transpositionTable = new Hashtable<>(initialCapacity);
    //double[]:
    //1: Zobrist Key
    //2: Depth
    //3: Eval
    //4: Flag

    public static int getIndex(long key) {
        return (int) (key % initialCapacity);
    }

    public static double getEval(int index, long key, int depth, double alpha, double beta) {
        double[] values = transpositionTable.get(index);
        if (values == null) {
            return noValue;
        }
        if (values[0] == key) {
            if (values[1] >= depth) {
                double flag = values[3], eval = values[2];
                if (flag == flagExact) return eval;
                if ((flag == alpha) && (eval <= alpha)) return alpha;
                if ((flag == beta) && (eval >= beta)) return beta;
            }
        }
        return noValue;
    }

    public static void writeTable(int index, long key, int depth, double eval, int flag) {
        double[] values = {key, depth, eval, flag};
        transpositionTable.put(index, values);
    }
}
