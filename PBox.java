import java.util.LinkedList;
import java.util.ArrayList;

public class PBox {    
    private static int CSIZE=4;
    private static int RSIZE=3;
    private static int LSIZE=3;
    
    
    static int box[] = new int[CSIZE*RSIZE*LSIZE];
    static int savedBox[][];

    static ArrayList<PPiece> pieces = new ArrayList<PPiece>();

    static LinkedList<int []> solutions = new LinkedList<int []>();

    static int MIN_SIZE=100;
    
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
	*/	
    
    private static boolean parsePuzzle(String fname) {
	//	java.util.LinkedList<String> piecesStrings = new java.util.LinkedList<String>();
	try {
	    java.io.BufferedReader in = new java.io.BufferedReader(new java.io.FileReader(fname));
	    //	String[] lines = pStr.split("\n");
	    
	    boolean gotDim = false;
	    boolean newPiece = false;
	    StringBuffer pStrBuf = null;
	    String name = null;
	    boolean doneLast = false;	    

	    String line;
	    //	for(String line:lines) {
	    while((line=in.readLine()) != null || doneLast == false) {
		if(line == null) {
		    doneLast = true;
		    line = "\n";
		}
		if(line.startsWith("//")) {
		    //ignore comments
		}
		else if(!gotDim) {
		    //first line should be dimensions
		    String[] tmp = line.split("x");
		    try {
			CSIZE = Integer.parseInt(tmp[0]);
			RSIZE = Integer.parseInt(tmp[1]);
			LSIZE = Integer.parseInt(tmp[2]);
		    }
		    catch(Exception e) {
			System.out.println("Failed to parse dimension string: "+line);
			return false;
		    }


		    gotDim = true;
		}
		else if(line.trim().length() == 0) {
		    //next line is a new piece
		    if(pStrBuf != null) {
			String piece = pStrBuf.toString();
			//System.out.println(name+": "+piece);
			PPiece p = new PPiece(piece, name);
			MIN_SIZE = Math.min(p.getWeight(), MIN_SIZE);
			pieces.add(p);	    
			
			pStrBuf = null;
			name = null;
		    }		
		    
		    newPiece = true;
		    
		}
		else if(newPiece && name == null) {
		    name = line.trim();
		    pStrBuf = new StringBuffer(20);		
		}
		else {
		    pStrBuf.append(line);
		    pStrBuf.append(',');
		}
	    }
	    in.close();	    
	}
	catch (java.io.IOException e) {
	    System.out.println(e);
	    return false;
	}

	System.out.println("Solving "+CSIZE+"x"+RSIZE+"x"+LSIZE+" puzzle with "+pieces.size()+" pieces");	
	
	savedBox = new int[pieces.size()][CSIZE*RSIZE*LSIZE];
	
	return true;
    }
    
    
    public static int get(int col, int row, int layer) {
	return box[layer*RSIZE*CSIZE + row*CSIZE + col];
    }
    
    public static void set(int col, int row, int layer, int value) {
	box[layer*RSIZE*CSIZE + row*CSIZE + col] = value;
    }


    static int isobox[] = new int[CSIZE*RSIZE*LSIZE];
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
	PPiece p = pieces.get(n);
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
				if(n == pieces.size()-1) {
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
			System.out.print(pieces.get(get(col, row, layer)-1).getName() + " ");
		    }
		    System.out.print("\t");		
		}
		System.out.println();		
	    }	    
	}	    	
    }
    
    public static void main(String args[]) {
	if(args.length != 1) {
	    System.out.println("Usage: java PBox <puzzle file name>");
	    return;	    
	}
	String fname = args[0];
	if( ! parsePuzzle(fname)) {
	    System.out.println("Failed to parse puzzle: "+fname);
	    return;	    
	}
	
	placePiece(0);

	if(solutions.size() < 1) {
	    System.out.println("Failed to lay puzzle! :-(");
	}	
    }
}
