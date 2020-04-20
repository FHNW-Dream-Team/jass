package jass.lib.message;

import org.json.JSONObject;

/**
 * The data model for the Game Found message.
 *
 * @author Thomas Weber
 */

public class GameFoundData extends MessageData {
    private final String token;

    private final int gameId;

    private final int playerOneId;
    private final String playerOne;

    private final int playerTwoId;
    private final String playerTwo;

    private final int playerThreeId;
    private final String playerThree;

    private final int playerFourId;
    private final String playerFour;

    public GameFoundData(String token, int gameId, int playerOneId, String playerOne, int playerTwoId, String playerTwo, int playerThreeId, String playerThree, int playerFourId, String playerFour) {
        super("GameFound");

        this.token = token;
        this.gameId = gameId;
        this.playerOneId = playerOneId;
        this.playerOne = playerOne;
        this.playerTwoId = playerTwoId;
        this.playerTwo = playerTwo;
        this.playerThreeId = playerThreeId;
        this.playerThree = playerThree;
        this.playerFourId = playerFourId;
        this.playerFour = playerFour;
    }

    public GameFoundData(JSONObject data) {
        super(data);
        token = data.getString("token");
        gameId = data.getInt("gameId");
        playerOneId = data.getInt("playerOneId");
        playerOne = data.getString("playerOne");
        playerTwoId = data.getInt("playerTwoId");
        playerTwo = data.getString("playerTwo");
        playerThreeId = data.getInt("playerThreeId");
        playerThree = data.getString("playerThree");
        playerFourId = data.getInt("playerFourId");
        playerFour = data.getString("playerFour");
    }

    public String getToken() { return token; }

    public int getGameId() { return gameId; }

    public int getPlayerOneId() { return playerOneId; }

    public String getPlayerOne() { return playerOne; }

    public int getPlayerTwoId() { return playerTwoId; }

    public String getPlayerTwo() { return playerTwo; }

    public int getPlayerThreeId() { return playerThreeId; }

    public String getPlayerThree() { return playerThree; }

    public int getPlayerFourId() { return playerFourId; }

    public String getPlayerFour() { return playerFour; }
}