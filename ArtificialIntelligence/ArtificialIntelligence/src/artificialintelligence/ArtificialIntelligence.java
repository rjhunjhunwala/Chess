package artificialintelligence;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * A modern Prometheus
 *
 * @author rohan
 */
public class ArtificialIntelligence {

	/**
	 * The size of the board
	 */
	private static int SIZE;

	/**
	 * The empty tile constant
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
	public static Board mainBoard = new BigOthello();
	/**
	 * The graphics display
	 */
	public static GameFrame g;

	public static final String[] GAMES = new String[]{"Tic-Tac-Toe", "Dall Ball (3x's and one O to win)", "Othello/Reversi (5x5)", "Othello (8x8)", "Chess"};

	public static class GameActionListener implements ActionListener {

		int game;

		GameActionListener(int game) {
			this.game = game;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setUpNewGame(game);
			g.dispose();
			g = new GameFrame();
						if (!humanPlaysFirst) {
				makeComputerMove();
			}
		}

	}

	/**
	 * set up game
	 */
	static void setUpNewGame(int gamePlayed) {
		switch (gamePlayed) {
			case 0:
				mainBoard = new TicTacToeBoard(0);
				setSIZE(3);
				break;
			case 1:
				mainBoard = new DallBallBoard(0);
				setSIZE(4);
				break;
			case 2:
				//	mainBoard = new Othello(Othello.STARTING_VALUE);
				mainBoard = new Othello(Math.random() < .5 ? Othello.STARTING_VALUE : Othello.ALT_STARTING_VALUE);
				setSIZE(5);
				break;
			case 3:
			default:
				mainBoard = new BigOthello();
				setSIZE(8);
				break;
			case 4:
				mainBoard = new Chess();
				setSIZE(8);
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

		GameStateNode n = new GameStateNode(mainBoard, 0, true);

		Integer bestMove = n.getBestMove();

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
	}

	/**
	 * @return the SIZE
	 */
	public static int getSIZE() {
		return mainBoard != null ? ((GenericBoardGame) mainBoard).getSize() : 3;
	}
	public static boolean humanPlaysFirst = true;

	/**
	 * @param aSIZE the SIZE to set
	 */
	public static void setSIZE(int aSIZE) {
		SIZE = aSIZE;
	}

	public static class GameFrame extends javax.swing.JFrame {

		/**
		 * Singleton anti-pattern sorry.
		 */
		public static GamePanel p;

		/**
		 * Create the bar that allows the user to select their preferences
		 *
		 * @return a brand new JMenuBar
		 */
		public JMenuBar buildMenuBar() {

			JMenuBar menuBar = new JMenuBar();
			JMenu menu = new JMenu("Start New Game");
			JMenuItem menuItem;
			for (int i = 0; i < GAMES.length; i++) {
				menuItem = new JMenuItem(GAMES[i]);
				menuItem.addActionListener(new GameActionListener(i));
				menu.add(menuItem);
			}
			menuBar.add(menu);
			menu = new JMenu("Preferences");
			menuItem = new JMenuItem("Toggle First Move");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					humanPlaysFirst = !humanPlaysFirst;
				}

			});
			menu.add(menuItem);
			menuBar.add(menu);
			return menuBar;
		}

		public GameFrame() {

			super("Jocor ludusque");
			this.setJMenuBar(buildMenuBar());
			this.setVisible(true);
			this.setResizable(false);
			this.add(p = new GamePanel());
			this.pack();

			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setAlwaysOnTop(true);
		}
	}

	public static class GamePanel extends javax.swing.JPanel {

		public static final int SCALE = 60;
		public static final int OFFSET = (int) (SCALE * .15);
		public static final int TOKEN_SIZE = SCALE - OFFSET * 2;

		/**
		 * the color that the AI uses, and what is "BLACK";
		 */
		public static final Color BLACK_COLOR_AI = Color.RED;
		/**
		 * The color the human uses, is "WHITE"
		 */
		public static final Color WHITE_COLOR_HUMAN = Color.BLUE;
		/**
		 * The color for the little details that go above the pieces
		 */
		public static final Color DETAIL_COLOR = Color.GREEN;

		GamePanel() {
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
					mainBoard = mainBoard.makeMove((y * getSIZE() + x), false);
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
		}

		@Override
		public Dimension getPreferredSize() {
			int dim = getSIZE();
			return new Dimension(dim * SCALE, dim * SCALE);
		}

		public void drawDetailLine(Graphics g, int x, int y, double angle) {
			g.setColor(DETAIL_COLOR);
			g.drawLine(x, y, (int) (x + TOKEN_SIZE * .4 * Math.cos(angle)), (int) (y + TOKEN_SIZE * .4 * Math.sin(angle)));

		}

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0, 0, getSIZE() * SCALE, getSIZE() * SCALE);
			g.setColor(Color.gray);
			for (int i = 0; i <= getSIZE(); i++) {
				g.fillRect(i * SCALE, 0, 2, getSIZE() * SCALE);
				g.fillRect(i * SCALE, 0, 2, getSIZE() * SCALE);
				g.fillRect(0, i * SCALE, getSIZE() * SCALE, 2);
			}
			if (mainBoard == null) {

			} else {
				for (int i = 0; i < getSIZE() * getSIZE(); i++) {
					int x = i % getSIZE();
					int y = i / getSIZE();
					int xStart = x * SCALE + OFFSET;
					int yStart = y * SCALE + OFFSET;
					int tile = ((GenericBoardGame) mainBoard).getTileAtSpot(i);
					if (mainBoard instanceof Chess) {
						g.setColor(((x + y) & 1) == 0 ? Color.white : Color.black);
						g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
						boolean isCompPiece = (tile & 8) != 0;
						Color mainColor = isCompPiece ? BLACK_COLOR_AI : WHITE_COLOR_HUMAN;
						Color opposingColor = !isCompPiece ? BLACK_COLOR_AI : WHITE_COLOR_HUMAN;

						switch (tile & 7) {
							case Chess.PAWN:
								g.setColor(mainColor);
								g.fillOval(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, isCompPiece ? Math.PI / 2 : -Math.PI / 2);
								break;
							case Chess.KNIGHT:
								g.setColor(mainColor);
								g.fillRect(xStart + TOKEN_SIZE / 4, yStart, TOKEN_SIZE / 2, TOKEN_SIZE);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, Math.PI / 3);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -Math.PI / 3);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, Math.PI / 6);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -Math.PI / 6);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, 2 * Math.PI / 3);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -2 * Math.PI / 3);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, 5 * Math.PI / 6);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -5 * Math.PI / 6);
								break;
							case Chess.BISHOP:
								g.setColor(mainColor);
								g.fillOval(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, Math.PI / 4);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -Math.PI / 4);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, 3 * Math.PI / 4);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -3 * Math.PI / 4);
								break;
							case Chess.ROOK:
								g.setColor(mainColor);
								g.fillRect(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, Math.PI / 2);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, -Math.PI / 2);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, 0);
								drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, Math.PI);
								break;
							case Chess.QUEEN:
								g.setColor(mainColor);
								g.fillRect(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE);
								for (int d = 0; d < 8; d++) {
									drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, d * Math.PI / 4);
								}
								break;
							case Chess.KING:
								g.setColor(mainColor);
								g.fillRect(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE);
								g.setColor(opposingColor);
								g.fillOval(xStart + OFFSET, yStart + OFFSET, TOKEN_SIZE - OFFSET * 2, TOKEN_SIZE - OFFSET * 2);
								for (int d = 0; d < 8; d++) {
									drawDetailLine(g, xStart + SCALE / 2 - OFFSET, yStart + SCALE / 2 - OFFSET, d * Math.PI / 4);
								}
						}
					} else {
						switch (tile) {
							case Othello.O_TILE:
								g.setColor(Color.blue);
								g.fillOval(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE);
								break;
							case Othello.X_TILE:
								g.setColor(Color.red);
								g.fill3DRect(xStart, yStart, TOKEN_SIZE, TOKEN_SIZE, true);

								break;

						}
					}
				}
			}
		}
	}

}
