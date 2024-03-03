import java.io.Serializable;
import java.util.Hashtable;

public class Package implements Serializable {
    Board board;
    Hashtable<Integer, Integer> positionHistory;

    public Package(Board board, Hashtable<Integer, Integer> positionHistory) {
        this.board = board;
        this.positionHistory = positionHistory;
    }
}
