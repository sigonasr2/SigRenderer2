package sig;
import javax.swing.JPanel;
import javax.vecmath.Vector3f;

import java.awt.Graphics;
import java.awt.Color;

import java.awt.Image;
import java.awt.image.MemoryImageSource;
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

        final int h=SigRenderer.SCREEN_HEIGHT;
        if(p.length != width * height) return;        
        for (int x=0;x<SigRenderer.SCREEN_WIDTH/SigRenderer.RESOLUTION;x++) {
            for (int y=0;y<SigRenderer.SCREEN_HEIGHT/SigRenderer.RESOLUTION;y++) {
                Vector3f dir = new Vector3f((-SigRenderer.SCREEN_WIDTH/2f+x*SigRenderer.RESOLUTION),(-SigRenderer.SCREEN_HEIGHT/2f+y*SigRenderer.RESOLUTION),SigRenderer.SCREEN_WIDTH);
                if (SigRenderer.tri.rayTriangleIntersect(SigRenderer.origin, dir)) {
                    p[ (int)(SigRenderer.SCREEN_WIDTH-x*SigRenderer.RESOLUTION) + (int)(SigRenderer.SCREEN_HEIGHT-y*SigRenderer.RESOLUTION) * width] = Color.WHITE.getRGB();
                }
                if (SigRenderer.tri2.rayTriangleIntersect(SigRenderer.origin, dir)) {
                    p[ (int)(SigRenderer.SCREEN_WIDTH-x*SigRenderer.RESOLUTION) + (int)(SigRenderer.SCREEN_HEIGHT-y*SigRenderer.RESOLUTION) * width] = Color.BLUE.getRGB();
                }
                if (SigRenderer.tri3.rayTriangleIntersect(SigRenderer.origin, dir)) {
                    p[ (int)(SigRenderer.SCREEN_WIDTH-x*SigRenderer.RESOLUTION) + (int)(SigRenderer.SCREEN_HEIGHT-y*SigRenderer.RESOLUTION) * width] = Color.RED.getRGB();
                }
                if (SigRenderer.tri4.rayTriangleIntersect(SigRenderer.origin, dir)) {
                    p[ (int)(SigRenderer.SCREEN_WIDTH-x*SigRenderer.RESOLUTION) + (int)(SigRenderer.SCREEN_HEIGHT-y*SigRenderer.RESOLUTION) * width] = Color.GREEN.getRGB();
                }
            }
        }   
        i += 1;
        j += 1;    
        endTime=System.nanoTime();      
        SigRenderer.DRAWLOOPTIME=(endTime-startTime)/1000000f;
    }    
    private int i=1,j=256;

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
