package sig;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class SigRenderer implements KeyListener,MouseListener,MouseMotionListener{
    public static Mesh cube;
    public static int SCREEN_WIDTH=1280;
    public static int SCREEN_HEIGHT=720;
    public final static long TIMEPERTICK = 16666667l;
    public static float DRAWTIME=0;
    public static float DRAWLOOPTIME=0;
    public static final float RESOLUTION=1;

    public static Vector3f origin = new Vector3f(0,0,10);
    public static float rot = (float)Math.PI/4; //In radians.

    public static List<Pixel> pixels;

    public static float fNear = 0.1f;
    public static float fFar = 1000f;
    public static float fFov = 90f;
    public static float fAspectRatio = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
    public static float fFovRad = 1f/(float)Math.tan(fFov*0.5f/180f*Math.PI);
    public static Matrix matProj = new Matrix(
        new float[][]{
            {fAspectRatio*fFovRad,0,0,0},
            {0,fFovRad,0,0},
            {0,0,fFar/(fFar-fNear),1f},
            {0,0,(-fFar*fNear)/(fFar-fNear),0f},
        });

    public void runGameLoop() {
        rot+=Math.PI/480d;
    }

    SigRenderer(JFrame f) {
        cube = new Mesh(Arrays.asList(
            new Triangle[]{
                new Triangle(new Vector3f(),new Vector3f(0,1,0),new Vector3f(1,1,0)),
                new Triangle(new Vector3f(),new Vector3f(1,1,0),new Vector3f(1,0,0)),
                new Triangle(new Vector3f(1,0,0),new Vector3f(1,1,0),new Vector3f(1,1,1)),
                new Triangle(new Vector3f(1,0,0),new Vector3f(1,1,1),new Vector3f(1,0,1)),
                new Triangle(new Vector3f(0,1,0),new Vector3f(0,1,1),new Vector3f(1,1,0)),
                new Triangle(new Vector3f(0,1,1),new Vector3f(1,1,1),new Vector3f(1,1,0)),
                new Triangle(new Vector3f(),new Vector3f(0,0,1),new Vector3f(1,0,0)),
                new Triangle(new Vector3f(0,0,1),new Vector3f(1,0,1),new Vector3f(1,0,0)),
                new Triangle(new Vector3f(0,0,1),new Vector3f(0,1,1),new Vector3f(1,0,1)),
                new Triangle(new Vector3f(0,1,1),new Vector3f(1,1,1),new Vector3f(1,0,1)),
                new Triangle(new Vector3f(),new Vector3f(0,1,0),new Vector3f(0,0,1)),
                new Triangle(new Vector3f(0,1,0),new Vector3f(0,1,1),new Vector3f(0,0,1)),
            }));

        Panel p = new Panel();

        f.getContentPane().addMouseListener(this);
        f.getContentPane().addMouseMotionListener(this);
        f.addKeyListener(this);
        f.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        f.add(p,BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        p.init();
        

        new Thread() {
            public void run(){
                while (true) {
                    long startTime = System.nanoTime();
                    runGameLoop();
                    p.repaint();
                    Toolkit.getDefaultToolkit().sync();
                    long endTime = System.nanoTime();
                    long diff = endTime-startTime;
                    try {
                        long sleepTime = TIMEPERTICK - diff;
                        long millis = (sleepTime)/1000000;
                        int nanos = (int)(sleepTime-(((sleepTime)/1000000)*1000000));
                        //System.out.println("FRAME DRAWING: Sleeping for ("+millis+"ms,"+nanos+"ns) - "+(diff)+"ns");
                        DRAWTIME = (float)diff/1000000;
                        f.setTitle("Game Loop: "+DRAWTIME+"ms, Draw Loop: "+DRAWLOOPTIME+"ms");
                        if (sleepTime>0) {
                            Thread.sleep(millis,nanos);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public static void main(String[] args) {
        JFrame f = new JFrame("SigRenderer");
        new SigRenderer(f);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:{
                origin.add(new Vector3f(0,0,-0.05f));
            }break;
            case KeyEvent.VK_RIGHT:{
                origin.add(new Vector3f(0.05f,0,0));
            }break;
            case KeyEvent.VK_LEFT:{
                origin.add(new Vector3f(-0.05f,0,0));
            }break;
            case KeyEvent.VK_DOWN:{
                origin.add(new Vector3f(0,0,0.05f));
            }break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}
