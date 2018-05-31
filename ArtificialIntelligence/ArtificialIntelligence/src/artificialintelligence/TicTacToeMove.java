
package artificialintelligence;

/**
 * Yes, I recognize this is just a dumbed down Integer class
 * @author rohan
 */
public class TicTacToeMove extends Move{
	/**
		* The spot being used
		*/
	int spot;
	/**
		* Create a move on a given spot
		* @param spot 
		*/
	public TicTacToeMove(int spot){
		this.spot = spot;
	}
	/**
		* Get a string representation of this move
		* @return the spot this move represents as a string
		*/
	@Override
	public String toString(){
		return spot+"";
	}
}
