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
public class ArtificialIntelligence {
/**
	* Maximum depth for brute force search
	*/
	public static int DEPTH = 20;
	/**
		* The main board
		*/
public static Board mainBoard;
/**
	* Whether or not it is the computer's turn actually
	*/	
public static boolean isComputerTurn = true;


public static void makeComputerMove(){
	GameStateNode n = new GameStateNode(mainBoard,null,0,true);
	System.out.println(n);
	Move bestMove = n.getBestMove();
	System.out.println(bestMove);
mainBoard = mainBoard.makeMove(bestMove, true);
isComputerTurn = false;
}
/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
	}
	
}
