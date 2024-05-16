import java.util.Hashtable;
import java.util.Objects;

public class Repetition {
    public static int positionSize = 6000;
    public static int treeSize = 1000000;
    public static Hashtable<Integer, Integer> positionHistory = new Hashtable<>(positionSize);
    public static Hashtable<Integer, Integer> treeHistory = new Hashtable<>(treeSize);
    public static boolean historyFlag = false, treeFlag = true;

    public static boolean addToHistory(long key, boolean flag) {
        int index;
        if (flag) {
            index = (int) (key%treeSize);
            Integer oldAmount = treeHistory.get(index);
            if (oldAmount != null) {
                oldAmount++;
                treeHistory.put(index, oldAmount);
                return oldAmount >= 3;
            } else {
                treeHistory.put(index, 1);
            }
        } else {
            index = (int) (key%treeSize);
            Integer oldAmount = positionHistory.get(index);
            if (oldAmount != null) {
                oldAmount++;
                positionHistory.put(index, oldAmount);
                return oldAmount >= 3;
            } else {
                positionHistory.put(index, 1);
            }
        }
        return false;
    }

    public static void removeFromHistory(long key) {
        int index = (int)(key%treeSize);
        Integer oldAmount = treeHistory.get(index);
        if (oldAmount != null) {
            treeHistory.put(index, oldAmount - 1);
        }
    }

    public static void refreshTables() {
        treeHistory.clear();
        treeHistory.putAll(positionHistory);
    }

    public static int getRepetitionAmount(long key, boolean flag) {
        if (flag) {
            return Objects.requireNonNullElse(treeHistory.get((int) (key % treeSize)), 0);
        } else {
            return Objects.requireNonNullElse(positionHistory.get((int) (key % treeSize)), 0);
        }
    }
}
