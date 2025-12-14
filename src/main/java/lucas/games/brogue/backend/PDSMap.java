package lucas.games.brogue.backend;

import io.vavr.collection.List;
import io.vavr.collection.Vector;
import lucas.games.brogue.backend.grid.Constants;
import lucas.games.brogue.backend.grid.TileGrid;

public class PDSMap {

    //    public static final int D_COLS = 79;
//    public static final int D_ROWS = 29;

    public static class PDSLink {
        private final int index;
        public int distance;
        public int movementCost;
        public PDSLink prev;
        public PDSLink next;

        public PDSLink(int index) {
            this.index = index;
            this.movementCost = Constants.PDS_FORBIDDEN;
            this.distance = Constants.MAX_DISTANCE;
            this.prev = null;
            this.next = null;
        }
    }

    private final int width, height;
    public final Vector<PDSLink> grid;

    public PDSLink head;

    public PDSMap(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.head = null;
        this.grid = Vector.tabulate(width * height, PDSLink::new);
    }

    public void setDistance(final int col, final int row, final int newDistance) {
        // Bound check (Brogue ignores the 1-tile border for safety in some loops)
        if (col <= 0 || col >= width - 1 || row <= 0 || row >= height - 1) {
            return;
        }

        PDSLink link = grid.get(col * width + row);

        // Only update if we found a shorter path (relaxation)
        if (newDistance < link.distance) {
            link.distance = newDistance;

            // --- REMOVE FROM LIST (Unlink) ---
            // If it's already in the queue, pull it out or move it
            if (link.next != null) {
                link.next.prev = link.prev;
            }
            if (link.prev != null) {
                link.prev.next = link.next;
            }

            if (link == head) {
                head = link.next; // If it was the head, the next becomes the new head
            }

            // Clean up pointers for safety
            link.prev = null;
            link.next = null;

            // --- FIND INSERTION POINT ---
            // Search from the start (head) for where this node belongs
            // List is sorted Ascending (smallest distance first)
            PDSLink current = head;
            PDSLink previous = null;

            while (current != null && current.distance < link.distance) {
                previous = current;
                current = current.next;
            }

            // --- INSERT INTO LIST (Relink) ---
            link.next = current;
            link.prev = previous;

            if (previous != null) {
                previous.next = link;
            } else {
                head = link; // We are the new smallest node
            }
            if (current != null) {
                current.prev = link;
            }
        }
    }

    public void update(boolean useDiagonals) {
        int dirs = useDiagonals ? 8 : 4;

        while (head != null) {
            // Pop closes node (head)
            PDSLink current = head;
            head = head.next;

            // Unlink head cleanly
            if (head != null) {
                head.prev = null;
            }
            current.next = null;
            current.prev = null;

            // Process neighbours
            for (int dir = 0; dir < dirs; dir++) {
                int nRow = (current.index / width) + Constants.NB_DIRS[dir][0];
                int nCol = (current.index % width) + Constants.NB_DIRS[dir][1];

                // Check bounds
                if (nRow <= 0 || nRow >= height - 1 || nCol <= 0 || nCol >= width - 1) {
                    continue;
                }

                PDSLink neighbor = grid.get(nCol * width + nRow);

                // Skip walls
                if (neighbor.movementCost == Constants.PDS_OBSTRUCTION) {
                    continue;
                }

                // Calculate potential new distance
                int newDistance = current.distance + neighbor.movementCost;

                // Attempt to update (setDistance handles sorting logic)
                setDistance(nCol, nRow, newDistance);
            }
        }
    }

    public void loadCosts(TileGrid world, int blockingFlags) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // If the cell is blocked by the provided flags, it's a wall
                if (world.cellHasFlag(col, row, blockingFlags)) {
                    grid.get(col * width + row).movementCost = Constants.PDS_OBSTRUCTION;
                } else {
                    grid.get(col * width + row).movementCost = 100; // Standard cost
                }
            }
        }
    }

    public void clear(int maxDistance) {
        this.head = null;

        for (PDSLink link : grid) {
            link.distance = maxDistance;
            link.prev = null;
            link.next = null;
            // movementCost not cleared here, it's set in loadCosts
        }
    }

    public void dijkstraScan(TileGrid world, List<Position> sources, int blockingFlags, boolean useDiagonals) {
        // Reset
        clear(Constants.MAX_DISTANCE);

        // Load costs
        loadCosts(world, blockingFlags);

        // Plant seeds
        for (Position source : sources) {
            setDistance(source.col(), source.row(), 0);
        }

        // Run logic
        update(useDiagonals);
    }

    public Position getNextStep(final int startCol, final int startRow) {
        if (!isInBounds(startCol, startRow)) {
            return new Position(startCol, startRow);
        }

        int bestDistance = grid.get(startCol * width + startRow).distance;
        List<Position> candidates = List.empty();

        // Check 8 neighbours
        for (int dir = 0; dir < 8; dir++) {
            int nRow = startRow + Constants.NB_DIRS[dir][0];
            int nCol = startCol + Constants.NB_DIRS[dir][1];

            if (!isInBounds(nCol, nRow)) {
                continue;
            }

            PDSLink neighbor = grid.get(nCol * width + nRow);
            if (neighbor.distance >= Constants.MAX_DISTANCE) {
                continue;
            }

            if (neighbor.distance < bestDistance) {
                // Strictly better: clear previous ties and add this one
                bestDistance = neighbor.distance;
                candidates = List.empty();
                candidates.append(new Position(nCol, nRow));
            } else if (neighbor.distance == bestDistance) {
                // Tie: add to candidates
                candidates = candidates.append(new Position(nCol, nRow));
            }
        }

        if (candidates.isEmpty()) {
            return new Position(startCol, startRow); // No valid moves
        }

        // Randomly choose from best candidates
        int index = new Random().randomInt(candidates.size());
        return candidates.get(index);
    }

    public int getDistance(final int col, final int row) {
        if (isInBounds(col, row)) {
            return grid.get(col * width + row).distance;
        }
        return Constants.MAX_DISTANCE;
    }

    private boolean isInBounds(final int col, final int row) {
        return col >= 0 && col < width && row >= 0 && row < height;
    }
}
