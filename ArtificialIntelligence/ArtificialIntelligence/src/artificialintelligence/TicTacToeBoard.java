package artificialintelligence;

import java.util.ArrayList;

/**
 * This class serves as a fancy decorator for an integer. Yes, even a Tic Tac
 * Toe board is at its core just a number We use shady bitshift to store (up to)
 * 15 2 bit integers in a single int
 *
 * @author Rohan
 */
public class TicTacToeBoard extends GenericBoardGame {

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
	 * Display Board
	 */
	public static void displayBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int tile = ((TicTacToeBoard) ArtificialIntelligence.getMainBoard()).getTileAtSpot(i * 3 + j);
				System.out.print(tile == EMPTY ? "_" : tile == X_TILE ? "X" : "O");
			}
			System.out.println();
		}
	}

	/**
	 * Main method
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		playTicTacToe();
	}

	/**
	 * As expected, plays tic tac toe
	 */
	public static void playTicTacToe() {
		ArtificialIntelligence.setMainBoard(new TicTacToeBoard(0));
		while (true) {
			displayBoard();
			ArtificialIntelligence.makeComputerMove();
			displayBoard();
			ArtificialIntelligence.setMainBoard(ArtificialIntelligence.getMainBoard().makeMove(new java.util.Scanner(System.in).nextInt(), false));
		}
	}

	public long hash(){
		return this.state;
	}

	public boolean equals(Object o){
		return this.state == ((TicTacToeBoard) o).state;
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
		return (state & (~(3 << (spot << 1)))) + (tile << (spot << 1));

	}

	/**
	 * The following integer is used to store the whole array as an integer The
	 * bitshift associated with storing 9 values in a single int is moderately
	 * sketchy, and beyond the scope of a single comment
	 */
	private int state = 0;

	/**
	 * Intialize a board with a given inState
	 *
	 * @param inState the initial state
	 */
	public TicTacToeBoard(int inState) {
		state = inState;
	}

	/**
	 * Gets the tile associated with a certain spot
	 *
	 * @param spot return the spot being used
	 * @return
	 */
	public int getTileAtSpot(int spot) {
		return (state & (3 << (spot << 1))) >> (spot << 1);
	}

	/**
	 * Sets the tile at a spot to be a certain value
	 *
	 * @param spot the spot being indexed
	 * @param tile the tile to set it to
	 */
	private void setTileAtSpot(int spot, int tile) {
		state = manipulateState(state, spot, tile);

	}

	/**
	 * gets the state
	 *
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * Evaluates a position for a win
	 *
	 * @return
	 */
	@Override
	public int getValue() {
		int value = 0;
		for (int i = 0; i < 3; i++) {
			int productRow = (getTileAtSpot(i) * getTileAtSpot(3 + i) * getTileAtSpot(6 + i));
			int productColumn = (getTileAtSpot(3 * i) * getTileAtSpot(3 * i + 1) * getTileAtSpot(3 * i + 2));
			//add points for completed tic tac toes
			value += getAddedPointsForProduct(productRow);
			value += getAddedPointsForProduct(productColumn);
		}
//check diagonals
		int prodDiagonalOne = (getTileAtSpot(0) * getTileAtSpot(4) * getTileAtSpot(8));
		int prodDiagonalTwo = (getTileAtSpot(2) * getTileAtSpot(4) * getTileAtSpot(6));
		value += getAddedPointsForProduct(prodDiagonalOne);
		value += getAddedPointsForProduct(prodDiagonalTwo);

		return value;
	}

	/**
	 * Determine whether or not a row is made based on the product, and
	 * determine whether or not to add or subtract points accordingly Bitshift
	 * nonsense is fully unjustifiable here, but fun nonetheless, and a
	 * branchless alternative this speeds up performance (I think, but haven't
	 * proved it)
	 *
	 * @param product the product of a line of three
	 * @return
	 */
	public static int getAddedPointsForProduct(int product) {
		return (product & 1) - (product >> 3);
	}

	/**
	 * Functions as expected
	 *
	 * @param isComputerMove whether or not it's the computer move
	 * @return just a list of open squares
	 */
	@Override
	public ArrayList<Integer> getPossibleMoves(boolean isComputerMove) {
		ArrayList<Integer> moves = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			if (getTileAtSpot(i) == EMPTY) {
				moves.add(i);
			}
		}
		return moves;
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
		return new TicTacToeBoard(manipulateState(state, spot, tile));
	}

	public int getSize() {
		return 3;
	}

	/**
	 * Game is over, if someone has won (the magnitude of the value is non-zero)
	 *
	 * @return whether or not someone has won
	 */
	@Override
	public boolean isGameOver() {
		return getValue() != 0;
	}

}
