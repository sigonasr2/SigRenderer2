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
            Texture t1 = block.triangles.get(Texture.SOUTH).tex;
            Texture t2 = block.triangles.get(Texture.SOUTH+1).tex;
            block.triangles.get(Texture.SOUTH).tex=block.triangles.get(Texture.EAST).tex;
            block.triangles.get(Texture.SOUTH+1).tex=block.triangles.get(Texture.EAST+1).tex;
            block.triangles.get(Texture.EAST).tex=block.triangles.get(Texture.NORTH).tex;
            block.triangles.get(Texture.EAST+1).tex=block.triangles.get(Texture.NORTH+1).tex;
            block.triangles.get(Texture.NORTH).tex=block.triangles.get(Texture.WEST).tex;
            block.triangles.get(Texture.NORTH+1).tex=block.triangles.get(Texture.WEST+1).tex;
            block.triangles.get(Texture.WEST).tex=t1;
            block.triangles.get(Texture.WEST+1).tex=t2;
            for (int i=8;i<=11;i++) {
                Triangle t = block.triangles.get(i);
                Vector2[] tt = new Vector2[]{t.T,t.U,t.V};
                for (Vector2 vec : tt) {
                    VertexOrder newOrder = VertexOrder.getOrder((int)vec.u,(int)vec.v).clockwise();
                    vec.u=newOrder.u;
                    vec.v=newOrder.v;
                }
            }
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
enum VertexOrder{
    UPPERLEFT(0,1),
    LOWERLEFT(0,0),
    LOWERRIGHT(1,0),
    UPPERRIGHT(1,1);

    static VertexOrder[] orderList = new VertexOrder[]{UPPERLEFT,LOWERLEFT,LOWERRIGHT,UPPERRIGHT};
    int u,v;
    VertexOrder(int u,int v) {
        this.u=u;
        this.v=v;
    }
    static VertexOrder getOrder(int u,int v) {
        for (VertexOrder vo : VertexOrder.values()) {
            if (vo.u==u&&vo.v==v) {
                return vo;
            }
        }
        return null;
    }
    VertexOrder clockwise() {
        return orderList[(this.ordinal()+1)%orderList.length];
    }
    VertexOrder counterClockwise() {
        return orderList[Math.floorMod((this.ordinal()-1),orderList.length)];
    }
}