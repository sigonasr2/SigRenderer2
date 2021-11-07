package sig;

import java.util.ArrayList;
import java.util.List;

public class Block {
    Vector pos;
    Mesh block;
    FaceList neighbors;
    Block(Vector pos,Mesh block) {
        this.neighbors=new FaceList();
        this.pos=pos;
        List<Triangle> newTris = new ArrayList<>();
        for (Triangle t : block.triangles) {
            Triangle newT = (Triangle)t.clone();
            newT.b=this;
            newTris.add(newT);
        }
        this.block=new Mesh(newTris);
    }
    public void updateFaces() {
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y+1)+"_"+pos.z)) {
            neighbors.UP=true;
            if (block.triangles.get(Texture.TOP).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).block.triangles.get(Texture.BOTTOM).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).neighbors.DOWN=true;
            } else
            if (block.triangles.get(Texture.TOP).tex.hasTransparency&&!SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).block.triangles.get(Texture.BOTTOM).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).neighbors.DOWN=false;
            }
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y-1)+"_"+pos.z)) {
            neighbors.DOWN=true;
            if (block.triangles.get(Texture.BOTTOM).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).block.triangles.get(Texture.TOP).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).neighbors.UP=true;
            } else
            if (block.triangles.get(Texture.BOTTOM).tex.hasTransparency&&!SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).block.triangles.get(Texture.TOP).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).neighbors.UP=false;
            }
        }
        if (SigRenderer.blockGrid.containsKey((pos.x-1)+"_"+(pos.y)+"_"+pos.z)) {
            neighbors.LEFT=true;
            if (block.triangles.get(Texture.WEST).tex.hasTransparency==SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.EAST).tex.hasTransparency) {
                SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).neighbors.RIGHT=true;
            } else
            if (block.triangles.get(Texture.WEST).tex.hasTransparency&&!SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.EAST).tex.hasTransparency) {
                SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).neighbors.RIGHT=false;
            }
        }
        if (SigRenderer.blockGrid.containsKey((pos.x+1)+"_"+(pos.y)+"_"+pos.z)) {
            neighbors.RIGHT=true;
            if (block.triangles.get(Texture.EAST).tex.hasTransparency==SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.WEST).tex.hasTransparency) {
                SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).neighbors.LEFT=true;
            } else
            if (block.triangles.get(Texture.EAST).tex.hasTransparency&&!SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).block.triangles.get(Texture.WEST).tex.hasTransparency) {
                SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).neighbors.LEFT=false;
            }
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z+1))) {
            neighbors.FORWARD=true;
            if (block.triangles.get(Texture.SOUTH).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).block.triangles.get(Texture.NORTH).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).neighbors.BACKWARD=true;
            } else
            if (block.triangles.get(Texture.SOUTH).tex.hasTransparency&&!SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).block.triangles.get(Texture.NORTH).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).neighbors.BACKWARD=false;
            }
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z-1))) {
            neighbors.BACKWARD=true;
            if (block.triangles.get(Texture.NORTH).tex.hasTransparency==SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).block.triangles.get(Texture.SOUTH).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).neighbors.FORWARD=true;
            } else
            if (block.triangles.get(Texture.NORTH).tex.hasTransparency&&!SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).block.triangles.get(Texture.SOUTH).tex.hasTransparency) {
                SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).neighbors.FORWARD=false;
            }
        }
    }
    @Override
    public String toString() {
        return "Block [pos=" + pos + ", neighbors=" + neighbors + "]";
    }
}
