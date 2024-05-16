import java.util.LinkedList;
import java.util.List;

public class SearchNode {
    static int StartNode = -1;
    static int BelowAlpha = 0;
    static int NewAlpha = 1;
    static int AboveBeta = 2;

    Board board;
    double eval;
    int flag;
    List<SearchNode> childNodes;

    public SearchNode(Board board, double eval, int flag) {
        this.board = board;
        this.eval = eval;
        this.flag = flag;
        childNodes = new LinkedList<>();
    }

    public SearchNode(Board board) {
        this.board = board;
        childNodes = new LinkedList<>();
    }

    public void addChild(SearchNode childNode) {
        this.childNodes.add(childNode);
    }

    public String toString() {
        String move = BitMethods.moveToStringMove(board.startSquare, board.endSquare);
        String nodeType = "";

        if (move.equals("h9h9")) {
            move = "start";
        }

        switch (flag) {
            case -1 -> nodeType = "StartNode";
            case 0 -> nodeType = "BelowAlpha";
            case 1 -> nodeType = "NewAlpha";
            case 2 -> nodeType = "AboveBeta";
        }
        return String.format("--------------------\n%s\n%-11.1f\n%-11s\n--------------------", move, eval, nodeType);
    }
}
