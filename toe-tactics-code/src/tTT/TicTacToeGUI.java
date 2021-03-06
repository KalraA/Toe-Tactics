package tTT;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TicTacToeGUI extends JFrame
{
	//unimportant
	private static final long serialVersionUID = 1L;
	//size of the board, in this case 19x19
	public static final int bSize = 15;
	//the number you need in a row to win
	public static final int rows = 5;
	public static final boolean aiFirst = false;
	//this is how i hard code the ai's first move, it goes to a center squre
	public static final int fMove = (int) ((int) (bSize + 1)*(Math.floor(bSize/2)));
	//this determines the number of possible wins on the board. this formula works
	public static int winsL = (4*bSize*bSize - 4*(rows - 1)*bSize - 2*(rows - 1)*(bSize - (rows - 1)));
	//unimportant
	private static final String TITLE="Tic Tac Toe";
	private static final int WIDTH=450;
	private static final int HEIGHT=600;
	int counter = 0;
	//this is how deep the minimax algorithm goes. so depth three means it calculates 3 moves deep and uses those evals
	private static final int MaxDepth = 3;
	//unimportant
	private Container content;
	private JLabel result;
	private JButton[] cells;
	private JButton exitButton;
	private JButton initButton;
	private CellButtonHandler[] cellHandlers;
	private ExitButtonHandler exitHandler;
	private InitButtonHandler initHandler;
	//whose turn it is. Also noughts = O and crosses = X
	private boolean noughts;
	//if the game has ended
	private boolean gameOver;
	//this is an integer array that holds the number of wins and each winning position. Also the board looks like this
	/*
	 * 1 2 3
	 * 4 5 6
	 * 7 8 9
	 * and wins would have wins[0][0] = 1 wins[0][1] = 2 wins[0][2] = 3, which would represent the win that is the three at the top
	 */
	public int[][] wins = new int[winsL][rows];
	//this is allocates memory for the moves being generated by the computer 
	int[][] moveBuffers = new int[MaxDepth + 1][bSize * bSize];
	//this is something im trying to work on. The idea is that I can store a score on each move and then sort them according to score
	// that way they are looked at in a specific order, but it doesnt seem to be working out :p
	int[][] analyzeBuffers = new int[MaxDepth + 1][bSize * bSize];
	//ignore this
	private boolean doubleAi = false;
	private Random random = new Random();
	//noughts array is an array of all the positions O's have occupied. it could be {1, 2, 5} and it starts at {}
	private ArrayList<Integer> noughtsArray = new ArrayList<Integer>();
	//crosses array is noughts array for Xs
	private ArrayList<Integer> crossesArray = new ArrayList<Integer>();
	//and array of all empty squares. Squares are removed as we keep going.
	private ArrayList<Integer> emptyArray = new ArrayList<Integer>();
	//Kmoves is an array containing all "King moves" from a specific square. This helps me trim down the number of moves to look at.
	private int[] Kmoves = new int[] {1, -1, -bSize, bSize, -bSize + 1, -bSize - 1, bSize + 1, bSize - 1};
//this is all gui stuff, not important
	public TicTacToeGUI()
	{
		//Necessary initialization code
		setTitle(TITLE);
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		//Get content pane
		content=getContentPane();
		content.setBackground(Color.pink.darker());
		
		//Set layout
		content.setLayout(new GridLayout(bSize + 1,bSize));

		//Create cells and handlers
		cells=new TTTButton[bSize*bSize];
		cellHandlers=new CellButtonHandler[bSize*bSize];
		for(int i=0; i<bSize*bSize; i++)
		{
			char ch=(char)('0'+i+1);
			cells[i]=new TTTButton(""+ch, i);
			cellHandlers[i]=new CellButtonHandler();
			cells[i].addActionListener(cellHandlers[i]);
		}
		
		//Create init and exit buttons and handlers
		exitButton=new JButton("EXIT");
		exitHandler=new ExitButtonHandler();
		exitButton.addActionListener(exitHandler);
		initButton=new JButton("CLEAR");
		initHandler=new InitButtonHandler();
		initButton.addActionListener(initHandler);
		
		//Create result label
		result=new JLabel("Noughts", SwingConstants.CENTER);
		result.setForeground(Color.white);
						
		//Add elements to the grid content pane
		for (int i=0; i<bSize*bSize; i++)
		{
			content.add(cells[i]);
		}
		content.add(initButton);
		content.add(result);
		content.add(exitButton);
		
		//Initialize
		init();
	}
	//this is just an algorithm that fills in the wins array with every possible winning combination given
	//the board size and how many in a row you need to win. this also adds the squares to the empty array
	public void makeWins()
	{	
		wins = perms(bSize, rows);
		for (int q = 0; q < bSize*bSize; q++) 
		{
			emptyArray.add(q);
		}
	}
	//This initializes the game. 
	public void init()
	{
		//Initialize booleans
		//it starts as the AI's turn
		noughts=false;
		gameOver=false;
		//Initialize text in buttons
		for(int i=0; i<bSize*bSize; i++)
		{
			//if its the hard coded first move, make it an X else leave it blank
				cells[i].setText(" ");
		}
		
		//Initialize result label
		result.setText("Noughts");
		crossesArray.clear();
		noughtsArray.clear();
		makeWins();
		setVisible(true);
		//switch it to players turn
		noughts = !noughts;
		if (aiFirst) {
		crossesArray.add(fMove);
		emptyArray.remove(emptyArray.indexOf(fMove));
		cells[fMove].setText("X");
		}
	}
	//this actually generates all the wins, u dont really need to see this.
	public int[][] perms (int n, int m) 
	{
		int a = 0;
		int[][] arr = new int[winsL][m];
		
		for (int i = 0; i < n*n; i++)
		{
			if (i%n < n - (m - 1))
			{
				int b = 0;
				for (int j = 0; j < m; j++) {
				arr[a][j] = i + b;
				b++;
				/*arr[a][2] = i + 1;
				arr[a][3] = i + 2;
				arr[a][0] = i + 3; */
				}
				a++;
				counter++;
			}
		}
		for (int i = 0; i < n*n; i++)
		{
			if (i < n*n - n*(m - 1))
			{
				for (int j = 0; j < m; j++) {
				arr[a][j] = i + j*n;
				}
		//		arr[a][2] = i + n;
			//	arr[a][3] = i + 2*n;
				//arr[a][0] = i + 3*n;
				a++;
				counter++;
			}
		}
		for (int i = 0; i < n*n; i++)
		{
			if (((i - (m-1)*n) >= (m-1)) && (i%n >= (m-1)))
			{
				for (int j = 0; j < m; j++) {
					arr[a][j] = i - j*(n + 1);	
				}
//				arr[a][2] = i - n - 1;
	//			arr[a][3] = i - (2*(n + 1));
		//		arr[a][0] = i - (3*(n + 1));
				a++;
			}
		}
		for (int i = 0; i < n*n; i++)
		{
			if ((i - (m-1)*n >= 0) && i%n < n-(m-1))
			{
				for (int j = 0; j < m; j++)
				{
				arr[a][j] = i - j*(n - 1);
				}
	//			arr[a][2] = i - n + 1;
		//		arr[a][3] = i - 2*(n - 1);
			//	arr[a][0] = i - 3*(n - 1);
				a++;
			}
		}
	return arr;
	}
//this checks to see if a player has won the game
	//returns 1 if crosses have won, and 2 if noughts have won, and 0 if no one has won
	public int checkWinner()
	{
		int winner = 0;
		for (int i = 0; i < winsL; i++)
		{
			ArrayList<Integer> lst = new ArrayList<Integer>();
			for (int j = 0; j < rows; j++)
			{
				lst.add(wins[i][j]);
			}
			if ((crossesArray.containsAll(lst))) {
				winner = 1;
			}
			if ((noughtsArray.containsAll(lst))) {
				winner = 2;
			}
		}
		
		return winner;
	}
	
	//computer needs to see in the future.
	//This function allows the computer to play into the future, so that it can calculate what the position will look like.
	public void aiNext (int i) 
	{
		//add the move to the given array based on whose turn it is
		if (!noughts) {
			crossesArray.add(i);
		} else {
			noughtsArray.add(i);
		}
		
//		adjacentArray.remove(i);
//		
//		for(int k = 0; k < emptyArray.size(); k++) {
//			int kInt = emptyArray.get(k);
//			
//			for(int m = 0; m < adjSquares.length; m++) {
//				if(adjSquares[m] == k) {
//					
//				}
//			}
//		}
//		//switch turns and remove it form the empty array
		noughts = !noughts;
		emptyArray.remove(emptyArray.indexOf(i));
	}
	//this allows the ai to takeback the move it played.
	public void aiBack (int i) 
	{
		//switch back the turns
		noughts = !noughts;
		//remove the move from a given array
		if (!noughts) 
		{
			crossesArray.remove(crossesArray.indexOf(i));
		} else {
			noughtsArray.remove(noughtsArray.indexOf(i));
		}
		//put it back into the empty array
		emptyArray.add(i);
	}
	//this is a scoring function
	/*
	 * it takes each x and o and decides how many winning possibilities it can be a part of
	 * ontop of that it takes into account how many x's or o's each player has in a particular win
	 * and if a win has both an x and an o, then its useless and so we dont count it.
	 */
	public int analyze () 
	{
		double c = 5;
	
		int pScore = 0;
//		int winn = (checkWinner());
//		if (winn == 1) {
//			return Integer.MAX_VALUE;
//		} else if (winn == 2) {
//			return Integer.MIN_VALUE;
//		}
		int j = 0;
		int k = 0;
		boolean c5 = false;
		boolean n5 = false;
		for (int i = 0; i < winsL; i++)
		{			
			boolean c4 = false;
			boolean n4 = false;
			if (i > counter) {
				c = 5;
			}  else {
				c = 5;
			}
			double a = 1;
			double b = 1;
			int ccounter = 0;
			int ncounter = 0;
			int c2counter = -10;
			int n2counter = -10;
			for (int h = 0; h < rows; h++)
			{
				
				for (int l = 0; l < crossesArray.size(); l++) {
					if (crossesArray.get(l) == wins[i][h])
					{
						ccounter++;
						if (h-c2counter == 1) {
							a*=2;
						}
						c2counter = h;
						a *= c;
						if (ccounter == rows-1) {
							c4 = true;
						}
						if (ccounter == rows) {
							c5 = true;
						}
					}
				}
				for (int v = 0; v < noughtsArray.size(); v++) {
					if (noughtsArray.get(v) == wins[i][h])
					{
						ncounter++;
						if (h-n2counter == 1) {
							b *= 2;
						}
						n2counter = h;
						b *= c + 1;
						if (ncounter == rows-1) {
							n4 = true;
						}
						if (ncounter == rows) {
							n5 = true;
						}
					}
				}
			}
			if (a > 1 && b > 1) {
			} else if (a > 1) {
				 pScore += a;
				if (c4) {
					j++;
				}
			} else if (b > 1) {
				if (n4) {
					k++;
				}
				pScore -= b;
			}
		}
		if (c5) {
			pScore = Integer.MAX_VALUE;
		} else if (n5) {
			pScore = Integer.MIN_VALUE;
		} else if (k > 1 && j == 0 && !noughts) {
			pScore = Integer.MIN_VALUE + 5;
		} else if (j > 1 && k == 0 && noughts) {
			pScore = Integer.MAX_VALUE - 5;
		} else if (k > 0 && noughts) {
			pScore = Integer.MIN_VALUE + 3;
		} else if (j > 0 && !noughts) {
			pScore = Integer.MAX_VALUE - 3;
		}
		
		return pScore;
	}
	//checks to see if n is adjacent to anything in a given lst, returns a boolean
	boolean adjacent (int n, ArrayList<Integer> lst) {
		for (int i : lst) {
			for (int j : Kmoves) {
				if (i == n + j) {
					return true;
				}
			}
		}
		return false;
	}
	//quicksort and partition are part of the sorting algorithm
	int partition(int[] moves, int[] anal, int left, int right, boolean max)
	{
	      int i = left, j = right;
	      int tmp;
	      int pivot = anal[(left + right) / 2];
	     
	      while (i <= j) {
	    	  if (max) {
	            while (anal[i] > pivot)
	                  i++;
	            while (anal[j] < pivot)
	                  j--;
	    	  } else {
	    	    while (anal[i] < pivot)
	                  i++;
	            while (anal[j] > pivot)
	                  j--;
	    	  }
	            if (i <= j) {
	                  tmp = moves[i];
	                  moves[i] = moves[j];
	                  moves[j] = tmp;
	                  
	                  tmp = anal[i];
	                  anal[i] = anal[j];
	                  anal[j] = tmp;
	                  
	                  i++;
	                  j--;
	            }
	      };
	     
	      return i;
	}
	 
	void quickSort(int moves[], int[] anal, int left, int right, boolean max) {
	      int index = partition(moves, anal, left, right, max);
	      if (left < index - 1)
	            quickSort(moves, anal, left, index - 1, max);
	      if (index < right)
	            quickSort(moves, anal, index, right, max);
	}

/*
 * This is the real deal
 * lbound and ubound are alpha and beta
 * boolean max is whether its max's turn or min's turn
 * int depth is how deep the minimax algorithm currently is.
 */
	
	private int[] Minimax (int depth, boolean max, int lbound, int ubound)
	{ 	
		//array containing all moves we want to check
		int[] moves = moveBuffers[depth];
    	int[] analysis = analyzeBuffers[depth];
		
		int movesCount = 0;
		//this fills up the moves array with squares adjacent to either crosses or noughts
		
		for (int i = 0; i < emptyArray.size(); i++) {
			//if (true || crossesArray.size() < bSize*bSize/15 && (adjacent(emptyArray.get(i), crossesArray) || adjacent(emptyArray.get(i), noughtsArray))){
			moves[movesCount] = emptyArray.get(i);
			if (depth != MaxDepth) {
			aiNext(moves[movesCount]);
			analysis[movesCount] = analyze();
			aiBack(moves[movesCount]);
			}
			movesCount ++;
			//}
		
		}
		int movesPlay;
		if (depth == MaxDepth) {
			movesPlay = movesCount;
			//System.out.println("last leaf");
		} else {
			movesPlay = Math.min(movesCount, 10);
			//System.out.println(depth);
		}
		quickSort(moves, analysis, 0, movesCount -1, max);
//		System.out.println(max);
//		for(int i = 0; i < movesCount - 1; i++) {
//				System.out.println(analysis[i]);
//			}
//			
//			aiNext(moves[i]);
//			if(analyze() != analysis[i]) {
//				throw new RuntimeException();
//			}
//			aiBack(moves[i]);
//		}
		
		//System.out.println(noughtsArray.toString() + crossesArray + emptyArray);
		//boolean move = false;
//		for (int i = 0; i < movesCount; i++) {
//			int a = emptyArray.get(i);
//			//if (adjacent(a, crossesArray) || adjacent(a, noughtsArray)) {
//				moves[i] = a;
//				//move = true;
////			} else {
////				moves[i] = -1;
////			}
//		}
		//best move is the current best move that it has. if nothing is found it is set to negative 1
		int bestMove = -1;
		//the current score given to the current best move
		int bestMoveValue = 0;
		//for each move we want to check
		for (int k = 0; k < movesPlay; k++) {
			//move we are checking is called minMove, really a bad name
				int minMove = moves[k];
				//play the move in the arrays
				aiNext(minMove);

				//calculated score of move we are looking at
				int value;
				
				//if we are not at our max depth and the game is not over
				if (depth < MaxDepth && (0 == (checkWinner()))) {
					//if this is not the first move we are looking at, apply alpha beta pruning
					if (k > 0) {
						if (max) {
							value = Minimax(depth + 1, !max, bestMoveValue, ubound)[1];
						} else {
							value = Minimax(depth + 1, !max, lbound, bestMoveValue)[1];
						}
					} else {
						value = Minimax(depth + 1, !max, lbound, ubound)[1];
					}
					
				} else {
					//if we are at a leaf node, then return the score for given position
					value = analyze();
				}
				//if this is the first move we are looking at, or it is better than the current move we are looking at
				if (bestMove < 0 ||
				   (max && value > bestMoveValue) || 
				   (!max && value < bestMoveValue))
				{
					// update the values
					bestMoveValue = value;
					bestMove = minMove;
					//if we broke the bounds, then stop calculating (alpha beta pruning)
					if (lbound > ubound) {
						aiBack(minMove);
						break;
					}
				}
				//play back the move we were calculating
				aiBack(minMove);
			}
		//return the best move, and the best move's score
		
		return new int[]{bestMove, bestMoveValue};	
	}

	
	public static void main(String[] args)
	{
		//Create TicTacToe object
		TicTacToeGUI gui=new TicTacToeGUI();
		
	}
	//this is where turns and shit happen, im really tired so im not commenting that. Ask me any questions and make it better if u can!!
	private class CellButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//If game over, ignore
			if(gameOver)
			{
				return;
			}
			
			//Get button pressed
			TTTButton pressed=(TTTButton)(e.getSource());
			
			//Get text of button
			String text=pressed.getText();
			
			//If noughts or crosses, ignore
			if(text.equals("O") || text.equals("X"))
			{
				return;
			}
			
			//Add nought or cross
			if(noughts)
			{
				pressed.setText("O");
				noughtsArray.add(pressed.getPosition());
			}
			else
			{
				pressed.setText("X");
				crossesArray.add(pressed.getPosition());
			}
			emptyArray.remove(emptyArray.indexOf(pressed.getPosition()));
			//Check winner
			if((checkWinner()) > 0)
			{
				//End of game
				gameOver=true;
				
				//Display winner message
				if(noughts)
				{
					result.setText("Noughts win!!");
				}
				else
				{
					result.setText("Crosses win!");
					
					if(doubleAi) {
						init();
					}
				}
			}
			else if ((noughtsArray.size() + crossesArray.size()) == bSize*bSize){
				gameOver = true;
				result.setText("Draw!");
				
				if(doubleAi) {
					init();
				}
			}
			else
			{
				for (int i = 0; i < winsL; i++)
				{			
					boolean a = false;
					boolean b = false;
					
					for (int h = 0; h < rows; h++)
					{
						for (int l = 0; l < crossesArray.size(); l++) {
							if (crossesArray.get(l) == wins[i][h])
							{
								a = true;
								break;
							}
						}
						for (int v = 0; v < noughtsArray.size(); v++) {
							if (noughtsArray.get(v) == wins[i][h])
							{
								b = true;
								break;
							}
						}
					}
					if (a && b) {
						for(int n = 0; n < rows; n++) {
							wins[i][n] = wins[winsL - 1][n];
						}
						winsL--;
					}
				}
				
				//Change player
				noughts=!noughts;

				//Display player message
				if(noughts)
				{
					result.setText("Noughts");
				}
				else
				{
					result.setText("Computer");
					
					int[] m = Minimax(0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
					System.out.println(m[1]);
					
					
					ActionEvent aClick = new ActionEvent(cells[m[0]], 1, "aiMove");
					this.actionPerformed(aClick);
					
					if(doubleAi) {
						makeRandomMove();
						
					}
				}
			}
		}
		
		public void makeRandomMove() {
			int index = random.nextInt(emptyArray.size());
			ActionEvent aClick = new ActionEvent(cells[emptyArray.get(index)], 1, "aiMove");
			this.actionPerformed(aClick);
			
		}
	}

	
	private class ExitButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}

	private class InitButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			init();
		}
	}
}