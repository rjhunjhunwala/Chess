I present an AI which has been taught to play a variety of games of perfect information including Chess. Here is a list of games it can play.

<ul>
<li>Chess</li>
<li>Othello/Reversi Classic (8x8)</li>
<li>Othello/Reversi 5x5</li>
<li>Tic-Tac-Toe</li>
<li>4x4 Tic-Tac-Toe where you need 3 of your kind and one of your opponents to win</li>
</ul>

![alt tag](https://github.com/rjhunjhunwala/AI/blob/master/Screenshot.gif)

So, it's a full fledged chess engine. Everything is supported including en passant, and castling. You can also undo moves through the "move" menu. I'd like to take a moment to talk baout how the AI works, and acknowledge some of its weaknesses.

Any two-player game of perfect information can be reduced to a "tree" structure, where the initial position serves as a branch, and each of the set of moves represent sub-branches. Ending states of the game, win/lose/draw, can be thought of as leafs, because they serve as terminal nodes of the tree, so formally, they are called leaf nodes. Conventionally, we call these tree a "game-tree". For a trivial game the game-tree might have a very small complexity, formally, we define the "game-tree-complexity" to be the number of "leaf-nodes" in the tree. Therefore for tic-tac-toe, since the game-tree is trivially small, we define a win to have value one, a draw to have value zero and a loss to have value one. Assuming "perfect-play" we then create a recursive definition of the "value" of a node, as being the value of the best child node iff, it's the computers turn, and the worst child node iff it's the humans turn. We then choose the node with the highest "value". This strategy is known as "mini-max" because we strive to minimize the maximum loss.

Now, chess, and essentially any game of note has as substantially larger game-tree complexity, so we can not parse the whole game tree. Instead we try to maximize a short term heuristic evaluation of the board, which factors in material and positional advantages.

Unfortunatley, the AI is admittedly shortsighted. It can only think about four moves into the future, giving it limited strength in the endgame. It also lacks some of the chess theory that is taught to a human player. We are working on improving the heursitics and pruning the game tree to give it better depth. 
