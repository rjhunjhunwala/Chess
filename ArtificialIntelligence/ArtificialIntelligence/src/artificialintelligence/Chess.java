package artificialintelligence;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The way we represent a chess board
 *
 * @author rohan
 */
public class Chess extends GenericBoardGame {

	/**
	 * Some constants to keep myself organized
	 */
	//======Start of Constants======
	/**
	 * The value of a king in Centi-pawns
	 */
	public static final int KING_VALUE = 1000000;
	/**
	 * The queen's value in centi-pawns
	 */
	public static final int QUEEN_VALUE = 910;
	/**
	 * The value of a rook
	 */
	public static final int ROOK_VALUE = 500;
	/**
	 * The Bishop's value, let us consider a bishop to be slightly better than a
	 * knight
	 */
	public static final int BISHOP_VALUE = 330;
	/**
	 * The knight's value
	 */
	public static final int KNIGHT_VALUE = 300;
	/**
	 * Logically, a pawn is worth 100/100 pawns
	 */
	public static final int PAWN_VALUE = 100;

	/**
	 * These constants are the constants used to represent pieces
	 */
	public static final int EMPTY = 0;
	public static final int PAWN = 1;
	public static final int KNIGHT = 2;
	public static final int BISHOP = 3;
	public static final int ROOK = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;

	/**
	 * These values are miscelaneuous (spelled incorrectly) values of flag bits
	 */
	/**
	 * The human is "white" even if they plays second
	 */
	public static final int WHITE = 0;
	/**
	 * The AI is "black" even if it plays second
	 */
	public static final int BLACK = 1;

	/**
	 * This array stores all the values for O(1) access
	 */
	public static final int[] VALUES = new int[7];

	static {
		VALUES[EMPTY] = 0;
		VALUES[PAWN] = PAWN_VALUE;
		VALUES[KNIGHT] = KNIGHT_VALUE;
		VALUES[BISHOP] = BISHOP_VALUE;
		VALUES[ROOK] = ROOK_VALUE;
		VALUES[QUEEN] = QUEEN_VALUE;
		VALUES[KING] = KING_VALUE;
	}
	//======End of Constants======

	/**
	 * Ok, so we store the state of a single chessboard as an array of longs This
	 * is for "performance" and "memory optimization" reasons (I think it helps but
	 * I can't prove it. Each long, represents one row. A long is 64 bits, so we
	 * can use around 8 bits to store each piece which should be "plenty"
	 */
	public long[] state;

	/**
	 * Creates a chess board with the standard legal starting position The AI is
	 * black
	 */
	public Chess() {
		state = new long[8];
		for (int i = 0; i < 8; i++) {
			Chess.setTileAtSpot(state, 8 + i, PAWN + (BLACK << 3));
			Chess.setTileAtSpot(state, 48 + i, PAWN);
		}
		Chess.setTileAtSpot(state, 0, ROOK + (BLACK << 3));
		Chess.setTileAtSpot(state, 7, ROOK + (BLACK << 3));
		Chess.setTileAtSpot(state, 1, KNIGHT + (BLACK << 3));
		Chess.setTileAtSpot(state, 6, KNIGHT + (BLACK << 3));
		Chess.setTileAtSpot(state, 2, BISHOP + (BLACK << 3));
		Chess.setTileAtSpot(state, 5, BISHOP + (BLACK << 3));
		Chess.setTileAtSpot(state, 4, KING + (BLACK << 3));
		Chess.setTileAtSpot(state, 3, QUEEN + (BLACK << 3));

		Chess.setTileAtSpot(state, 56, ROOK);
		Chess.setTileAtSpot(state, 63, ROOK);
		Chess.setTileAtSpot(state, 57, KNIGHT);
		Chess.setTileAtSpot(state, 62, KNIGHT);
		Chess.setTileAtSpot(state, 58, BISHOP);
		Chess.setTileAtSpot(state, 61, BISHOP);
		Chess.setTileAtSpot(state, 60, KING);
		Chess.setTileAtSpot(state, 59, QUEEN);

	}

	public int getSize() {
		return 8;
	}

	/**
	 * Create a board with an initial state
	 *
	 * @param inState the initialized state
	 */
	public Chess(long[] inState) {
		state = inState;
	}

	private static void setTileAtSpot(long[] inState, int spot, int tile) {
		inState[spot >> 3] = manipulateState(inState[spot >> 3], spot & 7, tile);
	}

	public static long manipulateState(long state, long spot, long tile) {
		return ((state & (~((((long) (255)) << ((spot << 3)))))) + (tile << (spot << 3)));

	}

	@Override
	public int getTileAtSpot(int spot) {
		return (int) (((state[spot >> 3]) & ((long) 255 << ((spot & 7) << 3))) >> ((spot & 7) << 3));
	}

	/**
	 * If either side has lost a king the game is over
	 *
	 * @return
	 */
	@Override
	public boolean isGameOver() {
		return Math.abs(this.getValue()) > KING_VALUE / 2;
	}

	/**
		* A heuristic way of evaluating a board
		* First, we take the difference of the computers values and the opponents, 
		* next, we add bonuses/penalties for center ownership and control
		* @return the value of a board
		*/
	@Override
	public int getValue() {
		int value = 0;
		for (int i = 0; i < 64; i++) {
			int piece = getTileAtSpot(i);
			value += (2 * (((piece & 8) >> 2) - 1)) * (VALUES[piece & 7]);
		}
		value += (2 * (((getTileAtSpot(27) & 8) >> 2) - 1)) * (45);
		value += (2 * (((getTileAtSpot(28) & 8) >> 2) - 1)) * (45);
		value += (2 * (((getTileAtSpot(35) & 8) >> 2) - 1)) * (45);
		value += (2 * (((getTileAtSpot(36) & 8) >> 2) - 1)) * (45);
		
		for(int move:this.getPossibleMoves(true)){
			int end = move>>6;
			if(end==27||end==28||end==35||end==36){
				value+=20;
			}
		}
		
				for(int move:this.getPossibleMoves(false)){
			int end = move>>6;
			if(end==27||end==28||end==35||end==36){
				value-=20;
			}
		}

		return value;
	}

	/**
	 * List of current rule simplifications NO Castling NO En Passant, NO
	 * under-promotion
	 *
	 * I apologize in advance for this absurd method. The repetition, is
	 * technically for performance.
	 *
	 * @param isComputerMove
	 * @return
	 */
	@Override
	public LinkedList<Integer> getPossibleMoves(boolean isComputerMove) {
		LinkedList<Integer> toRet = new LinkedList<>();
		for (int i = 0; i < 64; i++) {
			int piece = getTileAtSpot(i);
			int side;
			int b;
			//if this is a piece that can be moved
			if (((side = (piece & 8)) == 8) == (isComputerMove)) {
				switch (piece & 7) {
					case PAWN:
						//Comouter pawn... can be double moved
						if ((side == 8) && ((i >> 3) == 1)) {
							if (getTileAtSpot(i + 8) == 0 && getTileAtSpot(i + 16) == 0) {
								toRet.add(((i + 16) << 6) + i);
							}
						}
						//hostile pawn can be double moved
						if ((side == 0) && ((i >> 3) == 6)) {
							if (getTileAtSpot(i - 8) == 0 && getTileAtSpot(i - 16) == 0) {
								toRet.add(((i - 16) << 6) + i);
							}
						}
						int a = (side >> 2) - 1;
						int end = i + (a * 8);

						if (getTileAtSpot(end) == 0) {
							toRet.add((end << 6) + i);
						}

						if ((i & 7) > 0) {

							int leftDiagonalCapture = getTileAtSpot(end - 1);
							if (leftDiagonalCapture != 0 && (leftDiagonalCapture & 8) != (side)) {
								toRet.add(((end - 1) << 6) + i);
							}
						}
						if ((i & 7) < 7) {
							int leftDiagonalCapture = getTileAtSpot(end + 1);
							if (leftDiagonalCapture != 0 && (leftDiagonalCapture & 8) != (side)) {
								toRet.add(((end + 1) << 6) + i);
							}
						}
						break;
					case KNIGHT:

						//trust me, this was the most efficient way to handle this one...
						int x = i & 7;
						int y = i >> 3;

						if ((x + 2) < 8 && (y + 1) < 8) {
							end = (x + 2) + ((y + 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x + 1) < 8 && (y + 2) < 8) {
							end = (x + 1) + ((y + 2) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 1) >= 0 && (y + 2) < 8) {
							end = (x - 1) + ((y + 2) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 2) >= 0 && (y + 1) < 8) {
							end = (x - 2) + ((y + 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 2) >= 0 && (y - 1) >= 0) {
							end = (x - 2) + ((y - 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 1) >= 0 && (y - 2) >= 0) {
							end = (x - 1) + ((y - 2) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x + 1) < 8 && (y - 2) >= 0) {
							end = (x + 1) + ((y - 2) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x + 2) < 8 && (y - 1) >= 0) {
							end = (x + 2) + ((y - 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						break;

					case BISHOP:

						x = i & 7;
						y = i >> 3;

						for (int d = 1; d < 8; d++) {
							if ((x + d) < 8 && (y + d) < 8) {
								end = ((y + d) << 3) + (x + d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}

						for (int d = 1; d < 8; d++) {
							if ((x + d) < 8 && (y - d) >= 0) {
								end = ((y - d) << 3) + (x + d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((x - d) >= 0 && (y - d) >= 0) {
								end = ((y - d) << 3) + (x - d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((x - d) >= 0 && (y + d) < 8) {
								end = ((y + d) << 3) + (x - d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}

						break;
					case ROOK:
						x = i & 7;
						y = i >> 3;

						for (int d = 1; d < 8; d++) {
							if ((x + d) < 8) {
								end = ((y) << 3) + (x + d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}

						for (int d = 1; d < 8; d++) {
							if ((x - d) >= 0) {
								end = ((y) << 3) + (x - d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((y - d) >= 0) {
								end = ((y - d) << 3) + (x);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((y + d) < 8) {
								end = ((y + d) << 3) + (x);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						break;

					case QUEEN:
						//Sorry, you're almost through this method
						//yes, the following code is just both the bishop and rook code combined
						x = i & 7;
						y = i >> 3;

						for (int d = 1; d < 8; d++) {
							if ((x + d) < 8 && (y + d) < 8) {
								end = ((y + d) << 3) + (x + d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}

						for (int d = 1; d < 8; d++) {
							if ((x + d) < 8 && (y - d) >= 0) {
								end = ((y - d) << 3) + (x + d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((x - d) >= 0 && (y - d) >= 0) {
								end = ((y - d) << 3) + (x - d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((x - d) >= 0 && (y + d) < 8) {
								end = ((y + d) << 3) + (x - d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}

						for (int d = 1; d < 8; d++) {
							if ((x + d) < 8) {
								end = ((y) << 3) + (x + d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}

						for (int d = 1; d < 8; d++) {
							if ((x - d) >= 0) {
								end = ((y) << 3) + (x - d);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((y - d) >= 0) {
								end = ((y - d) << 3) + (x);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						for (int d = 1; d < 8; d++) {
							if ((y + d) < 8) {
								end = ((y + d) << 3) + (x);
								int thisTile = getTileAtSpot(end);
								if (thisTile == 0) {
									toRet.add((end << 6) + i);
								} else {
									if ((thisTile & 8) != side) {
										toRet.add((end << 6) + i);
									}
									break;
								}
							} else {
								break;
							}
						}
						break;
					case KING:
						//once again, unfortunately, this is the best way to handle the king
						x = i & 7;
						y = i >> 3;

						if ((x + 1) < 8 && (y + 1) < 8) {
							end = (x + 1) + ((y + 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x + 1) < 8) {
							end = (x + 1) + ((y) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x + 1) < 8 && (y - 1) >= 0) {
							end = (x + 1) + ((y - 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 1) >= 0 && (y + 1) < 8) {
							end = (x - 1) + ((y + 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 1) >= 0 && (y - 1) >= 0) {
							end = (x - 1) + ((y - 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((y + 1) < 8) {
							end = (x) + ((y + 1) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((x - 1) >= 0) {
							end = (x - 1) + ((y) << 3);
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}
						if ((y - 1) >= 0) {
							end = ((y - 1) << 3) + x;
							if (((b = getTileAtSpot(end)) & 8) != side || b == 0) {
								toRet.add((end << 6) + i);
							}
						}

						break;
				}
			}

		}

		return toRet;
	}

	/**
	 * The bottom 6 bits represent the initial spot, and the next 6 bits represent
	 * the final spot
	 *
	 * @param move
	 * @param isComputerTurn
	 * @return
	 */
	@Override
	public Board makeMove(int move, boolean isComputerTurn) {
		int tile = getTileAtSpot(move & 63);
		int start = move & 63;
		int end = move >> 6;
		//	Sketchy pawn promotion hack
		if ((tile & 7) == PAWN && ((end >> 3) == 0 || ((end >> 3) == 7))) {
			tile += (QUEEN - PAWN);
		}
		//"performance"
		long[] newState = {state[0], state[1], state[2], state[3],
			state[4], state[5], state[6], state[7]};
		Chess.setTileAtSpot(newState, start, 0);
		Chess.setTileAtSpot(newState, end, tile);
		return new Chess(newState);
	}

}
