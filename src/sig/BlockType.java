package sig;

public class BlockType {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int FRONT = 2;
    public static final int RIGHT = 3;
    public static final int LEFT = 4;
    public static final int BACK = 5;

    

    TextureType sides[] = new TextureType[6];
    /*
        0   TOP
        1   BOTTOM
        2   FRONT
        3   RIGHT
        4   LEFT
        5   BACK
    */
    BlockType(TextureType allSides) {
        for (int i=0;i<6;i++) {
            sides[i]=allSides;
        }
    }
    BlockType(TextureType top,TextureType bottom,TextureType side) {
        sides[TOP]=top;
        sides[BOTTOM]=bottom;
        sides[FRONT]=sides[RIGHT]=sides[LEFT]=sides[BACK]=side;
    }
    BlockType(TextureType top,TextureType bottom,TextureType front,TextureType back,TextureType side) {
        sides[TOP]=top;
        sides[BOTTOM]=bottom;
        sides[FRONT]=front;
        sides[BACK]=back;
        sides[RIGHT]=sides[LEFT]=side;
    }
}
