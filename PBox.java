import java.util.LinkedList;

public class PBox {    
    private static int CSIZE=4;
    private static int RSIZE=3;
    private static int LSIZE=3;
    
    
    static int box[] = new int[CSIZE*RSIZE*LSIZE];
    static int savedBox[][];

    static PPiece pieces[];

    static LinkedList<int []> solutions = new LinkedList<int []>();

    /*
     * Pieces enetered as array, row by row, starting from bottommost layer.
     * Eg. The letter 'L' would look like: {1,1, 1,0, 1,0} (2 cols, 3 rows, 1 layer)
     *     A 3D plus would be: {0,0,0, 0,1,0, 0,0,0,   0,1,0, 1,1,1, 0,1,0,   0,0,0, 0,1,0, 0,0,0} (3 cols,, 3 rows, 3 layers) 
     */
    static {
	/*
	//original puzzle, 6 solutions
	  String puzzleString =
	    "  1,1,1 - 1,0,0" +
	    "P  1,1,1 - 0,0,1 # 0,1,0 - 0,0,0" +
	    "P  1,1,1 - 0,0,0 # 1,0,0 - 1,0,0" +
	    "P  1,1 - 0,0 # 0,1 - 0,1" +
	    "P  1,1 - 0,0 # 1,0 - 1,0" +
	    "P  1,1,0 - 0,1,0 # 0,0,0 - 0,1,1";
	
	//six piece, 4 solutions
	String puzzleString =
	    "  1,1,1 - 0,0,0 # 0,1,0 - 0,1,0" +
	    "P  1,1,1 # 0,1,0" +
	    "P  1,0,0 - 1,1,1 # 0,0,0 - 0,1,0" +
	    "P  0,1 - 1,1 # 0,0 - 1,0" +
	    "P  1,1,1 # 1,0,0" +
	    "P  1,0 - 1,1 - 0,1 # 0,0 - 1,0 - 0,0";


 	//six piece, 1 solution
	//https://knolleary.net/2006/10/04/ruby-cubes/
	String puzzleString =
	    "  1,1,1 - 0,0,0 # 0,1,0 - 0,1,0" +
	    "P  1,1,1 # 0,1,0" +
	    "P  0,0,1 - 1,1,1 # 0,0,0 - 0,1,0" +
	    "P  1,0 - 1,1 # 0,0 - 0,1" +
	    "P  1,1,1 # 1,0,0" +
	    "P  1,0 - 1,1 - 0,1 # 0,0 - 1,0 - 0,0";

	//six piece, 78 sol
	String puzzleString =
	    "  1,1,1 # 0,0,0" +
	    "P  1,1,1 - 0,0,1 # 0,0,1 - 0,0,0" +
	    "P  0,0,1 - 1,1,1 # 0,0,0 - 0,1,0" +
	    "P  1,0 - 1,0 - 1,1 # 0,0 - 0,0 - 0,1" +
	    "P  1,1,1 # 1,0,0" +
	    "P  1,0 - 1,1 - 0,1 # 0,0 - 1,0 - 0,0";


	//six piece, 100 sol
	String puzzleString =
	    "  1,1 - 1,0" +
	    "P  1,1,1 - 0,0,1 # 0,0,1 - 0,0,0" +
	    "P  0,0,1 - 1,1,1 # 0,0,0 - 0,1,0" +
	    "P  1,0 - 1,0 - 1,1 # 0,0 - 0,0 - 0,1" +
	    "P  1,1,1 # 1,0,0" +
	    "P  1,0 - 1,1 - 0,1 # 0,0 - 1,0 - 0,0";
	*/	


	// 6/8 piece 3x3x3/4 100/524 sol
	String puzzleString =
	    "  1,1 - 1,0" +
	    "P  1,1,1 - 0,0,1 # 0,0,1 - 0,0,0" +
	    "P  0,0,1 - 1,1,1 # 0,0,0 - 0,1,0" +
	    "P  1,0 - 1,0 - 1,1 # 0,0 - 0,0 - 0,1" +
	    "P  1,1,1 # 1,0,0" +	    
	    "P  1,0 - 1,1 - 0,1 # 0,0 - 1,0 - 0,0" +
	    "P  1,1,1,1 " + //only for 3x3x4
	    "P  0,0,1,0 - 1,1,1,1" ; //only for 3x3x4

	
	/*
	//13 piece 4x4x4
	// http://www.2ndlook.nl/3dpuzzles/puzzles/bedlamcube/engdescription.htm
	String puzzleString =
	    "  0,1,0 # 1,1,1 # 0,0,1" +
	    "P 0,1,0 # 1,1,1 # 0,1,0 " +
	    "P 0,1,1 # 1,1,0 # 1,0,0 " +
	    "P 0,0 - 0,1 # 0,0 - 1,1 # 1,0 - 1,0 " +
	    "P 0,0 - 1,0 # 1,0 - 1,1 # 0,0 - 1,0" +
	    "P 0,0 - 0,1 # 1,0 - 1,1 # 0,0 - 1,0 " +
	    "P 0,0 - 1,0 # 1,1 - 1,0 # 0,0 - 1,0 " +
	    "P 1,0 - 1,1 # 0,0 - 1,0 # 0,0 - 1,0 " +
	    "P 0,0 - 1,1 # 0,0 - 1,0 # 1,0 - 1,0 " +
	    "P 0,0 - 1,0 # 0,0 - 1,0 # 0,1 - 1,1 " +
	    "P 0,0 - 0,1 # 1,0 - 1,1 # 1,0 - 0,0 " +
	    "P 0,0 - 1,1 # 1,0 - 1,0 # 0,0 - 1,0 " +	    
	    "P 0,1 - 1,1 # 0,0 - 1,0 ";

	    
		
	//six piece, 2 solutions
	String puzzleString =
	    "  1,1,1 - 1,0,0" +
	    "P  1,1,1 - 0,0,1 # 0,1,0 - 0,0,0" +
	    "P  1,1,1 - 0,0,0 # 1,0,0 - 1,0,0" +
	    "P  1,1 - 0,0 # 0,1 - 0,1" +
	    "P  1,1 - 1,0 # 1,0 - 0,0" +
	    "P  1,1,0 - 0,1,0 # 0,0,0 - 0,1,1";


	//seven piece puzzle, 960 solutions 
	String puzzleString =
	    "  0,1,1 - 1,1,0" + 
	    "P 1,1 - 0,1 - 0,1" +
	    "P 1,0 - 1,1 # 0,0 - 0,1" +
	    "P 0,1 - 1,1 # 0,0 - 1,0" +
	    "P 1,0 - 1,1 # 0,0 - 1,0" +
	    "P 1,1,1 - 0,1,0" +
	    "P 1,0 - 1,1";
	*/	
	
	String[] pieceStrings = puzzleString.split("[P]+");
	pieces = new PPiece[pieceStrings.length];

	System.out.println("Solving puzzle with "+pieces.length+" pieces");
	
	int n=0;
	for(String piece : pieceStrings) {
	    System.out.println("Piece "+(n+1)+": "+piece);
	    pieces[n++] = new PPiece(piece);
    	}
	savedBox = new int[pieces.length][CSIZE*RSIZE*LSIZE];
    }

    public static int get(int col, int row, int layer) {
	return box[layer*RSIZE*CSIZE + row*CSIZE + col];
    }
    
    public static void set(int col, int row, int layer, int value) {
	box[layer*RSIZE*CSIZE + row*CSIZE + col] = value;
    }

    static int isobox[] = new int[CSIZE*RSIZE*LSIZE];
    static final int MIN_SIZE=3;

    static int found;
    public static int measure(int pc, int pr, int pl) {
	//	System.out.println(pc+","+pr+","+pl+" f="+found);

	    if((pc+1) < CSIZE && isobox[pl*RSIZE*CSIZE + pr*CSIZE + (pc+1)] == 0) {
		found++;
		isobox[pl*RSIZE*CSIZE + pr*CSIZE + (pc+1)] = 1; //mark
		measure(pc+1, pr, pl);
	    }
	    if((pc-1) >= 0 && isobox[pl*RSIZE*CSIZE + pr*CSIZE + (pc-1)] == 0) {
		found++;
		isobox[pl*RSIZE*CSIZE + pr*CSIZE + (pc-1)] = 1; //mark
		measure(pc-1, pr, pl);
	    }
	    
	    if((pr+1) < RSIZE && isobox[pl*RSIZE*CSIZE + (pr+1)*CSIZE + pc] == 0) {
		found++;
		isobox[pl*RSIZE*CSIZE + (pr+1)*CSIZE + pc] = 1; //mark
		measure(pc, pr+1, pl);
	    }
	    if((pr-1) >= 0 && isobox[pl*RSIZE*CSIZE + (pr-1)*CSIZE + pc] == 0) {
		found++;
		isobox[pl*RSIZE*CSIZE + (pr-1)*CSIZE + pc] = 1; //mark
		measure(pc, pr-1, pl);
	    }
	    
	    if((pl+1) < LSIZE && isobox[(pl+1)*RSIZE*CSIZE + pr*CSIZE + pc] == 0) {
		found++;
		isobox[(pl+1)*RSIZE*CSIZE + pr*CSIZE + pc] = 1; //mark
		measure(pc, pr, pl+1);
	    }
	    if((pl-1) >= 0 && isobox[(pl-1)*RSIZE*CSIZE + pr*CSIZE + pc] == 0) {
		found++;
		isobox[(pl-1)*RSIZE*CSIZE + pr*CSIZE + pc] = 1; //mark
		measure(pc, pr, pl-1);
	    }
	    
	return found;
    }
    
    public static boolean hasIsolatedSmall() {
	System.arraycopy(box, 0, isobox, 0, LSIZE*RSIZE*CSIZE);    

	for(int l=0 ; l < LSIZE ; l++) {	    
	    for(int r=0 ; r < RSIZE ; r++) {
		for(int c=0 ; c < CSIZE ; c++) {
		    if(isobox[l*RSIZE*CSIZE + r*CSIZE + c] == 0) {
			//hole, check size
			found = 1;
			isobox[l*RSIZE*CSIZE + r*CSIZE + c] = 1; //mark	    
			if(measure(c,r,l) < MIN_SIZE) {
			    return true;
			}			
		    }
		}
	    }
	}
	
	return false;
    }
    

    public static boolean placePiece(int n) {
	PPiece p = pieces[n];
	if(hasIsolatedSmall()) {
	    return false;
	}

	//	p.rotate(0);
	//int places = ((SIZE-p.getLayers()+1)*(SIZE-p.getRows()+1)*(SIZE-p.getCols()+1));
	//int tried = 0;
	for(int t=0 ; (n>0 && t<24) || t<1 ; t++) { //skip rotation of first piece since that will only yield duplicate solutions
		p.rotate(t);
	
		for(int lAdd=0 ; lAdd <= (LSIZE-p.getLayers()) ; lAdd++) {	    
		    for(int rAdd=0 ; rAdd <= (RSIZE-p.getRows()) ; rAdd++) {
			for(int cAdd=0 ; cAdd <= (CSIZE-p.getCols()) ; cAdd++) {
			    boolean fail=false;
			    /* if(n<6) {
				for(int i=0; i<n ;i++) {
				    System.out.print("  ");
				}
				tried++;
				System.out.println(tried +"/"+ places*(n==0?1:24));
						   
				}*/


			    System.arraycopy(box, 0, savedBox[n], 0, LSIZE*RSIZE*CSIZE);    
			    //Try place
			    for(int l=0; l<p.getLayers() && !fail; l++) {
				for(int r=0; r<p.getRows() && !fail; r++) {
				    for(int c=0; c<p.getCols() &&!fail; c++) {
					if(p.get(c,r,l) > 0) {					    					    
					    if(get(c+cAdd,r+rAdd,l+lAdd) > 0) {
						fail=true;						
					    }
					    else {
						set(c+cAdd,r+rAdd,l+lAdd, n+1);
					    }
					}
				    }
				}
			    }
			    if(!fail) {				
				if(n == pieces.length-1) {
				    //complete solution!
				    newSolution(); //post solution to be printed if unique, then search on for more solutions
				}
				else {
				    //try next piece
				    if(placePiece(n+1)) {
					return true;
				    }
				}
			    }

			    //restore
			    System.arraycopy(savedBox[n], 0, box, 0, LSIZE*RSIZE*CSIZE);			    
			}
		    }	    
		}
	}
	
	return false;
    }

    private static void newSolution() {
	boolean found = false;

	
	for(int[] sol : solutions) {
	    if(java.util.Arrays.equals(box, sol)) {
		found = true;
		break;
	    }
	}
	if(!found) {
	    //add new solution
	    solutions.add(box.clone());

	    //print it
	    System.out.println("Solution "+solutions.size());				    
	    for(int i=0 ; i<LSIZE ;i++) {
		System.out.print("L" + i + "\t");	
	    }
	    System.out.println();			
	    for(int row=RSIZE-1 ; row >= 0 ; row--) {
		for(int layer=0 ; layer < LSIZE ; layer++) {
		    
		    for(int col=0 ; col < CSIZE ; col++) {
			System.out.print(get(col, row, layer) + " ");
		    }
		    System.out.print("\t");		
		}
		System.out.println();		
	    }	    
	}	    	
    }
    
    public static void main(String args[]) {

	placePiece(0);

	if(solutions.size() < 1) {
	    System.out.println("Failed to lay puzzle! :-(");
	}	
    }
}
