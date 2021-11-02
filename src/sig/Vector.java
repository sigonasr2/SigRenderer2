package sig;

public class Vector {
    public float x,y,z;
    Vector() {
        this(0,0,0);
    }
    public Vector(float x,float y,float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    @Override
    protected Object clone(){
        return new Vector(x,y,z);
    }
}
