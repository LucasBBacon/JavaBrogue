package lucas.games.brogue.backend;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GridTest {

    @Nested
    class BuilderTests {

        @ParameterizedTest
        @CsvSource({"3, 5", "10, 50", "390, 200", "1, 3", "7, 7", "99, 83"})
        void gridBuilderReturnsAGridInstanceOfGivenDimensions(int cols, int rows) {
            Grid grid = Grid.builder()
                    .withDimensions(cols, rows)
                    .build();

            assertThat(grid.getCols()).isEqualTo(cols);
            assertThat(grid.getRows()).isEqualTo(rows);
        }

        @Test
        void gridBuilderReturnsAGridInstanceWithDefaultDimensionsWhenNoDimensionsAreGiven() {
            Grid grid = Grid.builder().build();

            assertThat(grid.getCols()).isEqualTo(Grid.Builder.DEFAULT_COLS);
            assertThat(grid.getRows()).isEqualTo(Grid.Builder.DEFAULT_ROWS);
        }

        @Test
        void gridBuilderReturnsAGridInstanceWithAllTilesInitializedAsEmpty() {
            Grid grid = Grid.builder().build();

            for (int row = 0; row < grid.getRows(); row++) {
                for (int col = 0; col < grid.getCols(); col++) {
                    assertTrue(grid.getTile(col, row).isEmpty());
                }
            }
        }
    }

    @Nested
    class EditTests {

        @Nested
        class CopyTests {

            @Test
            void copyReturnsEditInstanceWithSameDimensionsAndTilesAsGivenGrid() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 6)
                        .build();

                Grid editedGrid = grid.edit()
                        .copy(grid)
                        .build();

                assertThat(editedGrid.getCols()).isEqualTo(grid.getCols());
                assertThat(editedGrid.getRows()).isEqualTo(grid.getRows());
            }

            @Test
            void copyReturnsEditInstanceWithSameTilesAsGivenGrid() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 6)
                        .build();

                Grid editedGrid = grid.edit()
                        .copy(grid)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        assertThat(editedGrid.getTile(col, row)).isEqualTo(grid.getTile(col, row));
                    }
                }
            }
        }

        @Nested
        class FillTests {

            @Test
            void fillRequiresTileTypeParameter() {
                Grid grid = Grid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit().fill(null).build())
                        .withMessage("TileType value is required");
            }

            @Test
            void fillReturnsNewGridInstance() {
                Grid grid = Grid.builder().build();

                Grid editedGrid = grid.edit()
                        .fill(Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isNotSameAs(grid);
            }

            @Test
            void fillReturnsNewGridInstanceWithAllTilesWithSpecificValue() {
                Grid grid = Grid.builder().build();

                Grid editedGrid = grid.edit()
                        .fill(Tile.TileType.WALL)
                        .build();

                for (int col = 0; col < grid.getCols(); col++) {
                    for (int row = 0; row < grid.getRows(); row++) {
                        assertThat(grid.getTile(col, row).type()).isEqualTo(Tile.TileType.EMPTY);
                    }
                }
                for (int col = 0; col < editedGrid.getCols(); col++) {
                    for (int row = 0; row < editedGrid.getRows(); row++) {
                        assertThat(editedGrid.getTile(col, row).type()).isEqualTo(Tile.TileType.WALL);
                    }
                }
            }

        }

        @Nested
        class FindReplaceTests {

            @Test
            void findReplaceRequiresTileTypeParameters() {
                Grid grid = Grid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit().findReplace(null, Tile.TileType.WALL).build())
                        .withMessage("TileType to find is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit().findReplace(Tile.TileType.EMPTY, null).build())
                        .withMessage("TileType to fill is required");
            }

            @Test
            void findReplaceReturnsNewGridInstanceWithSpecificTilesReplaced() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL)}
                };

                Grid grid = Grid.builder()
                        .withTiles(tiles)
                        .build();

                Grid editedGrid = grid.edit()
                        .findReplace(Tile.TileType.WALL, Tile.TileType.WATER)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        if (grid.getTile(col, row).type() == Tile.TileType.WALL) {
                            assertThat(editedGrid.getTile(col, row).type()).isEqualTo(Tile.TileType.WATER);
                        } else {
                            assertThat(editedGrid.getTile(col, row).type()).isEqualTo(grid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void findReplaceReturnsNewGridInstanceWhenNoTilesAreReplaced() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY)}
                };

                Grid grid = Grid.builder()
                        .withTiles(tiles)
                        .build();

                Grid editedGrid = grid.edit()
                        .findReplace(Tile.TileType.WALL, Tile.TileType.WATER)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }
        }

        @Nested
        class FloodFillTests {

            @Test
            void floodFillRequiresTileTypeParameter() {
                Grid grid = Grid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .floodFill(0, 0, Tile.TileType.EMPTY, null)
                                .build())
                        .withMessage("TileType to fill is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .floodFill(0, 0, null, Tile.TileType.WALL)
                                .build())
                        .withMessage("TileType to replace is required");
            }

            @Test
            void floodFillReturnsSameGridIfCoordinatesInvalid() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 4)
                        .build();

                Grid editedGrid = grid.edit()
                        .floodFill(-1, 0, Tile.TileType.EMPTY, Tile.TileType.WATER)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void floodFillReturnsNewGridInstanceWithConnectedTilesReplaced() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)}
                };

                Grid grid = Grid.builder()
                        .withTiles(tiles)
                        .build();

                Grid editedGrid = grid.edit()
                        .floodFill(0, 0, Tile.TileType.EMPTY, Tile.TileType.WATER)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        if ((col == 0 && row == 0) || (col == 1 && row == 0) || (col == 0 && row == 1) ||
                            (col == 0 && row == 2) || (col == 1 && row == 2) || (col == 1 && row == 3)) {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WATER);
                        } else {
                            assertThat(editedGrid.getTile(col, row).type()).isEqualTo(grid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void floodFillReturnsSameGridIfNoTilesAreReplaced() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)}
                };

                Grid grid = Grid.builder()
                        .withTiles(tiles)
                        .build();

                Grid editedGrid = grid.edit()
                        .floodFill(1, 1, Tile.TileType.EMPTY, Tile.TileType.WATER)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }
        }

        @Nested
        class DrawRectangleTests {

            @Test
            void drawRectangleRequiresTileTypeParameter() {
                Grid grid = Grid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawRectangle(0, 0, 2, 2, null)
                                .build())
                        .withMessage("TileType to draw is required");
            }

            @Test
            void drawRectangleReturnsNewGridInstanceWithRectangleDrawn() {
                Grid grid = Grid.builder()
                        .withDimensions(5, 5)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(1, 1, 3, 3, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        if (col >= 1 && col <= 3 && row >= 1 && row <= 3) {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(grid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void drawRectangleReturnsSameGridIfRectangleIsOutOfBounds() {
                Grid grid = Grid.builder()
                        .withDimensions(5, 5)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(-1, -1, 6, 6, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawRectangleReturnsSameGridIfNoTilesAreChanged() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)}
                };

                Grid grid = Grid.builder()
                        .withTiles(tiles)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(0, 0, 2, 2, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawRectangleHandlesSingleTileRectangle() {
                Grid grid = Grid.builder()
                        .withDimensions(3, 3)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(1, 2, 1, 1, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        if (col == 1 && row == 2) {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(grid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void drawRectangleHandlesFullGridRectangle() {
                Grid grid = Grid.builder()
                        .withDimensions(2, 2)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(0, 0, 2, 2, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        assertThat(editedGrid.getTile(col, row).type())
                                .as("Tile at (%d, %d)", col, row)
                                .isEqualTo(Tile.TileType.WALL);
                    }
                }
            }

            @Test
            void drawRectangleHandlesNegativeSizeGracefully() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 4)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(2, 2, -2, -1, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawRectangleHandlesZeroSizeGracefully() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 4)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(1, 1, 0, 0, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawRectangleHandlesSizeOutOfBoundsGracefully() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 4)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawRectangle(2, 2, 5, 5, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        if (col >= 2 && row >= 2) {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(grid.getTile(col, row).type());
                        }
                    }
                }
            }
        }

        @Nested
        class DrawCircleTests {

            @Test
            void drawCircleRequiresTileTypeParameter() {
                Grid grid = Grid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawCircle(2, 2, 2, null)
                                .build())
                        .withMessage("TileType to draw is required");
            }

            @Test
            void drawCircleReturnsNewGridInstanceWithCircleDrawn() {
                Grid grid = Grid.builder()
                        .withDimensions(6, 6)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawCircle(2, 3, 2, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        int dx = col - 2;
                        int dy = row - 3;
                        if ((dx * dx + dy * dy) <= (2 * 2)) {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(grid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void drawCircleReturnsSameGridIfCircleIsOutOfBounds() {
                Grid grid = Grid.builder()
                        .withDimensions(5, 8)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawCircle(-3, -3, 2, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawCircleReturnsSameGridIfNoTilesAreChanged() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)}
                };

                Grid grid = Grid.builder()
                        .withTiles(tiles)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawCircle(1, 1, 2, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawCircleHandlesZeroRadiusGracefully() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 4)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawCircle(2, 2, 0, Tile.TileType.WALL)
                        .build();

                assertThat(editedGrid).isEqualTo(grid);
            }

            @Test
            void drawCircleHandlesLargeRadiusGracefully() {
                Grid grid = Grid.builder()
                        .withDimensions(4, 4)
                        .build();

                Grid editedGrid = grid.edit()
                        .drawCircle(2, 2, 10, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < grid.getRows(); row++) {
                    for (int col = 0; col < grid.getCols(); col++) {
                        assertThat(editedGrid.getTile(col, row).type())
                                .as("Tile at (%d, %d)", col, row)
                                .isEqualTo(Tile.TileType.WALL);
                    }
                }
            }
        }

        @Nested
        class GetTerrainTests {

            /* The following is the C code for getTerrain():
void getTerrainGrid(short **grid, short value, unsigned long terrainFlags, unsigned long mapFlags) {
    short i, j;
    for(i = 0; i < DCOLS; i++) {
        for(j = 0; j < DROWS; j++) {
            if (grid[i][j] != value && cellHasTerrainFlag((pos){ i, j }, terrainFlags) || (pmap[i][j].flags & mapFlags)) {
                grid[i][j] = value;
            }
        }
    }
}
            */

            @Test
            void getTerrainReturnsNewGridInstanceWithSpecifiedTilesSetToValue() {
                fail("To be implemented");
            }

            @Test
            void getTerrainReturnsSameGridIfNoTilesAreChanged() {
                fail("To be implemented");
            }

            @Test
            void getTerrainHandlesEmptyGridGracefully() {
                fail("To be implemented");
            }

            @Test
            void getTerrainHandlesFullGridGracefully() {
                fail("To be implemented");
            }

            @Test
            void getTerrainHandlesNoMatchingTilesGracefully() {
                fail("To be implemented");
            }
        }
    }
}