package sig;
import javax.swing.JPanel;

import sig.utils.DrawUtils;

import java.awt.Graphics;
import java.awt.Color;

import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.image.ColorModel;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;

public class Panel extends JPanel implements Runnable {
    long startTime = System.nanoTime();
    long endTime = System.nanoTime();
    public int pixel[];
    public int width=SigRenderer.SCREEN_WIDTH;
    public int height=SigRenderer.SCREEN_HEIGHT;
    private Image imageBuffer;   
    private MemoryImageSource mImageProducer;   
    private ColorModel cm;    
    private Thread thread;
    List<Triangle> accumulatedTris = new ArrayList<Triangle>();

    public Panel() {
        super(true);
        thread = new Thread(this, "MyPanel Thread");
    }

    /**
     * Get Best Color model available for current screen.
     * @return color model
     */
    protected static ColorModel getCompatibleColorModel(){        
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();        
        return gfx_config.getColorModel();
    }

    /**
     * Call it after been visible and after resizes.
     */
    public void init(){        
        cm = getCompatibleColorModel();
        width = getWidth();
        height = getHeight();
        SigRenderer.SCREEN_WIDTH=getWidth();
        SigRenderer.SCREEN_HEIGHT=getHeight();
        int screenSize = width * height;
        if(pixel == null || pixel.length < screenSize){
            pixel = new int[screenSize];
        }        
        mImageProducer =  new MemoryImageSource(width, height, cm, pixel,0, width);
        mImageProducer.setAnimated(true);
        mImageProducer.setFullBufferUpdates(true);  
        imageBuffer = Toolkit.getDefaultToolkit().createImage(mImageProducer);        
        if(thread.isInterrupted() || !thread.isAlive()){
            thread.start();
        }
        SigRenderer.depthBuffer = new float[width*height];
    }
    /**
    * Do your draws in here !!
    * pixel is your canvas!
    */
    public /* abstract */ void render(){
        int[] p = pixel; // this avoid crash when resizing
        //a=h/w

        final int h=SigRenderer.SCREEN_HEIGHT;
        if(p.length != width * height) return;        
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                p[ (int)(x*SigRenderer.RESOLUTION) + (int)(y*SigRenderer.RESOLUTION) * width] = 0;
                SigRenderer.depthBuffer[x+y*width]=0;
            }
        }   

        accumulatedTris.clear();

        //Matrix matRotZ = Matrix.MakeRotationZ(fTheta*0.5f),matRotX = Matrix.MakeRotationX(fTheta);
        Matrix matRotZ = Matrix.IDENTITY;
        Matrix matRotX = Matrix.IDENTITY;
        Matrix matTranslation = Matrix.MakeTranslation(0,0,0);
        Matrix matWorld = Matrix.IDENTITY;
        matWorld = Matrix.MultiplyMatrix(matRotZ,matRotX);
        matWorld = Matrix.MultiplyMatrix(matWorld,matTranslation);

        Vector vUp = new Vector(0,1,0);
        Vector vTarget = new Vector(0,(float)Math.sin(SigRenderer.pitch),(float)Math.cos(SigRenderer.pitch));
        Matrix matCameraRot = Matrix.MakeRotationY(SigRenderer.yaw);
        SigRenderer.vLookDir = Matrix.MultiplyVector(matCameraRot,vTarget);
        vTarget = Vector.add(SigRenderer.vCamera,SigRenderer.vLookDir);

        Matrix matCamera = Matrix.PointAt(SigRenderer.vCamera, vTarget, vUp);
        Matrix matView = Matrix.QuickInverse(matCamera);

        for (Block b : SigRenderer.blocks) {
            for (Triangle t : b.block.triangles) {
                Triangle triProjected = new Triangle(),triTransformed=new Triangle(),triViewed=new Triangle();

                matWorld = Matrix.MakeTranslation(b.pos.x,b.pos.y,b.pos.z);
                
                triTransformed.A = Matrix.MultiplyVector(matWorld,t.A);
                triTransformed.B = Matrix.MultiplyVector(matWorld,t.B);
                triTransformed.C = Matrix.MultiplyVector(matWorld,t.C);
                triTransformed.T = t.T;
                triTransformed.U = t.U;
                triTransformed.V = t.V;
                triTransformed.tex = t.tex;

                Vector normal=new Vector(),line1=new Vector(),line2=new Vector();
                line1 = Vector.subtract(triTransformed.B,triTransformed.A);
                line2 = Vector.subtract(triTransformed.C,triTransformed.A);

                normal = Vector.crossProduct(line1,line2);
                normal = Vector.normalize(normal);
                
                Vector cameraRay = Vector.subtract(triTransformed.A,SigRenderer.vCamera);

                if (Vector.dotProduct(normal,cameraRay)<0) {
                    /*Vector lightDir = Vector.multiply(SigRenderer.vLookDir, -1);
                    lightDir = Vector.normalize(lightDir);*/

                    //System.out.println(-Vector.dotProduct(normal,Vector.normalize(cameraRay)));
                    //float dp = Math.max(0.1f,Math.min(1,-1/Vector.dotProduct(normal,cameraRay)));
                    /*Vector center = Vector.divide(Vector.add(triTransformed.A,Vector.add(triTransformed.B,triTransformed.C)),3);
                    Vector cameraRay2 = Vector.subtract(center,SigRenderer.vCamera);
                    float dp = Math.max(0.1f,Math.min(1,(1f/((cameraRay2.x-center.x)*(cameraRay2.x-center.x)+
                    (cameraRay2.y-center.y)*(cameraRay2.y-center.y)+
                    (cameraRay2.z-center.z)*(cameraRay2.z-center.z))*4)));*/
                    float dp = Math.max(0.1f,Math.min(1,(1f/((b.pos.x-SigRenderer.vCamera.x)+
                    (b.pos.y-SigRenderer.vCamera.y)*(b.pos.y-SigRenderer.vCamera.y)+
                    (b.pos.z-SigRenderer.vCamera.z)*(b.pos.z-SigRenderer.vCamera.z))*4)));

                    triViewed.A = Matrix.MultiplyVector(matView,triTransformed.A);
                    triViewed.B = Matrix.MultiplyVector(matView,triTransformed.B);
                    triViewed.C = Matrix.MultiplyVector(matView,triTransformed.C);
                    triViewed.setColor(new Color(dp,dp,dp));
                    triViewed.T = triTransformed.T;
                    triViewed.U = triTransformed.U;
                    triViewed.V = triTransformed.V;
                    triViewed.tex = triTransformed.tex;

                    int clippedTriangles = 0;
                    Triangle[] clipped = new Triangle[]{new Triangle(),new Triangle()};

                    clippedTriangles = Triangle.ClipAgainstPlane(new Vector(0,0,0.1f),new Vector(0,0,1), triViewed, clipped);
                    for (int i=0;i<clippedTriangles;i++) {
                        if (i>0) {
                            triProjected = new Triangle();
                        }
                        triProjected.A = Matrix.MultiplyVector(SigRenderer.matProj,clipped[i].A);
                        triProjected.B = Matrix.MultiplyVector(SigRenderer.matProj,clipped[i].B);
                        triProjected.C = Matrix.MultiplyVector(SigRenderer.matProj,clipped[i].C);
                        triProjected.col = clipped[i].col;
                        triProjected.tex = clipped[i].tex;
                        triProjected.T = (Vector2)clipped[i].T.clone();
                        triProjected.U = (Vector2)clipped[i].U.clone();
                        triProjected.V = (Vector2)clipped[i].V.clone();

                        triProjected.T.u = triProjected.T.u/triProjected.A.w;
                        triProjected.U.u = triProjected.U.u/triProjected.B.w;
                        triProjected.V.u = triProjected.V.u/triProjected.C.w;
                        triProjected.T.v = triProjected.T.v/triProjected.A.w;
                        triProjected.U.v = triProjected.U.v/triProjected.B.w;
                        triProjected.V.v = triProjected.V.v/triProjected.C.w;

                        triProjected.T.w = 1.0f/triProjected.A.w;
                        triProjected.U.w = 1.0f/triProjected.B.w;
                        triProjected.V.w = 1.0f/triProjected.C.w;

                        triProjected.A = Vector.divide(triProjected.A, triProjected.A.w);
                        triProjected.B = Vector.divide(triProjected.B, triProjected.B.w);
                        triProjected.C = Vector.divide(triProjected.C, triProjected.C.w);

                        triProjected.A.x*=-1f;
                        triProjected.A.y*=-1f;
                        triProjected.B.x*=-1f;
                        triProjected.B.y*=-1f;
                        triProjected.C.x*=-1f;
                        triProjected.C.y*=-1f;

                        Vector viewOffset = new Vector(1,1,0);
                        triProjected.A = Vector.add(triProjected.A,viewOffset);
                        triProjected.B = Vector.add(triProjected.B,viewOffset);
                        triProjected.C = Vector.add(triProjected.C,viewOffset);
                        triProjected.A.x*=0.5f*SigRenderer.SCREEN_WIDTH;
                        triProjected.A.y*=0.5f*SigRenderer.SCREEN_HEIGHT;
                        triProjected.B.x*=0.5f*SigRenderer.SCREEN_WIDTH;
                        triProjected.B.y*=0.5f*SigRenderer.SCREEN_HEIGHT;
                        triProjected.C.x*=0.5f*SigRenderer.SCREEN_WIDTH;
                        triProjected.C.y*=0.5f*SigRenderer.SCREEN_HEIGHT;

                        accumulatedTris.add(triProjected);
                    }
                }
            } 
        }

        /*Collections.sort(accumulatedTris, new Comparator<Triangle>() {
            @Override
            public int compare(Triangle t1, Triangle t2) {
                float z1=(t1.A.z+t1.B.z+t1.C.z)/3f;
                float z2=(t2.A.z+t2.B.z+t2.C.z)/3f;
                return (z1<z2)?1:(z1==z2)?0:-1;
            }
        });*/

        for (Triangle t : accumulatedTris) {
            Triangle[] clipped = new Triangle[]{new Triangle(),new Triangle()};
            List<Triangle> triList = new ArrayList<>();
            triList.add(t);
            int newTriangles=1;
            for (int pl=0;pl<4;pl++) {
                int trisToAdd=0;
                while (newTriangles>0) {
                    clipped = new Triangle[]{new Triangle(),new Triangle()};
                    Triangle test = triList.remove(0);
                    newTriangles--;
                    switch (pl) {
                        case 0:{trisToAdd = Triangle.ClipAgainstPlane(new Vector(0,0,0),new Vector(0,1,0),test,clipped);}break;
                        case 1:{trisToAdd = Triangle.ClipAgainstPlane(new Vector(0,getHeight()-1f,0),new Vector(0,-1,0),test,clipped);}break;
                        case 2:{trisToAdd = Triangle.ClipAgainstPlane(new Vector(0,0,0),new Vector(1,0,0),test,clipped);}break;
                        case 3:{trisToAdd = Triangle.ClipAgainstPlane(new Vector(getWidth()-1f,0,0),new Vector(-1,0,0),test,clipped);}break;
                    }
                    for (int w=0;w<trisToAdd;w++) {
                        triList.add(clipped[w]);
                    }
                }
                newTriangles=triList.size();
            }

            for (Triangle tt : triList) {
                if (tt.tex!=null) {
                    DrawUtils.TexturedTriangle(p, 
                        (int)tt.A.x,(int)tt.A.y,tt.T.u,tt.T.v,tt.T.w,
                        (int)tt.B.x,(int)tt.B.y,tt.U.u,tt.U.v,tt.U.w,
                        (int)tt.C.x,(int)tt.C.y,tt.V.u,tt.V.v,tt.V.w,
                    tt.tex,tt.col.getRed());
                } else {
                    DrawUtils.FillTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,tt.getColor());
                }
                if (SigRenderer.WIREFRAME) {
                    DrawUtils.DrawTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,Color.WHITE);
                }
            }
        }
    }    

    public void repaint() {
        super.repaint();
        startTime = System.nanoTime();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // perform draws on pixels
        render();
        // ask ImageProducer to update image
        mImageProducer.newPixels();            
        // draw it on panel          
        g.drawImage(this.imageBuffer, 0, 0, this);  
        endTime=System.nanoTime();      
        SigRenderer.DRAWLOOPTIME=(endTime-startTime)/1000000f;
    }
    
    /**
     * Overrides ImageObserver.imageUpdate.
     * Always return true, assuming that imageBuffer is ready to go when called
     */
    @Override
    public boolean imageUpdate(Image image, int a, int b, int c, int d, int e) {
        return true;
    }
    @Override
    public void run() {
        while (true) {
            // request a JPanel re-drawing
            repaint();                                  
            try {Thread.sleep(5);} catch (InterruptedException e) {}
        }
    }
}
