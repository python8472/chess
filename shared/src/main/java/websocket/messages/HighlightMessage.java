package websocket.messages;

import chess.ChessPosition;
import chess.ChessMove;
import java.util.Collection;

public class HighlightMessage extends ServerMessage {
    private final Collection<ChessPosition> highlights;

    public HighlightMessage(Collection<ChessPosition> highlights) {
        super(ServerMessageType.HIGHLIGHT);
        this.highlights = highlights;
    }

    public Collection<ChessPosition> getHighlights() {
        return highlights;
    }
}

