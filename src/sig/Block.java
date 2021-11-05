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
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).neighbors.DOWN=true;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y-1)+"_"+pos.z)) {
            neighbors.DOWN=true;
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).neighbors.UP=true;
        }
        if (SigRenderer.blockGrid.containsKey((pos.x-1)+"_"+(pos.y)+"_"+pos.z)) {
            neighbors.LEFT=true;
            SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).neighbors.RIGHT=true;
        }
        if (SigRenderer.blockGrid.containsKey((pos.x+1)+"_"+(pos.y)+"_"+pos.z)) {
            neighbors.RIGHT=true;
            SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).neighbors.LEFT=true;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z+1))) {
            neighbors.FORWARD=true;
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).neighbors.BACKWARD=true;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z-1))) {
            neighbors.BACKWARD=true;
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).neighbors.FORWARD=true;
        }
    }
    @Override
    public String toString() {
        return "Block [pos=" + pos + ", neighbors=" + neighbors + "]";
    }
}
