package sig;

public class Vector {
    public float x,y,z,w;
    Vector() {
        this(0,0,0,1);
    }
    public Vector(float x,float y,float z) {
        this(x,y,z,1);
    }
    public Vector(float x,float y,float z,float w) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.w=w;
    }
    @Override
    protected Object clone(){
        return new Vector(x,y,z);
    }

    public static Vector add(Vector v1,Vector v2) {
        return new Vector(v1.x+v2.x,v1.y+v2.y,v1.z+v2.z);
    }
    public static Vector subtract(Vector v1,Vector v2) {
        return new Vector(v1.x-v2.x,v1.y-v2.y,v1.z-v2.z);
    }
    public static Vector multiply(Vector v1,float k) {
        return new Vector(v1.x*k,v1.y*k,v1.z*k);
    }
    public static Vector divide(Vector v1,float k) {
        return new Vector(v1.x/k,v1.y/k,v1.z/k);
    }
    public static float dotProduct(Vector v1,Vector v2) {
        return v1.x*v2.x+v1.y*v2.y+v1.z*v2.z;
    }
    public static float length(Vector v) {
        return (float)Math.sqrt(dotProduct(v,v));
    }
    public static Vector normalize(Vector v) {
        float l = length(v);
        return new Vector(v.x/l,v.y/l,v.z/l);
    }
    public static Vector crossProduct(Vector v1,Vector v2) {
        Vector v = new Vector();
        v.x=v1.y*v2.z-v1.z*v2.y;
        v.y=v1.z*v2.x-v1.x*v2.z;
        v.z=v1.x*v2.y-v1.y*v2.x;
        return v;
    }

    @Override
    public String toString() {
        return "Vector ["+x+","+y+","+z+","+w+"]";
    }
    public static Vector IntersectPlane(Vector plane_p,Vector plane_n,Vector lineStart,Vector lineEnd) {
        plane_n = Vector.normalize(plane_n);
        float plane_d = -Vector.dotProduct(plane_n,plane_p);
        float ad = Vector.dotProduct(lineStart,plane_n);
        float bd = Vector.dotProduct(lineEnd,plane_n);
        float t = (-plane_d-ad)/(bd-ad);
        Vector lineStartToEnd = Vector.subtract(lineEnd,lineStart);
        Vector lineToIntersect = Vector.multiply(lineStartToEnd,t);
        return Vector.add(lineStart,lineToIntersect);
    }
}
