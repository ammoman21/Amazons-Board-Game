package amazons;

import org.junit.Test;

import static org.junit.Assert.*;

import ucb.junit.textui;

import java.util.Iterator;

import static amazons.Piece.*;

/**
 * The suite of all JUnit tests for the enigma package.
 *
 * @author Amol Pant
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class, SquareTest.class,
                BoardTest.class, IteratorTests.class);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /**
     * Tests toString for initial board state and a smiling board state. :)
     */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";


    @Test
    public void squareTests() {
        Square square = Square.sq(54);
        Square toDiagonal = Square.sq(65);
        Square invalid = Square.sq(28);
        assertEquals(square.isQueenMove(Square.sq(toDiagonal.index())), true);
        assertFalse(square.isQueenMove(Square.sq(invalid.index())));
        assertEquals(square.queenMove(0, 3), Square.sq(84));
        assertEquals(square.queenMove(0, 6), null);
        Square up = Square.sq(64);
        Square down = Square.sq(24);
        Square ne = toDiagonal;
        Square nw = Square.sq(63);
        Square sw = Square.sq(10);
        Square se = Square.sq(36);
        Square left = Square.sq(51);
        Square right = Square.sq(57);
        assertEquals(square.direction(up), 0);
        assertEquals(square.direction(ne), 1);
        assertEquals(square.direction(right), 2);
        assertEquals(square.direction(se), 3);
        assertEquals(square.direction(down), 4);
        assertEquals(square.direction(sw), 5);
        assertEquals(square.direction(left), 6);
        assertEquals(square.direction(nw), 7);
        assertEquals(Square.sq(63), Square.sq("d7"));
    }

    @Test
    public void boardTests() {
        Board b = new Board();
        b.makeMove(Square.sq(03), Square.sq(23), Square.sq(33));
        boolean a = b.isLegal(Square.sq(23));
        boolean c = b.isUnblockedMove(Square.sq(23),
                Square.sq(23).queenMove(1, 1), null);
        Iterator<Square> x = b.reachableFrom(Square.sq(23), null);
        Iterator<Move> y = b.legalMoves();
        b.makeMove(Square.sq(93), Square.sq(83), Square.sq(73));
        boolean binFalse = b.isUnblockedMove(Square.sq(23),
                Square.sq(43), null);
        assertFalse(binFalse);
        boolean binTrue = b.isUnblockedMove(Square.sq(23), Square.sq(34), null);
        assertTrue(binTrue);
    }

}



