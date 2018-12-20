import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PBox {
    private static int CSIZE = 0;
    private static int RSIZE = 0;
    private static int LSIZE = 0;


    private static int[] box;
    private static int[][] savedBox;

    private static ArrayList<PPiece> pieces = new ArrayList<>();

    private static LinkedList<int[]> solutions = new LinkedList<>();

    private static int MIN_SIZE = 100;

    private static boolean parsePuzzle(String fname) {
        int totWeight = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));

            boolean gotDim = false;
            boolean newPiece = false;
            StringBuffer pStrBuf = null;
            String name = null;
            boolean doneLast = false;

            String line;
            //	for(String line:lines) {
            while ((line = in.readLine()) != null || !doneLast) {
                if (line == null) {
                    doneLast = true;
                    line = "\n";
                }
                if (line.startsWith("//")) {
                    //ignore comments
                } else if (!gotDim) {
                    //first line should be dimensions
                    String[] tmp = line.split("x");
                    try {
                        CSIZE = Integer.parseInt(tmp[0]);
                        RSIZE = Integer.parseInt(tmp[1]);
                        LSIZE = Integer.parseInt(tmp[2]);
                    } catch (Exception e) {
                        System.out.println("Failed to parse dimension string: " + line);
                        return false;
                    }


                    gotDim = true;
                } else if (line.trim().length() == 0) {
                    //next line is a new piece
                    if (pStrBuf != null) {
                        String piece = pStrBuf.toString();
                        //System.out.println(name+": "+piece);
                        PPiece p = new PPiece(piece, name);
                        MIN_SIZE = Math.min(p.getWeight(), MIN_SIZE);
                        pieces.add(p);

                        totWeight += p.getWeight();

                        pStrBuf = null;
                        name = null;
                    }

                    newPiece = true;

                } else if (newPiece && name == null) {
                    name = line.trim();
                    pStrBuf = new StringBuffer(20);
                } else if (pStrBuf != null) {
                    pStrBuf.append(line);
                    pStrBuf.append(',');
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Solving " + CSIZE + "x" + RSIZE + "x" + LSIZE + " puzzle with " + pieces.size() + " pieces");
        if (totWeight != CSIZE * RSIZE * LSIZE) {
            System.out.println("Mismatching pieces! Weight is " + totWeight + " while is should be " + CSIZE * RSIZE * LSIZE);
            //	    return false;
        }

        savedBox = new int[pieces.size()][CSIZE * RSIZE * LSIZE];
        isobox = new int[CSIZE * RSIZE * LSIZE];
        box = new int[CSIZE * RSIZE * LSIZE];
        return true;
    }


    private static int get(int col, int row, int layer) {
        return box[layer * RSIZE * CSIZE + row * CSIZE + col];
    }

    private static void set(int col, int row, int layer, int value) {
        box[layer * RSIZE * CSIZE + row * CSIZE + col] = value;
    }


    private static int[] isobox;
    private static int found;

    private static int measure(int pc, int pr, int pl) {
        //	System.out.println(pc+","+pr+","+pl+" f="+found);

        if ((pc + 1) < CSIZE && isobox[pl * RSIZE * CSIZE + pr * CSIZE + (pc + 1)] == 0) {
            found++;
            isobox[pl * RSIZE * CSIZE + pr * CSIZE + (pc + 1)] = 1; //mark
            measure(pc + 1, pr, pl);
        }
        if ((pc - 1) >= 0 && isobox[pl * RSIZE * CSIZE + pr * CSIZE + (pc - 1)] == 0) {
            found++;
            isobox[pl * RSIZE * CSIZE + pr * CSIZE + (pc - 1)] = 1; //mark
            measure(pc - 1, pr, pl);
        }

        if ((pr + 1) < RSIZE && isobox[pl * RSIZE * CSIZE + (pr + 1) * CSIZE + pc] == 0) {
            found++;
            isobox[pl * RSIZE * CSIZE + (pr + 1) * CSIZE + pc] = 1; //mark
            measure(pc, pr + 1, pl);
        }
        if ((pr - 1) >= 0 && isobox[pl * RSIZE * CSIZE + (pr - 1) * CSIZE + pc] == 0) {
            found++;
            isobox[pl * RSIZE * CSIZE + (pr - 1) * CSIZE + pc] = 1; //mark
            measure(pc, pr - 1, pl);
        }

        if ((pl + 1) < LSIZE && isobox[(pl + 1) * RSIZE * CSIZE + pr * CSIZE + pc] == 0) {
            found++;
            isobox[(pl + 1) * RSIZE * CSIZE + pr * CSIZE + pc] = 1; //mark
            measure(pc, pr, pl + 1);
        }
        if ((pl - 1) >= 0 && isobox[(pl - 1) * RSIZE * CSIZE + pr * CSIZE + pc] == 0) {
            found++;
            isobox[(pl - 1) * RSIZE * CSIZE + pr * CSIZE + pc] = 1; //mark
            measure(pc, pr, pl - 1);
        }

        return found;
    }

    private static boolean hasIsolatedSmall() {
        System.arraycopy(box, 0, isobox, 0, LSIZE * RSIZE * CSIZE);

        for (int l = 0; l < LSIZE; l++) {
            for (int r = 0; r < RSIZE; r++) {
                for (int c = 0; c < CSIZE; c++) {
                    if (isobox[l * RSIZE * CSIZE + r * CSIZE + c] == 0) {
                        //hole, check size
                        found = 1;
                        isobox[l * RSIZE * CSIZE + r * CSIZE + c] = 1; //mark
                        if (measure(c, r, l) < MIN_SIZE) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static int piecesTried = 0;

    private static boolean placePiece(int n) {
        piecesTried++;

        PPiece p = pieces.get(n);
        if (hasIsolatedSmall()) {
            return false;
        }

        //	p.rotate(0);
        //int places = ((SIZE-p.getLayers()+1)*(SIZE-p.getRows()+1)*(SIZE-p.getCols()+1));
        //int tried = 0;
        for (int t = 0; (n > 0 && t < 24) || t < 1; t++) { //skip rotation of first piece since that will only yield duplicate solutions
            p.rotate(t);

            for (int lAdd = 0; lAdd <= (LSIZE - p.getLayers()); lAdd++) {
                for (int rAdd = 0; rAdd <= (RSIZE - p.getRows()); rAdd++) {
                    for (int cAdd = 0; cAdd <= (CSIZE - p.getCols()); cAdd++) {
                        boolean fail = false;
			    /* if(n<6) {
				for(int i=0; i<n ;i++) {
				    System.out.print("  ");
				}
				tried++;
				System.out.println(tried +"/"+ places*(n==0?1:24));
						   
				}*/


                        System.arraycopy(box, 0, savedBox[n], 0, LSIZE * RSIZE * CSIZE);
                        //Try place
                        for (int l = 0; l < p.getLayers() && !fail; l++) {
                            for (int r = 0; r < p.getRows() && !fail; r++) {
                                for (int c = 0; c < p.getCols() && !fail; c++) {
                                    if (p.get(c, r, l) > 0) {
                                        if (get(c + cAdd, r + rAdd, l + lAdd) > 0) {
                                            fail = true;
                                        } else {
                                            set(c + cAdd, r + rAdd, l + lAdd, n + 1);
                                        }
                                    }
                                }
                            }
                        }
                        if (!fail) {
                            if (n == pieces.size() - 1) {
                                //complete solution!
                                newSolution(); //post solution to be printed if unique, then search on for more solutions
                            } else {
                                //try next piece
                                if (placePiece(n + 1)) {
                                    return true;
                                }
                            }
                        }

                        //restore
                        System.arraycopy(savedBox[n], 0, box, 0, LSIZE * RSIZE * CSIZE);
                    }
                }
            }
        }

        return false;
    }

    private static void newSolution() {
        boolean found = false;


        for (int[] sol : solutions) {
            if (Arrays.equals(box, sol)) {
                found = true;
                break;
            }
        }
        if (!found) {
            //add new solution
            solutions.add(box.clone());

            //print it
            System.out.println("Solution " + solutions.size());
            for (int i = 0; i < LSIZE; i++) {
                System.out.print("L" + i + "\t");
            }
            System.out.println();
            for (int row = RSIZE - 1; row >= 0; row--) {
                for (int layer = 0; layer < LSIZE; layer++) {

                    for (int col = 0; col < CSIZE; col++) {
                        int p = get(col, row, layer);
                        System.out.print((p == 0 ? " " : pieces.get(p - 1).getName()) + " ");
                    }
                    System.out.print("\t");
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PBox <puzzle file name> [cad file prefix]");
            return;
        }
        String fname = args[0];
        if (!parsePuzzle(fname)) {
            System.out.println("Failed to parse puzzle: " + fname);
            return;
        }

        if (args.length == 2) {
            String cadName = args[1];
            try {
                for (PPiece p : pieces) {
                    PrintWriter cf = new PrintWriter(cadName + "_" + p.getName() + ".scad");

                    cf.println("size=8;");
                    cf.println("extra=1;");
                    cf.println("slack=0.05;");
                    cf.println("module chamfer() {");
                    cf.println("    intersection() {");
                    cf.println("        cylinder($fn=4, r=extra-slack, h=2*extra-2*slack, center=true);");
                    cf.println("        rotate([0,90,0]) cylinder($fn=4, r=extra-slack, h=2*extra-2*slack, center=true);");
                    cf.println("        rotate([90,0,0]) cylinder($fn=4, r=extra-slack, h=2*extra-2*slack, center=true);");
                    cf.println("    }");
                    cf.println("}");
                    cf.println("minkowski() {");
                    cf.println("    union() {");

                    p.rotate(0);
                    for (int col = 0; col < p.getCols(); col++) {
                        for (int row = 0; row < p.getRows(); row++) {
                            for (int lay = 0; lay < p.getLayers(); lay++) {
                                if (p.get(col, row, lay) > 0) {
                                    cf.println("         translate([" + col + "*(size+2*extra)," + row + "*(size+2*extra)," + lay + "*(size+2*extra)]) cube([size,size,size]);");
                                }
                            }
                        }
                    }
                    for (int col = 0; col < p.getCols(); col++) {
                        for (int row = 0; row < p.getRows(); row++) {
                            for (int lay = 0; lay < p.getLayers(); lay++) {
                                if (col < (p.getCols() - 1) && p.get(col, row, lay) > 0 && p.get(col + 1, row, lay) > 0) {
                                    cf.println("        translate([" + (col + 0.5) + "*(size+2*extra)," + row + "*(size+2*extra)," + lay + "*(size+2*extra)]) cube([size,size,size]);");
                                }
                                if (row < (p.getRows() - 1) && p.get(col, row, lay) > 0 && p.get(col, row + 1, lay) > 0) {
                                    cf.println("        translate([" + col + "*(size+2*extra)," + (row + 0.5) + "*(size+2*extra)," + lay + "*(size+2*extra)]) cube([size,size,size]);");
                                }
                                if (lay < (p.getLayers() - 1) && p.get(col, row, lay) > 0 && p.get(col, row, lay + 1) > 0) {
                                    cf.println("        translate([" + col + "*(size+2*extra)," + row + "*(size+2*extra)," + (lay + 0.5) + "*(size+2*extra)]) cube([size,size,size]);");
                                }
                            }
                        }
                    }

                    cf.println("    }");
                    cf.println("    chamfer();");
                    cf.println("}");

                    cf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        placePiece(0);
        System.out.println("tried: " + piecesTried);

        if (solutions.size() < 1) {
            System.out.println("Failed to lay puzzle! :-(");
        }
        System.exit(Math.min(solutions.size(), 255));
    }
}
