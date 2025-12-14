package lucas.games.brogue.backend;

import io.vavr.control.Option;
import lucas.games.brogue.backend.grid.Constants;
import lucas.games.brogue.backend.grid.TileGrid;
import lucas.games.brogue.backend.grid.cells.TerrainFlags;
import lucas.games.brogue.backend.grid.cells.Tile;
import lucas.games.brogue.backend.grid.cells.WorldCell;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TileGridTest {

    @Nested
    class BuilderTests {

        @ParameterizedTest
        @CsvSource({"3, 5", "10, 50", "390, 200", "1, 3", "7, 7", "99, 83"})
        void gridBuilderReturnsAGridInstanceOfGivenDimensions(int cols, int rows) {
            TileGrid tileGrid = TileGrid.filled(cols, rows, WorldCell.EMPTY);

            assertThat(tileGrid.getCols()).isEqualTo(cols);
            assertThat(tileGrid.getRows()).isEqualTo(rows);
        }

        @Test
        void gridBuilderReturnsAGridInstanceWithDefaultDimensionsWhenNoDimensionsAreGiven() {
            TileGrid tileGrid = TileGrid.defaultGrid();

            assertThat(tileGrid.getCols()).isEqualTo(Constants.COLS);
            assertThat(tileGrid.getRows()).isEqualTo(Constants.ROWS);
        }

        @Test
        void gridBuilderReturnsAGridInstanceWithAllTilesInitializedAsEmpty() {
            TileGrid tileGrid = TileGrid.defaultGrid();

            for (int row = 0; row < tileGrid.getRows(); row++) {
                for (int col = 0; col < tileGrid.getCols(); col++) {
                    assertThat(tileGrid.get(col, row)).isEqualTo(WorldCell.EMPTY);
                }
            }
        }
    }

    @Nested
    class GetQualifyingPathLocNearTests {

        private final WorldCell floor = WorldCell.EMPTY.withDungeon(Tile.FLOOR);
        private final WorldCell wall = WorldCell.EMPTY.withDungeon(Tile.WALL);
        private final WorldCell lava = WorldCell.EMPTY.withDungeon(Tile.NOTHING.withLavaInstaDeath());

        @Test
        void shouldReturnNoneForOutOfBoundsStartPosition() {
            TileGrid grid = TileGrid.defaultGrid();
            Position startPos = new Position(-1, -1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    0,
                    0,
                    0,
                    0,
                    true);

            assertThat(result).isEqualTo(Option.none());
        }

        @Test
        void shouldResultStartPositionWhenItQualifies() {
            TileGrid grid = TileGrid.filled(5, 5, floor);
            Position startPos = new Position(2, 2);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    0,
                    0,
                    0,
                    0,
                    true);

            assertThat(result).contains(startPos);
        }

        @Test
        void shouldReturnNoneWhenNoQualifyingPositionExists() {
            TileGrid grid = TileGrid.filled(5, 5, wall);
            Position startPos = new Position(2, 2);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    TerrainFlags.OBSTRUCTS_PASSABILITY,
                    0,
                    0,
                    0,
                    true);

            assertThat(result).isEqualTo(Option.none());
        }

        @Test
        void shouldFindNeighborWhenStartIsBlocked() {
            TileGrid grid = TileGrid.filled(5, 5, wall)
                    .set(1, 0, floor);
            Position startPos = new Position(1, 1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    TerrainFlags.OBSTRUCTS_PASSABILITY,
                    0,
                    0,
                    0,
                    true);

            assertThat(result).contains(new Position(1, 0));
        }

        @Test
        void shouldAvoidForbiddenCells() {
            TileGrid grid = TileGrid.filled(3, 3, floor)
                    .set(1, 0, lava);
            Position startPos = new Position(1, 1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    0,
                    0,
                    TerrainFlags.LAVA_INSTA_DEATH,
                    0,
                    true);

            assertThat(result).contains(new Position(1, 1));
        }

        @Test
        void shouldNotQualifyHallwayWhenNotAllowed() {
            TileGrid grid = TileGrid.filled(3, 3, wall)
                    .set(1, 0, floor)
                    .set(1, 1, floor)
                    .set(1, 2, floor);
            Position startPos = new Position(1, 1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    false,
                    TerrainFlags.OBSTRUCTS_PASSABILITY,
                    0,
                    0,
                    0,
                    true);

            // (1, 1) is a hallway, so it's blocking
            // (1, 0) is a dead end (passable arc count 1), so it qualifies
            assertThat(result).contains(new Position(1, 0));
        }

        @Test
        void shouldQualifyHallwayWhenAllowed() {
            TileGrid grid = TileGrid.filled(3, 3, wall)
                    .set(1, 0, floor)
                    .set(1, 1, floor)
                    .set(1, 2, floor);
            Position startPos = new Position(1, 1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    TerrainFlags.OBSTRUCTS_PASSABILITY,
                    0,
                    0,
                    0,
                    true);

            assertThat(result).contains(startPos);
        }

        @Test
        void shouldReturnNoneIfOnlyForbiddenCellsAreAvailable() {
            TileGrid grid = TileGrid.filled(3, 3, lava);
            Position startPos = new Position(1, 1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    0,
                    0,
                    TerrainFlags.LAVA_INSTA_DEATH,
                    0,
                    true);

            assertThat(result).isEqualTo(Option.none());
        }

        @Test
        void shouldFindQualifyingLocationWhenStartIsForbidden() {
            TileGrid grid = TileGrid.filled(3, 3, floor)
                    .set(1, 1, lava);
            Position startPos = new Position(1, 1);

            Option<Position> result = grid.getQualifyingPathLocNear(startPos,
                    true,
                    0,
                    0,
                    TerrainFlags.LAVA_INSTA_DEATH,
                    0,
                    true);

            // The start (1,1) is forbidden, so it continues searching and finds a neighbor.
            assertThat(result).contains(new Position(1, 0));
        }
    }

//    @Nested
//    class EditTests {
//
//        @Nested
//        class CopyTests {
//
//            @Test
//            void copyReturnsEditInstanceWithSameDimensionsAndTilesAsGivenGrid() {
//                TileGrid tileGrid = TileGrid.empty(4, 6);
//
//                TileGrid editedTileGrid = tileGrid.edit()
//                        .copy(tileGrid)
//                        .build();
//
//                assertThat(editedTileGrid.getCols()).isEqualTo(tileGrid.getCols());
//                assertThat(editedTileGrid.getRows()).isEqualTo(tileGrid.getRows());
//            }
//
//            @Test
//            void copyReturnsEditInstanceWithSameTilesAsGivenGrid() {
//                TileGrid tileGrid = TileGrid.builder()
//                        .withDimensions(4, 6)
//                        .build();
//
//                TileGrid editedTileGrid = tileGrid.edit()
//                        .copy(tileGrid)
//                        .build();
//
//                for (int row = 0; row < tileGrid.getRows(); row++) {
//                    for (int col = 0; col < tileGrid.getCols(); col++) {
//                        assertThat(editedTileGrid.getTile(col, row)).isEqualTo(tileGrid.getTile(col, row));
//                    }
//                }
//            }
//        }
//
//    }
}