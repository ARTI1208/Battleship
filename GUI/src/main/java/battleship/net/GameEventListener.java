package battleship.net;

import battleship.ui.controls.FieldCell;

public interface GameEventListener {

    void onMyShotDone(int row, int column, FieldCell.ShootResult result);

    void onOpponentShot(int row, int column, FieldCell.ShootResult result);

    void onStopRequest(String initiator);

    void onStop(String initiator);

    void onGameOver(GameResult gameResult);
}
