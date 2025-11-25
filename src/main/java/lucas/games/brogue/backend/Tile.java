package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.Entity;

/**
 * Represents a single tile square in the dungeon grid.
 * This object is mutable because the state of a tile changes (light levels, memory).
 */
public class Tile {

    private TerrainType terrain;

    // Light memory: the color of light stored on this tile
    private BrogueColor lightColor;

    // Visibility
    private boolean isVisible;  // Is the player seeing it right now?
    private boolean isExplored; // Has the player seen it before? (Fog of war)

    private Entity occupant;

    public Tile(TerrainType terrain) {
        this.terrain = terrain;
        this.lightColor = BrogueColor.BLACK;
        this.isVisible = false;
        this.isExplored = false;
        this.occupant = null;
    }

    public TerrainType getTerrain() { return terrain; }
    public BrogueColor getLightColor() { return lightColor; }
    public boolean isVisible() { return isVisible; }
    public boolean isExplored() { return isExplored; }
    public Entity getOccupant() { return occupant; }

    public boolean hasOccupant() {
        return this.occupant != null;
    }

    public void setTerrain(TerrainType terrain) { this.terrain = terrain; }
    public void setLightColor(BrogueColor lightColor) { this.lightColor = lightColor; }
    public void setVisible(boolean visible) {
        isVisible = visible;
        if (visible) {
            isExplored = true;
        }
    }

    public void setOccupant(Entity occupant) { this.occupant = occupant; }

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
