/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artificialintelligence;

/**
 *
 * @author rohan
 */
public class TicTacToeMove extends Move{
	int spot;
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
