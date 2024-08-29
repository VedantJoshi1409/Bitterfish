public class HashEntry {
    long key; //zobrist key
    int depth; //depth to which the eval is accurate
    int flag; //type of node (fail high, fail low, pv)
    double value; //could be alpha, beta, or eval value based on flag
    long bestMove; //used for move reordering

    /*public HashEntry(long key, int depth, int flag, double value, long bestMove) {
        this.key = key;
        this.depth = depth;
        this.flag = flag;
        this.value = value;
        this.bestMove = bestMove;
    }*/

    public HashEntry(long key, int depth, int flag, double value, long bestMove) {
        this.key = key;
        this.depth = depth;
        this.flag = flag;
        this.value = value;
        this.bestMove = bestMove;
    }

    public String toString() {
        return String.format("Key: %s, Depth: %d, Flag: %d, Value: %.1f, Best move: %d\n",key, depth, flag, value, bestMove);
    }
}
