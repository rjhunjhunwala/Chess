/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artificialintelligence;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rohan
 */
public class AlphaBetaNode {

	public static int alphaBeta(AlphaBetaNode parent, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if (depth == ArtificialIntelligence.DEPTH || parent.board.isGameOver()) {
			return parent.board.getValue();
		}
		if (parent.board.getPossibleMoves(maximizingPlayer).isEmpty()) {
			if ((parent.board instanceof Chess)) {

				for (int move : parent.board.getPossibleMoves(!maximizingPlayer)) {
					if ((((Chess) parent.board).getTileAtSpotSpecial((move >> 6) & 63) & 7) == Chess.KING) {
						return maximizingPlayer ? -Chess.KING_VALUE + (depth << 2): Chess.KING_VALUE - (depth <<2);
					}
				}

			}
			return 0;

		}
		List<Integer> moves = parent.board.getPossibleMoves(maximizingPlayer);
//old pre-sort heuristic
//		if (parent.board instanceof Chess) {
//			if (depth < 1) {
//				Chess chessBoard = (Chess) parent.board; //yes, java makes me do this
//				moves.sort(new Comparator<Integer>() {
//
//					@Override
//					public int compare(Integer o1, Integer o2) {
//                        return Chess.VALUES[chessBoard.getTileAtSpot((o2>>6)&63)&7] - Chess.VALUES[chessBoard.getTileAtSpot((o1>>6)&63)&7];
//						
//					}
//				});
//			}
//		}

		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;

			int bestMove = 0;
			int i = 0;
			for (Integer move : moves) {
				int oldValue = value;
				value = Math.max(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(move, maximizingPlayer)), depth + 1, alpha, beta, false));
				alpha = Math.max(alpha, value);
				if (value != oldValue) {
					bestMove = i;
				}
				i++;
				if (alpha >= beta) {
					break;
				}
			}
			parent.bestMove = moves.get(bestMove);
			return value;
		} else {
			int value = Integer.MAX_VALUE;

			int bestMove = 0;
			int i = 0;
			for (Integer move : moves) {
				int oldValue = value;
				value = Math.min(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(move, maximizingPlayer)), depth + 1, alpha, beta, true));
				beta = Math.min(beta, value);
				if (value != oldValue) {
					bestMove = i;
				}
				i++;
				if (alpha >= beta) {
					break;
				}
			}
			parent.bestMove = bestMove;
			return value;
		}
	}
	public final Board board;
	private int bestMove;

	AlphaBetaNode(Board board) {

		this.board = board;
	}

	public int getBestMove() {
		alphaBeta(this, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		return bestMove;
	}

}
