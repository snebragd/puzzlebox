
public class PBox {
    
    private static final int SIZE = 3;
    private static final int PIECES = 6;
    private static final int MAXSOL = 100;
    
    static int box[] = new int[SIZE*SIZE*SIZE];
    static int savedBox[][] = new int[PIECES][SIZE*SIZE*SIZE];

    static PPiece pieces[] = new PPiece[PIECES];

    static int solutions[][] = new int[MAXSOL][SIZE*SIZE*SIZE];
    static int nSolutions = 0;

    /*
     * Pieces enetered as array, row by row, starting from bottommost layer.
     * Eg. The letter 'L' would look like: {1,1, 1,0, 1,0} (2 cols, 3 rows, 1 layer)
     *     A 3D plus would be: {0,0,0, 0,1,0, 0,0,0,   0,1,0, 1,1,1, 0,1,0,   0,0,0, 0,1,0, 0,0,0} (3 cols,, 3 rows, 3 layers) 
     */
    static {
	int p1[] = {1,1,1, 1,0,0};
	pieces[0] = new PPiece(3, 2, 1, p1);

	int p2[] = {1,1,1, 0,0,1,   0,1,0, 0,0,0};
	pieces[1] = new PPiece(3, 2, 2, p2);
	
    	int p3[] = {1,1,1, 0,0,0,   1,0,0, 1,0,0};
	pieces[2] = new PPiece(3, 2, 2, p3);
	
	int p4[] = {1,1, 0,0,   0,1, 0,1};
	pieces[3] = new PPiece(2, 2, 2, p4);
		
	int p5[] = {1,1, 0,0,   1,0, 1,0};
	pieces[4] = new PPiece(2, 2, 2, p5);
	
	int p6[] = {1,1,0, 0,1,0,   0,0,0, 0,1,1};
	pieces[5] = new PPiece(3, 2, 2, p6);
	
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
				if(n == PIECES-1) {
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
	for(int s=0 ; s<nSolutions ; s++) {
	    if(java.util.Arrays.equals(box, solutions[s])) {
		found = true;
		break;
	    }
	}
	if(!found) {
	    //add new solution
	    System.arraycopy(box, 0, solutions[nSolutions++], 0, SIZE*SIZE*SIZE);


	    //print it
	    System.out.println("Solution "+nSolutions);				    
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

	if(nSolutions < 1) {
	    System.out.println("Failed to lay puzzle! :-(");
	}	
    }
}
