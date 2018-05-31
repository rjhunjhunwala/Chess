package artificialintelligence;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * A modern Prometheus
 *
 * @author rohan
 */
public class ArtificialIntelligence {

	/**
	 * The size of the board
	 */
	public static final int SIZE;

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
	 * The graphics display
	 */
	public static GameFrame g;

	/**
	 * set up game
	 */
	static {
		System.out.println("0: Tic Tac Toe, 1: Dall Ball 2: Othello/Reversi (Small) 3: Othello/Reversi (Big)");
		switch (new java.util.Scanner(System.in).nextInt()) {
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
				//	mainBoard = new Othello(Othello.STARTING_VALUE);
				mainBoard = new Othello(Math.random() < .5 ? Othello.STARTING_VALUE : Othello.ALT_STARTING_VALUE);
				SIZE = 5;
				break;
			case 3:
				mainBoard = new BigOthello();
				SIZE = 8;
				break;
		}
	}

	/**
	 * Let the ai make it's move
	 */
	public static void makeComputerMove() {
		if (mainBoard.isGameOver()) {
			System.out.println("GAME OVER!");
			return;
		} else if (mainBoard.getPossibleMoves(true).size() == 0) {
			//no moves... do nothing
			return;
		}

		if (mainBoard instanceof TicTacToeBoard) {
			DEPTH = 10;
		} else {
			//create the depth by limiting us to searching through a max of about 2*10^6
			//positions
			int DEPTH = (int) (1 + Math.log(20000000.0) / Math.log(mainBoard.getPossibleMoves(true).size() + 1));
		}

		GameStateNode n = new GameStateNode(mainBoard, null, 0, true);

		Move bestMove = n.getBestMove();

		mainBoard = mainBoard.makeMove(bestMove, true);
		if (mainBoard.isGameOver()) {
			System.out.println("GAME OVER!");
			return;
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		playGenericGame();
	}

	/**
	 * Plays a generic Game, A generic Game is any Board instance with a
	 * getTileAtSpot method, and is some kind of square
	 */
	public static void playGenericGame() {
		g = new GameFrame();
		new Thread(new Runnable() {
			public void run() {
				for (;;) {
					g.repaint();
				}
			}
		}).start();

		if (Math.random() < .5) {
			makeComputerMove();
		}
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
					//	System.out.println(((Othello)mainBoard).getState());
//Othello.displayBoard();
//System.out.println("-------\n");

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					ArtificialIntelligence.makeComputerMove();
					//Othello.displayBoard();
					//System.out.println("-------\n");
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
