public class PrincipleVariation {
    public long[][] moves;
    public int length;

    public PrincipleVariation(int length) {
        this.length = length;
        moves = new long[length][2];
    }

    public void printLength() {System.out.println(length);}

    public String getPrincipleVariation() {
        String start, end, output = "";
        output += "Principle Variation: ";
        for (int i = 0; i < moves.length; i++) {
            start = MoveGeneration.moveToStringMove(moves[i][0]);
            end = MoveGeneration.moveToStringMove(moves[i][1]);
            output += String.format("%d.%s ", i + 1, start + end);
        }
        return output;
    }

    public String getMove() {
        return MoveGeneration.moveToStringMove(moves[0][0]) + MoveGeneration.moveToStringMove(moves[0][1]);
    }

    public boolean mate() {
        for (long[] currentMove: moves) {
            if (currentMove[0] == 0 || currentMove[1] == 0) {
                return true;
            }
        }
        return false;
    }
}