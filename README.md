# The Turk*

An AI which has been taught to play a variety of games of perfect information, including Chess. Here is a list of games it can play.

<ul>
<li>Chess</li>
<li>Othello/Reversi Classic (8x8)</li>
<li>Othello/Reversi 5x5</li>
<li>Tic-Tac-Toe</li>
<li>4x4 Tic-Tac-Toe variant where you need 3 of your kind and one of your opponents to win (in one line, but in any order) </li>
</ul>

![alt tag](https://github.com/rjhunjhunwala/AI/blob/master/Screenshot.gif)

It's a full fledged chess engine. Everything is supported including en passant, and castling. You can also undo moves through the "move" menu, and choose different difficulties through the difficulty menu. I'd like to take a moment to talk about how the AI works, and acknowledge some of its weaknesses.

Any two-player game of perfect information can be reduced to a "tree" structure, where the initial position serves as a "trunk", and each of the set of moves represent branches, (responses to those moves are sub-branches, etc...). Ending states of the game, win/lose/draw, can be thought of as leafs, because they serve as terminal nodes of the tree, so formally, they are called leaf nodes. Conventionally, we call these tree a "game-tree". For a trivial game the game-tree might have a very small complexity, formally, we define the "game-tree-complexity" to be the number of "leaf-nodes" in the tree. Therefore for tic-tac-toe, since the game-tree is trivially small, we define a win to have value one, a draw to have value zero and a loss to have value one. Assuming "perfect-play" we then create a recursive definition of the "value" of a node, as being the value of the best child node iff, it's the computers turn, and the worst child node iff it's the humans turn. We then choose the node with the highest "value". This strategy is known as "mini-max" because we strive to minimize the maximum loss. 

Now, chess, and essentially any game of note has as substantially larger game-tree complexity, so we can not parse the whole game tree. Instead we try to maximize a short term heuristic evaluation of the board, which factors in material and positional advantages.

Unfortunatley, the AI is admittedly shortsighted. Because chess has such a high "branching factor" (formally, defined as, the (geometric) average number of states accessible from any one state), It can only think about four moves into the future (it'll go to five on the "Hard" setting of difficulty, in the menu, but each move generally takes a handful of seconds as opposed to being almost immediate on four). We do use "alpha-beta pruning" (https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning) in order to not traverse down paths once we know that it is equal to or worse than a current path. This garuntees the same result as the naive implementation, but in the best case (if we get "lucky" in the way we are looking through our moves) will allow us to prune twice as deep. With this pruning, all we have done is slightly speed up 4-ply, and make 5-ply accessible, but additional heursitics (perhaps even agressive pruning heuristics which eliminate moves that *might* be valid) are needed to improve the depth of our analysis and the quality of play.


<hr/>

*The Turk was presented as a mechanical commputer of a sort, which was capable of playing chess, solving the knights tour and even conversing with users in multiple languages. It was noted, that the machine would play agressively, and defeat almost any challenger in a short amount of time. Sometime later, it was noted that this was an elaborate hoax, and a team of human operators were really responsible for the operation of the machine. 

This machine, neither speaks any languages, nor poses any real threat to a well versed chess player. However, what my machine lacks as a polyglot or a chess grandmaster, it makes up for by not concealing a human operator, like the turk

https://en.wikipedia.org/wiki/The_Turk
