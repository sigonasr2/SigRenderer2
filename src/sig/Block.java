package sig;

import java.util.ArrayList;
import java.util.List;

public class Block {
    final static int CLOCKWISE = 1;
    final static int COUNTER_CLOCKWISE = -1;
    final static int SOUTH = 0;
    final static int WEST = 1;
    final static int NORTH = 2;
    final static int EAST = 3;
    Vector pos;
    Mesh block;
    FaceList neighbors;
    private FacingDirection facingDir;
    Block(Vector pos,Mesh block,FacingDirection facingDir) {
        this.neighbors=new FaceList();
        this.pos=pos;
        List<Triangle> newTris = new ArrayList<>();
        for (Triangle t : block.triangles) {
            Triangle newT = (Triangle)t.clone();
            newT.b=this;
            newTris.add(newT);
        }
        this.block=new Mesh(newTris);
        this.facingDir=facingDir;
    }
    private void updateFacingDirection(FacingDirection targetDirection) {
        while (facingDir!=targetDirection) {
            Texture t1 = block.triangles.get(0).tex;
            Texture t2 = block.triangles.get(1).tex;
            block.triangles.get(0).tex=block.triangles.get(2).tex;
            block.triangles.get(1).tex=block.triangles.get(3).tex;
            block.triangles.get(2).tex=block.triangles.get(4).tex;
            block.triangles.get(3).tex=block.triangles.get(5).tex;
            block.triangles.get(4).tex=block.triangles.get(6).tex;
            block.triangles.get(5).tex=block.triangles.get(7).tex;
            block.triangles.get(6).tex=t1;
            block.triangles.get(7).tex=t2;
            facingDir=facingDir.clockwise();
        }
    }
    public void setFacingDirection(FacingDirection direction) {
        updateFacingDirection(direction);
    }
    public FacingDirection getFacingDirection() {
        return facingDir; 
    }
    public void rotateClockwise() {
        updateFacingDirection(facingDir.clockwise());
    }
    public void rotateCounterClockwise() {
        updateFacingDirection(facingDir.counterClockwise());
    }
    public void updateFaces() {
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y+1)+"_"+pos.z)) {
            neighbors.UP=SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).neighbors.DOWN=block.triangles.get(Texture.TOP).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).block.triangles.get(Texture.BOTTOM).tex.hasTransparency&&block.triangles.get(Texture.TOP).tex.hasTranslucency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).block.triangles.get(Texture.BOTTOM).tex.hasTranslucency;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y-1)+"_"+pos.z)) {
            neighbors.DOWN=SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).neighbors.UP=block.triangles.get(Texture.BOTTOM).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).block.triangles.get(Texture.TOP).tex.hasTransparency&&block.triangles.get(Texture.BOTTOM).tex.hasTranslucency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).block.triangles.get(Texture.TOP).tex.hasTranslucency;
        }
        if (SigRenderer.blockGrid.containsKey((pos.x-1)+"_"+(pos.y)+"_"+pos.z)) {
            neighbors.LEFT=SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).neighbors.RIGHT=block.triangles.get(Texture.WEST).tex.hasTransparency==SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.EAST).tex.hasTransparency&&block.triangles.get(Texture.WEST).tex.hasTranslucency==SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.EAST).tex.hasTranslucency;
        }
        if (SigRenderer.blockGrid.containsKey((pos.x+1)+"_"+(pos.y)+"_"+pos.z)) {
            neighbors.RIGHT=SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).neighbors.LEFT=block.triangles.get(Texture.EAST).tex.hasTransparency==SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.WEST).tex.hasTransparency&&block.triangles.get(Texture.EAST).tex.hasTranslucency==SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.WEST).tex.hasTranslucency;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z+1))) {
            neighbors.FORWARD=SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).neighbors.BACKWARD=block.triangles.get(Texture.SOUTH).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).block.triangles.get(Texture.NORTH).tex.hasTransparency&&block.triangles.get(Texture.SOUTH).tex.hasTranslucency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).block.triangles.get(Texture.NORTH).tex.hasTranslucency;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z-1))) {
            neighbors.BACKWARD=SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).neighbors.FORWARD=block.triangles.get(Texture.NORTH).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).block.triangles.get(Texture.SOUTH).tex.hasTransparency&&block.triangles.get(Texture.NORTH).tex.hasTranslucency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).block.triangles.get(Texture.SOUTH).tex.hasTranslucency;
        }
    }
    @Override
    public String toString() {
        return "Block [pos=" + pos + ", neighbors=" + neighbors + "]";
    }
}
