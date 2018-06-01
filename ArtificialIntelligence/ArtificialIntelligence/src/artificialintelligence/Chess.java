package artificialintelligence;

import java.util.ArrayList;

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
	public static final int KING_VALUE = 100000;
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
		VALUES[KING] = ROOK_VALUE;
	}
	//======End of Constants======
	
	/**
	 * Ok, so we store the state of a single chessboard as an array of longs This
	 * is for "performance" and "memory optimization" reasons (I think it helps but
	 * I can't prove it. Each long, represents one row. A long is 64 bits, so we can use
		* around 8 bits to store each piece which should be "plenty"
	 */
	public long[] state;

	/**
	 * Creates a chess board with the standard legal starting position The AI is
	 * black
	 */
	public Chess() {
		state = new long[8];
		for(int i = 0;i<8;i++){
			Chess.setTileAtSpot(state, 8+i, PAWN+(BLACK<<3));
						Chess.setTileAtSpot(state, 48+i, PAWN);
		}
		Chess.setTileAtSpot(state, 0, ROOK+(BLACK<<3));
		Chess.setTileAtSpot(state, 7, ROOK+(BLACK<<3));
				Chess.setTileAtSpot(state, 1, KNIGHT+(BLACK<<3));
		Chess.setTileAtSpot(state, 6, KNIGHT+(BLACK<<3));
				Chess.setTileAtSpot(state, 2, BISHOP+(BLACK<<3));
		Chess.setTileAtSpot(state, 5, BISHOP+(BLACK<<3));
						Chess.setTileAtSpot(state, 4, KING+(BLACK<<3));
		Chess.setTileAtSpot(state, 3, QUEEN+(BLACK<<3));
		
		
		Chess.setTileAtSpot(state, 56, ROOK);
			Chess.setTileAtSpot(state, 63, ROOK);
		Chess.setTileAtSpot(state, 57, KNIGHT);
			Chess.setTileAtSpot(state, 62, KNIGHT);
					Chess.setTileAtSpot(state, 58, BISHOP);
			Chess.setTileAtSpot(state, 61, BISHOP);
								Chess.setTileAtSpot(state, 60, KING);
			Chess.setTileAtSpot(state, 59, QUEEN);

	}
	
	public int getSize(){
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
private static void setTileAtSpot(long[] inState, int spot, int tile){
	inState[spot>>3] = manipulateState(inState[spot>>3],spot&7, tile);
}

	public static long manipulateState(long state, long spot, long tile) {
		return ((state & (~((((long) (255)) << ((spot << 3)))))) + (tile << (spot << 3)));

	}
	@Override
	public int getTileAtSpot(int spot) {
		return (int) (((state[spot >> 3])&((long)255<<((spot&7)<<3)))>>((spot&7)<<3));
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

	@Override
	public int getValue() {
	int value = 0;
		for(int i = 0;i<64;i++){
		int piece = getTileAtSpot(i);
		value += (2*(((piece&8)>>2)-1))*(piece);
		//todo: improve this hueristic. Currently we just add our pieces and subract 
		//the humans
	}
		return value;
	}

	@Override
	public ArrayList<Integer> getPossibleMoves(boolean isComputerMove){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
/**
	* The bottom 6 bits represent the initial spot, and the next 6 bits represent
	* the final spot
	* @param move
	* @param isComputerTurn
	* @return 
	*/
	@Override
	public Board makeMove(int move, boolean isComputerTurn) {
    int tile = getTileAtSpot(move&63);
				//"performance"
				long[] newState = {state[0],state[1],state[2],state[3],
				state[4],state[5],state[6],state[7]};
				Chess.setTileAtSpot(newState, move&63, 0);
				Chess.setTileAtSpot(newState,move>>6,tile);
	return new Chess(newState);
	}

}
