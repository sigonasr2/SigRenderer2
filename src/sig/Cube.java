package sig;

import sig.utils.OBJReader;

public class Cube extends Mesh{

    Cube(BlockType type) {
        super(type);
        this.triangles=OBJReader.ReadOBJFile("cube.obj",true);
        triangles.get(0).tex=type.getTexture(BlockType.FRONT);
        triangles.get(1).tex=type.getTexture(BlockType.FRONT);
        triangles.get(0).dir=triangles.get(1).dir=BlockType.FRONT;
        triangles.get(2).tex=type.getTexture(BlockType.RIGHT);
        triangles.get(3).tex=type.getTexture(BlockType.RIGHT);
        triangles.get(2).dir=triangles.get(3).dir=BlockType.RIGHT;
        triangles.get(4).tex=type.getTexture(BlockType.BACK);
        triangles.get(5).tex=type.getTexture(BlockType.BACK);
        triangles.get(4).dir=triangles.get(5).dir=BlockType.BACK;
        triangles.get(6).tex=type.getTexture(BlockType.LEFT);
        triangles.get(7).tex=type.getTexture(BlockType.LEFT);
        triangles.get(6).dir=triangles.get(7).dir=BlockType.LEFT;
        triangles.get(8).tex=type.getTexture(BlockType.TOP);
        triangles.get(9).tex=type.getTexture(BlockType.TOP);
        triangles.get(8).dir=triangles.get(9).dir=BlockType.TOP;
        triangles.get(10).tex=type.getTexture(BlockType.BOTTOM);
        triangles.get(11).tex=type.getTexture(BlockType.BOTTOM);
        triangles.get(10).dir=triangles.get(11).dir=BlockType.BOTTOM;
    }
    
}
