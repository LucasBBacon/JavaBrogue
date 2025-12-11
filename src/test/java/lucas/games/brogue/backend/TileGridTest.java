package lucas.games.brogue.backend;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TileGridTest {

    @Nested
    class BuilderTests {

        @ParameterizedTest
        @CsvSource({"3, 5", "10, 50", "390, 200", "1, 3", "7, 7", "99, 83"})
        void gridBuilderReturnsAGridInstanceOfGivenDimensions(int cols, int rows) {
            TileGrid tileGrid = TileGrid.builder()
                    .withDimensions(cols, rows)
                    .build();

            assertThat(tileGrid.getCols()).isEqualTo(cols);
            assertThat(tileGrid.getRows()).isEqualTo(rows);
        }

        @Test
        void gridBuilderReturnsAGridInstanceWithDefaultDimensionsWhenNoDimensionsAreGiven() {
            TileGrid tileGrid = TileGrid.builder().build();

            assertThat(tileGrid.getCols()).isEqualTo(TileGrid.Builder.DEFAULT_COLS);
            assertThat(tileGrid.getRows()).isEqualTo(TileGrid.Builder.DEFAULT_ROWS);
        }

        @Test
        void gridBuilderReturnsAGridInstanceWithAllTilesInitializedAsEmpty() {
            TileGrid tileGrid = TileGrid.builder().build();

            for (int row = 0; row < tileGrid.getRows(); row++) {
                for (int col = 0; col < tileGrid.getCols(); col++) {
                    assertTrue(tileGrid.getTile(col, row).isEmpty());
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
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 6)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .copy(tileGrid)
                        .build();

                assertThat(editedTileGrid.getCols()).isEqualTo(tileGrid.getCols());
                assertThat(editedTileGrid.getRows()).isEqualTo(tileGrid.getRows());
            }

            @Test
            void copyReturnsEditInstanceWithSameTilesAsGivenGrid() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 6)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .copy(tileGrid)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        assertThat(editedTileGrid.getTile(col, row)).isEqualTo(tileGrid.getTile(col, row));
                    }
                }
            }
        }

        @Nested
        class FillTests {

            @Test
            void fillRequiresTileTypeParameter() {
                TileGrid tileGrid = TileGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit().fill(null).build())
                        .withMessage("TileType value is required");
            }

            @Test
            void fillReturnsNewGridInstance() {
                TileGrid tileGrid = TileGrid.builder().build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .fill(Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isNotSameAs(tileGrid);
            }

            @Test
            void fillReturnsNewGridInstanceWithAllTilesWithSpecificValue() {
                TileGrid tileGrid = TileGrid.builder().build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .fill(Tile.TileType.WALL)
                        .build();

                for (int col = 0; col < tileGrid.getCols(); col++) {
                    for (int row = 0; row < tileGrid.getRows(); row++) {
                        assertThat(tileGrid.getTile(col, row).type()).isEqualTo(Tile.TileType.EMPTY);
                    }
                }
                for (int col = 0; col < editedTileGrid.getCols(); col++) {
                    for (int row = 0; row < editedTileGrid.getRows(); row++) {
                        assertThat(editedTileGrid.getTile(col, row).type()).isEqualTo(Tile.TileType.WALL);
                    }
                }
            }

        }

        @Nested
        class FindReplaceTests {

            @Test
            void findReplaceRequiresTileTypeParameters() {
                TileGrid tileGrid = TileGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit().findReplace(null, Tile.TileType.WALL).build())
                        .withMessage("TileType to find is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit().findReplace(Tile.TileType.EMPTY, null).build())
                        .withMessage("TileType to fill is required");
            }

            @Test
            void findReplaceReturnsNewGridInstanceWithSpecificTilesReplaced() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL)}
                };

                TileGrid tileGrid = TileGrid.builder()
                        .withTiles(tiles)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .findReplace(Tile.TileType.WALL, Tile.TileType.WATER)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        if (tileGrid.getTile(col, row).type() == Tile.TileType.WALL) {
                            assertThat(editedTileGrid.getTile(col, row).type()).isEqualTo(Tile.TileType.WATER);
                        } else {
                            assertThat(editedTileGrid.getTile(col, row).type()).isEqualTo(tileGrid.getTile(col, row).type());
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

                TileGrid tileGrid = TileGrid.builder()
                        .withTiles(tiles)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .findReplace(Tile.TileType.WALL, Tile.TileType.WATER)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }
        }

        @Nested
        class FloodFillTests {

            @Test
            void floodFillRequiresTileTypeParameter() {
                TileGrid tileGrid = TileGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit()
                                .floodFill(0, 0, Tile.TileType.EMPTY, null)
                                .build())
                        .withMessage("TileType to fill is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit()
                                .floodFill(0, 0, null, Tile.TileType.WALL)
                                .build())
                        .withMessage("TileType to replace is required");
            }

            @Test
            void floodFillReturnsSameGridIfCoordinatesInvalid() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .floodFill(-1, 0, Tile.TileType.EMPTY, Tile.TileType.WATER)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void floodFillReturnsNewGridInstanceWithConnectedTilesReplaced() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.EMPTY)}
                };

                TileGrid tileGrid = TileGrid.builder()
                        .withTiles(tiles)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .floodFill(0, 0, Tile.TileType.EMPTY, Tile.TileType.WATER)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        if ((col == 0 && row == 0) || (col == 1 && row == 0) || (col == 0 && row == 1) ||
                            (col == 0 && row == 2) || (col == 1 && row == 2) || (col == 1 && row == 3)) {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WATER);
                        } else {
                            assertThat(editedTileGrid.getTile(col, row).type()).isEqualTo(tileGrid.getTile(col, row).type());
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

                TileGrid tileGrid = TileGrid.builder()
                        .withTiles(tiles)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .floodFill(1, 1, Tile.TileType.EMPTY, Tile.TileType.WATER)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }
        }

        @Nested
        class DrawRectangleTests {

            @Test
            void drawRectangleRequiresTileTypeParameter() {
                TileGrid tileGrid = TileGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit()
                                .drawRectangle(0, 0, 2, 2, null)
                                .build())
                        .withMessage("TileType to draw is required");
            }

            @Test
            void drawRectangleReturnsNewGridInstanceWithRectangleDrawn() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(5, 5)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(1, 1, 3, 3, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        if (col >= 1 && col <= 3 && row >= 1 && row <= 3) {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(tileGrid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void drawRectangleReturnsSameGridIfRectangleIsOutOfBounds() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(5, 5)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(-1, -1, 6, 6, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawRectangleReturnsSameGridIfNoTilesAreChanged() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)}
                };

                TileGrid tileGrid = TileGrid.builder()
                        .withTiles(tiles)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(0, 0, 2, 2, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawRectangleHandlesSingleTileRectangle() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(3, 3)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(1, 2, 1, 1, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        if (col == 1 && row == 2) {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(tileGrid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void drawRectangleHandlesFullGridRectangle() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(2, 2)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(0, 0, 2, 2, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        assertThat(editedTileGrid.getTile(col, row).type())
                                .as("Tile at (%d, %d)", col, row)
                                .isEqualTo(Tile.TileType.WALL);
                    }
                }
            }

            @Test
            void drawRectangleHandlesNegativeSizeGracefully() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(2, 2, -2, -1, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawRectangleHandlesZeroSizeGracefully() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(1, 1, 0, 0, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawRectangleHandlesSizeOutOfBoundsGracefully() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawRectangle(2, 2, 5, 5, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        if (col >= 2 && row >= 2) {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(tileGrid.getTile(col, row).type());
                        }
                    }
                }
            }
        }

        @Nested
        class DrawCircleTests {

            @Test
            void drawCircleRequiresTileTypeParameter() {
                TileGrid tileGrid = TileGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> tileGrid.edit()
                                .drawCircle(2, 2, 2, null)
                                .build())
                        .withMessage("TileType to draw is required");
            }

            @Test
            void drawCircleReturnsNewGridInstanceWithCircleDrawn() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(6, 6)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawCircle(2, 3, 2, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        int dx = col - 2;
                        int dy = row - 3;
                        if ((dx * dx + dy * dy) <= (2 * 2)) {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(Tile.TileType.WALL);
                        } else {
                            assertThat(editedTileGrid.getTile(col, row).type())
                                    .as("Tile at (%d, %d)", col, row)
                                    .isEqualTo(tileGrid.getTile(col, row).type());
                        }
                    }
                }
            }

            @Test
            void drawCircleReturnsSameGridIfCircleIsOutOfBounds() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(5, 8)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawCircle(-3, -3, 2, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawCircleReturnsSameGridIfNoTilesAreChanged() {
                Tile[][] tiles = {
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)},
                        {new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL), new Tile(Tile.TileType.WALL)}
                };

                TileGrid tileGrid = TileGrid.builder()
                        .withTiles(tiles)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawCircle(1, 1, 2, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawCircleHandlesZeroRadiusGracefully() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawCircle(2, 2, 0, Tile.TileType.WALL)
                        .build();

                assertThat(editedTileGrid).isEqualTo(tileGrid);
            }

            @Test
            void drawCircleHandlesLargeRadiusGracefully() {
                TileGrid tileGrid = TileGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                TileGrid editedTileGrid = tileGrid.edit()
                        .drawCircle(2, 2, 10, Tile.TileType.WALL)
                        .build();

                for (int row = 0; row < tileGrid.getRows(); row++) {
                    for (int col = 0; col < tileGrid.getCols(); col++) {
                        assertThat(editedTileGrid.getTile(col, row).type())
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