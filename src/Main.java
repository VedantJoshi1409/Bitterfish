import java.util.Scanner;

public class Main {
    static boolean player = true;

    public static void main(String[] args) {
        init();
        //Board board = new Board("4K3/4P3/3q4/8/8/8/6k1/8 b - -");
        //Board board = new Board(PosConstants.startPos);
        //SearchNode node = Engine.getSearchNodes(board, 4);
        //node.flag = SearchNode.StartNode;
        //TreeGUI.displayTree(node);

        //Board board = new Board("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3");
        //Gui gui = new Gui(board, 1.2, player);
        //engineTest(board, gui, 200);
        //Engine.engineMove(6, board);
        //play(board, gui, 3000, player);

        Client client = new Client(1409);
        client.initMatch();
    }

    static void play(Board board, Gui gui, int timeLimit, boolean player) {
        while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3) {
            System.out.println(board.boardToFen());
            if (board.player == player) {
                board = PlayerGame.playerMove(board, gui);
                System.out.println();

            } else {
                board = Engine.engineMove(board, timeLimit);
            }

            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            gui.panel.board = board;
            gui.repaint();
        }
        if (board.player) {
            System.out.printf("%.2f\n", Evaluation.evaluation(board)/100);
        } else {
            System.out.printf("%.2f\n", -Evaluation.evaluation(board)/100);
        }
        if (Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) >= 3) {
            System.out.println("Draw by repetition!");
        } else {
            if ((board.fKing & board.eAttackMask) != 0) {
                System.out.println("Checkmate!");
            } else{
                System.out.println("Stalemate!");
            }
        }
    }

    static void engineTest(Board board, Gui gui, int timeLimit) {
        //Scanner sc = new Scanner(System.in);
        while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3) {
            System.out.println(board.boardToFen());
            board = Engine.engineMove(board, timeLimit);
            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            gui.panel.board = board;
            gui.repaint();
            System.out.println();
            //sc.nextInt();
        }
        if (board.player) {
            System.out.printf("%.2f\n", Evaluation.evaluation(board)/100);
        } else {
            System.out.printf("%.2f\n", -Evaluation.evaluation(board)/100);
        }
        if (Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) >= 3) {
            System.out.println("Draw by repetition!");
        } else {
            if ((board.fKing & board.eAttackMask) != 0) {
                System.out.println("Checkmate!");
            } else{
                System.out.println("Stalemate!");
            }
        }
    }

    static void init() {
        MoveGeneration.initAttack();
        Zobrist.initKeys();
    }

    private static void speedTest(Board board, int repetitions) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < repetitions; i++) {
            MoveGeneration.getPinnedPieces(board.fKing, board.occupied, board.eRook, board.eBishop, board.eQueen);
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}
