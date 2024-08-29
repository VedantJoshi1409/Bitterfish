public class TTable {
    static final int flagExact = 0;
    static final int flagAlpha = 1;
    static final int flagBeta = 2;
    static final int noValue = 123456789;

    static int capacity;
    static FixedCapacityHashMap<Integer, HashEntry> table;

    static void init(int capacity) {
        TTable.capacity = capacity;
        table = new FixedCapacityHashMap<>(capacity);
    }

    static Pair getValue(long key, int depth, double alpha, double beta) {
        HashEntry hashEntry = table.get((int) (key % capacity));
        if (hashEntry != null && hashEntry.key == key) {
            if (hashEntry.depth >= depth) {
                //if there is not an entry, hashEntry will be null
                //incase key % initialCapacity leads to diff entry
                //make sure that depth is equal to or more than the depth we are searching at
                if (hashEntry.flag == flagExact) {
                    return new Pair(hashEntry.value, hashEntry.bestMove);
                } else if (hashEntry.flag == flagAlpha && hashEntry.value <= alpha) {
                    return new Pair(alpha, hashEntry.bestMove);
                } else if (hashEntry.flag == flagBeta && hashEntry.value >= beta) {
                    return new Pair(beta, hashEntry.bestMove);
                }
            }
            return new Pair(noValue, hashEntry.bestMove);
        }
        return new Pair(noValue, 0);
    }

    static void writeValue(long key, int depth, double value, int flag, long bestMove) {
        HashEntry hashEntry;
        int intKey = (int) (key % capacity);
        hashEntry = table.get(intKey);
        if (hashEntry != null) {
            if (hashEntry.key == key) {
                if (hashEntry.depth <= depth) {
                    hashEntry.depth = depth;
                    hashEntry.flag = flag;
                    hashEntry.value = value;
                }
            } else {
                hashEntry = new HashEntry(key, depth, flag, value, bestMove);
                table.put(intKey, hashEntry);
            }
        } else {
            hashEntry = new HashEntry(key, depth, flag, value, bestMove);
            table.put(intKey, hashEntry);
        }
    }

    static void clearTables() {
        table.clear();
    }

    static double hashfull() {
        return (double) table.size() / capacity * 1000;
    }

    static String memoryUsage() {
        return String.format("%.2f mb used!", ((double) table.size() * 72 / 1000000));
    }
}
