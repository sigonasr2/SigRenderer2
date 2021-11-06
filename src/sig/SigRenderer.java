package sig;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import sig.utils.OBJReader;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.BorderLayout;

public class SigRenderer implements KeyListener,MouseListener,MouseMotionListener{

    public static boolean WIREFRAME = false;
    public static boolean PROFILING = false;

    public static List<Triangle> triRender = new ArrayList<>();
    public static int SCREEN_WIDTH=1280;
    public static int SCREEN_HEIGHT=720;
    public final static long TIMEPERTICK = 16666667l;
    public static float DRAWTIME=0;
    public static float DRAWLOOPTIME=0;
    public static final float RESOLUTION=1;
    public static float rot = (float)Math.PI/4; //In radians.
    public static Map<String,Block> blockGrid = new HashMap<>();

    public static List<Pixel> pixels;

    public static float fNear = 0.1f;
    public static float fFar = 1000f;
    public static float fFov = 90f;
    public static float fAspectRatio = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
    public static Matrix matProj = Matrix.MakeProjection(fFov,fAspectRatio,fNear,fFar);

    public static Vector vCamera = new Vector(0.5f,2f,-15f);
    public static Vector vLookDir = new Vector(0,0,1);
    public static float yaw = (float)(-Math.PI/8);
    public static float pitch = (float)(Math.PI/8);
    public static float roll = 0;

    final float MOVESPEED = 0.2f;
    final float TURNSPEED = 0.05f;

    public static float[] depthBuffer;

    public static HashMap<TextureType,Texture> blockTextures = new HashMap<TextureType,Texture>();

    boolean upHeld=false,downHeld=false,leftHeld=false,rightHeld=false,
    aHeld=false,sHeld=false,dHeld=false,wHeld=false;

    public static MouseEvent request;
    public static Block answer;
    public static Block tempAnswer = null;

    public void runGameLoop() {
        if (upHeld) {
            pitch+=TURNSPEED;
        }
        if (downHeld) {
            pitch-=TURNSPEED;
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
        if (answer!=null) {
            System.out.println(answer);
            answer=null;
        }
    }

    public static void addBlock(Vector pos,BlockType type) {
        Block b = new Block(pos,new Cube(type));
        blockGrid.put(pos.x+"_"+pos.y+"_"+pos.z,b);
        b.updateFaces();
    }

    public static void updateRenderGrid() {
        triRender.clear();
        for (String key : blockGrid.keySet()) {
            Block b = blockGrid.get(key);
            if (!b.neighbors.UP) {triRender.add(b.block.triangles.get(8));triRender.add(b.block.triangles.get(9));}
            if (!b.neighbors.DOWN) {triRender.add(b.block.triangles.get(10));triRender.add(b.block.triangles.get(11));}
            if (!b.neighbors.LEFT) {triRender.add(b.block.triangles.get(6));triRender.add(b.block.triangles.get(7));}
            if (!b.neighbors.RIGHT) {triRender.add(b.block.triangles.get(2));triRender.add(b.block.triangles.get(3));}
            if (!b.neighbors.FORWARD) {triRender.add(b.block.triangles.get(4));triRender.add(b.block.triangles.get(5));}
            if (!b.neighbors.BACKWARD) {triRender.add(b.block.triangles.get(0));triRender.add(b.block.triangles.get(1));}
        }  
    }

    SigRenderer(JFrame f) {
        //cube = new Mesh(OBJReader.ReadOBJFile("teapot.obj",false));
        Random r = new Random(438107);
        for (int x=0;x<64;x++) {
            for (int z=0;z<64;z++) {
                if (Math.random()<=0.5) {
                    addBlock(new Vector(x,0,z),BlockType.DIRT);
                } else {
                    addBlock(new Vector(x,0,z),BlockType.SNOW_DIRT);
                }
            }
        }
        updateRenderGrid();

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

        try {

            final int BLOCK_WIDTH = 128;
            final int BLOCK_HEIGHT = 128;

            BufferedImage img = ImageIO.read(new File("textures.png"));
            WritableRaster r = img.getRaster();
            for (TextureType tt : TextureType.values()) {
                int[] pixelData = new int[tt.texWidth*BLOCK_WIDTH*tt.texHeight*BLOCK_HEIGHT];
                int startX=tt.texX*BLOCK_WIDTH;
                int startY=tt.texY*BLOCK_HEIGHT;
                for (int x=0;x<tt.texWidth*BLOCK_WIDTH;x++) {
                    for (int y=0;y<tt.texHeight*BLOCK_HEIGHT;y++) {
                        int[] pixel = r.getPixel(x+startX,y+startY,new int[4]);
                        pixelData[x+y*tt.texWidth*BLOCK_WIDTH]=pixel[2]+(pixel[1]<<8)+(pixel[0]<<16)+(pixel[3]<<24);
                    }
                }
                blockTextures.put(tt,new Texture(pixelData,tt.texWidth*BLOCK_WIDTH,tt.texHeight*BLOCK_HEIGHT));
            }

            JFrame f = new JFrame("SigRenderer");
            new SigRenderer(f);
        } catch (IOException e) {
            System.err.println("Cannot find game textures! (textures.png required)");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        request=e;
        answer=null;
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
