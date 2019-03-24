package amazons;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SquareTest {

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
}
