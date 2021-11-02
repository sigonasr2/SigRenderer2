package sig;
import java.awt.Color;
import java.util.Arrays;

public class Triangle {
    Vector A,B,C;
    Color col = Color.WHITE;
    public Triangle() {
        this(new Vector(),new Vector(),new Vector());
    }
    public Triangle(Vector A,Vector B,Vector C) {
        this.A=A;
        this.B=B;
        this.C=C;
    }
    @Override
    protected Object clone(){
        Triangle t = new Triangle((Vector)this.A.clone(),(Vector)this.B.clone(),(Vector)this.C.clone());
        t.col = col;
        return t;
    }
    @Override
    public String toString() {
        return "Triangle [A=" + A + ", B=" + B + ", C=" + C + ", col=" + col + "]";
    }
    public Color getColor() {
        return col;
    }
    public void setColor(Color col) {
        this.col=col;
    }

    static float dist(Vector plane_p,Vector plane_n,Vector p) {
        return plane_n.x*p.x+plane_n.y*p.y+plane_n.z*p.z-Vector.dotProduct(plane_n,plane_p);
    }

    public static int ClipAgainstPlane(Vector plane_p,Vector plane_n,Triangle in,Triangle[] out_tri) {
        plane_n = Vector.normalize(plane_n);
        Vector[] inside_points = new Vector[]{new Vector(),new Vector(),new Vector()};
        Vector[] outside_points = new Vector[]{new Vector(),new Vector(),new Vector()};
        int insidePointCount=0,outsidePointCount=0;

        float d0=dist(plane_p,plane_n,in.A);
        float d1=dist(plane_p,plane_n,in.B);
        float d2=dist(plane_p,plane_n,in.C);

        if (d0>=0) {
            inside_points[insidePointCount++]=in.A;
        } else {
            outside_points[outsidePointCount++]=in.A;
        }
        if (d1>=0) {
            inside_points[insidePointCount++]=in.B;
        } else {
            outside_points[outsidePointCount++]=in.B;
        }
        if (d2>=0) {
            inside_points[insidePointCount++]=in.C;
        } else {
            outside_points[outsidePointCount++]=in.C;
        }

        if (insidePointCount==0) {
            return 0;
        } else
        if (insidePointCount==3) {
            out_tri[0] = in;
            return 1;
        } else
        if (insidePointCount==1&&outsidePointCount==2) {
            out_tri[0].col = in.col;
            out_tri[0].A = inside_points[0];
            out_tri[0].B = Vector.IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);
            out_tri[0].C = Vector.IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[1]);
            return 1;
        } else
        if (insidePointCount==2&&outsidePointCount==1) {
            out_tri[0].col=out_tri[1].col=in.col;
            out_tri[0].A = inside_points[0];
            out_tri[0].B = inside_points[1];
            out_tri[0].C = Vector.IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);
            out_tri[1].A = inside_points[1];
            out_tri[1].B = out_tri[0].C;
            out_tri[1].C = Vector.IntersectPlane(plane_p, plane_n, inside_points[1], outside_points[0]);
            return 2;
        }

        return 0;
    }
}
