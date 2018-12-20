import java.io.FileNotFoundException;
import java.io.PrintWriter;

class PPieceWriter {

    static void writePuzzle(PPiece piece, String fileName) throws FileNotFoundException {
        PrintWriter cf = new PrintWriter(fileName);

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

        piece.rotate(0);
        for (int col = 0; col < piece.getCols(); col++) {
            for (int row = 0; row < piece.getRows(); row++) {
                for (int lay = 0; lay < piece.getLayers(); lay++) {
                    if (piece.get(col, row, lay) > 0) {
                        cf.println("         translate([" + col + "*(size+2*extra)," + row + "*(size+2*extra)," + lay + "*(size+2*extra)]) cube([size,size,size]);");
                    }
                }
            }
        }
        for (int col = 0; col < piece.getCols(); col++) {
            for (int row = 0; row < piece.getRows(); row++) {
                for (int lay = 0; lay < piece.getLayers(); lay++) {
                    if (col < (piece.getCols() - 1) && piece.get(col, row, lay) > 0 && piece.get(col + 1, row, lay) > 0) {
                        cf.println("        translate([" + (col + 0.5) + "*(size+2*extra)," + row + "*(size+2*extra)," + lay + "*(size+2*extra)]) cube([size,size,size]);");
                    }
                    if (row < (piece.getRows() - 1) && piece.get(col, row, lay) > 0 && piece.get(col, row + 1, lay) > 0) {
                        cf.println("        translate([" + col + "*(size+2*extra)," + (row + 0.5) + "*(size+2*extra)," + lay + "*(size+2*extra)]) cube([size,size,size]);");
                    }
                    if (lay < (piece.getLayers() - 1) && piece.get(col, row, lay) > 0 && piece.get(col, row, lay + 1) > 0) {
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
}
