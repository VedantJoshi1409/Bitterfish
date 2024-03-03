import java.util.ArrayList;

public class MoveReorder {

    /*(Victims) Pawn Knight Bishop   Rook  Queen   King
 (Attackers)
    Pawn     105    305    305    405    505    605
    Knight   103    303    303    403    503    603
    Bishop   103    303    303    403    503    603
    Rook     102    302    302    402    502    602
    Queen    101    301    301    401    501    601
    King     100    300    300    400    500    600 */

    //[attacker][victim]
    static final int[][] attackScores = {
            {105, 305, 305, 405, 505, 605,   105, 305, 305, 405, 505, 605},
            {103, 303, 303, 403, 503, 603,   103, 303, 303, 403, 503, 603},
            {103, 303, 303, 403, 503, 603,   103, 303, 303, 403, 503, 603},
            {102, 302, 302, 402, 502, 602,   102, 302, 302, 402, 502, 602},
            {101, 301, 301, 401, 501, 601,   101, 301, 301, 401, 501, 601},
            {100, 300, 300, 400, 500, 600,   100, 300, 300, 400, 500, 600},

            {105, 305, 305, 405, 505, 605,   105, 305, 305, 405, 505, 605},
            {103, 303, 303, 403, 503, 603,   103, 303, 303, 403, 503, 603},
            {103, 303, 303, 403, 503, 603,   103, 303, 303, 403, 503, 603},
            {102, 302, 302, 402, 502, 602,   102, 302, 302, 402, 502, 602},
            {101, 301, 301, 401, 501, 601,   101, 301, 301, 401, 501, 601},
            {100, 300, 300, 400, 500, 600,   100, 300, 300, 400, 500, 600}
    };

    public static Long[][] moveReorder(ArrayList<Long[]> moves, long[] pvMove) {
        int size = moves.size(), score;
        Long[][] newMoves = new Long[size][21];
        Long[] currentBoard;
        long attacker, victim;
        int[][] moveScores = new int[size][2];
        for (int i = 0; i < size; i++) {
            currentBoard = moves.get(i);
            if (currentBoard[15] == pvMove[0] && currentBoard[16] == pvMove[1]) {
                score = 999999999;
            } else if ((currentBoard[17] & 1) == 1) {
                attacker = currentBoard[19];
                victim = currentBoard[20];
                score = attackScores[(int) attacker][(int) victim];
            } else {
                score = 0;
            }
            if ((currentBoard[21] & 1) == 1) {
                score += 250;
            }
            moveScores[i][0] = i;
            moveScores[i][1] = score;
        }
        quickSort(moveScores, 0, size-1);
        for (int i = 0; i < size; i++) {
            newMoves[i] = moves.get(moveScores[i][0]);
        }
        return newMoves;
    }

    public static void swap(int[][] arr, int i, int j) {
        int[] temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static int partition(int[][] arr, int low, int high) {
        int[] pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j <= high - 1; j++) {
            if (arr[j][1] > pivot[1]) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    public static void quickSort(int[][] arr, int low, int high)
    {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
}
