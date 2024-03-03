import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class DataPackage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long[] longArray;
    private boolean booleanValue, theory;
    private int intValue;
    private ArrayList<String> movesSoFar;

    public DataPackage(Long[] longArray, boolean booleanValue, int intValue, ArrayList<String> moves, boolean inTheory) {
        this.longArray = longArray;
        this.booleanValue = booleanValue;
        this.intValue = intValue;
        this.movesSoFar = moves;
        this.theory = inTheory;
    }

    public Long[] getLongArray() {
        return longArray;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public ArrayList<String> getMovesSoFar() {
        return movesSoFar;
    }

    public int getIntValue() {
        return intValue;
    }

    public boolean getTheory() {
        return theory;
    }
}
