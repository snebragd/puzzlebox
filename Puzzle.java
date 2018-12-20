import java.util.ArrayList;

class Puzzle {
    int cSize;
    int rSize;
    int lSize;
    int minSize;

    ArrayList<PPiece> pieces = new ArrayList<>();

    int getWeight() {
        return cSize * rSize * lSize;
    }
}
