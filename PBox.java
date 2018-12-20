import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class PBox {
    private int[] box;
    private int[][] savedBox;

    private Puzzle puzzle;

    private final LinkedList<int[]> solutions = new LinkedList<>();

    private boolean parsePuzzle(String fname) {
        puzzle = new Puzzle();

        int totalPieceWeight = 0;

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
                        puzzle.cSize = Integer.parseInt(tmp[0]);
                        puzzle.rSize = Integer.parseInt(tmp[1]);
                        puzzle.lSize = Integer.parseInt(tmp[2]);
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
                        puzzle.minSize = Math.min(p.getWeight(), puzzle.minSize);
                        puzzle.pieces.add(p);

                        totalPieceWeight += p.getWeight();

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

        System.out.printf("Solving %dx%dx%d puzzle with %d pieces%n", puzzle.cSize, puzzle.rSize, puzzle.lSize, puzzle.pieces.size());
        if (totalPieceWeight != puzzle.getWeight()) {
            System.out.printf("Mismatching pieces! Weight is %d while is should be %d%n", totalPieceWeight, puzzle.getWeight());
            //	    return false;
        }

        savedBox = new int[puzzle.pieces.size()][puzzle.getWeight()];
        box = new int[puzzle.getWeight()];

        return true;
    }

    private int index(int col, int row, int layer) {
        return layer * puzzle.rSize * puzzle.cSize + row * puzzle.cSize + col;
    }

    private int get(int col, int row, int layer) {
        return box[index(col, row, layer)];
    }

    private void set(int col, int row, int layer, int value) {
        box[index(col, row, layer)] = value;
    }

    private int found;

    private int measure(int[] isobox, int pc, int pr, int pl) {
        //	System.out.println(pc+","+pr+","+pl+" f="+found);

        if ((pc + 1) < puzzle.cSize && isobox[index(pc + 1, pr, pl)] == 0) {
            found++;
            isobox[index(pc + 1, pr, pl)] = 1; //mark
            measure(isobox, pc + 1, pr, pl);
        }
        if ((pc - 1) >= 0 && isobox[index(pc - 1, pr, pl)] == 0) {
            found++;
            isobox[index(pc - 1, pr, pl)] = 1; //mark
            measure(isobox, pc - 1, pr, pl);
        }

        if ((pr + 1) < puzzle.rSize && isobox[index(pc, pr + 1, pl)] == 0) {
            found++;
            isobox[index(pc, pr + 1, pl)] = 1; //mark
            measure(isobox, pc, pr + 1, pl);
        }
        if ((pr - 1) >= 0 && isobox[index(pc, pr - 1, pl)] == 0) {
            found++;
            isobox[index(pc, pr - 1, pl)] = 1; //mark
            measure(isobox, pc, pr - 1, pl);
        }

        if ((pl + 1) < puzzle.lSize && isobox[index(pc, pr, pl + 1)] == 0) {
            found++;
            isobox[index(pc, pr, pl + 1)] = 1; //mark
            measure(isobox, pc, pr, pl + 1);
        }
        if ((pl - 1) >= 0 && isobox[index(pc, pr, pl - 1)] == 0) {
            found++;
            isobox[index(pc, pr, pl - 1)] = 1; //mark
            measure(isobox, pc, pr, pl - 1);
        }

        return found;
    }

    private boolean hasIsolatedSmall() {
        int[] isobox = new int[puzzle.getWeight()];

        System.arraycopy(box, 0, isobox, 0, puzzle.getWeight());

        for (int l = 0; l < puzzle.lSize; l++) {
            for (int r = 0; r < puzzle.rSize; r++) {
                for (int c = 0; c < puzzle.rSize; c++) {
                    if (isobox[index(c, r, l)] == 0) {
                        //hole, check size
                        found = 1;
                        isobox[index(c, r, l)] = 1; //mark
                        if (measure(isobox, c, r, l) < puzzle.minSize) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private int piecesTried = 0;

    private boolean placePiece(int n) {
        piecesTried++;

        PPiece p = puzzle.pieces.get(n);
        if (hasIsolatedSmall()) {
            return false;
        }

        //	p.rotate(0);
        //int places = ((SIZE-p.getLayers()+1)*(SIZE-p.getRows()+1)*(SIZE-p.getCols()+1));
        //int tried = 0;
        for (int t = 0; (n > 0 && t < 24) || t < 1; t++) { //skip rotation of first piece since that will only yield duplicate solutions
            p.rotate(t);

            for (int lAdd = 0; lAdd <= (puzzle.lSize - p.getLayers()); lAdd++) {
                for (int rAdd = 0; rAdd <= (puzzle.rSize - p.getRows()); rAdd++) {
                    for (int cAdd = 0; cAdd <= (puzzle.cSize - p.getCols()); cAdd++) {
                        boolean fail = false;
			    /* if(n<6) {
				for(int i=0; i<n ;i++) {
				    System.out.print("  ");
				}
				tried++;
				System.out.println(tried +"/"+ places*(n==0?1:24));
						   
				}*/


                        System.arraycopy(box, 0, savedBox[n], 0, puzzle.getWeight());
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
                            if (n == puzzle.pieces.size() - 1) {
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
                        System.arraycopy(savedBox[n], 0, box, 0, puzzle.getWeight());
                    }
                }
            }
        }

        return false;
    }

    private void newSolution() {
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
            for (int i = 0; i < puzzle.lSize; i++) {
                System.out.print("L" + i + "\t");
            }
            System.out.println();
            for (int row = puzzle.rSize - 1; row >= 0; row--) {
                for (int layer = 0; layer < puzzle.lSize; layer++) {

                    for (int col = 0; col < puzzle.cSize; col++) {
                        int p = get(col, row, layer);
                        System.out.print((p == 0 ? " " : puzzle.pieces.get(p - 1).getName()) + " ");
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

        PBox box = new PBox();

        String fname = args[0];
        if (!box.parsePuzzle(fname)) {
            System.out.println("Failed to parse puzzle: " + fname);
            return;
        }

        if (args.length == 2) {
            String cadName = args[1];
            try {
                for (PPiece p : box.puzzle.pieces) {
                    PPieceWriter.writePuzzle(p, String.format("%s_%s.scad", cadName, p.getName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        box.placePiece(0);
        System.out.println("tried: " + box.piecesTried);

        if (box.solutions.size() < 1) {
            System.out.println("Failed to lay puzzle! :-(");
        }
        System.exit(Math.min(box.solutions.size(), 255));
    }
}
