import java.util.Hashtable;
import java.util.Objects;

public class Repetition {
    public static int positionSize = 6000;
    public static int treeSize = 1000000;
    public static Hashtable<Long, Integer> positionHistory = new Hashtable<>(positionSize);
    public static Hashtable<Long, Integer> treeHistory = new Hashtable<>(treeSize);
    public static boolean historyFlag = false, treeFlag = true;

    public static boolean addToHistory(long key, boolean flag) {
        if (flag) {
            Integer oldAmount = treeHistory.get(key);
            if (oldAmount != null) {
                oldAmount++;
                treeHistory.put(key, oldAmount);
                return oldAmount >= 2;
            } else {
                treeHistory.put(key, 1);
            }
        } else {
            Integer oldAmount = positionHistory.get(key);
            if (oldAmount != null) {
                oldAmount++;
                positionHistory.put(key, oldAmount);
                return oldAmount >= 2;
            } else {
                positionHistory.put(key, 1);
            }
        }
        return false;
    }

    public static void removeFromHistory(long key) {
        Integer oldAmount = treeHistory.get(key);
        if (oldAmount != null) {
            treeHistory.put(key, oldAmount - 1);
        }
    }

    public static void refreshTables() {
        treeHistory.clear();
        treeHistory.putAll(positionHistory);
    }

    public static int getRepetitionAmount(long key, boolean flag) {
        if (flag) {
            return Objects.requireNonNullElse(treeHistory.get(key), 0);
        } else {
            return Objects.requireNonNullElse(positionHistory.get(key), 0);
        }
    }

    public static void clearTables() {
        treeHistory.clear();
        positionHistory.clear();
    }
}
