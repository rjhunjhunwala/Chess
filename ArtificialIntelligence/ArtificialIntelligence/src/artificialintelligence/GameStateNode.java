
package artificialintelligence;

import java.util.ArrayList;

/**
 * One single node in the tree
 * @author rohan
 */
public class GameStateNode {

/**
	* The current state
	*/
	private Board state;
//how far down the tree we are
	private int depth = 0;
	/**
		* Whether or not it's the computers turn
		*/
	private boolean isComputerTurn;

	/**
		* The nodes that can be reached from this node
		*/
	private ArrayList<GameStateNode> children;

	/**
	 * Creates a Game Node
	 *
	 * @param inLastMove is the last move
	 * @param inState the state of the board coming
	 * @param inDepth how "deep" it is in the tree
	 * @param inIsComputerMove whether or not, the computer is yet to move in this
	 * case
	 */
	public GameStateNode(Board inState, int inDepth, boolean inIsComputerMove) {
		state = inState;
		depth = inDepth;
		isComputerTurn = inIsComputerMove;
		if (depth < ArtificialIntelligence.DEPTH && !inState.isGameOver()) {
			int nextDepth = depth + 1;
			boolean nextTurn = !isComputerTurn;
			ArrayList<Integer> moves = state.getPossibleMoves(isComputerTurn);
			children = new ArrayList<>(moves.size());
			for (Integer m : moves) {
				children.add(new GameStateNode(inState.makeMove(m, isComputerTurn), nextDepth, nextTurn));
			}
		}
	}

	/**
	 * Gets the best move
	 *
	 * @return the best move
	 */
	public int getBestMove() {
		int bestMove = -1;
		int index = 0;
		int i = 0;
		int bestValue = Integer.MIN_VALUE;
		for (GameStateNode node : children) {
			int value = node.getValue();
			if (value > bestValue) {
				bestValue = value;
				index = i;
			}
		i++;
		}
		return state.getPossibleMoves(isComputerTurn).get(index);
	}

	/**
	 * Get's the expected "value" of the board for a given move by recursively
	 * going down the tree. The value of a node with children, is defined in one of
	 * two ways... The worst child when it's the computers turn, and the worst when
	 * it's the humans choice (assume perfect play) The value of a terminal node
	 * (without children, either due to depth restrictions or just no moves left)
	 * is determined by just it's current state
	 *
	 * @return the predict future value of the state based on predicting the future
	 * moves
	 */
	public int getValue() {
		if (children != null && !children.isEmpty()) {
			if (isComputerTurn) {
				//the computer can choose which of the paths it take, so logically the value of this node will be the best
				return getBestChild();
			} else {
				//Assume perfect play from opponents (somewhat flawed, but let's go with it)
				return getWorstChild();
			}
		} else {
			//perhaps quiescience search, but I'm not exactly competent...
			//just don't go further down the tree and call it a day...
			return state.getValue()<<6+depth*3;
		}
	}

	/**
	 * If it's the AI's turn, it will choose only the highest following sub-branch,
	 * so just assume and move on
	 *
	 * @return the value of the best branch the ai chooses
	 */
	public int getBestChild() {
		int best = Integer.MIN_VALUE;
		for (GameStateNode n : children) {
			int a = n.getValue();
			if (a > best) {
				best = a;
			}
		}
		return best;
	}

	/**
	 * If it's the computers turn, it will choose only the highest following
	 * sub-branch, so just assume and move on
	 *
	 * @return the value of the worst branch the opponent can choose
	 */
	public int getWorstChild() {
		int worst = Integer.MAX_VALUE;
		for (GameStateNode n : children) {
			int a = n.getValue();
			if (a < worst) {
				worst = a;
			}
		}
		return worst;
	}

	/**
	 * Get a string representation Please, note, this is VERY computationally
	 * expensive...
	 *
	 * @return
	 */
	public String toString() {
		return this.getValue() + "|" + children;
	}
}
