/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artificialintelligence;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;




/**
 *
 * @author rohan
 */
public class AlphaBetaNode {
	public static class Pair<A, B>{
		A a;
		B b;
		public Pair(A a, B b){
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean equals(Object obj) {
			Pair p = (Pair) obj;
			return p.a.equals(a) && p.b.equals(b);
		}

		public int hashCode(){
			return b.hashCode();
		}
	}


	public static boolean isCacheHit(HashMap<Board, Pair<Integer, Integer>> transpositionTable, Board b, int depth){
		return  transpositionTable.containsKey(b) && transpositionTable.get(b).a >= depth;
	}
	public static void updateCache(HashMap<Board, Pair<Integer, Integer>> transpositionTable, Board b, int depth, int value){
		if(!transpositionTable.containsKey(b) || transpositionTable.get(b).a < depth){
			Pair<Integer, Integer> v = new Pair<>(depth, value);
			transpositionTable.put(b, v);
		}

	}
	public static final int TP_TABLE_SIZE = 2 << 27 - 1;

	public static HashMap<Board, Pair<Integer, Integer>> transpositionTableMax = new HashMap<>();
	public static HashMap<Board, Pair<Integer, Integer>> transpositionTableMin = new HashMap<>();

	public static void clearTPTable(){
		transpositionTableMax.clear();
		transpositionTableMin.clear();
	}

	public static int alphaBeta(AlphaBetaNode parent, int depth, int alpha, int beta, boolean maximizingPlayer) {

		HashMap<Board, Pair<Integer, Integer>> transpositionTable = maximizingPlayer ? transpositionTableMax : transpositionTableMin;
		HashMap<Board, Pair<Integer, Integer>> transpositionTableOpp = !maximizingPlayer ? transpositionTableMax : transpositionTableMin;

		if(isCacheHit(transpositionTable, parent.board, depth)){
			// return transpositionTable.get(parent.board).b;
		}
		if (depth == 0 || parent.board.isGameOver()) {
			int value = parent.board.getValue();
			updateCache(transpositionTable, parent.board, depth, value);
			return value;
		}
		if (parent.board.getPossibleMoves(maximizingPlayer).isEmpty()) {
			if ((parent.board instanceof Chess)) {

				for (int move : parent.board.getPossibleMoves(!maximizingPlayer)) {
					if ((((Chess) parent.board).getTileAtSpotSpecial((move >> 6) & 63) & 7) == Chess.KING) {
						 int out = maximizingPlayer ? -Chess.KING_VALUE + (depth << 2) : Chess.KING_VALUE - (depth << 2);
						 updateCache(transpositionTable, parent.board, depth, out);
						 return out;
					}
				}

			}
			updateCache(transpositionTable, parent.board, depth, 0);
			return 0;
		}
		List<Integer> moves = parent.board.getPossibleMoves(maximizingPlayer);
//old pre-sort heuristica
			if (false && ArtificialIntelligence.DEPTH >= 2) {
				HashMap<Integer, Integer> values = new HashMap<>();
				for(Integer move: moves){
					values.put(move, transpositionTableOpp.getOrDefault(parent.board.makeMove(move, true), new Pair<Integer, Integer>(0, 0)).b);
				}
				moves.sort(new Comparator<Integer>() {

					@Override
					public int compare(Integer o1, Integer o2) {
							return values.get(o1) - values.get(o2);

					}
				});

			}


		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;

			int bestMove = 0;
			int i = 0;
			if(depth >= 3) {
				value = Math.max(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(bestMove, maximizingPlayer)), depth - 1, alpha, beta, false));
				alpha = Math.max(alpha, value);
			}
			for (Integer move : moves) {
				int oldValue = value;
				value = Math.max(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(move, maximizingPlayer)), depth - 1, alpha, beta, !maximizingPlayer));
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
			updateCache(transpositionTable, parent.board, depth, value);
			return value;
		} else {
			int value = Integer.MAX_VALUE;

			int bestMove = 0;
			int i = 0;
			for (Integer move : moves) {
				int oldValue = value;
				value = Math.min(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(move, maximizingPlayer)), depth - 1, alpha, beta, !maximizingPlayer));
				beta = Math.min(beta, value);
				if (value != oldValue) {
					bestMove = i;
				}
				i++;
				if (alpha >= beta) {
					break;
				}
			}
			parent.bestMove = moves.get(bestMove);
			updateCache(transpositionTable, parent.board, depth, value);
			return value;
		}
	}

	public final Board board;
	private int bestMove;

	AlphaBetaNode(Board board) {

		this.board = board;
	}

	public int getBestMove() {
		    clearTPTable();
			int oldDepth = ArtificialIntelligence.DEPTH;
			int depth;
			for(depth = 1; depth <= oldDepth; depth++) {
				ArtificialIntelligence.DEPTH = depth;
					int val = alphaBeta(this, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

			}
		ArtificialIntelligence.DEPTH = oldDepth;

		return this.bestMove;
	}

}