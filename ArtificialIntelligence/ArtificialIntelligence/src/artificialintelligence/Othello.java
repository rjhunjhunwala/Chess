package artificialintelligence;

import java.util.ArrayList;

/**
 * This class serves as a fancy decorator for an integer.
 *
 * @author Rohan
 */
public class Othello extends GenericBoardGame {

	public static final int STARTING_VALUE = 37773312;
	public static final int ALT_STARTING_VALUE = 25202688;

	/**
	 * Corners are counted as (C_B_W+1)*whoOccupiesIt
	 */
	public static final int CORNER_BONUS_WEIGHT = 2;

	/**
	 * The empty tile constant
	 */
	public static final int EMPTY = 0;
	/**
	 * The X Tile constant
	 */
	public static final int X_TILE = 1;
	/**
	 * The O tile Constant
	 */
	public static final int O_TILE = 2;

	/**
	 * Returns a manipulated version of "state" so that the given spot now has a
	 * given tile value
	 *
	 * @param state the original state
	 * @param spot the spot to be used (less than 15)
	 * @param tile the tile value (0 1 or 2)
	 * @return the new version of state
	 */
	public static long manipulateState(long state, long spot, long tile) {
		return (state & (~(((long) 3) << ((spot << 1))))) + (tile << (spot << 1));

	}

	/**
	 * The following integer is used to store the whole array as an integer
	 */
	private long state = 0;

	public Othello(long inState) {
		state = inState;
	}

	/**
	 * Get's the tile associated with a certain spot
	 *
	 * @param spot return the spot being used
	 * @return
	 */
	public int getTileAtSpot(int spot) {
		return (int) (((state & (((long) 3) << (spot << 1))) >> (spot << 1)));
	}

	/**
	 * Sets the tile at a spot to be a certain value
	 *
	 * @param spot the spot being indexed
	 * @param tile the tile to set it to
	 */
	private void setTileAtSpot(long spot, long tile) {
		state = manipulateState(state, spot, tile);

	}

	/**
	 * gets the state
	 *
	 * @return the state
	 */
	public long getState() {
		return state;
	}

	/**
	 * Evaluates a position for it's value, basically counting computer dots,
	 * and awarding the computer for corners
	 *
	 * @return the "Value" (heuristically)
	 */
	@Override
	public int getValue() {

		int value = 0;
		for (int i = 0; i < 25; i++) {
			value += getTileAtSpot(i) & 1;
		}
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(0) & 1) - ((getTileAtSpot(0) & 2) << 2));
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(4) & 1) - ((getTileAtSpot(4) & 2) << 2));
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(20) & 1) - ((getTileAtSpot(20) & 2) << 2));
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(24) & 1) - ((getTileAtSpot(24) & 2) << 2));
		return value; //* (1 - ((1 - notGottenFromIllegalMove)<<15));
	}

	/**
	 * Gets a list of the legal moves available
	 *
	 * @param isComputerMove whether or not it's the computer turn
	 * @return the list of valid moves
	 */
	@Override
	public ArrayList<Integer> getPossibleMoves(boolean isComputerMove) {
		ArrayList<Integer> moves = new ArrayList<>();
		for (int i = 0; i < 25; i++) {
			if (isLegalMove(i, isComputerMove)) {
				moves.add((i));
			}
		}
		if (moves.isEmpty()) {
			moves.add(BigOthello.NO_MOVE);
		}
		return moves;
	}

	/**
	 * Checks legality
	 *
	 * @param spot to move to
	 * @param isComputerMove whether not the computer is moving
	 * @return whether or not the move is legal
	 */
	public boolean isLegalMove(int spot, boolean isComputerMove) {
		int tile = isComputerMove ? X_TILE : O_TILE;
		if (this.getTileAtSpot(spot) != EMPTY) {
			return false;
		}
		int x = spot % 5;
		int y = spot / 5;
		for (int a = -1; a < 2; a++) {
			for (int b = -1; b < 2; b++) {
				if (a != 0 || b != 0) {
					int i = 1;
					boolean foundEnd = false;

					while (true) {
						int xOne = x + a * i;
						int yOne = y + b * i;
						int z;
						if (xOne < 0 || xOne >= 5 || yOne < 0 || yOne >= 5 || (z = getTileAtSpot(yOne * 5 + xOne)) == EMPTY) {
							break;
						}
						if (z == tile) {
							foundEnd = true;
							break;
						}
						i++;
					}
					if (foundEnd) {
						for (int j = 1; j < i; j++) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public int getSize() {
		return 5;
	}

	/**
	 * Make a declared move based on it's description, and who's moving
	 *
	 * @param m the move to be made
	 * @param isComputerMove whether or not the computer is making it
	 * @return a new copy of the board. Note, boards, are generally immutable,
	 */
	@Override
	public Board makeMove(int m, boolean isComputerMove) {
		int tile = isComputerMove ? X_TILE : O_TILE;
		int spot = m;
		int x = spot % 5;
		int y = spot / 5;
		long newState = state;
		newState = manipulateState(state, spot, tile);
		for (int a = -1; a < 2; a++) {
			for (int b = -1; b < 2; b++) {
				if (a != 0 || b != 0) {
					int i = 1;
					boolean foundEnd = false;

					while (true) {
						int xOne = x + a * i;
						int yOne = y + b * i;
						int z;
						if (xOne < 0 || xOne >= 5 || yOne < 0 || yOne >= 5 || (z = getTileAtSpot(yOne * 5 + xOne)) == EMPTY) {
							break;
						}
						if (z == tile) {
							foundEnd = true;
							break;
						}
						i++;
					}
					if (foundEnd) {
						for (int j = 1; j < i; j++) {
							newState = manipulateState(newState, (y + b * j) * 5 + (x + a * j), tile);
						}
					}
				}
			}
		}
		Othello o = new Othello(newState);

		return o;

	}

	/**
	 * Checks whether or not we should end the game pre-maturely
	 *
	 * @return
	 */
	@Override
	public boolean isGameOver() {
		return false;
	}

}
