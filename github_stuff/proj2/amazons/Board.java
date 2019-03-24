package amazons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import static amazons.Piece.BLACK;
import static amazons.Piece.WHITE;
import static amazons.Piece.SPEAR;
import static amazons.Piece.EMPTY;

/**
 * The state of an Amazons Game.
 *
 * @author Amol Pant
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        this.board = model.board.clone();
        this._turn = model.turn();
        this._winner = model._winner;
        this._moves = new ArrayList<>(model._moves);
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _turn = WHITE;
        _winner = EMPTY;
        board = new Piece[10][10];
        for (int i = 0; i < board.length; i += 1) {
            for (int j = 0; j < board[i].length; j += 1) {
                board[i][j] = EMPTY;
            }
        }
        put(WHITE, 0, 3);
        put(WHITE, 9, 3);
        put(WHITE, 3, 0);
        put(WHITE, 6, 0);
        put(BLACK, 3, 9);
        put(BLACK, 6, 9);
        put(BLACK, 0, 6);
        put(BLACK, 9, 6);
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return _moves.size();
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        Iterator<Move> myMoves = this.legalMoves(this.turn());
        Iterator<Move> oppMoves = this.legalMoves(this.turn().opponent());
        if (!myMoves.hasNext()) {
            return turn().opponent();
        } else if (!oppMoves.hasNext()) {
            return turn();
        } else {
            return null;
        }
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return board[col][row];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        board[col][row] = p;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            return false;
        }
        int steps = Math.max(Math.abs(from.col() - to.col()),
                Math.abs(from.row() - to.row()));
        if (steps == 0) {
            return false;
        }
        int dir = from.direction(to);
        for (int i = 1; i <= steps; i += 1) {
            Square square = from.queenMove(dir, i);
            if (square != asEmpty) {
                if (get(square) != EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        if (get(from) != turn()) {
            return false;
        }
        return Square.exists(from.col(), from.row());
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        if (!isUnblockedMove(from, to, null)) {
            return false;
        }
        return isLegal(from);
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        if (!isLegal(from, to)) {
            return false;
        }
        if (!isUnblockedMove(to, spear, from)) {
            return false;
        }
        return Square.exists(spear.col(),
                spear.row())
                && (board[spear.col()][spear.row()]) != null;
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        Piece temp = get(from);
        put(temp, to);
        put(EMPTY, from);
        put(SPEAR, spear);
        _turn = turn().opponent();
        _moves.add(Move.mv(from, to, spear));
        _winner = this.winner();
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (_moves.size() < 1) {
            return;
        }
        Move lastMove = _moves.get(numMoves() - 1);
        _moves.remove(numMoves() - 1);
        Piece temp = get(lastMove.to());
        put(temp, lastMove.from());
        put(EMPTY, lastMove.to());
        put(EMPTY, lastMove.spear());
        _turn = turn().opponent();
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            assert hasNext();
            Square nextOne = _from.queenMove(_dir, _steps);
            toNext();
            return nextOne;
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            while (_dir < 8) {
                _steps += 1;
                if (Square.exists(_from.col(), _from.row())
                        && isUnblockedMove(_from, _from.queenMove(
                        _dir, _steps), _asEmpty)) {
                    break;
                } else if (_steps >= SIZE) {
                    _steps = 0;
                    _dir += 1;
                }


            }
        }

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            int point = 0;
            for (int i = ((SIZE * SIZE) - 1); i >= 0; i -= 1) {
                if (get(Square.sq(i)) == _fromPiece) {
                    point = i;
                    break;
                }
            }
            if (_spearThrows.hasNext() || _pieceMoves.hasNext()) {
                return true;
            }
            if (_start.index() < point) {
                return true;
            }
            return false;
        }

        @Override
        public Move next() {
            if (_spearThrows.hasNext()) {
                Square curr = _spearThrows.next();
                if (curr != null) {
                    Move m = Move.mv(_start, _nextSquare, curr);
                    toNext();
                    return m;
                }
            }
            return null;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            if (!_spearThrows.hasNext()) {
                if (!_pieceMoves.hasNext()) {
                    if (!_startingSquares.hasNext()) {
                        return;
                    } else {
                        while (_startingSquares.hasNext()) {
                            Square curr = _startingSquares.next();
                            if (get(curr).equals(_fromPiece)) {
                                _start = curr;
                                _pieceMoves = reachableFrom(_start, null);
                                if (_pieceMoves.hasNext()) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (_pieceMoves.hasNext()) {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                }
            }
        }

        /**
         * Color of side whose moves we are iterating.
         */
        private Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String boardString = "";
        for (int i = SIZE - 1; i >= 0; i -= 1) {
            boardString += "  ";
            for (int j = 0; j < SIZE; j += 1) {
                if (get(j, i).equals(EMPTY)) {
                    boardString += " -";
                }
                if (get(j, i).equals(WHITE)) {
                    boardString += " W";
                }
                if (get(j, i).equals(BLACK)) {
                    boardString += " B";
                }
                if (get(j, i).equals(SPEAR)) {
                    boardString += " S";
                }
            }
            boardString += "\n";
        }
        return boardString;
    }

    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;

    /**
     * The representation of the board.
     */
    private Piece[][] board;

    /**
     * The moves stored in a list.
     */
    private ArrayList<Move> _moves = new ArrayList<>();
}
