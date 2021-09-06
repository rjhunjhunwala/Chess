package artificialintelligence;

import java.util.ArrayList;

/**
 * This class stores an Othello board using as many hacks as I came with
 * Officially these hacks are for "performance", but to be entirely honest,
 * they're fun
 *
 * @author rohan
 */
public class BigOthello extends GenericBoardGame {

	/**
	 * Corners receive a bonus of (C_B_W) for being owned, and a penalty for not
	 * being owned of -C_B_W
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
	 * represents making no move... remember, sometimes the only winning move,
	 * is not to play...
	 */
	public static final int NO_MOVE = 0b111111111111111111111111111;
	
	/**
	 * Display Board
	 */
	public static void displayBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				long tile = ((BigOthello) ArtificialIntelligence.getMainBoard()).getTileAtSpot(i * 8 + j);
				System.out.print(tile == EMPTY ? "_" : tile == X_TILE ? "X" : "O");
			}
			System.out.println();
		}
	}

	/**
	 * Returns a manipulated version of "state" so that the given spot now has a
	 * given tile value
	 *
	 * @param state the original state
	 * @param spot the spot to be used (less than 15)
	 * @param tile the tile value (0 1 or 2)
	 * @return the new version of state
	 */
	public static int manipulateState(int state, int spot, int tile) {
		return ((state & (~(((3) << ((spot << 1)))))) + (tile << (spot << 1)));

	}

	/**
	 * The following array stores the state of the board through questionable
	 * bitshift
	 */
	private int[] state = new int[8];

	@Override
	public long hash() {
		long out = 0;
		out += state[0];
		out *= BIG_PRIME;
		out += state[1];
		out *= BIG_PRIME;
		out += state[2];
		out *= BIG_PRIME;
		out += state[3];
		out *= BIG_PRIME;
		out += state[4];
		out *= BIG_PRIME;
		out += state[5];
		out *= BIG_PRIME;
		out += state[6];
		out *= BIG_PRIME;
		out += state[7];
		out *= BIG_PRIME;

		return out;
	}

	@Override
	public boolean equals(Object o){
		BigOthello c = (BigOthello) o;
		return c.state[0] == this.state[0] && c.state[1] == this.state[1] && c.state[2] == this.state[2]
				&& c.state[3] == this.state[3] && c.state[4] == this.state[4] && c.state[5] == this.state[5] && c.state[6] == this.state[6]
				&& c.state[7] == this.state[7];
	}


	/**
	 * Make an Othello board with the original starting configuration
	 */
	public BigOthello() {
		setTileAtSpot(state, 27, X_TILE);
		setTileAtSpot(state, 28, O_TILE);
		setTileAtSpot(state, 36, X_TILE);
		setTileAtSpot(state, 35, O_TILE);
	}

	/**
	 * makes an Othello board with an input state
	 *
	 * @param inState
	 */
	public BigOthello(int[] inState) {
		state = inState;
	}

	/**
	 * Evaluates a position for its "value" Essentially counts the number of dots
	 * the computer has, and adds a bonus for owning corners
	 *
	 * @return a heuristic value of a board
	 */
	@Override
	public int getValue() {

		int value = 0;
		for (int i = 0; i < 64; i++) {
			value += getTileAtSpot(i) & 1;
		}
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(0) & 1) - ((getTileAtSpot(0) & 2) << 2));
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(7) & 1) - ((getTileAtSpot(7) & 2) << 2));
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(56) & 1) - ((getTileAtSpot(56) & 2) << 2));
		value += CORNER_BONUS_WEIGHT * ((getTileAtSpot(63) & 1) - ((getTileAtSpot(63) & 2) << 2));
		return value; //* (1 - ((1 - notGottenFromIllegalMove)<<15));
	}

	@Override
	public ArrayList<Integer> getPossibleMoves(boolean isComputerMove) {
		ArrayList<Integer> moves = new ArrayList<>();
		for (int i = 0; i < 64; i++) {
			if (isLegalMove(i, isComputerMove)) {
				moves.add(i);
			}
		}
		if(moves.isEmpty()){
			moves.add(NO_MOVE);
		}
		return moves;
	}

	/**
	 * Checks if a spot is a legal move 
	 *
	 * @param spot the spot to move to
	 * @param isComputerMove whether or not it's the computers move
	 * @return
	 */
	public boolean isLegalMove(int spot, boolean isComputerMove) {
		int tile = isComputerMove ? X_TILE : O_TILE;
		if (this.getTileAtSpot(spot) != EMPTY) {
			return false;
		}
		int x = spot & 7;
		int y = spot / 8;
		for (int a = -1; a < 2; a++) {
			for (int b = -1; b < 2; b++) {
				if (a != 0 || b != 0) {
					int i = 1;
					boolean foundEnd = false;

					while (true) {
						int xOne = x + a * i;
						int yOne = y + b * i;
						int z;
						if (xOne < 0 || xOne >= 8 || yOne < 0 || yOne >= 8 || (z = getTileAtSpot(yOne * 8 + xOne)) == EMPTY) {
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

	public int getSize(){
		return 8;
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
		int[] newState = new int[]{state[0], state[1], state[2], state[3], state[4], state[5], state[6], state[7]};
		if(m == NO_MOVE){
			return new BigOthello(newState);
		}
		int tile = isComputerMove ? X_TILE : O_TILE;
		int spot = m;
		int x = spot & 7;
		int y = spot / 8;
		//looks stupid, but 
		
		newState[y] = manipulateState(newState[y], x, tile);
		for (int a = -1; a < 2; a++) {
			for (int b = -1; b < 2; b++) {
				if (a != 0 || b != 0) {
					int i = 1;
					boolean foundEnd = false;

					while (true) {
						int xOne = x + a * i;
						int yOne = y + b * i;
						int z;
						if (xOne < 0 || xOne >= 8 || yOne < 0 || yOne >= 8 || (z = getTileAtSpot(yOne * 8 + xOne)) == EMPTY) {
							break;
						}
						if (z == tile) {
							foundEnd = true;
							break;
						}
						i++;
					}
					if (foundEnd) {
						//System.out.println("FOUND:"+i);
						for (int j = 1; j < i; j++) {
							newState[(y + b * j)] = manipulateState(newState[(y + b * j)], (x + a * j), tile);
						}
					}
				}
			}
		}
		BigOthello o = new BigOthello(newState);

		return o;

	}

	/**
	 * Takes in an array of state, and sets a given spot to a given tile value
	 *
	 * @param toMod the given state to be modified in place
	 * @param spot the spot being used
	 * @param tile the tile being chosen
	 */
	public static void setTileAtSpot(int[] toMod, int spot, int tile) {
		toMod[spot >> 3] = manipulateState(toMod[spot >> 3], spot & 7, tile);
	}

	@Override
	public boolean isGameOver() {
		return false;
	}

	/**
	 * Gets a tile at a given spot (sorry, had to)
	 *
	 * @param spot
	 * @return
	 */
	@Override
	public int getTileAtSpot(int spot) {
		return (state[spot >> 3] & (3 << ((spot & 7) << 1))) >> ((spot & 7) << 1);
	}

}
