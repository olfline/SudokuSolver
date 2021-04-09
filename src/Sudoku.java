import java.util.concurrent.TimeUnit;

public class Sudoku{


	// order:
	// - unsolved Sudoku
	// - for each empty box determine all possibilities
	// - for each empty determined box, test if number is only choice.
	// 	- yes: write into box, delete number from this and other boxes in row,col,grid
	// 	- no: next box




	//public static int [][][] sudoku = new int [9][9];


	//Print sudoku grid
	public static void printSudoku(int [][] su){
		for(int i = 0; i< su.length; i++){
			for(int k = 0; k<su[i].length; k++){
				if(su[i][k] == 0) System.out.print("  ");
				else System.out.print(" " + su[i][k]);
				if(k == 2 || k == 5) System.out.print(" |");
			}
			System.out.println("");
			if(i == 2|| i == 5) System.out.println(" - - - + - - - + - - -");
		}
	}

	//Print determine grid
	public static void printDetermine(int [][][] poss){
		for(int i = 0; i< poss.length;i++){
			System.out.print("ROW: " + i + " ");
			for(int j = 0; j< poss[i].length;j++){
				System.out.println("\t COL: " +j + " ");
				System.out.print("\t\t -- [");
				if(poss[i][j] != null){
					for(int n =0; n<poss[i][j].length; n++){
						System.out.print(" " + poss[i][j][n] + " ");
					}
				}
				System.out.println("]");
			}
		}
	}
	
	//is Number in sudoku Row
	public static boolean isInRow(int su[][], int number, int row){
		for(int i = 0; i<su[row].length; i++){
			if(su[row][i] == number){
				return true;
			}
		}
		return false;
	}
	
	//is number in sudoku col
	public static boolean isInCol(int su[][], int number, int col){
		for(int i = 0; i<su.length; i++){
			if(su[i][col] == number){
				return true;
			}
		}
		return false;
	}
	
	//is number in sudoku grid
	public static boolean isInGrid(int su[][], int number, int grid){

		//   0 1 2   3 4 5   6 7 8 
		// 0       |       |      
		// 1   0   |   1   |   2  
		// 2       |       |      
		//   - - - + - - - + - - -
		// 3       |       |      
		// 4   3   |   4   |   5  
		// 5       |       |      
		//   - - - + - - - + - - -
		// 6       |       |      
		// 7   6   |   7   |   8  
		// 8       |       |       

		if(grid<0 || grid > 8){
			return false;
		}

		for(int i=0; i<3; i++){
			for(int k=0; k<3; k++){
				//grid to [row][col]
				if(su[grid - ((grid)%3) + i][k + 3* ((grid)%3)] == number){
						return true;
				}
			}
		}
		return false;
	}

	
	//determine possibilities for each empty box
	public static int[][][] determine(int [][] sudoku){
		int [][][] back = new int[9][9][];
		for(int i = 0; i<sudoku.length; i++){
			for(int k = 0; k < sudoku[i].length;k++){
				if(sudoku[i][k] <1 || sudoku[i][k] > 9){
					//dont make array, if number is present
					int lang = 0;
					for(int h = 1; h <=9; h++){
						//row, col to grid
						if(!isInRow(sudoku, h, i) && !isInCol(sudoku, h, k) && !isInGrid(sudoku, h, ((i/3 *3 + k/3)))){
							lang += 1;
						}
					}
					back [i][k] = new int[lang];
					int index = 0;
					for(int h = 1; h <= 9; h++){
						//row, col to grid
						if(!isInRow(sudoku, h, i) && !isInCol(sudoku, h, k) && !isInGrid(sudoku, h, ((i/3 *3+ k/3)))){
							back[i][k][index] = h;
							index++;
						}
					}
				}
			}
		}
		return back;
	}

	
	//if only choice in row,col,grid write it into sudoku
	public static void solve(int su[][]){
		int[][][] poss = determine(su);
		//return array
		int suback [][] = su;
		
		System.out.println("Given Sudoku: \n");
		printSudoku(su);
		System.out.println("\n\n");

		while(!isSolved(su)){
			//rows
			for(int i = 0; i<poss.length;i++){
				//cols
				for(int j = 0; j<poss[i].length; j++){
					//possibilities
					if(poss[i][j] != null)
					for(int k = 0; k<poss[i][j].length; k++){
						if(poss[i][j].length == 1 || testRowColGrid(poss, poss[i][j][k], i, j)){
							suback[i][j] = poss[i][j][k];
							poss = removeFromDetermine(poss,poss[i][j][k], i, j);
							//nullpointerexception^
							printSudoku(suback);
							System.out.println("\n\n\n");
							printDetermine(poss);
						//	try{
						//		TimeUnit.SECONDS.sleep(10);
						//	}catch(InterruptedException e){System.out.println(e);}
						}
					}
				}
			}
		}
		System.out.println("Solved Sudoku:");
		printSudoku(suback);
	}

	//test for row,col,grid if only choice in there -> TRUE IF SO
	public static boolean testRowColGrid(int poss[][][], int number, int row, int col){
		
		boolean ret = false;
		
		//test row
		outer:
		for(int i =0; i<poss.length; i++){
			if(poss[i][col] != null){
				for(int n = 0; n < poss[i][col].length; n++){
					if(poss[i][col][n] == number){
						ret = false;
						//System.out.println("row");
						break outer;
					}else{
						ret = true;
						System.out.println("row");
					}
				}
			}
		}

		//test col
		outer2:
		for(int i = 0; i<poss[row].length; i++){
			if(poss[row][i] != null){
				for(int n = 0; n<poss[row][i].length; n++){
					if(poss[row][i][n] == number){
						ret = ret || false;
						//System.out.println("Col");
						break outer2;
					}else{
						ret = true;
						System.out.println("Col");
					}
				}
			}
		}

		//test grid
		outer3:
		for(int i = 0; i<3; i++){
			for(int j = 0; j<3;j++){
				if(poss[row - row%3 +i][col - col%3 +j] != null){
					for(int n = 0; n<poss[row - row%3+i][col - col%3 +j].length; n++){
						if(poss[row - row%3 +i][col - col%3 +j][n] == number){
							ret = ret || false;
						//	System.out.println("grid");
							break outer3;
						}else{
							ret = true;
							System.out.println("grid");
						}
					}
				}
			}
		}
		
		//else return true;
		return ret;
	}

	// TODO remove from row, col and grid!!
	public static int[][][] removeFromDetermine(int[][][] poss, int number, int row, int col){
		
		poss[row][col] = null;		
		//row
		for(int i = 0; i<poss.length; i++){
			if(poss[i][col] != null){
				poss[i][col] = isInRemoveFromArray(poss[i][col], number); 
			}
		}

		//col
		for(int i = 0; i<poss[row].length; i++){
			if(poss[row][i] != null){
				poss[row][i] = isInRemoveFromArray(poss[row][i], number);
			}
		}
		
		//grid
		for(int i = 0; i<3; i++){
			for(int j = 0; j<3; j++){
				if(poss[row-row%3+i][col-col%3 +j] != null){
					poss[row-row%3+i][col-col%3+j] = isInRemoveFromArray(poss[row-row%3+i][col-col%3+j], number);
				}
			}
		}

		return poss;
	}


	public static boolean isSolved(int su[][]){
		for(int i = 0; i<su.length;i++){
			for(int j = 0; j<su[i].length; j++){
				if(su[i][j] < 1|| su[i][j]>9){
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * remove number from array
	 * return array without number
	 * return null if array lenght 1
	 */
	public static int [] isInRemoveFromArray(int [] array, int number){
		for(int i = 0; i<array.length; i++){
			if(array[i] == number){
				if(array.length == 1){
					return null;
				}
				int[] ret = new int [array.length -1];
				int k= 0;
				for(int j = 0;j<ret.length; j++){
					if(array[j] == number){
						k++;
					}
					ret[j] = array[k];
					k++;
				}
				return ret;
			}
		}
		return array;
	}


	public static void main(String [] args){

		// 9 6 3 | 5 8 7 | 2 4 1
		// 5 2 4 | 3 9 1 | 8 7 6
		// 8 7 1 | 6 2 4 | 3 9 5
		// - - - + - - - + - - -
		// 6 1 8 | 4 7 9 | 5 2 3
		// 3 9 7 | 8 5 2 | 6 1 4
		// 2 4 5 | 1 3 6 | 9 8 7
		// - - - + - - - + - - -
		// 7 3 2 | 9 1 5 | 4 6 8
		// 4 8 9 | 7 6 3 | 1 5 2
		// 1 5 6 | 2 4 8 | 7 3 9
		
		
		
		// 9 6   |       |     
		//     4 |       |   7  
		//       |       | 3 9
		// - - - + - - - + - - -
		// 6     |       | 5
		// 3 9   | 8     | 6 1 
		// 2     | 1 3   | 
		// - - - + - - - + - - -
		// 7 3   |     5 | 
		//       | 7     |     2
		//       | 2 4 8 |     9
		
		int [][]sudoku = {
			{9,6,0,0,0,0,0,0,0},
			{0,0,4,0,0,0,0,7,0},
			{0,0,0,0,0,0,3,9,0},
			{6,0,0,0,0,0,5,0,0},
			{3,9,0,8,0,0,6,1,0},
			{2,0,0,1,3,0,0,0,0},
			{7,3,0,0,0,5,0,0,0},
			{0,0,0,7,0,0,0,0,2},
			{0,0,0,2,4,8,0,0,9}
		};

		9, 6 3 5 8 7 2 4 1
		5, 2 4 3 9 1 8 7 6
		8, 7 1 6 2 4 3 9 5
		6, 1 8 4 7 9 5 2 3
		3, 9 7 8 5 2 6 1 4
		2, 4 5 1 3 6 9 8 7
		7, 3 2 9 1 5 4 6 8
		4, 8 9 7 6 3 1 5 2
		1, 5 6 2 4 8 7 3 9

 //		printSudoku(sudoku);

		//int test [] = {1,2,3,4,5};
		//test = isInRemoveFromArray(test,1);
		//for(int i = 0; i<test.length; i++){
		//	System.out.println(test[i]);
		//}
	//	printDetermine(determine(sudoku));
		//solve(sudoku);
		
		//test
		//int test [] = {1,2,3,4,5,6,7};
		//for(int i = 0; i< isInRemoveFromArray(test,3).length; i++){
		//	System.out.println(isInRemoveFromArray(test, 3)[i]);
		//}
		
		Sysytem.out.println(isSolved(




	}
}
