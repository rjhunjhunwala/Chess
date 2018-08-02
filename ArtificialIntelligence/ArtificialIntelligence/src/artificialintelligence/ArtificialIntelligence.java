package artificialintelligence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * A modern Prometheus
 *
 * @author rohan
 */
public class ArtificialIntelligence {

	public static enum Difficulty {

		baby,
		child,
		easy,
		medium,
		hard;
	}

	public static Difficulty difficulty = Difficulty.medium;

	public static final int[] CHESS_DIFFICULTY = new int[]{1, 2, 3, 4, 5};
	public static final int[] TIC_TAC_TOE_DIFFICULTY = new int[]{1, 3, 5, 7, 9};
	public static final int[] OTHELLO_DIFFICULTY = new int[]{1, 3, 5, 7, 9};
	public static final int[] DALL_BALL_DIFFICULTY = new int[]{1, 2, 4, 8, 12};

	public static final AtomicBoolean computerIsThinking = new AtomicBoolean(false);

	/**
	 * @return the mainBoard
	 */
	public static Board getMainBoard() {
		return boards.getLast();
	}

	/**
	 * @param aMainBoard the mainBoard to set
	 */
	public static void setMainBoard(Board aMainBoard) {
		boards.add(aMainBoard);
	}

	private static LinkedList<Board> boards = new LinkedList<>();

	static {
		setMainBoard(new Chess());
	}

	public static boolean unMoved = true;
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
	 * The graphics display
	 */
	public static GameFrame g;

	public static final String[] GAMES = new String[]{"Tic-Tac-Toe", "Dall Ball (3x's and one O to win)", "Othello/Reversi (5x5)", "Othello (8x8)", "Chess"};

	public static final String[] DIFFICULTY_TEXTS = new String[]{"Baby", "Child", "Easy", "Medium", "Hard (SLOW)"};

	public static class DifficultyActionListener implements ActionListener {

		Difficulty difficulty;

		DifficultyActionListener(Difficulty difficulty) {
			this.difficulty = difficulty;
		}

		public void actionPerformed(ActionEvent e) {
			ArtificialIntelligence.difficulty = this.difficulty;
		}
	}

	public static class GameActionListener implements ActionListener {

		int game;

		GameActionListener(int game) {
			this.game = game;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			humanPlaysFirst = humanWillPlayFirst;
			boards = new LinkedList<>();
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
		unMoved = true;
		switch (gamePlayed) {
			case 0:
				setMainBoard(new TicTacToeBoard(0));
				setSIZE(3);
				break;
			case 1:
				setMainBoard(new DallBallBoard(0));
				setSIZE(4);
				break;
			case 2:
				//	mainBoard = new Othello(Othello.STARTING_VALUE);
				setMainBoard(new Othello(Math.random() < .5 ? Othello.STARTING_VALUE : Othello.ALT_STARTING_VALUE));
				setSIZE(5);
				break;
			case 3:
			default:
				setMainBoard(new BigOthello());
				setSIZE(8);
				break;
			case 4:
				setMainBoard(new Chess());
				setSIZE(8);
		}
	}

	/**
	 * Let the ai make it's move
	 */
	public static void makeComputerMove() {
		if (!getMainBoard().isGameOver() && !ArtificialIntelligence.getMainBoard().getPossibleMoves(true).isEmpty()) {
			ArtificialIntelligence.computerIsThinking.set(true);
			try {
				Board mainBoard = getMainBoard();
				if (mainBoard instanceof Chess) {
					DEPTH = ArtificialIntelligence.CHESS_DIFFICULTY[difficulty.ordinal()];
				} else if (mainBoard instanceof TicTacToeBoard) {
					DEPTH = ArtificialIntelligence.TIC_TAC_TOE_DIFFICULTY[difficulty.ordinal()];
				} else if (mainBoard instanceof Othello || mainBoard instanceof BigOthello) {
					DEPTH = ArtificialIntelligence.OTHELLO_DIFFICULTY[difficulty.ordinal()];
				} else if (mainBoard instanceof DallBallBoard) {
					DEPTH = ArtificialIntelligence.DALL_BALL_DIFFICULTY[difficulty.ordinal()];
				}

				AlphaBetaNode node = new AlphaBetaNode(mainBoard);
				if (!getMainBoard().getPossibleMoves(true).isEmpty()) {
					int bestMove = node.getBestMove();
					setMainBoard(mainBoard.makeMove(bestMove, true));
				}
			} finally {
				ArtificialIntelligence.computerIsThinking.set(false);
			}
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
		return getMainBoard() != null ? ((GenericBoardGame) getMainBoard()).getSize() : 3;
	}
	/**
	 * Whether or not the human is the first player in the current game
	 */
	public static boolean humanPlaysFirst = true;

	/**
	 * Whether or not the human will be the first player in the next game
	 */
	public static boolean humanWillPlayFirst = true;

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
					humanWillPlayFirst = !humanWillPlayFirst;
				}

			});
			menu.add(menuItem);
			menuBar.add(menu);
			menuItem = new JMenuItem("Toggle Graphics (Chess)");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					GamePanel.useFancyGraphics = !GamePanel.useFancyGraphics;
				}

			});
			menu.add(menuItem);
			menuBar.add(menu);

			menuItem = new JMenuItem("Increase Size");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					GamePanel.SCALE += 10;
					GamePanel.OFFSET = (int) (GamePanel.SCALE * .15);
					GamePanel.TOKEN_SIZE = GamePanel.SCALE - GamePanel.OFFSET * 2;
					g.pack();
				}

			});
			menu.add(menuItem);
			menuBar.add(menu);

			menuItem = new JMenuItem("Decrease Size");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					GamePanel.SCALE -= 10;
					GamePanel.OFFSET = (int) (GamePanel.SCALE * .15);
					GamePanel.TOKEN_SIZE = GamePanel.SCALE - GamePanel.OFFSET * 2;
					g.pack();
				}

			});
			menu.add(menuItem);
			menuBar.add(menu);

			menu = new JMenu("Moves");

			menuItem = new JMenuItem("Undo Move");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (boards.size() > 2) {
						boards.removeLast();
						boards.removeLast();
						g.repaint();
					}
				}

			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Pass (If no moves in Othello)");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Board mainBoard = getMainBoard();
					if (mainBoard instanceof Othello || mainBoard instanceof BigOthello) {
						if (mainBoard.getPossibleMoves(false).get(0).equals(BigOthello.NO_MOVE)) {
							System.out.println("here");
							makeComputerMove();
						}
					}
				}

			});

			menu.add(menuItem);
			menuBar.add(menu);

			menu = new JMenu("Difficulty");

			for (int i = 0; i < DIFFICULTY_TEXTS.length; i++) {
				menu.add(menuItem = new JMenuItem(DIFFICULTY_TEXTS[i]));
				menuItem.addActionListener(new DifficultyActionListener(Difficulty.values()[i]));

			}

			menuBar.add(menu);
			return menuBar;

		}

		public GameFrame() {

			super("Delicii Ludique (Fun and Games)");
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

		public static int SCALE = 90;
		public static int OFFSET = (int) (SCALE * .15);
		public static int TOKEN_SIZE = SCALE - OFFSET * 2;

		public static int mouseDownLoc = -1;

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
		/**
		 * Whether or not we using fancy or sample graphics.
		 */
		public static boolean useFancyGraphics = true;

		GamePanel() {
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
//nil
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (!ArtificialIntelligence.computerIsThinking.get()) {
						int x = e.getX();
						int y = e.getY();
						x /= GamePanel.SCALE;
						y /= GamePanel.SCALE;
						mouseDownLoc = y * getSIZE() + x;
						if (!(getMainBoard() instanceof Chess)) {
							if(getMainBoard().getPossibleMoves(false).contains(mouseDownLoc)){
							setMainBoard(getMainBoard().makeMove(mouseDownLoc, false));
							}else{
								mouseDownLoc = -1;
							}
						}
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (mouseDownLoc != -1) {
						int x = e.getX();
						int y = e.getY();
						x /= GamePanel.SCALE;
						y /= GamePanel.SCALE;

						int move = (((y << 3) + x) << 6) + mouseDownLoc;
						if (getMainBoard() instanceof Chess) {
							boolean wasPawnMoved = (((Chess) getMainBoard()).getTileAtSpot(mouseDownLoc) & 7) == Chess.PAWN;
							if (getMainBoard().getPossibleMoves(false).contains(move) && !((Chess) getMainBoard().makeMove(move, false)).isInCheck(false)) {
								setMainBoard(getMainBoard().makeMove(move, false));
								g.repaint();

								if (((Chess) getMainBoard()).getTileAtSpot((y << 3) + x) == Chess.QUEEN && wasPawnMoved) {
									PawnPromotionFrame p = new PawnPromotionFrame((y << 3) + x);
								} else {
									new Thread(new Runnable() {
										public void run() {
											try {
												Thread.sleep(100);
											} catch (Exception ex) {

											}
											ArtificialIntelligence.makeComputerMove();
										}
									}).start();
								}
							} else if (getMainBoard().getPossibleMoves(false).contains(move + 4096) && !((Chess) getMainBoard().makeMove(move, false)).isInCheck(false)) {
								setMainBoard(getMainBoard().makeMove(move + 4096, false));
								g.repaint();
								new Thread(new Runnable() {
									public void run() {
										try {
											Thread.sleep(100);
										} catch (Exception ex) {

										}
										ArtificialIntelligence.makeComputerMove();
									}
								}).start();
							}

						} else {
							ArtificialIntelligence.makeComputerMove();
						}
						mouseDownLoc = -1;
					}
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
			if (getMainBoard() == null) {

			} else {

				for (int i = 0; i < getSIZE() * getSIZE(); i++) {
					int x = i % getSIZE();
					int y = i / getSIZE();
					int xStart = x * SCALE + OFFSET;
					int yStart = y * SCALE + OFFSET;
					int tile = ((GenericBoardGame) getMainBoard()).getTileAtSpot(i);
					if (getMainBoard() instanceof Chess) {
						g.setColor(((x + y) & 1) == 0 ? Color.white : Color.black);
						g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
						boolean isCompPiece = (tile & 8) != 0;
						Color mainColor = isCompPiece ? BLACK_COLOR_AI : WHITE_COLOR_HUMAN;
						Color opposingColor = !isCompPiece ? BLACK_COLOR_AI : WHITE_COLOR_HUMAN;
						if (useFancyGraphics) {
							xStart -= OFFSET;
							yStart -= OFFSET;
							if (tile != 0) {
								g.setColor(mainColor);
								for (int j = 0; j < SCALE; j++) {
									for (int k = 0; k < SCALE; k++) {
										int xIndex = (int) (((((double) j)) / ((double) SCALE)) * Chess.SOURCE_SIZE);
										int yIndex = (int) (((((double) k)) / ((double) SCALE)) * Chess.SOURCE_SIZE);
										if (((Chess.SPRITES[tile & 7][yIndex] & (((long) 1) << xIndex)) > 0)) {
											g.drawRect(xStart + j, yStart + k, 1, 1);
										}
									}
								}
							}
						} else {
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
				if (mouseDownLoc != -1) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setStroke(new BasicStroke(4));
					List<Integer> moves = ArtificialIntelligence.getMainBoard().getPossibleMoves(false);
					for (int move : moves) {
						move &= 4095;
						if ((move & 63) == mouseDownLoc) {
							if (!((Chess) ArtificialIntelligence.getMainBoard().makeMove(move, false)).isInCheck(false)) {
								g2.setColor(Color.green);
								g2.drawRect(((move >> 6) & 7) * SCALE, ((move >> 9) * SCALE), SCALE, SCALE);
							}
						}

					}
				}
			}
		}
	}

	public static class PawnPromotionFrame extends javax.swing.JFrame {

		int spot = -1;
		/**
		 * Gotta love java naming conventions
		 */
		public static PawnPromotionFrame singletonPromotionFrame = null;
		public static final int[] pieces = new int[]{Chess.QUEEN, Chess.KNIGHT, Chess.ROOK, Chess.BISHOP};

		public PawnPromotionFrame(int spot) {
			super("Pawn Promotion:");
			this.spot = spot;
			//bad practice, but I won't abuse this from another thread...
			singletonPromotionFrame = this;
			this.setVisible(true);
			this.add(new PawnPromotionPanel());
			this.pack();
			this.setResizable(false);
			this.setAlwaysOnTop(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.repaint();
		}

	}

	public static class PawnPromotionPanel extends javax.swing.JPanel {

		public PawnPromotionPanel() {
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
//nil
				}

				@Override
				public void mousePressed(MouseEvent e) {
					Chess.setTileAtSpot(((Chess) getMainBoard()).state, PawnPromotionFrame.singletonPromotionFrame.spot, PawnPromotionFrame.pieces[e.getX() / GamePanel.SCALE]);
					PawnPromotionFrame.singletonPromotionFrame.dispose();
					ArtificialIntelligence.makeComputerMove();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
//nil
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
			return new Dimension(GamePanel.SCALE * PawnPromotionFrame.pieces.length, GamePanel.SCALE);
		}

		@Override
		public void paintComponent(Graphics g) {
			for (int i = 0; i < PawnPromotionFrame.pieces.length; i++) {
				//branchless = fast, and "performance" really matters
				int a = (((i & 1) << 8) - (i & 1));
				//that's the more fun way of saying, alternating between white and black, starting on white
				g.setColor(new Color(a, a, a));
				g.fillRect(i * GamePanel.SCALE, 0, GamePanel.SCALE, GamePanel.SCALE);
				a = 255 - a;

				g.setColor(new Color(a, a, a));

				for (int j = 0; j < GamePanel.SCALE; j++) {
					for (int k = 0; k < GamePanel.SCALE; k++) {
						int xIndex = (int) (((((double) j)) / ((double) GamePanel.SCALE)) * Chess.SOURCE_SIZE);
						int yIndex = (int) (((((double) k)) / ((double) GamePanel.SCALE)) * Chess.SOURCE_SIZE);

						if (((Chess.SPRITES[PawnPromotionFrame.pieces[i]][yIndex] & (((long) 1) << xIndex)) > 0)) {
							g.drawRect(i * GamePanel.SCALE + j, k, 1, 1);
						}
					}
				}

			}
		}
	}

}
