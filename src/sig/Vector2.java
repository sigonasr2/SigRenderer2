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
    final public static int COLLINEAR=0;
    final public static int CLOCKWISE=1;
    final public static int COUNTER_CLOCKWISE=2;
    public static int orientation(Vector2 v1,Vector2 v2,Vector2 v3) {
        int val = (int)((v2.v-v1.v)*(v3.u-v2.u)-(v2.u-v1.u)*(v3.v-v2.v));
        if (val==0) {return COLLINEAR;}
        return (val>0)?CLOCKWISE:COUNTER_CLOCKWISE;
    }
    public static boolean onSegment(Vector2 v1,Vector2 v2,Vector2 v3) {
        return v2.u<=Math.max(v1.u,v3.u)&&v2.u>=Math.min(v1.u,v3.u)&&
            v2.v<=Math.max(v1.v,v3.v)&&v2.v>=Math.min(v1.v,v3.v);
    }
    public static boolean intersect(Vector2 p1,Vector2 p2,Vector2 p3,Vector2 p4) {
        int o1=orientation(p1,p3,p2);
        int o2=orientation(p1,p3,p4);
        int o3=orientation(p2,p4,p1);
        int o4=orientation(p2,p4,p3);

        if (o1!=o2&&o3!=o4) {return true;}
        if ((o1==COLLINEAR&&onSegment(p1,p2,p3))||
            (o2==COLLINEAR&&onSegment(p1,p4,p3))||
            (o3==COLLINEAR&&onSegment(p2,p1,p4))||
            (o4==COLLINEAR&&onSegment(p2,p3,p4))) {
            return true;
        }
        return false;
    }
    @Override
    protected Object clone(){
        return new Vector2(u,v,w);
    }
    @Override
    public String toString() {
        return "Vector2 [u=" + u + ", v=" + v + ", w=" + w + "]";
    }
}
