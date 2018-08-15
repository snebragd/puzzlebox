import java.util.LinkedList;

public class PBox {    
    private static int SIZE=3;
    
    
    static int box[] = new int[SIZE*SIZE*SIZE];
    static int savedBox[][];

    static PPiece pieces[];

    static LinkedList<int []> solutions = new LinkedList<int []>();

    /*
     * Pieces enetered as array, row by row, starting from bottommost layer.
     * Eg. The letter 'L' would look like: {1,1, 1,0, 1,0} (2 cols, 3 rows, 1 layer)
     *     A 3D plus would be: {0,0,0, 0,1,0, 0,0,0,   0,1,0, 1,1,1, 0,1,0,   0,0,0, 0,1,0, 0,0,0} (3 cols,, 3 rows, 3 layers) 
     */
    static {
	//original puzzle, 6 solutions
	String puzzleString =
	    "  1,1,1 - 1,0,0" +
	    "P  1,1,1 - 0,0,1 # 0,1,0 - 0,0,0" +
	    "P  1,1,1 - 0,0,0 # 1,0,0 - 1,0,0" +
	    "P  1,1 - 0,0 # 0,1 - 0,1" +
	    "P  1,1 - 0,0 # 1,0 - 1,0" +
	    "P  1,1,0 - 0,1,0 # 0,0,0 - 0,1,1";
	
	/*
	//seven piece puzzle, 1372 solutions 
	String puzzleString =
	    "  0,1,1 - 1,1,0" + 
	    "P 1,1 - 0,1 - 0,1" +
	    "P 1,0 - 1,1 # 0,0 - 0,1" +
	    "P 0,1 - 1,1 # 0,0 - 1,0" +
	    "P 0,1 - 1,1 # 0,0 - 1,0" +
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
	savedBox = new int[pieces.length][SIZE*SIZE*SIZE];
    }

    public static int get(int col, int row, int layer) {
	return box[layer*SIZE*SIZE + row*SIZE + col];
    }
    
    public static void set(int col, int row, int layer, int value) {
	box[layer*SIZE*SIZE + row*SIZE + col] = value;
    }


    public static boolean placePiece(int n) {
	PPiece p = pieces[n];

	for(int t=0 ; (n>0 && t<24) || t<1 ; t++) { //skip rotation of first piece since that will only yield duplicate solutions
		p.rotate(t);
	
		for(int lAdd=0 ; lAdd <= (SIZE-p.getLayers()) ; lAdd++) {	    
		    for(int rAdd=0 ; rAdd <= (SIZE-p.getRows()) ; rAdd++) {
			for(int cAdd=0 ; cAdd <= (SIZE-p.getCols()) ; cAdd++) {
			    boolean fail=false;

			    System.arraycopy(box, 0, savedBox[n], 0, SIZE*SIZE*SIZE);    
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
			    System.arraycopy(savedBox[n], 0, box, 0, SIZE*SIZE*SIZE);			    
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
	    for(int i=0 ; i<SIZE ;i++) {
		System.out.print("L" + i + "\t");	
	    }
	    System.out.println();			
	    for(int row=SIZE-1 ; row >= 0 ; row--) {
		for(int layer=0 ; layer < SIZE ; layer++) {
		    
		    for(int col=0 ; col < SIZE ; col++) {
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
