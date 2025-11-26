package lucas.games.brogue.backend.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a history of game events to be displayed to the player.
 */
public class MessageLog {
    private final List<String> messages;
    private static final int MAX_HISTORY = 100;

    public MessageLog() {
        this.messages = new ArrayList<>();
    }

    public void add(String message) {
        messages.add(message);
        if (messages.size() > MAX_HISTORY) {
            messages.removeFirst();
        }
    }

    /**
     * Returns the most recent N messages.
     * Useful for the HUD.
     */
    public List<String> getRecentMessages(int count) {
        int start = Math.max(0, messages.size() - count);
        return Collections.unmodifiableList(messages.subList(start, messages.size()));
    }

    public List<String> getAllMessages() {
        return Collections.unmodifiableList(messages);
    }
}
