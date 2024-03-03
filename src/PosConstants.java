import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PosConstants {
    static final String startPos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";
    static final String trickyEndgame = "8/k7/3p4/p2P1p2/P2P1P2/8/8/K7 w - -";
    static final String qVp = "8/3K4/4P3/8/8/8/6k1/7q w - -";
    static final String[] fens = new String[95];

    static void readFens() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("Games/RandomFens.txt"));
            for (int i = 0; i < fens.length; i++) {
                fens[i] = in.readLine()+" w KQkq -";
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    static String randomFen() {
        return fens[(int)(Math.random()*fens.length)];
    }
}
