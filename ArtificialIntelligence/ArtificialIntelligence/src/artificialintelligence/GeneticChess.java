package artificialintelligence;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * The way we represent a chess board
 *
 * @author rohan
 */
public class GeneticChess extends Chess {
   static ChessPlayer currPlayer = new ChessPlayer();
   static int[] distances = new int[64];
   static{
       for(int i = 0;i<64;i++){
           int x = i / 8;
           int y = i & 7;
           distances[i] = (int) Math.ceil(Math.max(Math.abs(x - 3.5), Math.abs(y - 3.5)));
       }
   }

    public GeneticChess() {
        super(true);
    }
    public GeneticChess(long[] longlong){
        super(longlong);
    }

    @Override
    public int getSize() {
        return 8;
    }

    public boolean isInCheck(boolean isAgainstComputerPlayer) {
        int kingLoc = -1;
        for (int i = 0; i < 64; i++) {
            if (getTileAtSpot(i) == ((isAgainstComputerPlayer ? 1 : 0) << 3) + KING) {
                kingLoc = i;
                break;
            }
        }

        for (int a : this.getPossibleMoves(!isAgainstComputerPlayer, true, true)) {
            if (a >> 6 == kingLoc) {
                return true;
            }
        }
        return false;
    }


    public static void setTileAtSpot(long[] inState, int spot, int tile) {
        inState[spot >> 3] = manipulateState(inState[spot >> 3], spot & 7, tile);
    }

    public static long manipulateState(long state, long spot, long tile) {
        return ((state & (~((((long) (255)) << ((spot << 3)))))) + (tile << (spot << 3)));

    }

    @Override
    public int getTileAtSpot(int spot) {
        return (int) ((((state[spot >> 3]) & ((long) 255 << ((spot & 7) << 3))) >> ((spot & 7) << 3)) & 15);
    }

    public int getTileAtSpotSpecial(int spot) {
        return (int) ((((state[spot >> 3]) & ((long) 255 << ((spot & 7) << 3))) >> ((spot & 7) << 3)));
    }

    /**
     * If either side has lost a king the game is over
     *
     * @return
     */
    @Override
    public boolean isGameOver() {
        return Math.abs(this.getValue()) > KING_VALUE / 2;
    }


    //		if(isInCheck(true)){
//			if(getPossibleMoves(true).isEmpty()){
//				return -2 * VALUES[KING];
//			}
//		}
//				if(isInCheck(false)){
//			if(getPossibleMoves(false).isEmpty()){
//				return 2 * VALUES[KING];
//			}
//		}
    public int getValue() {
        if(currPlayer.pastBoards.contains(Arrays.asList(this.state))){
            return 0;
        }

        int value = 0;
        int pieceCount = 64;
        for (int i = 0; i < 64; i++) {
            int piece = getTileAtSpotSpecial(i);
//System.out.println(piece);
            // Branchless == fast, a very quick (in theory) this is the fun way of saying subtract one iff empty
            int empty =  (((~(piece & 15)) & 15) / 15);
            pieceCount -= empty;


//encourage owning material
            value += (((((piece & 8) >> 2) - 1))) * (currPlayer.dna[5 + (piece & 7)]);

            //value += (((((piece & 8) >> 2) - 1))) * currPlayer.dna[GeneticChess.distances[i]] * (1 - empty);
            if ((piece & 7) == KING || (piece & 7) == ROOK) {
                //encourage not moving the king or rook... heavily
                value += (((((piece & 8) >> 2) - 1))) * currPlayer.dna[23];
            }

            if (((i >> 3) > 0 && (piece & 8) == 8) && ((piece & 7) == BISHOP || (piece & 7) == KNIGHT)) {
                //lightly encourage piece developement
                value += currPlayer.dna[22];
            }
            if (((i >> 3) < 7 && (piece & 8) == 0) && ((piece & 7) == BISHOP || (piece & 7) == KNIGHT)) {
                //lightly discourage hostile developement
                value -=   currPlayer.dna[22];
            }

//lightly encourage moving pawns up the board, and discourage enemy advancement
            if ((piece & 7) == PAWN) {
                if ((piece & 8) == (BLACK << 3)) {
                    value += currPlayer.dna[ 24 + (i >> 3)];
                } else {
                    value -= currPlayer.dna[31 - (i >> 3)];
                }
            }
        }
        if (pieceCount < 5) {
            List<Integer> compMoves = getPossibleMoves(true, false, true);
            List<Integer> compChecks = new ArrayList<>();
            for (Integer c : compMoves) {
                compChecks.add((c >> 6) & 63);
            }
            List<Integer> humanMoves = getPossibleMoves(false, false, true);
            List<Integer> humanChecks = new ArrayList<>();
            for (Integer c : humanMoves) {
                compChecks.add((c >> 6) & 63);
            }
            for (Integer c : compMoves) {
                compChecks.add((c >> 6) & 63);
            }
            int start = 0;
            int compKingStartX, compKingStartY;
            for (int i = 0; i < 64; i++) {
                if ((getTileAtSpot(i) & 15) == (KING + (BLACK << 3))) {
                    start = i;
                    break;
                }
            }
            compKingStartX = start % 8;
            compKingStartY = start & 0b111000;
            int compBoxSize = getFloodFillSize(start, humanChecks);
            int humanKingStartX, humanKingStartY;
            for (int i = 0; i < 64; i++) {
                if ((getTileAtSpot(i) & 15) == (KING)) {
                    start = i;
                    break;
                }
            }
            humanKingStartX = start % 8;
            humanKingStartY = start & 0b111000;
            value += (4 - Math.max(Math.abs(humanKingStartX - compKingStartX), Math.abs(humanKingStartY - compKingStartY)));
            int humanBoxSize = getFloodFillSize(start, compChecks);
            value -= (64 - compBoxSize) << 1;
            value += (64 - humanBoxSize) << 1;
        }else{


            //incentivize castling
            if (((getTileAtSpot(2) & 7) == KING) && ((getTileAtSpot(3) & 7) == ROOK)) {
                value += currPlayer.dna[20];
            }
            if ((getTileAtSpot(6) & 7) == KING && ((getTileAtSpot(5) & 7) == ROOK)) {
                value += currPlayer.dna[21];
            }

            //incentivize castling
            if (((getTileAtSpot(58) & 7) == KING) && ((getTileAtSpot(59) & 7) == ROOK)) {
                value -= currPlayer.dna[20];
            }
            if ((getTileAtSpot(62) & 7) == KING && ((getTileAtSpot(61) & 7) == ROOK)) {
                value -= currPlayer.dna[21];
            }
        }
        return value;

    }

    /**
     * The bottom 6 bits represent the initial spot, and the next 6 bits
     * represent the final spot
     *
     * @param move
     * @param isComputerTurn
     * @return
     */
    @Override
    public Board makeMove(int move, boolean isComputerTurn) {
        int tile = getTileAtSpot(move & 63);
        int start = move & 63;
        int end = (move >> 6) & 63;
        boolean cleanUpGhosts = true;
        //"performance"
        long[] newState = {state[0], state[1], state[2], state[3],
                state[4], state[5], state[6], state[7]};
        if ((move & (1 << 12)) > 0) {

            switch (end) {
                case 2:
                    Chess.setTileAtSpot(newState, 0, 0);
                    Chess.setTileAtSpot(newState, 3, ROOK + 24);
                    break;
                case 6:
                    Chess.setTileAtSpot(newState, 7, 0);
                    Chess.setTileAtSpot(newState, 5, ROOK + 24);
                    break;
                case 58:
                    Chess.setTileAtSpot(newState, 56, 0);
                    Chess.setTileAtSpot(newState, 59, ROOK + 16);
                    break;
                case 62:
                    Chess.setTileAtSpot(newState, 63, 0);
                    Chess.setTileAtSpot(newState, 61, ROOK + 16);
            }
        } else {

            if ((tile & 7) == PAWN) {
                //	Sketchy pawn promotion hack
                if (((end >> 3) == 0 || ((end >> 3) == 7))) {
                    tile += (QUEEN - PAWN);
                }
                //double move pawn

                //en passant hacks
                if (end - start == 16) {
                    Chess.setTileAtSpot(newState, end - 8, (1 << 5) + (GHOST_PAWN));
                    cleanUpGhosts = false;
                } else if (start - end == 16) {
                    Chess.setTileAtSpot(newState, end + 8, +(GHOST_PAWN));
                    cleanUpGhosts = false;
                }
//ending en passant hacks
                int specialTile;
                if (((specialTile = getTileAtSpotSpecial(end)) & 31) == 16) {
                    int d = ((specialTile & 32) >> 4) - 1;
//System.out.println(specialTile);
                    Chess.setTileAtSpot(newState, end + (d << 3), 0);
                }

            }

        }

        Chess.setTileAtSpot(newState, start, 0);
        Chess.setTileAtSpot(newState, end, tile & 15);
        for (int i = 0; i < 64; i++) {
            if ((getTileAtSpotSpecial(i) & 31) == 16) {
                if (end != i) {
                    Chess.setTileAtSpot(newState, i, 0);
                }
            }
        }
        return new GeneticChess(newState);
    }
}