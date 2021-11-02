package sig;

public class Matrix {
    float[][] m = new float[4][4];
    Matrix(){}
    Matrix(float[][]m) {
        this.m=m;
    }

    public final static Matrix IDENTITY = new Matrix(new float[][]{
        {1,0,0,0},
        {0,1,0,0},
        {0,0,1,0},
        {0,0,0,1},
     });

    public static Vector MultiplyVector(Matrix m, Vector i) {
        return new Vector(
            i.x*m.m[0][0]+i.y*m.m[1][0]+i.z*m.m[2][0]+i.w*m.m[3][0],
            i.x*m.m[0][1]+i.y*m.m[1][1]+i.z*m.m[2][1]+i.w*m.m[3][1],
            i.x*m.m[0][2]+i.y*m.m[1][2]+i.z*m.m[2][2]+i.w*m.m[3][2],
            i.x*m.m[0][3]+i.y*m.m[1][3]+i.z*m.m[2][3]+i.w*m.m[3][3]
        );
    }

    public static Matrix MakeRotationX(float angle) {
        return new Matrix(new float[][]{
            {1,0,0,0,},
            {0,(float)Math.cos(angle),(float)Math.sin(angle),0,},
            {0,(float)-Math.sin(angle),(float)Math.cos(angle),0,},
            {0,0,0,1,},
        });
    }
    public static Matrix MakeRotationY(float angle) {
        return new Matrix(new float[][]{
            {(float)Math.cos(angle),0,(float)Math.sin(angle),0,},
            {0,1,0,0,},
            {(float)-Math.sin(angle),0,(float)Math.cos(angle),0,},
            {0,0,0,1,},
        });
    }
    public static Matrix MakeRotationZ(float angle) {
        return new Matrix(new float[][]{
            {(float)Math.cos(angle),(float)Math.sin(angle),0,0,},
            {(float)-Math.sin(angle),(float)Math.cos(angle),0,0,},
            {0,0,1,0,},
            {0,0,0,1,},
        });
    }
    public static Matrix MakeTranslation(float x,float y,float z) {
        return new Matrix(new float[][]{
            {1,0,0,0},
            {0,1,0,0},
            {0,0,1,0},
            {x,y,z,1},
        });
    }   
    public static Matrix MakeProjection(float fov,float aspectRatio,float near,float far) {
        float fFovRad = 1f/(float)Math.tan(fov*0.5f/180f*Math.PI);
        return new Matrix(
            new float[][]{
                {aspectRatio*fFovRad,0,0,0},
                {0,fFovRad,0,0},
                {0,0,far/(far-near),1f},
                {0,0,(-far*near)/(far-near),0f},
            });
    }
    public static Matrix MultiplyMatrix(Matrix m1,Matrix m2) {
        Matrix mm = new Matrix();
        for (int c=0;c<4;c++) {
            for (int r=0;r<4;r++) {
                mm.m[r][c]=
                    m1.m[r][0]*m2.m[0][c]+
                    m1.m[r][1]*m2.m[1][c]+
                    m1.m[r][2]*m2.m[2][c]+
                    m1.m[r][3]*m2.m[3][c];
            }
        }
        return mm;
    }
}
