package sig;
import java.awt.Color;

public class Triangle {
    Vector A,B,C;
    Color col = Color.WHITE;
    public Triangle(Vector A,Vector B,Vector C) {
        this.A=A;
        this.B=B;
        this.C=C;
    }
    @Override
    protected Object clone(){
        return new Triangle((Vector)this.A.clone(),(Vector)this.B.clone(),(Vector)this.C.clone());
    }
    @Override
    public String toString() {
        return "Triangle [A=" + A + ", B=" + B + ", C=" + C + "]";
    }
    public Color getColor() {
        return col;
    }
    public void setColor(Color col) {
        this.col=col;
    }
}
