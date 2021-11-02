package sig;
import javax.vecmath.Vector3f;
import java.awt.Color;

public class Triangle {
    Vector3f A,B,C;
    Color col = Color.WHITE;
    Triangle(Vector3f A,Vector3f B,Vector3f C) {
        this.A=A;
        this.B=B;
        this.C=C;
    }
    @Override
    protected Object clone(){
        return new Triangle((Vector3f)this.A.clone(),(Vector3f)this.B.clone(),(Vector3f)this.C.clone());
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
