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
    float fTheta=0f;
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
                boolean found=false;
                if (!found) {
                    p[ (int)(x*SigRenderer.RESOLUTION) + (int)(y*SigRenderer.RESOLUTION) * width] = 0;
                }
            }
        }   

        accumulatedTris.clear();

        Matrix matRotZ = Matrix.MakeRotationZ(fTheta),matRotX = Matrix.MakeRotationX(fTheta*0.5f);
        Matrix matTranslation = Matrix.MakeTranslation(0,0,16);
        Matrix matWorld = Matrix.IDENTITY;
        matWorld = Matrix.MultiplyMatrix(matRotZ,matRotX);
        matWorld = Matrix.MultiplyMatrix(matWorld,matTranslation);
        fTheta+=0.01f;

        for (Triangle t : SigRenderer.cube.triangles) {
            Triangle triProjected = new Triangle(),triTransformed=new Triangle();
            
            triTransformed.A = Matrix.MultiplyVector(matWorld,t.A);
            triTransformed.B = Matrix.MultiplyVector(matWorld,t.B);
            triTransformed.C = Matrix.MultiplyVector(matWorld,t.C);

            Vector normal=new Vector(),line1=new Vector(),line2=new Vector();
            line1 = Vector.subtract(triTransformed.B,triTransformed.A);
            line2 = Vector.subtract(triTransformed.C,triTransformed.A);

            normal = Vector.crossProduct(line1,line2);
            normal = Vector.normalize(normal);
            
            Vector cameraRay = Vector.subtract(triTransformed.A,SigRenderer.vCamera);

            if (Vector.dotProduct(normal,cameraRay)<0) {

                Vector lightDir = new Vector(0,0,-1);
                lightDir = Vector.normalize(lightDir);

                float dp = Math.max(0.1f,Vector.dotProduct(lightDir,normal));

                triProjected.A = Matrix.MultiplyVector(SigRenderer.matProj,triTransformed.A);
                triProjected.B = Matrix.MultiplyVector(SigRenderer.matProj,triTransformed.B);
                triProjected.C = Matrix.MultiplyVector(SigRenderer.matProj,triTransformed.C);
                triProjected.setColor(new Color(dp,dp,dp));

                triProjected.A = Vector.divide(triProjected.A, triProjected.A.w);
                triProjected.B = Vector.divide(triProjected.B, triProjected.B.w);
                triProjected.C = Vector.divide(triProjected.C, triProjected.C.w);

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

        Collections.sort(accumulatedTris, new Comparator<Triangle>() {
            @Override
            public int compare(Triangle t1, Triangle t2) {
                float z1=(t1.A.z+t1.B.z+t1.C.z)/3f;
                float z2=(t2.A.z+t2.B.z+t2.C.z)/3f;
                return (int)(z1-z2);
            }
        });

        for (Triangle t : accumulatedTris) {
            DrawUtils.FillTriangle(p,(int)t.A.x,(int)t.A.y,(int)t.B.x,(int)t.B.y,(int)t.C.x,(int)t.C.y,t.getColor());
            if (SigRenderer.WIREFRAME) {
                DrawUtils.DrawTriangle(p,(int)t.A.x,(int)t.A.y,(int)t.B.x,(int)t.B.y,(int)t.C.x,(int)t.C.y,Color.BLACK);
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
