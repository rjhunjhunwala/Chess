package artificialintelligence;

import java.util.HashSet;
import java.util.List;

public class ChessPlayer {
    public static  double MUTATION_CHANCE = 2;
    public static double MUTATION_MAGNITUDE = 2 ;
    public HashSet<List> pastBoards = new HashSet<>();

    public int[] dna= new int[32];
    {
        dna[1] = 60;
        dna[2] = 10;
        dna[3] = 0;
        dna[4] = -10;
        dna[5 + Chess.EMPTY] = 0;
        dna[5 + Chess.PAWN] = 100;
        dna[5 + Chess.KNIGHT] = 300;
        dna[5 + Chess.BISHOP] = 325;
        dna[5 + Chess.ROOK] = 500;
        dna[ 5 + Chess.QUEEN] = 900;
        dna[5 + Chess.KING] = Chess.KING_VALUE;
        dna[20] = 50;
        dna[21] = 35;
        dna[22] = 30;
        dna[23] = 25;
        dna[24] = 0;
        dna[25] = 10;
        dna[26] = 20;
        dna[27] = 30;
        dna[28] = 40;
        dna[29] = 50;
        dna[30] = 60;
        dna[31] = 70;
    }

    public ChessPlayer(){

   }

   public ChessPlayer getChild(){
       ChessPlayer child = new ChessPlayer();
       //System.out.println("here");
       for(int i = 0;i<this.dna.length;i++){

                   int val = this.dna[i];
                   if(Math.random() < MUTATION_CHANCE){
                    val+= (100 * (Math.random() * MUTATION_MAGNITUDE - MUTATION_MAGNITUDE/2));
                   }
                   child.dna[i] = val;
               }
    return child;
    }

    }

