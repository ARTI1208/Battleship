package battleship.net;

import battleship.Ocean;

public class NetOcean extends Ocean {

    private BattleshipSocketMediator socketMediator;

    public NetOcean(BattleshipSocketMediator socketMediator) {
        this.socketMediator = socketMediator;
    }

    @Override
    public Boolean shootAt(int row, int column) {
//        boolean localResult = super.shootAt(row, column);
//        boolean opponentResult = false;
//
//        if (localResult != opponentResult) {
//            throw new GameSynchronizationException("Shoot at row:column " + row + ":" + column +
//                    "; local result = " + localResult + ", opponent result = " + opponentResult);
//        }

        return socketMediator.shootAt(row, column);
    }
}
