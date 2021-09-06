package artificialintelligence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithmRunner {
public static int N =  50;
public static ChessPlayer[] population = new ChessPlayer[N];
public static HashMap<ChessPlayer, Integer> winCounts;
public static final int GAMES_PER_ROUND = 7;
public static void main(String... args){

    ChessPlayer adam = new ChessPlayer();
    for(int i = 0;i<N;i++){
        if(i%3 == 4){
            population[i] = new ChessPlayer();
        }else{
            population[i] = adam.getChild();
        }
    }
    ArtificialIntelligence.main(null);
    for(int gen = 1;;gen++){
        ChessPlayer.MUTATION_MAGNITUDE = 2 /Math.pow(gen, .7);
        ChessPlayer.MUTATION_CHANCE = 2 / Math.pow(gen, .7);
        ArtificialIntelligence.DEPTH = (int) ((gen/15) + 1);
        simulateGames(gen);
        getNewPopulation();
        //System.out.println(java.util.Arrays.deepToString(population[0].values));
    }
}
public static void shuffle(Object[] stuff){
    int l = stuff.length;
    for(int i = 0;i<l;i++){
        int index = (int) (i + ((l - i) * Math.random()));
        Object thisThing = stuff[i];
        Object next = stuff[index];
        stuff[index] = thisThing;
        stuff[i] = next;
    }
}
public static void simulateGames(int gen){
    winCounts = new HashMap<>();
    for(ChessPlayer cp: population) {
        winCounts.put(cp, 0);
    }
    //System.out.println(Arrays.toString(population));
    //shuffle(population);
    //System.out.println(Arrays.toString(population));
    //System.exit(0);
    for(int i = 0;i<GAMES_PER_ROUND;i++) {
        shuffle(population);
        for(int j = 0;j<N;j+=2) {
            ChessPlayer winner = duel(population[j], population[j+1],25 + 15 * (gen/15));
            if(winner!=null)
            winCounts.put(winner, winCounts.getOrDefault(winner, 0) + 1);
        }
    }

}
public static ChessPlayer duel(ChessPlayer white, ChessPlayer black, int cap){
    //System.out.println(Arrays.deepEquals(white.values, black.values));
   //  System.out.println(Arrays.toString(white.dna));
    GeneticChess board = new GeneticChess();
    int ply = 0;
    HashMap<List, Integer> counts = new HashMap<>();
    for(ply = 0;ply < cap && !board.getPossibleMoves((ply & 1) == 1).isEmpty() && !board.isGameOver();ply++){

        GeneticChess oldBoard = board;
        counts.put(Arrays.asList(oldBoard.state), counts.getOrDefault(Arrays.asList(oldBoard.state), 0) + 1);
        // if(counts.get(Arrays.asList(oldBoard.state)) >= 3){
           // return null;
        // }
        board = (GeneticChess) board.makeMove( (new AlphaBetaNode(board)).getBestMove(), (ply & 1) == 1);

        ((ply & 1) == 1?black:white).pastBoards.add(Arrays.asList(oldBoard.state));
       // try{Thread.sleep(500);}catch(Throwable t){}
    if(ply%1 == 0){
        ArtificialIntelligence.setMainBoard(board);
    }
    }
    // System.out.println("Game ended after " + ply);
    GeneticChess.currPlayer = new ChessPlayer();
    if(board.getValue() > 150){
        return black;
    }
    if(board.getValue() < -150){
        return white;
    }
    else{
        return null;
    }

}
public static void getNewPopulation(){
    ArrayList<ChessPlayer> newPlayers = new ArrayList<>();
    for(ChessPlayer p: population){
        if(winCounts.get(p) > GAMES_PER_ROUND / 2){
            System.out.println("Winner!");
            newPlayers.add(p);
            System.out.println(Arrays.toString(p.dna));
        }
    }
    if(newPlayers.isEmpty()){
        newPlayers.add((new ChessPlayer()).getChild());
    }
    if(!newPlayers.isEmpty()){
        int origSize = newPlayers.size();

        for(int i = 0;i<N - origSize;i++){
            newPlayers.add(newPlayers.get(i%origSize).getChild());
        }

    population = new ChessPlayer[N];
        for(int i = 0; i< population.length; i++){
            population[i] = newPlayers.get(i);
            population[i].pastBoards.clear();
        }
    }
}
}
