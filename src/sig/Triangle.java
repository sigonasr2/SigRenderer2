package sig;
import java.awt.Color;

public class Triangle {
    Vector A,B,C;
    Vector2 T,U,V;
    Color col = Color.WHITE;
    Block b = null;
    public Texture tex = null;
    public Triangle() {
        this(new Vector(),new Vector(),new Vector());
    }
    public Triangle(Vector A,Vector B,Vector C) {
        this(A,B,C,new Vector2(),new Vector2(),new Vector2());
    }
    public Triangle(Vector A,Vector B,Vector C,Vector2 T,Vector2 U,Vector2 V) {
        this.A=A;
        this.B=B;
        this.C=C;
        this.T=T;
        this.U=U;
        this.V=V;
    }
    public void copyExtraDataTo(Triangle targetTriangle) {
        targetTriangle.T=this.T;
        targetTriangle.U=this.U;
        targetTriangle.V=this.V;
        targetTriangle.col=this.col;
        targetTriangle.tex=this.tex;
        targetTriangle.b=this.b;
    }
    @Override
    protected Object clone(){
        Triangle t = new Triangle((Vector)this.A.clone(),(Vector)this.B.clone(),(Vector)this.C.clone(),(Vector2)this.T.clone(),(Vector2)this.U.clone(),(Vector2)this.V.clone());
        t.col = col;
        t.tex=tex;
        t.b=b;
        return t;
    }
    @Override
    public String toString() {
        return "Triangle [A=" + A + ", B=" + B + ", C=" + C + ", T=" + T + ", U=" + U + ", V=" + V + ", col=" + col
                + "]";
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
        Vector2[] inside_tex = new Vector2[]{new Vector2(),new Vector2(),new Vector2()};
        Vector2[] outside_tex = new Vector2[]{new Vector2(),new Vector2(),new Vector2()};
        int insidePointCount=0,outsidePointCount=0;
        int insideTexCount=0,outsideTexCount=0;

        float d0=dist(plane_p,plane_n,in.A);
        float d1=dist(plane_p,plane_n,in.B);
        float d2=dist(plane_p,plane_n,in.C);

        if (d0>=0) {
            inside_points[insidePointCount++]=in.A;
            inside_tex[insideTexCount++]=in.T;
        } else {
            outside_points[outsidePointCount++]=in.A;
            outside_tex[outsideTexCount++]=in.T;
        }
        if (d1>=0) {
            inside_points[insidePointCount++]=in.B;
            inside_tex[insideTexCount++]=in.U;
        } else {
            outside_points[outsidePointCount++]=in.B;
            outside_tex[outsideTexCount++]=in.U;
        }
        if (d2>=0) {
            inside_points[insidePointCount++]=in.C;
            inside_tex[insideTexCount++]=in.V;
        } else {
            outside_points[outsidePointCount++]=in.C;
            outside_tex[outsideTexCount++]=in.V;
        }

        if (insidePointCount==0) {
            return 0;
        } else
        if (insidePointCount==3) {
            out_tri[0] = in;
            return 1;
        } else
        if (insidePointCount==1&&outsidePointCount==2) {
            ExtraData t = new ExtraData(0);
            out_tri[0].col = in.col;
            out_tri[0].tex = in.tex;
            out_tri[0].b=in.b;
            out_tri[0].A = inside_points[0];
            out_tri[0].T = inside_tex[0];
            out_tri[0].B = Vector.IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0],t);
            out_tri[0].U.u = t.t*(outside_tex[0].u-inside_tex[0].u)+inside_tex[0].u;
            out_tri[0].U.v = t.t*(outside_tex[0].v-inside_tex[0].v)+inside_tex[0].v;
            out_tri[0].U.w = t.t*(outside_tex[0].w-inside_tex[0].w)+inside_tex[0].w;
            out_tri[0].C = Vector.IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[1],t);
            out_tri[0].V.u = t.t*(outside_tex[1].u-inside_tex[0].u)+inside_tex[0].u;
            out_tri[0].V.v = t.t*(outside_tex[1].v-inside_tex[0].v)+inside_tex[0].v;
            out_tri[0].V.w = t.t*(outside_tex[1].w-inside_tex[0].w)+inside_tex[0].w;
            return 1;
        } else
        if (insidePointCount==2&&outsidePointCount==1) {
            ExtraData t = new ExtraData(0);
            out_tri[0].col=out_tri[1].col=in.col;
            out_tri[0].tex=out_tri[1].tex=in.tex;
            out_tri[0].b=out_tri[1].b=in.b;
            out_tri[0].A = inside_points[0];
            out_tri[0].B = inside_points[1];
            out_tri[0].T = inside_tex[0];
            out_tri[0].U = inside_tex[1];
            out_tri[0].C = Vector.IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0],t);
            out_tri[0].V.u = t.t*(outside_tex[0].u-inside_tex[0].u)+inside_tex[0].u;
            out_tri[0].V.v = t.t*(outside_tex[0].v-inside_tex[0].v)+inside_tex[0].v;
            out_tri[0].V.w = t.t*(outside_tex[0].w-inside_tex[0].w)+inside_tex[0].w;
            out_tri[1].A = inside_points[1];
            out_tri[1].T = inside_tex[1];
            out_tri[1].B = out_tri[0].C;
            out_tri[1].U = out_tri[0].V;
            out_tri[1].C = Vector.IntersectPlane(plane_p, plane_n, inside_points[1], outside_points[0],t);
            out_tri[1].V.u = t.t*(outside_tex[0].u-inside_tex[1].u)+inside_tex[1].u;
            out_tri[1].V.v = t.t*(outside_tex[0].v-inside_tex[1].v)+inside_tex[1].v;
            out_tri[1].V.w = t.t*(outside_tex[0].w-inside_tex[1].w)+inside_tex[1].w;
            return 2;
        }

        return 0;
    }
}
