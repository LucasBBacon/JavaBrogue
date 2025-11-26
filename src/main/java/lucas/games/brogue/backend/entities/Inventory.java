package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.entities.items.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages a collection of items.
 * Modeled after Brogue's 'Pack': a fixed capacity list where items are accessed by index (char).
 */
public class Inventory {

    private static final int CAPACITY = 26; // a through z
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>(CAPACITY);
    }

    /**
     * Attempts to add an item to the pack.
     * @return true if successful, false if full.
     */
    public boolean add(Item item) {
        if (isFull()) return false;

        items.add(item);
        return true;
    }

    /**
     * Removes a specific item from the pack.
     */
    public void remove(Item item) {
        items.remove(item);
    }

    /**
     * Returns the item at a specific index (0-25).
     */
    public Item get(int index) {
        if (index < 0 || index >= items.size()) return null;

        return items.get(index);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isFull() {
        return items.size() >= CAPACITY;
    }

    public int size() {
        return items.size();
    }

    /**
     * Helper to get the character label for an item index.
     * 0 -> 'a', 1 -> 'b', ..., 25 -> 'z'
     */
    public char getLabelForIndex(int index) {
        return (char) ('a' + index);
    }
}
