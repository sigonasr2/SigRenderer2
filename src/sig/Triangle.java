package sig;
import javax.vecmath.Vector3f;

public class Triangle {
    Vector3f A,B,C;
    Triangle(Vector3f A,Vector3f B,Vector3f C) {
        this.A=A;
        this.B=B;
        this.C=C;
    }
    @Override
    public String toString() {
        return "Triangle [A=" + A + ", B=" + B + ", C=" + C + "]";
    }

    public Vector3f getNormal() {
        Vector3f AB = (Vector3f)A.clone();
        AB.sub(B);
        Vector3f BC = (Vector3f)B.clone();
        BC.sub(C);
        Vector3f crossP = new Vector3f();
        crossP.cross(AB,BC);
        //crossP.normalize();
        return crossP;
    }
    public float distanceFromOrigin() {
        return getNormal().dot(A);
    }

    public boolean rayTriangleIntersect(Vector3f origin,Vector3f dir) {
        Vector3f N = getNormal();

        float NrayDir = N.dot(dir);
        if (NrayDir<=0.001) { //Very small, so it's parallel.
            return false;
        }

        float d = distanceFromOrigin();

        float T=(getNormal().dot(origin)+d)/(NrayDir);

        if (T<0) {return false;} //Triangle is behind the ray.

        //System.out.println("Not behind.");

        Vector3f scaleMult = (Vector3f)dir.clone();
        scaleMult.scale(T);
        Vector3f P = (Vector3f)origin.clone();
        P.add(scaleMult);

        Vector3f C;

        Vector3f edge0 = (Vector3f)B.clone(); edge0.sub(A);
        Vector3f vp0 = (Vector3f)P.clone(); vp0.sub(A);
        C = new Vector3f(); C.cross(edge0,vp0);
        if (N.dot(C)<0) {return false;}

        Vector3f edge1 = (Vector3f)this.C.clone(); edge1.sub(B);
        Vector3f vp1 = (Vector3f)P.clone(); vp1.sub(B);
        C = new Vector3f(); C.cross(edge1,vp1);
        if (N.dot(C)<0) {return false;}

        Vector3f edge2 = (Vector3f)A.clone(); edge2.sub(this.C);
        Vector3f vp2 = (Vector3f)P.clone(); vp2.sub(this.C);
        C = new Vector3f(); C.cross(edge2,vp2);
        if (N.dot(C)<0) {return false;}

        return true;
    }
}
