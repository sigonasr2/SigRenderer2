package sig;

import javax.vecmath.Vector3f;

public class Matrix {
    float[][] m = new float[4][4];
    Matrix(float[][]m) {
        this.m=m;
    }

    public static void MultiplyMatrixVector(Vector3f i,Vector3f o,Matrix m) {
        o.x=i.x*m.m[0][0]+i.y*m.m[1][0]+i.z*m.m[2][0]+m.m[3][0];
        o.y=i.x*m.m[0][1]+i.y*m.m[1][1]+i.z*m.m[2][1]+m.m[3][1];
        o.z=i.x*m.m[0][2]+i.y*m.m[1][2]+i.z*m.m[2][2]+m.m[3][2];
        float w=i.x*m.m[0][3]+i.y*m.m[1][3]+i.z*m.m[2][3]+m.m[3][3];

        if (w!=0f) {
            o.x/=w;
            o.y/=w;
            o.z/=w;
        }
    }
}
