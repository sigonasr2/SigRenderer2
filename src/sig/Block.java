package sig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import sig.models.Staircase;

public class Block {
    final static int CLOCKWISE = 1;
    final static int COUNTER_CLOCKWISE = -1;
    final static int SOUTH = 0;
    final static int WEST = 1;
    final static int NORTH = 2;
    final static int EAST = 3;
    public Vector pos;
    public Mesh block;
    public FaceList neighbors;
    private FacingDirection facingDir;
    Block(Vector pos,Mesh block,FacingDirection facingDir) {
        this.neighbors=new FaceList();
        this.pos=pos;
        try {
            this.block=block.getClass().getConstructor(BlockType.class).newInstance(block.type);
            for (Triangle t : this.block.triangles) {
                t.b = this;
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.facingDir=facingDir;
    }
    private void updateFacingDirection(FacingDirection targetDirection) {
        while (facingDir!=targetDirection) {
            if (block instanceof Cube) {
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
            }
            facingDir=facingDir.clockwise();
        }
        if (!(block instanceof Cube)) {
            updateFaces();
            /*for (int x=-1;x<=1;x++) {
                for (int y=-1;y<=1;y++) {
                    for (int z=-1;z<=1;z++) {
                        if (Math.abs(x)+Math.abs(y)+Math.abs(z)==1) {
                            String key = (pos.x+x)+"_"+(pos.y+y)+"_"+(pos.z+z);
                            if (SigRenderer.blockGrid.containsKey(key)) {
                                Block b = SigRenderer.blockGrid.get(key);
                                b.updateFaces();
                            }
                        }
                    }
                }
            }*/
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
        String key = pos.x+"_"+(pos.y+1)+"_"+pos.z;
        Block b = SigRenderer.blockGrid.get(key);
        if (SigRenderer.blockGrid.containsKey(key)) {
            if (b.block instanceof Staircase && block instanceof Staircase) {
                neighbors.UP=true;
                b.neighbors.DOWN=false;
            } else {
                neighbors.UP=b.neighbors.DOWN=block.triangles.get(Texture.TOP).tex.hasTransparency==b.block.triangles.get(Texture.BOTTOM).tex.hasTransparency&&block.triangles.get(Texture.TOP).tex.hasTranslucency==b.block.triangles.get(Texture.BOTTOM).tex.hasTranslucency;
            }
        }
        key = pos.x+"_"+(pos.y-1)+"_"+pos.z;
        b = SigRenderer.blockGrid.get(key);
        if (SigRenderer.blockGrid.containsKey(key)) {
            if (b.block instanceof Staircase && block instanceof Staircase) {
                neighbors.DOWN=false;
                b.neighbors.UP=true;
            } else {
                neighbors.DOWN=b.neighbors.UP=block.triangles.get(Texture.BOTTOM).tex.hasTransparency==b.block.triangles.get(Texture.TOP).tex.hasTransparency&&block.triangles.get(Texture.BOTTOM).tex.hasTranslucency==b.block.triangles.get(Texture.TOP).tex.hasTranslucency;
            }
        }
        key=(pos.x-1)+"_"+(pos.y)+"_"+pos.z;
        b = SigRenderer.blockGrid.get(key);
        if (SigRenderer.blockGrid.containsKey(key)) {
            if (b.block instanceof Staircase && block instanceof Staircase) {
                neighbors.LEFT=b.neighbors.RIGHT=
                    FacingDirection.isFacingAwayFromEachOther(this,b)||
                    (FacingDirection.isFacingEachOther(this,b)||
                    (b.getFacingDirection()==getFacingDirection()&&b.getFacingDirection().ordinal()%2==0));
            } else {
                neighbors.LEFT=b.neighbors.RIGHT=block.triangles.get(Texture.WEST).tex.hasTransparency==b.block.triangles.get(Texture.EAST).tex.hasTransparency&&block.triangles.get(Texture.WEST).tex.hasTranslucency==b.block.triangles.get(Texture.EAST).tex.hasTranslucency;
            }
        }
        key=(pos.x+1)+"_"+(pos.y)+"_"+pos.z;
        b = SigRenderer.blockGrid.get(key);
        if (SigRenderer.blockGrid.containsKey(key)) {
            if (b.block instanceof Staircase && block instanceof Staircase) {
                neighbors.RIGHT=b.neighbors.LEFT=
                FacingDirection.isFacingAwayFromEachOther(this,b)||
                (FacingDirection.isFacingEachOther(this,b)||
                (b.getFacingDirection()==getFacingDirection()&&b.getFacingDirection().ordinal()%2==0));
            } else {
                neighbors.RIGHT=b.neighbors.LEFT=block.triangles.get(Texture.EAST).tex.hasTransparency==b.block.triangles.get(Texture.WEST).tex.hasTransparency&&block.triangles.get(Texture.EAST).tex.hasTranslucency==b.block.triangles.get(Texture.WEST).tex.hasTranslucency;
            }
        }
        key=pos.x+"_"+(pos.y)+"_"+(pos.z+1);
        b = SigRenderer.blockGrid.get(key);
        if (SigRenderer.blockGrid.containsKey(key)) {
            if (b.block instanceof Staircase && block instanceof Staircase) {
                neighbors.FORWARD=b.neighbors.BACKWARD=
                FacingDirection.isFacingAwayFromEachOther(this,b)||
                (FacingDirection.isFacingEachOther(this,b)||
                (b.getFacingDirection()==getFacingDirection()&&b.getFacingDirection().ordinal()%2==1));
            } else {
                neighbors.FORWARD=b.neighbors.BACKWARD=block.triangles.get(Texture.SOUTH).tex.hasTransparency==b.block.triangles.get(Texture.NORTH).tex.hasTransparency&&block.triangles.get(Texture.SOUTH).tex.hasTranslucency==b.block.triangles.get(Texture.NORTH).tex.hasTranslucency;
            }
        }
        key=pos.x+"_"+(pos.y)+"_"+(pos.z-1);
        b = SigRenderer.blockGrid.get(key);
        if (SigRenderer.blockGrid.containsKey(key)) {
            if (b.block instanceof Staircase && block instanceof Staircase) {
                neighbors.BACKWARD=b.neighbors.FORWARD=
                FacingDirection.isFacingAwayFromEachOther(this,b)||
                (FacingDirection.isFacingEachOther(this,b)||
                (b.getFacingDirection()==getFacingDirection()&&b.getFacingDirection().ordinal()%2==1));
            } else {
                neighbors.BACKWARD=b.neighbors.FORWARD=block.triangles.get(Texture.NORTH).tex.hasTransparency==b.block.triangles.get(Texture.SOUTH).tex.hasTransparency&&block.triangles.get(Texture.NORTH).tex.hasTranslucency==b.block.triangles.get(Texture.SOUTH).tex.hasTranslucency;
            }
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