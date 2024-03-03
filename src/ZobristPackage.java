import java.io.Serializable;

public class ZobristPackage implements Serializable {
    public long[][] pieceKeys;
    public long[] enPassantKeys;
    public long[] castleKeys;
    public long sideKey;

    public ZobristPackage() {
        this.pieceKeys = Zobrist.pieceKeys;
        this.enPassantKeys = Zobrist.enPassantKeys;
        this.castleKeys = Zobrist.castleKeys;
        this.sideKey = Zobrist.sideKey;
    }

    public void implementValues() {
        Zobrist.pieceKeys = pieceKeys;
        Zobrist.enPassantKeys = enPassantKeys;
        Zobrist.castleKeys = castleKeys;
        Zobrist.sideKey = sideKey;
    }
}
