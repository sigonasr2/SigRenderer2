package sig;
import javax.swing.JFrame;
import javax.vecmath.Point2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Toolkit;
import java.awt.Color;

public class SigRenderer implements KeyListener,MouseListener,MouseMotionListener{
    public static Triangle tri,tri2,tri3,tri4,tri5,tri6;
    public final static int SCREEN_WIDTH=1280;
    public final static int SCREEN_HEIGHT=720;
    public final static long TIMEPERTICK = 16666667l;
    public static float DRAWTIME=0;
    public static float DRAWLOOPTIME=0;
    public static final float RESOLUTION=1;

    public static Vector3f origin = new Vector3f(0,0,10);
    public static float rot = (float)Math.PI/4; //In radians.

    public static List<Pixel> pixels;

    public void runGameLoop() {
        rot+=Math.PI/480d;
    }

    SigRenderer(JFrame f) {

        tri = new Triangle(new Vector3f(-1,-1,0),new Vector3f(0,-1,0),new Vector3f(-1,0,0));
        tri2 = new Triangle(new Vector3f(-1,0,0),new Vector3f(0,-1,0),new Vector3f(0,0,0));
        tri3 = new Triangle(new Vector3f(0,0,0),new Vector3f(0,-1,0),new Vector3f(0,-1,-1));
        tri4 = new Triangle(new Vector3f(0,-1,-1),new Vector3f(0,0,-1),new Vector3f(0,-1,0));
        /*tri5 = new Triangle(new Vector3f(0,-1,0),new Vector3f(0,-1,-1),new Vector3f(0,0,0));
        tri6 = new Triangle(new Vector3f(0,0,0),new Vector3f(0,-1,-1),new Vector3f(0,0,-1));*/

        Panel p = new Panel();

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

        f.getContentPane().addMouseListener(this);
        f.getContentPane().addMouseMotionListener(this);
        f.addKeyListener(this);
        f.add(p);
        f.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        p.init();
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
