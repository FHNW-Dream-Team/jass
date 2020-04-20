package jass.client.message;

import jass.client.util.SocketUtil;
import jass.lib.message.GameFoundData;
import jass.lib.message.MessageData;
import jass.lib.message.ResultData;

public class GameFound extends Message {

    private GameFoundData data;

    public GameFound(MessageData rawData) {
        super(rawData);
        data = (GameFoundData) rawData;
    }

    @Override
    public boolean process(SocketUtil socket) {
        return false;
    }
}