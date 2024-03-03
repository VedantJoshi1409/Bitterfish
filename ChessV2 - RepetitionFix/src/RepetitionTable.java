import java.util.Hashtable;

public class RepetitionTable {
    public static Hashtable<Long, Integer> positionHistory = new Hashtable<>(6000);
    public static Hashtable<Long, Integer> treeHistory = new Hashtable<>(1000000);
    public static boolean historyFlag = false, treeFlag = true;

    public static boolean addToHistory(long key, boolean flag) {
        if (flag) {
            if (treeHistory.containsKey(key)) {
                int oldAmount = treeHistory.get(key);
                treeHistory.put(key, oldAmount + 1);
                return oldAmount >= 2;
            } else {
                treeHistory.put(key, 1);
            }
        } else {
            if (positionHistory.containsKey(key)) {
                int oldAmount = positionHistory.get(key);
                positionHistory.put(key, oldAmount + 1);
                return oldAmount >= 2;
            } else {
                positionHistory.put(key, 1);
            }
        }
        return false;
    }

    public static void removeFromHistory(long key) {
        int oldAmount = treeHistory.get(key);
        treeHistory.put(key, oldAmount - 1);
    }

    public static void refreshTables() {
        treeHistory.clear();
        treeHistory.putAll(positionHistory);
    }

    public static int getRepetitionAmount(long key) {
        if (treeHistory.get(key) == null) {
            return 0;
        } else {
            return treeHistory.get(key);
        }
    }
}
