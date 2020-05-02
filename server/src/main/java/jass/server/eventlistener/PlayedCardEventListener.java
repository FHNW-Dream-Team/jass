package jass.server.eventlistener;

import jass.lib.message.PlayedCardData;

/**
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 0.0.1
 */
public interface PlayedCardEventListener {
    /**
     * Executes when a user played a card.
     *
     * @param data The data (basically the ID).
     */
    void onPlayedCard(PlayedCardData data);
}