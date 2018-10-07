class PPiece {
    private static final int COL=0;
    private static final int ICOL=1;
    private static final int ROW=2;
    private static final int IROW=3;
    private static final int LAY=4;
    private static final int ILAY=5;

    private int[] transform = new int[3];

    private int rows, cols, layers;
    private int[][][] shapeArr;
    private int weight;
    private String name;
       
    public int getCols() { return axisToDim(transform[0]); }
    public int getRows() { return axisToDim(transform[1]); }
    public int getLayers() { return axisToDim(transform[2]); }
    public int getWeight() { return weight; }
    public String getName() { return name; }
    
    private int axisToDim(int ax) {
	switch(ax) {
	case COL:
	case ICOL:
	    return cols;
	case ROW:
	case IROW:
	    return rows;
	default:
	    return layers;
	}
    }    

    public int get(int col, int row, int lay) {
	int iCol=getCols()-col-1;
	int iRow=getRows()-row-1;
	int iLay=getLayers()-lay-1;

	int tcol=-1, trow=-1, tlay=-1;

	switch(transform[0]) {
	case COL:           tcol=col; break;
	case ICOL:          tcol=iCol; break;
	case ROW:           trow=col; break;
	case IROW:          trow=iCol; break;
	case LAY:           tlay=col; break;
	case ILAY: default: tlay=iCol; break;
	}
	switch(transform[1]) {
	case COL:           tcol=row; break;
	case ICOL:          tcol=iRow; break;
	case ROW:           trow=row; break;
	case IROW:          trow=iRow; break;
	case LAY:           tlay=row; break;
	case ILAY: default: tlay=iRow; break;
	}
	switch(transform[2]) {
	case COL:           tcol=lay; break;
	case ICOL:          tcol=iLay; break;
	case ROW:           trow=lay; break;
	case IROW:          trow=iLay; break;
	case LAY:           tlay=lay; break;
	case ILAY: default: tlay=iLay; break;
	}
	
	return shapeArr[tcol][trow][tlay];
    }
    
    void rotate(int twist) {		
	switch(twist) {
//up
	case 0:
	    transform[0] = COL;
	    transform[1] = ROW;
	    transform[2] = LAY;
	    break;
	case 1:
	    transform[0] = IROW;
	    transform[1] = COL;
	    transform[2] = LAY;
	    break;
	case 2:
	    transform[0] = ICOL;
	    transform[1] = IROW;
	    transform[2] = LAY;
	    break;
	case 3:
	    transform[0] = ROW;
	    transform[1] = ICOL;
	    transform[2] = LAY;
	    break;
//down
	case 4: //icol, row, ilay	    
	    transform[0] = ICOL;
	    transform[1] = ROW;
	    transform[2] = ILAY;
	    break;
	case 5: //irow, icol, ilay
	    transform[0] = IROW;
	    transform[1] = ICOL;
	    transform[2] = ILAY;
	    break;
	case 6: //col, irow, ilay
	    transform[0] = COL;
	    transform[1] = IROW;
	    transform[2] = ILAY;
	    break;
	case 7: //row, col, ilay
	    transform[0] = ROW;
	    transform[1] = COL;
	    transform[2] = ILAY;
	    break;

//left
	case 8: //ilay, row, col
	    transform[0] = ILAY;
	    transform[1] = ROW;
	    transform[2] = COL;
	    break;
	case 9: //irow, ilay, col
	    transform[0] = ILAY;
	    transform[1] = ICOL;
	    transform[2] = ROW;
	    break;
	case 10: //lay, irow, col
	    transform[0] = ILAY;
	    transform[1] = IROW;
	    transform[2] = ICOL;
	    break;
	case 11: //row, lay, col
 	    transform[0] = ILAY;
	    transform[1] = COL;
	    transform[2] = IROW;
	    break;

//right
	case 12: 
	    transform[0] = LAY;
	    transform[1] = ROW;
	    transform[2] = ICOL;
	    break;
	case 13: 
	    transform[0] = LAY;
	    transform[1] = ICOL;
	    transform[2] = IROW;
	    break;
	case 14: 
	    transform[0] = LAY;
	    transform[1] = IROW;
	    transform[2] = COL;
	    break;
	case 15: 
 	    transform[0] = LAY;
	    transform[1] = COL;
	    transform[2] = ROW;
	    break;

//away
	case 16: 
	    transform[0] = COL;
	    transform[1] = LAY;
	    transform[2] = IROW;
	    break;
	case 17: 
	    transform[0] = ROW;
	    transform[1] = LAY;
	    transform[2] = COL;
	    break;
	case 18: 
	    transform[0] = ICOL;
	    transform[1] = LAY;
	    transform[2] = ROW;
	    break;
	case 19: 
 	    transform[0] = IROW;
	    transform[1] = LAY;
	    transform[2] = ICOL;
	    break;

//toward
	case 20: 
	    transform[0] = COL;
	    transform[1] = ILAY;
	    transform[2] = ROW;
	    break;
	case 21: 
	    transform[0] = IROW;
	    transform[1] = ILAY;
	    transform[2] = COL;
	    break;
	case 22: 
	    transform[0] = ICOL;
	    transform[1] = ILAY;
	    transform[2] = IROW;
	    break;
	case 23: 
 	    transform[0] = ROW;
	    transform[1] = ILAY;
	    transform[2] = ICOL;
	    break;

	}		
    }

    PPiece(String piece, String pName) {	
	String[] rowStrings = piece.split("[,]+");
	rows = rowStrings.length;
	weight = 0;
	name = pName;
	for(int row=0 ; row < rowStrings.length ; row++) {
	    String[] layStrings = rowStrings[row].split("[ ]+");
	    layers = layStrings.length;
	    for(int lay=0; lay < layStrings.length ; lay++) {		
		cols = layStrings[lay].length();
		if(shapeArr == null) {
		    shapeArr = new int[cols][rows][layers];
		}		   
		for(int col=0 ; col < layStrings[lay].length() ; col++) {
		    int val = Integer.parseInt(""+layStrings[lay].charAt(col)); //Integer.parseInt(colStrings[col].trim());
		    if(val > 0) {
			shapeArr[col][row][lay] = val;
			weight++;
		    }
		}
	    }
	}
	//	System.out.println("  dim = "+cols+","+rows+","+layers+"  weight="+weight);
    }
    
    PPiece(int p_cols, int p_rows, int p_layers, int p_shape[]) {
	cols = p_cols;
	rows = p_rows;
	layers = p_layers;	

	shapeArr = new int[p_cols][p_rows][p_layers];
	int i=0;
	for(int l=0 ; l<layers ; l++) {
	    for(int r=0 ; r<rows ; r++) {
		for(int c=0 ; c<cols ; c++) {
		    shapeArr[c][r][l] = p_shape[i++];
		}
	    }
	}	    
    }

}
