/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artificialintelligence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;

/*
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
			//old pre-sort heuristics
		if(DO_PRESORT && depth >= ArtificialIntelligence.DEPTH) {
				HashMap<Integer, Integer> values = new HashMap<>();
				for (Integer move : moves) {
					values.put(move, transpositionTableOpp.getOrDefault(parent.board.makeMove(move, true), new Pair<Integer, Integer>(0, 0)).b);
				}

				if (maximizingPlayer) {
					moves.sort(new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {
							return (values.get(o2) - values.get(o1));

						}
					});
				} else {
					moves.sort(new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {
							return (values.get(o1) - values.get(o2));

						}
					});
				}
		}
 */


/**
 *
 * @author rohan
 */
public class AlphaBetaNode {
	public static final boolean DO_PRESORT = false;
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

	static int a = 0;

	public static int alphaBeta(AlphaBetaNode parent, int depth, int alpha, int beta, boolean maximizingPlayer, int maxDepth, AlphaBetaNode grandparent) {
		a++;
		HashMap<Board, Pair<Integer, Integer>> transpositionTable = maximizingPlayer ? transpositionTableMax : transpositionTableMin;

		if(isCacheHit(transpositionTable, parent.board, depth)){
			return transpositionTable.get(parent.board).b;
		}

		// forever games are draws:

		if(depth != maxDepth && (maxDepth - depth) % 2 == 0 && parent.board.equals(grandparent.board)){
			return 0;
		}

        List<Integer> moves = parent.board.getPossibleMoves(maximizingPlayer);

		//old pre-sort heuristics
		if(DO_PRESORT && (maxDepth - depth) <= 2 ) {
			HashMap<Integer, Integer> values = new HashMap<>();
			for (Integer move : moves) {
				values.put(move, transpositionTable.getOrDefault(parent.board.makeMove(move, maximizingPlayer), new Pair<>(0, 0)).b);
			}
			moves.sort(Comparator.comparingInt(values::get));
			if(maximizingPlayer){
				Collections.reverse(moves);
			}
		}



        if (depth == 0 || parent.board.isGameOver()) {
            int value = parent.board.getValue();
            updateCache(transpositionTable, parent.board, depth, value);
            return value;
        }
        if (moves.isEmpty()) {
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


		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;

			int bestMove = 0;
			int i = 0;

			for (Integer move : moves) {
				int oldValue = value;
				value = Math.max(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(move, maximizingPlayer)), depth - 1, alpha, beta, !maximizingPlayer, maxDepth, grandparent));
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
				value = Math.min(value, alphaBeta(new AlphaBetaNode(parent.board.makeMove(move, maximizingPlayer)), depth - 1, alpha, beta, !maximizingPlayer, maxDepth, grandparent));
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
		    //clearTPTable();
			//int depth;
			//int val = 0;
			// long origTime = System.nanoTime();
			// for(depth = 2; System.nanoTime() - origTime <= 1e9; depth++) {
					// a = 0;
					int val = alphaBeta(this, ArtificialIntelligence.DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true, ArtificialIntelligence.DEPTH, this);
					// System.out.println(a);
			// }
		System.out.println(val);
		return this.bestMove;
	}

}