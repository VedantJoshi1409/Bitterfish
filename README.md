
# Bitterfish

## About
A Chess engine I made for fun\
Has 2 options, a custom GUI and using the UCI (Universal Chess Interface)

## Features

### UCI Options
- Enable or disable NNUE Evaluation
- Clear transposition tables
- Set the path to the tablebase directory 


### Move Generation:
- Moves are generated using bitboards and attack masks
- Attack mask information is stored and updated inside a Board object
- A 64 bit integer is used to represent a move with all the information needed encoded into their respective group of bits

### Search
- An alpha beta Negamax search is used
- Move reordering based on attacker and victim values of a move
- The principle variation is collected and used for iterative deepening
- Killer moves are collected and used to reorder moves
- Transposition table using Zobrist hashing
- Quiescence search 
- Syzygy tablebase for endgames

### Evaluation
- #### Handcrafted evaluation
	- Piece Square Tables
	- King area safety
	- Piece safety
	- Endgame tapering
	- Piece mobility 
	- Pawn structure
- #### NNUE
	- The [Stockfish NNUE](https://tests.stockfishchess.org/nns) is implemented through [my library](https://github.com/VedantJoshi1409/stockfish_nnue_probe) 

## Usage
A minimum of Java Runtime Environment version 21 is required.
Download the latest GUI or UCI release and play some chess!

## Changelog

#### 10.0 - 1st July 2024
- Implemented UCI option and a new menu for the custom GUI. Figured out how to compile and create executables as well!

#### 9.0 - 18th July 2024
- Implemented evaluation through Stockfish's NNUE using my [library](https://github.com/VedantJoshi1409/stockfish_nnue_probe)

#### 8.0 - 20th March 2024
- Implemented access to [lila-tablebase](https://github.com/lichess-org/lila-tablebase) for 6-piece tablebases. Planning to implement support for local tablebases due to the online one not being fast enough for usage in the search tree farther than ply 1. Also, offline access is nice!

#### 7.0 - 16th May 2024
- Implemented the fifty-move rule as well as fixed a bug with the GUI that caused possible moves not to show up if you went first

#### 6.0 - 9th March 2024
- #### Turns out my matchmaker flipped the wins and losses for the different versions so I thought that any detrimental changes I made were beneficial
	- After 4 hours of value testing protection penalties seem to be useless
	-  After another 4 hours of value testing, hopefully, found close to perfect mobility values and defense scores
	- Implemented doubled pawn penalties
	- Tweaked king piece table endgame tapering
	- Re-enabled PV search since PV collection is now fixed

#### 5.0 - 24th February 2024
- #### Fixed Principle Variation collection
	- Originally it was just whatever the last alpha move was on that depth no matter what move was played before
	- Implemented TSCP's pv table collection
	- Implemented a search node record system for debugging transposition table and other search issues

#### 4.0 - 23rd February 2024
- #### Improved evaluation
	- Implemented mobility score based on how many squares each piece can move to
	- Implemented defense score based on king safety
	- Implemented protection score based on whether a piece is protected or not
	
#### 3.0 - 23rd February 2024
- #### Refactored the entire project
	- Created a Board class to replace the previous system of using a long[] array of length 20+
	- Recreated and optimized move generation
	- Changed from using new board instances to a 64 bit integer to represent each move
	- Fixed move generation bugs
	- Implemented PeSTO position tables
	- Stopped using ArrayLists everywhere 
	
#### 2.0 - 9th August 2023
- Fixed search not registering three-fold repetition 

#### 1.0 - 19th June 2023
- Working move generation
- Working alpha beta search
- Working evaluation

## Credits
- The [Chess Programming YouTube channel](https://www.youtube.com/@chessprogramming591) for explaining a lot of search and search optimization techniques
- [Stockfish](https://github.com/official-stockfish/Stockfish) for their NNUEs
- [Fathom](https://github.com/jdart1/Fathom) for their Syzygy Tablebase probe


 
