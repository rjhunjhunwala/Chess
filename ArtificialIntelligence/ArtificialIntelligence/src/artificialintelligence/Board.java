package artificialintelligence;

import java.util.ArrayList;
import java.util.List;

/**
 * A generalized interface to describe operations to hold state of two play Game
 * of perfect information.
 *
 * @author rohan
 */
public interface Board {

	/**
	 * Returns whether or not the game is over
	 *
	 * @return some boolean whether or not play should continue
	 */
	public abstract boolean isGameOver();

	/**
	 * Return some integer, such that more positive values are more favored to the
	 * computer, and more negative values are more favored for the human Initially,
	 * and whenever the game is "tied" this method
	 *
	 * @return an integer determining the position's "value"
	 */
	public abstract int getValue();

	/**
	 * Gets the possible moves (sorry, couldn't turn down that chance)
	 *
	 * @param isComputerMove A boolean determining whether or not we are looking at
	 * moves for the human or computer
	 */
	public abstract List<Integer> getPossibleMoves(boolean isComputerMove);

	/**
	 * Make a move and return, the board resulting from the new state
	 *
	 * @param spot the move to make
	 * @param isComputerTurn whether or not the move is being made by the computer
	 * @return The new board after the move has been made
	 */
	public abstract Board makeMove(int spot, boolean isComputerTurn);
}
