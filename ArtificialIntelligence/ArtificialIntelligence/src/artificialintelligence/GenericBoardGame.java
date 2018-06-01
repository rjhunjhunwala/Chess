
package artificialintelligence;

/**
 *
 * @author rohan
 */
public abstract class GenericBoardGame implements Board {
	/**
		* Gets the "tile" at a given spot in a generic board game
		* @param spot
		* @return the tile at that spot
		*/
	public abstract int getTileAtSpot(int spot);
/**
	* Get Size of square
	*/
public abstract int getSize();
}
