
package artificialintelligence;

import java.util.ArrayList;

/**
 * This class serves as a fancy decorator for an integer. Yes, even a Tic Tac
 * Toe board is at its core just a number We use shady bitshift to store (up to)
 * 15 2 bit integers in a single int
 *
 * @author Rohan
 */
public class DallBallBoard extends GenericBoardGame {

	/**
	 * The empty tile cosntant
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
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int tile = ((DallBallBoard) ArtificialIntelligence.mainBoard).getTileAtSpot(i * 4 + j);
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
		ArtificialIntelligence.mainBoard = new DallBallBoard(0);

		
		while (true) {
			displayBoard();
			System.out.println("----");
			System.out.println(ArtificialIntelligence.mainBoard.getValue());
			System.out.println("----");
			ArtificialIntelligence.mainBoard = ArtificialIntelligence.mainBoard.makeMove(new TicTacToeMove(new java.util.Scanner(System.in).nextInt()), false);
			displayBoard();
			System.out.println("----");
			System.out.println(ArtificialIntelligence.mainBoard.getValue());
			System.out.println("----");
			ArtificialIntelligence.makeComputerMove();
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
	public static int manipulateState(long state, int spot, int tile) {
		return (int) (state & (~((long)3 << (spot << 1)))) + (tile << (spot << 1));

	}

	/**
	 * The following integer is used to store the whole array as an integer
	 */
	private long state = 0;

	public DallBallBoard(int inState) {
		state = inState;
	}

	/**
	 * Get's the tile associated with a certain spot
	 *
	 * @param spot return the spot being used
	 * @return
	 */
	public int getTileAtSpot(int spot) {
		return (int) (((state & ( (long) 3 << (spot << 1))) >> (spot << 1)));
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
	public long getState() {
		return state;
	}

	/**
	 * Evaluates a position for a win. + means comp win, 0 = draw, - means loss
	 *
	 * @return
	 */
	@Override
	public int getValue() {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int productRow = (getTileAtSpot(i) * getTileAtSpot(4 + i) * getTileAtSpot(8 + i) * getTileAtSpot(12 + i));
			int productColumn = (getTileAtSpot(4 * i) * getTileAtSpot(4 * i + 1) * getTileAtSpot(4 * i + 2) * getTileAtSpot(4 * i + 3));
			//add points for completed tic tac toes
			value += getAddedPointsForProduct(productRow);
			value += getAddedPointsForProduct(productColumn);
		}
//check diagonals
		int prodDiagonalOne = (getTileAtSpot(0) * getTileAtSpot(5) * getTileAtSpot(10) * getTileAtSpot(15));
		int prodDiagonalTwo = (getTileAtSpot(3) * getTileAtSpot(6) * getTileAtSpot(9) * getTileAtSpot(12));
		value += getAddedPointsForProduct(prodDiagonalOne);
		value += getAddedPointsForProduct(prodDiagonalTwo);

		return value;
	}

	/**
	 * Determine whether or not a row is made based on the product, and determine
	 * whether or not to add or subtract points accordingly Bitshift nonsense is
	 * fully unjustifiable here, but fun nonetheless
	 *
	 * @param product the product of a line of three
	 * @return
	 */
	public static int getAddedPointsForProduct(int product) {
		return ((product & 2) >> 1) - ((product & 8) >> 3);
	}

	@Override
	public ArrayList<Move> getPossibleMoves(boolean isComputerMove) {
		ArrayList<Move> moves = new ArrayList<>();
		for (int i = 0; i < 16; i++) {
			if (getTileAtSpot(i) == EMPTY) {
				moves.add(new TicTacToeMove(i));
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
	public Board makeMove(Move m, boolean isComputerMove) {
		int tile = isComputerMove ? X_TILE : O_TILE;
		int spot = ((TicTacToeMove) m).spot;
		
		return new DallBallBoard(manipulateState(state, spot, tile));
	}

	@Override
	public boolean isGameOver() {
		return getValue() != 0;
	}

}
