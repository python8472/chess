package websocket.commands;

/**
 * A command to notify the server that the user is connected to a game.
 */
public class ConnectCommand extends UserGameCommand {

    public ConnectCommand(String authToken, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
