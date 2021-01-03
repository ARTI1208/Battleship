package battleship.net;

public class GameResult {

    boolean youWon;
    int yourMoveCount;
    int opponentMoveCount;

    public GameResult(boolean youWon, int yourMoveCount, int opponentMoveCount) {
        this.youWon = youWon;
        this.yourMoveCount = yourMoveCount;
        this.opponentMoveCount = opponentMoveCount;
    }

    public boolean isYouWon() {
        return youWon;
    }

    public int getYourMoveCount() {
        return yourMoveCount;
    }

    public int getOpponentMoveCount() {
        return opponentMoveCount;
    }
}
