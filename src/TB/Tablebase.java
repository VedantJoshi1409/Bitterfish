package TB;

import java.io.File;

public class Tablebase {
    static {
        File dll = new File("dependencies/tbProbe.dll");
        System.load(dll.getAbsolutePath());
    }

    public static native void init(String path);

    public static native long probeDTZ(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, long castling, long enPassant, boolean turn);
}
