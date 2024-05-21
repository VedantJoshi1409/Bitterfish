import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tablebase {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String base = "https://tablebase.lichess.ovh/standard/mainline?fen=";

    public static long[] getEval(Board board) {
        if (!Main.tablebase) {
            return new long[] {};
        }
        StringBuffer response = new StringBuffer();
        try {
            String fen = board.boardToFen();
            fen = fen.replace(' ', '_');
            String url = base + fen;

            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

            // Set request method
            httpClient.setRequestMethod("GET");
            httpClient.setRequestProperty("User-Agent", USER_AGENT);

            // Get response
            int responseCode = httpClient.getResponseCode();
//            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if (response.toString().equals("{\"mainline\":[],\"winner\":null,\"dtz\":0,\"precise_dtz\":0}")) {
                    return new long[] {};
                }
            } else if (responseCode == 429) { //Too many requests
                return new long[] {};
            } else {
                System.out.println("GET request failed. Error Code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Tablebase error: ");
            e.printStackTrace();
        }

        String cutResponse = response.substring(21);
        int shift = 0;
        if (cutResponse.charAt(4) != '"') {
            shift++;
        }
        String move = cutResponse.substring(0, 4 + shift);
        int startIndex = cutResponse.indexOf("precise_dtz") + 13 + shift;
        int endIndex;
        for (int i = startIndex + 1; ; i++) {
            if (!Character.isDigit(cutResponse.charAt(i))) {
                endIndex = i;
                break;
            }
        }
        int dtz = Integer.parseInt(cutResponse.substring(startIndex, endIndex));
        long longMove = MoveGeneration.getMoves(board).getMoveFromString(move);
        int eval;

        if (dtz < 0) {
            eval = 999999999 + dtz;
        } else {
            eval = -100 + dtz;
        }

        return new long[]{longMove, eval};
    }
}