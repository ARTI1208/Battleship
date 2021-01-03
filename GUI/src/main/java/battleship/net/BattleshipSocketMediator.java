package battleship.net;

import battleship.Ocean;
import battleship.ui.AcquaintanceWindow;
import battleship.ui.controls.FieldCell;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class BattleshipSocketMediator {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private static final String GAME_START_EVENT = "start";
    private static final String GAME_STOP_REQUEST_EVENT = "stop_request";
    private static final String GAME_STOP_EVENT = "stop";
    private static final String GAME_END_LOSER_EVENT = "end_loser";
    private static final String GAME_END_WINNER_EVENT = "end_winner";
    private static final String SHOOT_EVENT = "shoot";
    private static final String SHOOT_RESULT_EVENT = "shoot_result";

    private AcquaintanceWindow.AppType turn = AcquaintanceWindow.AppType.CLIENT;

    public final AcquaintanceWindow.AppType appType;

    private PrintStream printStream;
    private BufferedReader bufferedReader;

    public final String host;
    public final int port;
    public final String name;

    public String opponentName;

    GameEventListener gameEventListener;

    private Ocean playerOcean;

    private final AtomicBoolean runListener = new AtomicBoolean(true);

    public BattleshipSocketMediator(AcquaintanceWindow.AppType appType, String host, int port, String name) {
        this.appType = appType;
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public GameEventListener getGameEventListener() {
        return gameEventListener;
    }

    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public Ocean getPlayerOcean() {
        return playerOcean;
    }

    public boolean shootAt(int row, int column) {

        if (appType != turn) {
            return false;
        }

        checkState();

        printStream.println(SHOOT_EVENT);
        printStream.println(row);
        printStream.println(column);

        return false;
    }

    private AcquaintanceWindow.AppType getOpponentType() {
        if (appType == AcquaintanceWindow.AppType.CLIENT)
            return AcquaintanceWindow.AppType.SERVER;
        else
            return AcquaintanceWindow.AppType.CLIENT;
    }

    public AcquaintanceWindow.AppType getTurnOwner() {
        return turn;
    }

    private void checkState() {
        if (appType == AcquaintanceWindow.AppType.SERVER && serverSocket == null)
            throw new RuntimeException("Server socket not found");

        if (appType == AcquaintanceWindow.AppType.CLIENT && clientSocket == null)
            throw new RuntimeException("Client socket not found");

        if (playerOcean == null)
            throw new RuntimeException("Player ocean is not attached!");
    }

    public void sendStopGameRequest() {
        printStream.println(GAME_STOP_REQUEST_EVENT);
        if (gameEventListener != null)
            Platform.runLater(() -> gameEventListener.onStopRequest(name));
    }

    public void sendStopSignal() {
        if (printStream != null)
            printStream.println(GAME_STOP_EVENT);
        onGameStop();
    }

    private void onGameStop() {

        runListener.set(false);

        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (gameEventListener != null)
            Platform.runLater(() -> gameEventListener.onStop(null));
    }

    private void onReceiveStopGameRequest() {
        if (gameEventListener != null)
            Platform.runLater(() -> gameEventListener.onStopRequest(opponentName));
    }

    private void onShoot() throws IOException {
        int row = Integer.parseInt(bufferedReader.readLine());
        int column = Integer.parseInt(bufferedReader.readLine());

        int shootResult;
        if (playerOcean.isHit(row, column)) {
            shootResult = 3;
        } else {
            shootResult = playerOcean.shootAt(row, column) ? 0 : 2;
            if (shootResult == 0 && playerOcean.getShipArray()[row][column].isSunk()) {
                shootResult = 1;
            }
        }

        final FieldCell.ShootResult result = FieldCell.ShootResult.fromInt(shootResult);
        if (gameEventListener != null) {
            Platform.runLater(() -> {
                gameEventListener.onOpponentShot(row, column, result);
            });
        }

        if (shootResult == FieldCell.ShootResult.MISS.ordinal()) {
            turn = appType;
        } else {
            turn = getOpponentType();
        }

        printStream.println(SHOOT_RESULT_EVENT);
        printStream.println(row);
        printStream.println(column);
        printStream.println(shootResult);

        if (playerOcean.isGameOver()) {
            printStream.println(GAME_END_LOSER_EVENT);
            printStream.println(opponentName);
            printStream.println(playerOcean.getShotsFired());
        }
    }

    private void onShootResult() throws IOException {
        String rowStr = bufferedReader.readLine();
        String columnStr = bufferedReader.readLine();
        String resStr = bufferedReader.readLine();

        int row = Integer.parseInt(rowStr);
        int column = Integer.parseInt(columnStr);
        int res = Integer.parseInt(resStr);
        if (gameEventListener != null) {
            Platform.runLater(() -> gameEventListener.onMyShotDone(row, column, FieldCell.ShootResult.fromInt(res)));
        }

        if (res == FieldCell.ShootResult.MISS.ordinal()) {
            turn = getOpponentType();
        } else {
            turn = appType;
        }
    }

    private String onGameOver() throws IOException {

        String winner = bufferedReader.readLine();
        int shotsFired = Integer.parseInt(bufferedReader.readLine());

        if (gameEventListener != null) {
            Platform.runLater(() -> {
                gameEventListener.onGameOver(new GameResult(winner.equals(name), shotsFired, playerOcean.getShotsFired()));
            });
        }

        return winner;
    }

    private void startGame() {
        printStream.println(GAME_START_EVENT);
        printStream.println(name);
    }

    private void onGameStart() throws IOException {
        opponentName = bufferedReader.readLine();
    }

    public void startListeningForGameEvents() {
        new Thread(() -> {
            while (runListener.get()) {
                try {
                    String line = bufferedReader.readLine();
                    switch (line) {
                        case SHOOT_EVENT:
                            onShoot();
                            break;
                        case SHOOT_RESULT_EVENT:
                            onShootResult();
                            break;
                        case GAME_END_LOSER_EVENT:
                            String winner = onGameOver();
                            printStream.println(GAME_END_WINNER_EVENT);
                            printStream.println(winner);
                            printStream.println(playerOcean.getShotsFired());
                            break;
                        case GAME_END_WINNER_EVENT:
                            onGameOver();
                            break;
                        case GAME_START_EVENT:
                            onGameStart();
                            break;
                        case GAME_STOP_REQUEST_EVENT:
                            onReceiveStopGameRequest();
                            break;
                        case GAME_STOP_EVENT:
                            onGameStop();
                            break;
                        default:
                            System.out.println("Unknown event: " + line);
                            break;
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }

        }).start();
    }

    public boolean waitForGameStart(Ocean ocean) {
        if (ocean == null)
            throw new IllegalArgumentException("Ocean must not be null");

        if (clientSocket != null) {
            return false;
        }

        runListener.set(true);

        playerOcean = ocean;

        if (appType == AcquaintanceWindow.AppType.SERVER) {
            try {
                serverSocket = new ServerSocket(port);

                while (clientSocket == null) {
                    clientSocket = serverSocket.accept();
                }

                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                printStream = new PrintStream(clientSocket.getOutputStream());
                startGame();

                while (!bufferedReader.readLine().equals(GAME_START_EVENT) && runListener.get());

                if (!runListener.get())
                    return false;

                onGameStart();

                startListeningForGameEvents();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            while (clientSocket == null) {
                try {
                    try {
                        clientSocket = new Socket(host, port);
                    } catch (Exception e) {
                        Thread.sleep(400);
                    }

                    printStream = new PrintStream(clientSocket.getOutputStream());
                    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    startGame();

                    while (!bufferedReader.readLine().equals(GAME_START_EVENT) && runListener.get()) ;

                    if (!runListener.get())
                        return false;

                    onGameStart();


                    startListeningForGameEvents();
                } catch (Exception e) {
                    e.printStackTrace();
                    clientSocket = null;
                }
            }
            return true;
        }
    }

}
