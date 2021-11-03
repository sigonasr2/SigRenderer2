package sig;

public class Vector2 {
    public float u,v;
    Vector2() {
        this(0,0);
    }
    public Vector2(float u,float v) {
        this.u=u;
        this.v=v;
    }
    @Override
    protected Object clone(){
        return new Vector2(u,v);
    }
}
