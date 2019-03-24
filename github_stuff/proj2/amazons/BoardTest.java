package amazons;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {
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
