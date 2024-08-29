import java.util.LinkedHashMap;
import java.util.Map;

public class FixedCapacityHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;

    public FixedCapacityHashMap(int maxCapacity) {
        super (maxCapacity, 0.75f, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}
