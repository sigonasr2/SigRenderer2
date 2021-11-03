package sig;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import sig.utils.OBJReader;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Toolkit;
import java.awt.BorderLayout;

public class SigRenderer implements KeyListener,MouseListener,MouseMotionListener{

    public static boolean WIREFRAME = false;

    public static List<Block> blocks = new ArrayList<Block>();
    public static int SCREEN_WIDTH=1280;
    public static int SCREEN_HEIGHT=720;
    public final static long TIMEPERTICK = 16666667l;
    public static float DRAWTIME=0;
    public static float DRAWLOOPTIME=0;
    public static final float RESOLUTION=1;
    public static float rot = (float)Math.PI/4; //In radians.

    public static List<Pixel> pixels;

    public static float fNear = 0.1f;
    public static float fFar = 1000f;
    public static float fFov = 90f;
    public static float fAspectRatio = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
    public static Matrix matProj = Matrix.MakeProjection(fFov,fAspectRatio,fNear,fFar);

    public static Vector vCamera = new Vector();
    public static Vector vLookDir = new Vector(0,0,1);
    public static float yaw = 0;
    public static float pitch = 0;
    public static float roll = 0;

    final float MOVESPEED = 0.03f;
    final float TURNSPEED = 0.03f;

    public static Texture dirtTex;

    public static float[] depthBuffer;

    boolean upHeld=false,downHeld=false,leftHeld=false,rightHeld=false,
    aHeld=false,sHeld=false,dHeld=false,wHeld=false;

    public void runGameLoop() {
        if (upHeld) {
            pitch+=MOVESPEED;
        }
        if (downHeld) {
            pitch-=MOVESPEED;
        }
        if (rightHeld) {
            roll-=MOVESPEED;
        }
        if (leftHeld) {
            roll+=MOVESPEED;
        }
        if (wHeld||sHeld) {
            Vector forward = Vector.multiply(vLookDir,MOVESPEED);
            if (wHeld) {
                vCamera = Vector.add(vCamera,forward);
            }
            if (sHeld) {
                vCamera = Vector.subtract(vCamera,forward);
            }
        }
        if (aHeld) {
            yaw-=TURNSPEED;
        }
        if (dHeld) {
            yaw+=TURNSPEED;
        }
    }

    SigRenderer(JFrame f) {
        //cube = new Mesh(OBJReader.ReadOBJFile("teapot.obj",false));
        Mesh dirtCube = new Mesh("cube.obj","dirt.png");
        for (int x=0;x<32;x++) {
            for (int y=0;y<32;y++) {
                blocks.add(new Block(
                    new Vector(x,0,y),
                    dirtCube
                ));
            }
        }

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
                upHeld=true;
            }break;
            case KeyEvent.VK_RIGHT:{
                rightHeld=true;
            }break;
            case KeyEvent.VK_LEFT:{
                leftHeld=true;
            }break;
            case KeyEvent.VK_DOWN:{
                downHeld=true;
            }break;
            case KeyEvent.VK_W:{
                wHeld=true;
            }break;
            case KeyEvent.VK_D:{
                dHeld=true;
            }break;
            case KeyEvent.VK_A:{
                aHeld=true;
            }break;
            case KeyEvent.VK_S:{
                sHeld=true;
            }break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:{
                upHeld=false;
            }break;
            case KeyEvent.VK_RIGHT:{
                rightHeld=false;
            }break;
            case KeyEvent.VK_LEFT:{
                leftHeld=false;
            }break;
            case KeyEvent.VK_DOWN:{
                downHeld=false;
            }break;
            case KeyEvent.VK_W:{
                wHeld=false;
            }break;
            case KeyEvent.VK_D:{
                dHeld=false;
            }break;
            case KeyEvent.VK_A:{
                aHeld=false;
            }break;
            case KeyEvent.VK_S:{
                sHeld=false;
            }break;
        }
    }
}
