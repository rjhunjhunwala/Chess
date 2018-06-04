package artificialintelligence;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.imageio.ImageIO;

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
	public static final int QUEEN_VALUE = 1180;
	/**
	 * The value of a rook
	 */
	public static final int ROOK_VALUE = 660;
	/**
	 * The Bishop's value, let us consider a bishop to be slightly better than a
	 * knight
	 */
	public static final int BISHOP_VALUE = 430;
	/**
	 * The knight's value
	 */
	public static final int KNIGHT_VALUE = 400;
	/**
	 * Base value of a pawn, later gets more points for being higher
	 */
	public static final int PAWN_VALUE = 70;

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
	 * All right, this one deserves some justification... A "Ghost pawn, is the way
	 * we'll handle en passant" For the turn after (and just the turn after) a pawn
	 * moves up two, we leave a ghost pawn, in it's square That way, the board
	 * knows that square is valid for an en-passant capture
	 */
	public static final int GHOST_PAWN = 1<<4;

	/**
	 * These values are values of flag bits
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
	 * Is unmoved bit location
	 */
	public static final int UNMOVED_BIT_INDEX = 4;

	public static final int UNMOVED = 1;
	public static final int MOVED = 0;
	/**
	 *
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

	public static final int[] PAWN_VALUE_TABLE = {0, 30, 40, 50, 100, 130, 160};

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
		Chess.setTileAtSpot(state, 0, ROOK + (BLACK << 3) + (UNMOVED << 4));
		Chess.setTileAtSpot(state, 7, ROOK + (BLACK << 3) + (UNMOVED << 4));
		Chess.setTileAtSpot(state, 1, KNIGHT + (BLACK << 3));
		Chess.setTileAtSpot(state, 6, KNIGHT + (BLACK << 3));
		Chess.setTileAtSpot(state, 2, BISHOP + (BLACK << 3));
		Chess.setTileAtSpot(state, 5, BISHOP + (BLACK << 3));
		Chess.setTileAtSpot(state, 4, KING + (BLACK << 3) + (UNMOVED << 4));
		Chess.setTileAtSpot(state, 3, QUEEN + (BLACK << 3));

		Chess.setTileAtSpot(state, 56, ROOK + (UNMOVED << 4));
		Chess.setTileAtSpot(state, 63, ROOK + (UNMOVED << 4));
		Chess.setTileAtSpot(state, 57, KNIGHT);
		Chess.setTileAtSpot(state, 62, KNIGHT);
		Chess.setTileAtSpot(state, 58, BISHOP);
		Chess.setTileAtSpot(state, 61, BISHOP);
		Chess.setTileAtSpot(state, 60, KING + (UNMOVED << 4));
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
		return (int) ((((state[spot >> 3]) & ((long) 255 << ((spot & 7) << 3))) >> ((spot & 7) << 3))&15);
	}
	

	public int getTileAtSpotSpecial(int spot){
		return (int) ((((state[spot >> 3]) & ((long) 255 << ((spot & 7) << 3))) >> ((spot & 7) << 3)));
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
	 * A heuristic way of evaluating a board First, we take the difference of the
	 * computers values and the opponents, next, we add bonuses/penalties for
	 * center ownership and control
	 *
	 * @return the value of a board
	 */
	@Override
	public int getValue() {
		int value = 0;
		for (int i = 0; i < 64; i++) {
			int piece = getTileAtSpot(i);
			value += (2 * (((piece & 8) >> 2) - 1)) * (VALUES[piece & 7]);

			if ((piece & 7) == PAWN) {
				if ((piece & 8) == 8) {
					value += PAWN_VALUE_TABLE[(i >> 3)];
				} else {
					value -= PAWN_VALUE_TABLE[(~(i >> 3)) & 7];
				}
			}
		}
		value += (2 * (((getTileAtSpot(27) & 8) >> 2) - 1)) * (35);
		value += (2 * (((getTileAtSpot(28) & 8) >> 2) - 1)) * (35);
		value += (2 * (((getTileAtSpot(35) & 8) >> 2) - 1)) * (35);
		value += (2 * (((getTileAtSpot(36) & 8) >> 2) - 1)) * (35);

		//incentivize castling
		if (((getTileAtSpot(2) & 7) == KING) && ((getTileAtSpot(3) & 7) == ROOK)) {
			value += 90;
		}
		if ((getTileAtSpot(6) & 7) == KING && ((getTileAtSpot(5) & 7) == ROOK)) {
			value += 135;
		}

		return value;
	}

	/**
	 * List of current rule simplifications NO En Passant, NO under-promotion
	 *
	 * I apologize in advance for this absurd method. The repetition, is
	 * technically for performance.
	 *
	 * @param isComputerMove
	 * @return
	 */
	@Override
	public LinkedList<Integer> getPossibleMoves(boolean isComputerMove) {
		return getPossibleMoves(isComputerMove, true);
	}

	/**
	 * I need a second version of this method to avoid an infinite loop when both
	 * sides can castle
	 *
	 * @param isComputerMove
	 * @param considerKing
	 * @return
	 */
	public LinkedList<Integer> getPossibleMoves(boolean isComputerMove, boolean considerKing) {
		LinkedList<Integer> toRet = new LinkedList<>();
		for (int i = 0; i < 64; i++) {
			int piece = getTileAtSpot(i);
			int side;
			int b;
			//if this is a piece that can be moved
			if (((side = (piece & 8)) == 8) == (isComputerMove)) {
				switch (piece & 7) {
					case PAWN:
						//Computer pawn... can be double moved
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
						int end = i + (a << 3);

						if (getTileAtSpot(end) == 0) {
							toRet.add((end << 6) + i);
						}
int specialTarget;
						if ((i & 7) > 0) {

							int leftDiagonalCapture = getTileAtSpot(end - 1);
							//when checking for control of squares (for castling check), pretend pawns can diagonal move
							//Also, allow pawns to capture en passant, yes I do know it's ugly
							if ((leftDiagonalCapture != 0 && (leftDiagonalCapture & 8) != (side)) || !considerKing 
															|| (((specialTarget=getTileAtSpotSpecial(end - 1))&GHOST_PAWN)!=0&&((side)!=((specialTarget&32)>>2)))) {
								toRet.add(((end - 1) << 6) + i);
							}
						}
						if ((i & 7) < 7) {
							int leftDiagonalCapture = getTileAtSpot(end + 1);
							//same justification
							if ((leftDiagonalCapture != 0 && (leftDiagonalCapture & 8) != (side)) || !considerKing
															|| (((specialTarget=getTileAtSpotSpecial(end + 1))&GHOST_PAWN)!=0&&((side)!=((specialTarget&32)>>2)))) {
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
						if (!considerKing) {
							break;
						}
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

						//Wait... You're not done yet! Castling
						//if the king is unmoved
						if ((getTileAtSpotSpecial(i) & 16) > 0) {
							//if it's the computer's king
							boolean inCheck = false;
							LinkedList<Integer> opponentAttacks = this.getPossibleMoves(!isComputerMove, false);
							int spotOfKing = 60 - 7 * side;
							for (int someInt : opponentAttacks) {
								if ((someInt >> 6) == spotOfKing) {
									inCheck = true;
									break;
								}
							}
							if (!inCheck) {

								if (side == 8) {
									//if the queenside rook is unmoved
									if ((getTileAtSpotSpecial(0) & 16) > 0 && getTileAtSpot(1) == 0 && getTileAtSpot(2) == 0 && getTileAtSpot(3) == 0) {
										inCheck = false;
										//check castling queen
										for (int someInt : opponentAttacks) {
											if ((someInt >> 6) == 3) {
												inCheck = true;
												break;
											}
										}
										if (!inCheck) {
											toRet.add((1 << 12) + (2 << 6) + i);
										}
									}
									if ((getTileAtSpotSpecial(7) & 16) > 0 && getTileAtSpot(5) == 0 && getTileAtSpot(6) == 0) {
										//check castling king
										for (int someInt : opponentAttacks) {
											inCheck = false;
											if ((someInt >> 6) == 5) {
												inCheck = true;
												break;
											}
										}
										if (!inCheck) {
											toRet.add((1 << 12) + (6 << 6) + i);
										}
									}
								} else /*Human castling*/ {
									if ((getTileAtSpotSpecial(56) & 16) > 0 && getTileAtSpot(57) == 0 && getTileAtSpot(58) == 0 && getTileAtSpot(59) == 0) {
										inCheck = false;
										//check castling queen
										for (int someInt : opponentAttacks) {
											if ((someInt >> 6) == 59) {
												inCheck = true;
												break;
											}
										}
										if (!inCheck) {

											toRet.add((1 << 12) + (58 << 6) + i);
										}
									}
									if ((getTileAtSpotSpecial(63) & 16) > 0 && getTileAtSpot(62) == 0 && getTileAtSpot(61) == 0) {
										inCheck = false;
										//check castling king
										for (int someInt : opponentAttacks) {
											if ((someInt >> 6) == 61) {
												inCheck = true;
												break;
											}
										}
										if (!inCheck) {
											//System.out.println("Can king Side Castle");
											toRet.add((1 << 12) + (62 << 6) + i);
											//System.out.println(toRet.getLast());
										}
									}
								}
							}//End in Check 
						}//End castling

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
		int end = (move >> 6) & 63;
		boolean cleanUpGhosts = true;
		//"performance"
		long[] newState = {state[0], state[1], state[2], state[3],
			state[4], state[5], state[6], state[7]};
		if ((move & (1 << 12)) > 0) {

			switch (end) {
				case 2:
					Chess.setTileAtSpot(newState, 0, 0);
					Chess.setTileAtSpot(newState, 3, ROOK + 24);
					break;
				case 6:
					Chess.setTileAtSpot(newState, 7, 0);
					Chess.setTileAtSpot(newState, 5, ROOK + 24);
					break;
				case 58:
					Chess.setTileAtSpot(newState, 56, 0);
					Chess.setTileAtSpot(newState, 59, ROOK + 16);
					break;
				case 62:
					Chess.setTileAtSpot(newState, 63, 0);
					Chess.setTileAtSpot(newState, 61, ROOK + 16);
			}
		} else {

			if ((tile & 7) == PAWN) {
				//	Sketchy pawn promotion hack
				if (((end >> 3) == 0 || ((end >> 3) == 7))) {
					tile += (QUEEN - PAWN);
				}
				//double move pawn
if(end - start == 16){
	Chess.setTileAtSpot(newState, end - 8,  (1 << 5) + (GHOST_PAWN));
	cleanUpGhosts = false;
}else if(start - end == 16){
	Chess.setTileAtSpot(newState, end + 8,  + (GHOST_PAWN));
cleanUpGhosts = false;
}
int specialTile;
if(((specialTile = getTileAtSpotSpecial(end))&31)==16){
	int d = ((specialTile & 32) >> 4) - 1;
//System.out.println(specialTile); 
Chess.setTileAtSpot(newState, end + (d<<3), 0);
}
				//Start of sketchy en passant kludge
						//end en passant kludge
			
			}

	
		}

		Chess.setTileAtSpot(newState, start, 0);
		Chess.setTileAtSpot(newState, end, tile & 15);
		for(int i = 0;i<64;i++){
	if((getTileAtSpotSpecial(i)&31)==16){
		if(end != i){
			Chess.setTileAtSpot(newState, i, 0);
		}
	}
}
		return new Chess(newState);
	}

//Chess Graphics stuff
	//These images have been used from the public domain
	//https://creativecommons.org/licenses/by-sa/3.0/
	public static final int SOURCE_SIZE = 45;

	public static void main(String[] args) throws IOException {
		BufferedImage b = ImageIO.read(new File("WHITE_PAWN.png"));
		System.out.print("public static final long[] PAWN_SPRITE = new long[]{");
		for (int i = 0; i < SOURCE_SIZE; i++) {
			long output = 0;
			for (int j = 0; j < SOURCE_SIZE; j++) {

				Color thisColor = new Color(b.getRGB(j, i));
				if (thisColor.getGreen() + thisColor.getRed() + thisColor.getGreen() > 100) {
					output += ((long) 1) << j;
				}
			}
			System.out.print(output + "l,");
		}
		System.out.println("};");
	}
	/**
	 * All right, here's where it gets questionable.... What's a good data-type to
	 * store an image? Think about it for a bit? Have an answer? You're wrong. We
	 * use an array of longs, to store a serialized image. each line represents one
	 * line of the image, and each bit in the long represents whether or not that
	 * pixel is active...
	 */
	public static final long[] KING_SPRITE = new long[]{0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 4194304l, 14680064l, 32505856l, 32505856l, 15064897536l, 68483612160l, 137404612352l, 274859556736l, 274860081024l, 549745328064l, 549749522368l, 274873712576l, 274873712512l, 137434759040l, 68715282176l, 34344009216l, 16172988416l, 1073725440l, 4294963200l, 4026593280l, 2684325888l, 2147475456l, 4279234560l, 3288084480l, 1073725440l, 2147475456l, 268369920l, 0l, 0l, 0l, 0l, 0l, 0l,};
	public static final long[] QUEEN_SPRITE = new long[]{0l, 0l, 0l, 0l, 0l, 0l, 14680064l, 14704640l, 3235966976l, 3221250048l, 0l, 824633720928l, 824633720928l, 0l, 4194304l, 1077985280l, 1616953344l, 1625341952l, 70613319936l, 104973058816l, 122153060096l, 53567800832l, 62175563264l, 66537905664l, 64961486336l, 268304384l, 34359737344l, 34359737344l, 17179867136l, 0l, 4294959104l, 4294959104l, 4228374528l, 1073709056l, 8589930496l, 17179867136l, 17179867136l, 17179867136l, 1073709056l, 0l, 0l, 0l, 0l, 0l, 0l,};
	public static final long[] ROOK_SPRITE = new long[]{0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 16138663936l, 16138663936l, 17179867136l, 17179867136l, 17179867136l, 8589930496l, 4294959104l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 2147467264l, 4294959104l, 8589930496l, 8589930496l, 8589930496l, 8589930496l, 68719476224l, 68719476224l, 68719476224l, 0l, 0l, 0l, 0l, 0l, 0l,};
	public static final long[] BISHOP_SPRITE = new long[]{0l, 0l, 0l, 0l, 0l, 0l, 14680064l, 14680064l, 14680064l, 14680064l, 0l, 14680064l, 66584576l, 133955584l, 268304384l, 532611072l, 1069514752l, 1069514752l, 2143272960l, 2143272960l, 2143272960l, 2147467264l, 2147467264l, 1073709056l, 536805376l, 133955584l, 133955584l, 536805376l, 536805376l, 536805376l, 536805376l, 1073709056l, 268304384l, 0l, 0l, 32505856l, 68719476224l, 274863226752l, 0l, 0l, 0l, 0l, 0l, 0l, 0l,};
	public static final long[] KNIGHT_SPRITE = new long[]{0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 1064960l, 3719168l, 8372224l, 134201344l, 536854528l, 1073733632l, 4294914048l, 8589905920l, 8589907968l, 17179868160l, 17179868160l, 34351349248l, 34351349248l, 68706893568l, 68704796416l, 68703747968l, 137431089024l, 137430695040l, 137430630336l, 137434791808l, 137434772352l, 137436860672l, 137437904896l, 137438429184l, 274877644800l, 274877644800l, 274877775872l, 274877841408l, 274877841408l, 274877841408l, 137438887936l, 0l, 0l, 0l, 0l, 0l, 0l,};
	public static final long[] PAWN_SPRITE = new long[]{0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 0l, 6291456l, 33030144l, 33030144l, 66846720l, 66846720l, 33030144l, 33030144l, 66846720l, 134086656l, 134086656l, 268369920l, 268369920l, 268369920l, 268369920l, 268369920l, 134086656l, 66846720l, 66846720l, 268369920l, 536838144l, 1073725440l, 2147475456l, 2147475456l, 4294963200l, 4294963200l, 4294963200l, 8589932544l, 8589932544l, 8589932544l, 8589932544l, 0l, 0l, 0l, 0l, 0l, 0l,};
	public static final long[] EMPTY_SPRITE = new long[SOURCE_SIZE];

	public static long[][] SPRITES = new long[][]{EMPTY_SPRITE, PAWN_SPRITE, KNIGHT_SPRITE, BISHOP_SPRITE, ROOK_SPRITE, QUEEN_SPRITE, KING_SPRITE};

}
