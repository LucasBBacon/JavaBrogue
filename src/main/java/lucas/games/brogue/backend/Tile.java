package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single tile square in the dungeon grid.
 * This object is mutable because the state of a tile changes (light levels, memory).
 */
public class Tile {

    private TerrainType terrain;

    // Light memory: the color of light stored on this tile
    private BrogueColor lightColor;

    // The entity blocking this tile (if any)
    private Entity occupant;

    // The items lying on the floor
    private final List<Item> items;

    // Visibility
    private boolean isVisible;  // Is the player seeing it right now?
    private boolean isExplored; // Has the player seen it before? (Fog of war)

    public Tile(TerrainType terrain) {
        this.terrain = terrain;
        this.lightColor = BrogueColor.BLACK;
        this.isVisible = false;
        this.isExplored = false;
        this.occupant = null;
        this.items = new ArrayList<>();
    }

    // --- Item management ---

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    public Item getTopItem() {
        if (items.isEmpty()) return null;
        return items.getLast();
    }

    public TerrainType getTerrain() { return terrain; }
    public void setTerrain(TerrainType terrain) { this.terrain = terrain; }

    public BrogueColor getLightColor() { return lightColor; }
    public void setLightColor(BrogueColor lightColor) { this.lightColor = lightColor; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) {
        isVisible = visible;
        if (visible) {
            isExplored = true;
        }
    }

    public boolean isExplored() { return isExplored; }

    public Entity getOccupant() { return occupant; }
    public void setOccupant(Entity occupant) { this.occupant = occupant; }
    public boolean hasOccupant() {
        return this.occupant != null;
    }

    /**
     * Reset the tile's lighting and visibility for a new turn.
     * 'Explored' is not reset because map memory persists.
     */
    public void resetForTurn() {
        this.isVisible = false;
        this.lightColor = BrogueColor.BLACK;
        // In Brogue, there's always ambient light, but that is calculated later
    }
}
