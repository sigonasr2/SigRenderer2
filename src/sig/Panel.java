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

        Matrix matRotZ = new Matrix(new float[][]{
            {(float)Math.cos(fTheta),(float)Math.sin(fTheta),0,0,},
            {(float)-Math.sin(fTheta),(float)Math.cos(fTheta),0,0,},
            {0,0,1,0,},
            {0,0,0,1,},
        }),matRotX = new Matrix(new float[][]{
            {1,0,0,0,},
            {0,(float)Math.cos(fTheta*0.5f),(float)Math.sin(fTheta*0.5f),0,},
            {0,(float)-Math.sin(fTheta*0.5f),(float)Math.cos(fTheta*0.5f),0,},
            {0,0,0,1,},
        });
        fTheta+=0.01f;

        for (Triangle t : SigRenderer.cube.triangles) {
            Triangle triProjected = new Triangle(new Vector(),new Vector(),new Vector()),triTranslated=new Triangle(new Vector(),new Vector(),new Vector()),triRotatedZ=new Triangle(new Vector(),new Vector(),new Vector()),triRotatedZX=new Triangle(new Vector(),new Vector(),new Vector());
            

            Matrix.MultiplyMatrixVector(t.A, triRotatedZ.A, matRotZ);
            Matrix.MultiplyMatrixVector(t.B, triRotatedZ.B, matRotZ);
            Matrix.MultiplyMatrixVector(t.C, triRotatedZ.C, matRotZ);
            Matrix.MultiplyMatrixVector(triRotatedZ.A, triRotatedZX.A, matRotX);
            Matrix.MultiplyMatrixVector(triRotatedZ.B, triRotatedZX.B, matRotX);
            Matrix.MultiplyMatrixVector(triRotatedZ.C, triRotatedZX.C, matRotX);


            triTranslated = (Triangle)triRotatedZX.clone();
            triTranslated.A.z=triRotatedZX.A.z+6f;
            triTranslated.B.z=triRotatedZX.B.z+6f;
            triTranslated.C.z=triRotatedZX.C.z+6f;

            Vector normal=new Vector(),line1=new Vector(),line2=new Vector();
            line1.x=triTranslated.B.x-triTranslated.A.x;
            line1.y=triTranslated.B.y-triTranslated.A.y;
            line1.z=triTranslated.B.z-triTranslated.A.z;
            line2.x=triTranslated.C.x-triTranslated.A.x;
            line2.y=triTranslated.C.y-triTranslated.A.y;
            line2.z=triTranslated.C.z-triTranslated.A.z;

            normal.x=line1.y*line2.z-line1.z*line2.y;
            normal.y=line1.z*line2.x-line1.x*line2.z;
            normal.z=line1.x*line2.y-line1.y*line2.x;

            float l = (float)Math.sqrt(normal.x*normal.x+normal.y*normal.y+normal.z*normal.z);
            normal.x/=l; normal.y/=l; normal.z/=l;

            if (normal.x*(triTranslated.A.x-SigRenderer.vCamera.x)+
                normal.y*(triTranslated.A.y-SigRenderer.vCamera.y)+
                normal.z*(triTranslated.A.z-SigRenderer.vCamera.z)<0) {

                Vector lightDir = new Vector(0,0,-1);
                l = (float)Math.sqrt(lightDir.x*lightDir.x+lightDir.y*lightDir.y+lightDir.z*lightDir.z);
                lightDir.x/=l; lightDir.y/=l; lightDir.z/=l;

                float dp = Math.abs(normal.x*lightDir.x+normal.y*lightDir.y+normal.z*lightDir.z);

                Matrix.MultiplyMatrixVector(triTranslated.A, triProjected.A, SigRenderer.matProj);
                Matrix.MultiplyMatrixVector(triTranslated.B, triProjected.B, SigRenderer.matProj);
                Matrix.MultiplyMatrixVector(triTranslated.C, triProjected.C, SigRenderer.matProj);
                triProjected.setColor(new Color(dp,dp,dp));

                triProjected.A.x+=1f;
                triProjected.A.y+=1f;
                triProjected.B.x+=1f;
                triProjected.B.y+=1f;
                triProjected.C.x+=1f;
                triProjected.C.y+=1f;
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
