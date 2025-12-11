package lucas.games.brogue.backend;

import io.vavr.collection.Vector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class IntGridTest {

    @Nested
    class BuilderTests {

        @Test
        void testBuildIntGridReturnsNonNullGrid() {
            IntGrid grid = IntGrid.builder()
                    .withDimensions(5, 5)
                    .withCells(io.vavr.collection.Vector.fill(25, 0))
                    .build();

            assertThat(grid).isNotNull();
        }

        @Test
        void testBuildIntGridWithNullCellsThrowsException() {
            assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
                IntGrid.builder()
                        .withDimensions(5, 5)
                        .withCells(null)
                        .build();
            }).withMessage("cells must not be null");
        }

        @Test
        void testBuildIntGridWithIncorrectCellCountThrowsException() {
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                IntGrid.builder()
                        .withDimensions(5, 5)
                        .withCells(io.vavr.collection.Vector.fill(20, 0)) // Incorrect size
                        .build();
            }).withMessage("cells size must be equal to cols * rows");
        }

        @Test
        void testBuildIntGridWithNegativeDimensionsThrowsException() {
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                IntGrid.builder()
                        .withDimensions(-5, 5)
                        .withCells(io.vavr.collection.Vector.fill(25, 0))
                        .build();
            }).withMessage("cols and rows must be non-negative");
        }

        @Test
        void testBuildIntGridWithZeroDimensionsCreatesEmptyGrid() {
            IntGrid grid = IntGrid.builder()
                    .withDimensions(0, 0)
                    .withCells(io.vavr.collection.Vector.empty())
                    .build();

            assertThat(grid.getCols()).isEqualTo(0);
            assertThat(grid.getRows()).isEqualTo(0);
        }

        @Test
        void testBuildIntGridWithValidParametersCreatesGrid() {
            IntGrid grid = IntGrid.builder()
                    .withDimensions(3, 3)
                    .withCells(io.vavr.collection.Vector.of(1, 2, 3, 4, 5, 6, 7, 8, 9))
                    .build();

            assertThat(grid.getCols()).isEqualTo(3);
            assertThat(grid.getRows()).isEqualTo(3);
            assertThat(grid.get(0, 0)).isEqualTo(1);
            assertThat(grid.get(2, 2)).isEqualTo(9);
        }

        @Test
        void testBuildIntGridUsesDefaultParameters() {
            IntGrid grid = IntGrid.builder().build();

            assertThat(grid.getCols()).isEqualTo(10);
            assertThat(grid.getRows()).isEqualTo(20);
            assertThat(grid.get(0, 0)).isEqualTo(0);
        }
    }

    @Nested
    class EditTests {

        @Test
        void editCreatesAnImmutableCopyOfGrid() {
            IntGrid originalGrid = IntGrid.builder().build();
            IntGrid editedGrid = originalGrid.edit().build();

            assertThat(editedGrid).isEqualTo(originalGrid);
        }

        @Test
        void editModificationsDoNotAffectOriginalGrid() {
            IntGrid originalGrid = IntGrid.builder().build();
            IntGrid editedGrid = originalGrid.edit()
                    .fill(42)
                    .build();

            assertThat(originalGrid.get(0, 0)).isEqualTo(0);
            assertThat(editedGrid.get(0, 0)).isEqualTo(42);
            assertThat(editedGrid).isNotEqualTo(originalGrid);
        }

        @Nested
        class FillTests {

            @Test
            void fillRequiresNonNullValue() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->
                        grid.edit()
                                .fill(null)
                                .build()).withMessage("Fill value is required");
            }

            @Test
            void fillOnEmptyGridDoesNothing() {
                IntGrid emptyGrid = IntGrid.builder()
                        .withDimensions(0, 0)
                        .withCells(io.vavr.collection.Vector.empty())
                        .build();

                IntGrid editedGrid = emptyGrid.edit()
                        .fill(5)
                        .build();

                assertThat(editedGrid.getCols()).isEqualTo(0);
                assertThat(editedGrid.getRows()).isEqualTo(0);
            }

            @Test
            void fillOnSingleCellGridSetsCellToSpecifiedValue() {
                IntGrid singleCellGrid = IntGrid.builder()
                        .withDimensions(1, 1)
                        .withCells(io.vavr.collection.Vector.of(0))
                        .build();

                IntGrid editedGrid = singleCellGrid.edit()
                        .fill(9)
                        .build();

                assertThat(editedGrid.get(0, 0)).isEqualTo(9);
            }

            @Test
            void fillSetsAllCellsToSpecifiedValue() {
                IntGrid editedGrid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 0))
                        .build()
                        .edit()
                        .fill(7)
                        .build();

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        assertThat(editedGrid.get(col, row)).isEqualTo(7);
                    }
                }
            }

            @Test
            void fillWithNegativeValuesThrowsException() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .fill(-1)
                                .build()).withMessage("Fill value must be non-negative");
            }
        }

        @Nested
        class findReplaceTests {

            @Test
            void findReplaceRequiresNonNullParameters() {
                IntGrid grid = IntGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .findReplace(null, 5, 1)
                                .build())
                        .withMessage("Minimum search value is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .findReplace(1, null, 1)
                                .build())
                        .withMessage("Maximum search value is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .findReplace(1, 5, null)
                                .build())
                        .withMessage("Fill value is required");
            }

            @Test
            void findReplaceWithNegativeValuesThrowsException() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .findReplace(-1, 2, 5)
                                .build()).withMessage("Search values must be non-negative");

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .findReplace(1, -2, 5)
                                .build()).withMessage("Search values must be non-negative");

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .findReplace(1, 2, -5)
                                .build()).withMessage("Fill value must be non-negative");
            }

            @Test
            void findReplaceReplacesValuesInRange() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.of(
                                1, 2, 3,
                                4, 5, 6,
                                7, 2, 4))
                        .build();

                IntGrid edited = grid.edit()
                        .findReplace(2, 4, 9)
                        .build();

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        int original = grid.get(col, row);
                        int expected = (original >= 2 && original <= 4) ? 9 : original;
                        assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(expected);
                    }
                }
            }

            @Test
            void findReplaceReturnsSameGridWhenNoCellsAreReplaced() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                IntGrid edited = grid.edit()
                        .findReplace(1, 2, 5)
                        .build();

                assertThat(edited).isEqualTo(grid);
            }

            @Test
            void findReplaceOnEmptyGridDoesNothing() {
                IntGrid empty = IntGrid.builder()
                        .withDimensions(0, 0)
                        .withCells(io.vavr.collection.Vector.empty())
                        .build();

                IntGrid edited = empty.edit()
                        .findReplace(0, 10, 5)
                        .build();

                assertThat(edited.getCols()).isEqualTo(0);
                assertThat(edited.getRows()).isEqualTo(0);
            }

            @Test
            void findReplaceHandlesSingleCellGrid() {
                IntGrid single = IntGrid.builder()
                        .withDimensions(1, 1)
                        .withCells(io.vavr.collection.Vector.of(3))
                        .build();

                IntGrid edited = single.edit()
                        .findReplace(3, 3, 7)
                        .build();

                assertThat(edited.get(0, 0)).isEqualTo(7);
            }

            @Test
            void findReplaceWithMinGreaterThanMaxThrowsException() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .findReplace(5, 2, 3)
                                .build()).withMessage("Minimum search value cannot be greater than maximum search value");
            }
        }

        @Nested
        class FloodFillTests {

            @Test
            void floodFillRequiresNonNullParameters() {
                IntGrid grid = IntGrid.builder().build();

                // Position overload requires non-null position
                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .floodFill(null, 0, 0, 1)
                                .build())
                        .withMessage("Position is required");

                // int overload requires non-null eligible and fill values
                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .floodFill(0, 0, null, 1, 2)
                                .build())
                        .withMessage("Minimum eligible value is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .floodFill(0, 0, 1, null, 2)
                                .build())
                        .withMessage("Maximum eligible value is required");

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .floodFill(0, 0, 1, 2, null)
                                .build())
                        .withMessage("Fill value is required");
            }

            @Test
            void floodFillWithNegativeValuesThrowsException() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .floodFill(0, 0, -1, 2, 3)
                                .build()).withMessage("Eligible values must be non-negative");

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .floodFill(0, 0, 1, -2, 3)
                                .build()).withMessage("Eligible values must be non-negative");

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .floodFill(0, 0, 1, 2, -3)
                                .build()).withMessage("Fill value must be non-negative");
            }

            @Test
            void floodFillWithMinGreaterThanMaxThrowsException() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .floodFill(0, 0, 5, 2, 9)
                                .build()).withMessage("Minimum eligible value cannot be greater than maximum eligible value");
            }

            @Test
            void floodFillRequiresFillValueOutsideEligibleRange() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.of(
                                0, 1, 0,
                                1, 0, 1,
                                0, 1, 0))
                        .build();

                // fillValue inside eligible range should be rejected
                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .floodFill(1, 1, 0, 2, 1) // 1 is inside [0,2]
                                .build()).withMessage("Fill value must be outside the eligible value range");
            }

            @Test
            void floodFillStartingPositionOutOfBoundsThrowsException() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
                        grid.edit()
                                .floodFill(-1, 0, 0, 0, 5)
                                .build()).withMessage("Starting position is out of bounds");
            }

            @Test
            void floodFillReplacesConnectedEligibleCells() {
                // 0 = eligible (empty), 1 = wall (not eligible)
                Vector<Integer> cells = io.vavr.collection.Vector.of(
                        0, 0, 1, 0,
                        0, 1, 1, 0,
                        0, 0, 1, 0,
                        1, 0, 1, 0
                );

                IntGrid grid = IntGrid.builder()
                        .withDimensions(4, 4)
                        .withCells(cells)
                        .build();

                IntGrid edited = grid.edit()
                        .floodFill(0, 0, 0, 0, 9) // replace eligible (0) with 9
                        .build();

                // Expected replaced coordinates (connected region starting at 0,0)
                int[][] expectedReplaced = {
                        {0,0},{1,0},{0,1},{0,2},{1,2},{1,3}
                };

                for (int row = 0; row < 4; row++) {
                    for (int col = 0; col < 4; col++) {
                        boolean shouldBeReplaced = false;
                        for (int[] p : expectedReplaced) {
                            if (p[0] == col && p[1] == row) {
                                shouldBeReplaced = true;
                                break;
                            }
                        }
                        int expected = shouldBeReplaced ? 9 : grid.get(col, row);
                        assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(expected);
                    }
                }
            }

            @Test
            void floodFillReturnsGridWithASingleCellReplacedIfNoValuesMatch() {
                // grid with no eligible cells (all walls = 1)
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 1))
                        .build();
                IntGrid expectedGrid = IntGrid.builder().withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 1).update(4, 2))
                        .build();

                IntGrid edited = grid.edit()
                        .floodFill(1, 1, 0, 0, 2)
                        .build();

                assertThat(edited).isEqualTo(expectedGrid);
            }
        }

        @Nested
        class DrawRectangleTests {

            @Test
            void drawRectangleRequiresNonNullValue() {
                IntGrid grid = IntGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawRectangle(0, 0, 1, 1, null)
                                .build())
                        .withMessage("Fill value is required");
            }

            @Test
            void drawRectangleRequiresNonNegativeValue() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawRectangle(0, 0, 1, 1, -1)
                                .build())
                        .withMessage("Fill value must be non-negative");
            }

            @Test
            void drawRectangleThrowsIfStartingPositionOutOfBounds() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawRectangle(4, 4, 2, 2, 1)
                                .build())
                        .withMessage("Starting position is out of bounds");
            }

            @Test
            void drawRectangleReturnsNewGridInstanceWithRectangleDrawn() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(5, 5)
                        .withCells(io.vavr.collection.Vector.fill(25, 0))
                        .build();

                IntGrid edited = grid.edit()
                        .drawRectangle(1, 1, 3, 3, 7)
                        .build();

                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 5; col++) {
                        if (col >= 1 && col < 1 + 3 && row >= 1 && row < 1 + 3) {
                            assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(7);
                        } else {
                            assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(grid.get(col, row));
                        }
                    }
                }
            }

            @Test
            void drawRectangleReturnsSameGridIfNoCellsAreChanged() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 5))
                        .build();

                IntGrid edited = grid.edit()
                        .drawRectangle(0, 0, 2, 2, 5)
                        .build();

                assertThat(edited).isEqualTo(grid);
            }

            @Test
            void drawRectangleHandlesSingleCellRectangle() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 0))
                        .build();

                IntGrid edited = grid.edit()
                        .drawRectangle(1, 2, 1, 1, 9)
                        .build();

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        if (col == 1 && row == 2) {
                            assertThat(edited.get(col, row)).isEqualTo(9);
                        } else {
                            assertThat(edited.get(col, row)).isEqualTo(grid.get(col, row));
                        }
                    }
                }
            }

            @Test
            void drawRectangleHandlesFullGridRectangle() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                IntGrid edited = grid.edit()
                        .drawRectangle(0, 0, 2, 2, 4)
                        .build();

                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 2; col++) {
                        assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(4);
                    }
                }
            }

            @Test
            void drawRectangleClipsWhenSizeExceedsBounds() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(4, 4)
                        .withCells(io.vavr.collection.Vector.fill(16, 0))
                        .build();

                // start inside bounds but width/height extend beyond grid — should clip to grid
                IntGrid edited = grid.edit()
                        .drawRectangle(2, 2, 5, 5, 3)
                        .build();

                for (int row = 0; row < 4; row++) {
                    for (int col = 0; col < 4; col++) {
                        if (col >= 2 && row >= 2) {
                            assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(3);
                        } else {
                            assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(grid.get(col, row));
                        }
                    }
                }
            }

            @Test
            void drawRectangleWithNonPositiveSizeThrows() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawRectangle(1, 1, 0, 2, 1)
                                .build())
                        .withMessage("Width and height must be positive");

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawRectangle(1, 1, -1, 2, 1)
                                .build())
                        .withMessage("Width and height must be positive");
            }
        }

        @Nested
        class DrawCircleTests {

            @Test
            void drawCircleRequiresNonNullValue() {
                IntGrid grid = IntGrid.builder().build();

                assertThatExceptionOfType(NullPointerException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawCircle(2, 2, 2, null)
                                .build())
                        .withMessage("Fill value is required");
            }

            @Test
            void drawCircleRequiresNonNegativeValue() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(2, 2)
                        .withCells(io.vavr.collection.Vector.fill(4, 0))
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawCircle(1, 1, 2, -1)
                                .build())
                        .withMessage("Fill value must be non-negative");
            }

            @Test
            void drawCircleThrowsIfCenterOutOfBounds() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawCircle(5, 5, 2, 1)
                                .build())
                        .withMessage("Center position is out of bounds");
            }

            @Test
            void drawCircleReturnsNewGridInstanceWithCircleDrawn() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(6, 6)
                        .withCells(io.vavr.collection.Vector.fill(36, 0))
                        .build();

                int centerCol = 2;
                int centerRow = 3;
                int radius = 2;
                int fillValue = 7;
                int threshold = radius * radius + radius; // matches implementation

                IntGrid edited = grid.edit()
                        .drawCircle(centerCol, centerRow, radius, fillValue)
                        .build();

                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 6; col++) {
                        int dx = col - centerCol;
                        int dy = row - centerRow;
                        boolean inside = (dx * dx + dy * dy) <= threshold;
                        int expected = inside ? fillValue : grid.get(col, row);
                        assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(expected);
                    }
                }
            }

            @Test
            void drawCircleReturnsSameGridIfNoCellsAreChanged() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(3, 3)
                        .withCells(io.vavr.collection.Vector.fill(9, 5))
                        .build();

                IntGrid edited = grid.edit()
                        .drawCircle(1, 1, 2, 5)
                        .build();

                assertThat(edited).isEqualTo(grid);
            }

            @Test
            void drawCircleWithNonPositiveRadiusThrows() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(4, 4)
                        .build();

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawCircle(1, 1, 0, 1)
                                .build())
                        .withMessage("Radius must be positive");

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> grid.edit()
                                .drawCircle(1, 1, -3, 1)
                                .build())
                        .withMessage("Radius must be positive");
            }

            @Test
            void drawCircleHandlesLargeRadiusGracefully() {
                IntGrid grid = IntGrid.builder()
                        .withDimensions(4, 4)
                        .withCells(io.vavr.collection.Vector.fill(16, 0))
                        .build();

                IntGrid edited = grid.edit()
                        .drawCircle(2, 2, 10, 9)
                        .build();

                for (int row = 0; row < 4; row++) {
                    for (int col = 0; col < 4; col++) {
                        assertThat(edited.get(col, row)).as("(%d,%d)", col, row).isEqualTo(9);
                    }
                }
            }
        }
    }
}