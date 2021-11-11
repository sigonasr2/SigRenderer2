package sig.models;

import java.util.ArrayList;
import java.util.List;

import sig.Block;
import sig.BlockType;
import sig.Mesh;
import sig.Triangle;
import sig.utils.OBJReader;

public class Staircase extends Mesh{
    public Staircase(BlockType type) {
        super(type);
        this.type = type;
        this.triangles=OBJReader.ReadOBJFile("models/staircase.obj",true);
        for (Triangle t : triangles) {
            t.tex=type.getTexture(BlockType.FRONT);
        }
    }
    
    protected List<Triangle> prepareRender(Block b) {
        List<Triangle> tris = new ArrayList<Triangle>();
        if (!b.neighbors.UP) {
        }
        if (!b.neighbors.DOWN) {
        }
        if (!b.neighbors.LEFT) {
            tris.add(b.block.triangles.get(12));
            tris.add(b.block.triangles.get(13));
            tris.add(b.block.triangles.get(14));
            tris.add(b.block.triangles.get(15));
        }
        if (!b.neighbors.RIGHT) {
            tris.add(b.block.triangles.get(16));
            tris.add(b.block.triangles.get(17));
            tris.add(b.block.triangles.get(18));
            tris.add(b.block.triangles.get(19));
        }
        if (!b.neighbors.FORWARD) {
        }
        if (!b.neighbors.BACKWARD) {
            tris.add(b.block.triangles.get(0));
            tris.add(b.block.triangles.get(1));
        }   
        tris.add(b.block.triangles.get(4));
        tris.add(b.block.triangles.get(5));
        tris.add(b.block.triangles.get(10));
        tris.add(b.block.triangles.get(11));
        tris.add(b.block.triangles.get(8));
        tris.add(b.block.triangles.get(9));
        tris.add(b.block.triangles.get(2));
        tris.add(b.block.triangles.get(3));
        tris.add(b.block.triangles.get(6));
        tris.add(b.block.triangles.get(7));
        return tris;
    }
}
