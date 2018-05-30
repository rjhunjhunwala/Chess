/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artificialintelligence;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

/**
 *
 * @author rohan
 */
public class ArtificialIntelligence {

	/**
	 * The size of the board
	 */
	public static final int SIZE;
	static{
		System.out.println("0: Tic Tac Toe, 1: Dall Ball 2: Othello/Reversi");
		switch(new java.util.Scanner(System.in).nextInt()){
			case 0:
				mainBoard = new TicTacToeBoard(0);
				SIZE = 3;
				break;
			case 1:
				mainBoard = new DallBallBoard(0);
				SIZE = 4;
				break;
			case 2:
			default:
		mainBoard = new Othello(Othello.STARTING_VALUE);
				SIZE = 5;
		}
	}
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
	 * Maximum depth for brute force search
	 */
	public static int DEPTH = 5;
	/**
	 * The main board
	 */
	public static Board mainBoard;

	/**
	 * Whether or not it is the computer's turn actually
	 */
	public static boolean isComputerTurn = true;

	public static void makeComputerMove() {
		if (mainBoard instanceof TicTacToeBoard) {
			DEPTH = 10;
		} else if (mainBoard instanceof DallBallBoard) {
			int moves = mainBoard.getPossibleMoves(true).size();
			if (moves < 10) {
				DEPTH = 12;
			} else if (moves < 12) {
				DEPTH = 10;
			} else if (moves < 14) {
				DEPTH = 7;
			} else {
				DEPTH = 6;
			}
		} else {
			DEPTH = 10;
			if (mainBoard.getPossibleMoves(true).size() < 13) {
				DEPTH += 1;
			}	
		}
		if (GameStateNode.GAME_IS_OTHELLO) {
			((Othello) mainBoard).notGottenFromIllegalMove = 1;
		}
		Move bestMove = mainBoard.getPossibleMoves(true).get(0);
		try{
		GameStateNode n = new GameStateNode(mainBoard, null, 0, true);
		bestMove = n.getBestMove();
		}catch(Exception ex){}
		
		mainBoard = mainBoard.makeMove(bestMove, true);
		
		isComputerTurn = false;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		playGenericGame();
	}
	public static GameFrame g;

	public static void playGenericGame() {
		g = new GameFrame();
		new Thread(new Runnable(){
			public void run(){
				for(;;){
					g.repaint();
				}
			}
		}).start();
	}

	public static class GameFrame extends javax.swing.JFrame {

		public GameFrame() {

			super("Ludus");
			this.setVisible(true);
			this.setResizable(false);
			this.add(new GamePanel());
			this.pack();

			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
//nil
				}

				@Override
				public void mousePressed(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					x /= GamePanel.SCALE;
					y /= GamePanel.SCALE;
					mainBoard = mainBoard.makeMove(new TicTacToeMove(y * SIZE + x), false);


				}

				@Override
				public void mouseReleased(MouseEvent e) {
				ArtificialIntelligence.makeComputerMove();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					//nil
				}

				@Override
				public void mouseExited(MouseEvent e) {
//nil
				}

			});
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		}
	}

	public static class GamePanel extends javax.swing.JPanel {

		public static final int SCALE = 100;
		public static final int OFFSET = 20;
		public static final int TOKEN_SIZE = SCALE - OFFSET * 2;

		@Override
		public Dimension getPreferredSize() {
			int dim = SIZE;
			return new Dimension(dim * SCALE, dim * SCALE);
		}

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0, 0, SIZE * SCALE, SIZE * SCALE);
			g.setColor(Color.gray);
			for (int i = 0; i <= SIZE; i++) {
				g.fillRect(i * SCALE, 0, 2, SIZE * SCALE);
				g.fillRect(i * SCALE, 0, 2, SIZE * SCALE);
				g.fillRect(0, i * SCALE, SIZE * SCALE, 2);
			}
			for (int i = 0; i < SIZE * SIZE; i++) {
				int x = i % SIZE;
				int y = i / SIZE;
				switch (((GenericBoardGame) mainBoard).getTileAtSpot(i)) {
					case Othello.O_TILE:
						g.setColor(Color.blue);
						g.fillOval(x * SCALE + OFFSET, y * SCALE + OFFSET, TOKEN_SIZE, TOKEN_SIZE);
						break;
					case Othello.X_TILE:
						g.setColor(Color.red);
						g.fill3DRect(x * SCALE + OFFSET, y * SCALE + OFFSET, TOKEN_SIZE, TOKEN_SIZE, true);

						break;

				}
			}
		}
	}

}
