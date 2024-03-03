import java.util.Arrays;

public class Ponder extends Thread {
    public static boolean kill = false;
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
        System.out.println("Pondering: ");
        for (int i = startPv.length; ; i++) {
            if (kill) {
                break;
            }
            Engine.pv = new PrincipleVariation(i);
            try {
                Engine.alphaBeta(i, i, board, player, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, startPv);
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
            finalPv = Engine.pv.moves.clone();
            if (Engine.pv.mate()) break;
            System.out.println("Ponder Depth: " + i + "  " + Engine.pv.getPrincipleVariation());
        }
    }
}
