package amazons;

import java.util.Iterator;

import static java.lang.Math.*;

import static amazons.Piece.*;

/**
 * A Player that automatically generates moves.
 *
 * @author Amol Pant
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * Value/score for the score for a minimax tree.
     */
    private int score;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            if (b.numMoves() < 5) {
                moveRandom(b);
            } else {
                findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
            }
        } else {
            if (b.numMoves() < 5) {
                moveRandom(b);
            } else {
                findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
            }
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        if (sense == 1) {
            int maxEval = -INFTY;
            Iterator<Move> iter = board.legalMoves();
            while (iter.hasNext()) {
                Move movement = iter.next();
                if (saveMove && (_lastFoundMove == null)) {
                    _lastFoundMove = movement;
                }
                Board b2 = new Board(board);
                int eval = findMove(b2, depth - 1, false, -sense, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                if (maxEval == eval) {
                    if (saveMove) {
                        _lastFoundMove = movement;
                    }
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = INFTY;
            Iterator<Move> iter = board.legalMoves();
            while (iter.hasNext()) {
                Move movement = iter.next();
                if (saveMove && (_lastFoundMove == null)) {
                    _lastFoundMove = movement;
                }
                Board b2 = new Board(board);
                int eval = findMove(b2, depth - 1, false, -sense, alpha, beta);
                minEval = Math.min(minEval, eval);
                if (minEval == eval) {
                    if (saveMove) {
                        _lastFoundMove = movement;
                    }
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    /**
     * Moves a random place.
     * @param board Is the board.
     */
    private void moveRandom(Board board) {
        Iterator<Move> iter = board.legalMoves();
        Move mvmt = iter.next();
        int rand = _controller.randInt(100);
        for (int i = 0; i < rand; i += 1) {
            if (iter.hasNext()) {
                mvmt = iter.next();
            }
        }
        _lastFoundMove = mvmt;
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N < 5) {
            return 1;
        }
        return (int) (log(N) / log(3));
    }


    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        Iterator<Move> whiteMoves = board.legalMoves(WHITE);
        int whiteScore = 0;
        while (whiteMoves.hasNext()) {
            Move whiteMovement = whiteMoves.next();
            whiteScore += 1;
        }
        Iterator<Move> blackMoves = board.legalMoves(BLACK);
        int blackScore = 0;
        while (blackMoves.hasNext()) {
            Move blackMovement = blackMoves.next();
            blackScore += 1;
        }
        return whiteScore - blackScore;
    }


}
