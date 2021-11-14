package sig.models;

import sig.Block;
import sig.BlockType;
import sig.Mesh;
import sig.Triangle;
import sig.utils.OBJReader;

public class Plant extends Mesh{
    public Plant(BlockType type) {
        super(type);
        this.type=type;
        this.triangles=OBJReader.ReadOBJFile("models/plant.obj",true);
        for (Triangle t : triangles) {
            t.tex=type.getTexture(BlockType.FRONT);
        }
    }
    public boolean handleCollision(Block b,float x,float z) {
        return true;
    }
}
