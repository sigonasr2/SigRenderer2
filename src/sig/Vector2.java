package sig;

public class Vector2 {
    public float u,v,w;
    Vector2() {
        this(0,0,1);
    }
    public Vector2(float u,float v) {
        this(u,v,1);
    }
    public Vector2(float u,float v,float w) {
        this.u=u;
        this.v=v;
        this.w=w;
    }
    @Override
    protected Object clone(){
        return new Vector2(u,v,w);
    }
}
