package artificialintelligence;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * The way we represent a chess board
 *
 * @author rohan
 */
public class MemoizedChess extends Chess {

    private int internalMemoizedValue;

    /**
     * This array stores all the values for O(1) access
     */
    public static final int[] VALUES = new int[7];

    static {
        VALUES[EMPTY] = 0;
        VALUES[PAWN] = PAWN_VALUE;
        VALUES[KNIGHT] = KNIGHT_VALUE;
        VALUES[BISHOP] = BISHOP_VALUE;
        VALUES[ROOK] = ROOK_VALUE;
        VALUES[QUEEN] = QUEEN_VALUE;
        VALUES[KING] = KING_VALUE;
    }


    /**
     *
     * @return
     */
    public static int getCentralizationPremium(int tile, int x, int y, boolean isAIPlayer){

        switch(tile){
            case PAWN:
            case KNIGHT:
            case BISHOP:
            case ROOK:
                double xCenterMultipler = 1 - (Math.abs(3.5 - x) / 3.5);
                double yValue = (isAIPlayer ? 1 : -1) * (y - 3.5) * 20;

                return (int) ((xCenterMultipler * yValue) * (x == 4 ? 2 : 1));
            case KING:
                double xKingCenterMultipler = 1 - (Math.abs(3.5 - x) / 3.5);
                double yKingValue = (isAIPlayer ? -1 : 1) * y * 30;

                return (int) (xKingCenterMultipler * yKingValue);

            default:
                return 0;
        }

    }

    /**
     * Creates a chess board with the standard legal starting position The AI is
     * black
     */
    public MemoizedChess(boolean aiIsBlack) {
        super(aiIsBlack);
    }

    public MemoizedChess(long[] inState) {
        super(inState);
    }

    public static void setTileAtSpot(long[] inState, int spot, int tile) {
        inState[spot >> 3] = manipulateState(inState[spot >> 3], spot & 7, tile);
    }

    public static long manipulateState(long state, long spot, long tile) {
        return ((state & (~((((long) (255)) << ((spot << 3)))))) + (tile << (spot << 3)));

    }


    @Override
    public int getValue() {

        return internalMemoizedValue;

    }

    public Board makeMove(int move, boolean isAIPlayer){

        int start = move & 63;
        int end = (move >> 6) & 63;
        int startTile = getTileAtSpot(start);
        int endTile = getTileAtSpot(end);

        int newInternalValue = this.internalMemoizedValue + (isAIPlayer ? 1 : -1) * VALUES[endTile & 7] + (isAIPlayer ? 1 : -1) *
                (getCentralizationPremium(startTile, start & 7, start >> 3, isAIPlayer) - getCentralizationPremium(startTile, end & 7, end >> 3, isAIPlayer));


        long[] state = this.getNewState(move, isAIPlayer);
        MemoizedChess out = new MemoizedChess(state);

        out.internalMemoizedValue = newInternalValue;

        return out;
    }

}
