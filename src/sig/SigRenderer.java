package sig;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import sig.models.Plant;
import sig.models.Staircase;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class SigRenderer implements WindowFocusListener,KeyListener,MouseListener,MouseMotionListener,MouseWheelListener{

    public static boolean windowActive=true;
    public static boolean WIREFRAME = false;
    public static boolean PROFILING = false;
    public static boolean FLYING_MODE = false;
    public static int SCREEN_WIDTH=1280;
    public static int SCREEN_HEIGHT=720;
    public final static long TIMEPERTICK = 16666667l;
    public static float DRAWTIME=0;
    public static float DRAWLOOPTIME=0;
    public static final float RESOLUTION=1;
    public static Robot myRobot;
    public static float rot = (float)Math.PI/4; //In radians.
    public static ConcurrentHashMap<String,Block> blockGrid = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,Triangle> renderMap = new ConcurrentHashMap<>();

    public static List<Pixel> pixels;

    public static float fNear = 0.1f;
    public static float fFar = 1000f;
    public static float fFov = 90f;
    public static float fAspectRatio = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
    public static Matrix matProj = Matrix.MakeProjection(fFov,fAspectRatio,fNear,fFar);

    public static Vector vCamera = new Vector(31.5f,20f,31.5f);

    public static final float cameraCollisionPadding = 0.2f;
    public static final float cameraHeight = 1.75f;
    public static final float cameraEyeHeight = 1.5f;

    public static Vector vCameraOffset = new Vector(0,cameraEyeHeight,0);
    public static Vector vCameraSpeed = new Vector(0,0,0);
    public static float vCameraFriction = 0.5f;
    public static Vector vLookDir = new Vector(0,0,1);
    public static float yaw = (float)(-Math.PI/8);
    public static float pitch = (float)(-Math.PI/6);
    public static float roll = 0;

    final static float MOVESPEED = FLYING_MODE?0.2f:0.075f;
    private static final float JUMP_HEIGHT = 0.21f;
    final static float TURNSPEED = 0.004f;
    public static float gravity = 0.01f;
    public static float fallSpd = 0;
    public static float xSpeed = 0f;
    public static float zSpeed = 0f;
    public static Staircase currentStaircase = null;
    
    public static int jumpsAvailable = 1;
    
    final public static Vector maxCameraSpeed = new Vector(MOVESPEED,1f,MOVESPEED);

    public static float[] depthBuffer;
    public static Triangle[] depthBuffer_tri;
    public static float[] depthBuffer_noTransparency;
    public static boolean[] translucencyBuffer;

    public static HashMap<TextureType,Texture> blockTextures = new HashMap<TextureType,Texture>();

    boolean upHeld=false,downHeld=false,leftHeld=false,rightHeld=false,
    aHeld=false,sHeld=false,dHeld=false,wHeld=false,qHeld=false,eHeld=false,
    spaceHeld=false;

    boolean wLast=true,aLast=true;

    public static MouseEvent request;
    public static MouseEvent temp_request;
    public static MouseHandler answer;
    public static MouseHandler tempAnswer = null;

    public static Panel panel;
    public static JFrame frame;

    public static Cursor invisibleCursor;

    public static int selectedMode=0;

    void addSpeed(Vector v) {
        //vCameraSpeed = Vector.add(vCameraSpeed,v);
        xSpeed+=v.x;
        zSpeed+=v.z;
    }

    public static boolean checkCollisionSquare(float x,float y,float z) {
        for (int yy=0;yy<cameraHeight;yy++) {
            Block b1 = blockGrid.get((float)Math.floor(vCamera.x+x+cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z+cameraCollisionPadding));
            Block b2 = blockGrid.get((float)Math.floor(vCamera.x+x-cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z+cameraCollisionPadding));
            Block b3 = blockGrid.get((float)Math.floor(vCamera.x+x+cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z-cameraCollisionPadding));
            Block b4 = blockGrid.get((float)Math.floor(vCamera.x+x-cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z-cameraCollisionPadding));
            if (b1!=null) {if (!b1.block.handleCollision(b1,x,z)) {return false;}}
            if (b2!=null) {if (!b2.block.handleCollision(b2,x,z)) {return false;}}
            if (b3!=null) {if (!b3.block.handleCollision(b3,x,z)) {return false;}}
            if (b4!=null) {if (!b4.block.handleCollision(b4,x,z)) {return false;}}
        }
        return true;
    }

    public static boolean checkRawCollision(float x,float y,float z) {
        for (int yy=0;yy<cameraHeight;yy++) {
            Block b1 = blockGrid.get((float)Math.floor(vCamera.x+x+cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z+cameraCollisionPadding));
            Block b2 = blockGrid.get((float)Math.floor(vCamera.x+x-cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z+cameraCollisionPadding));
            Block b3 = blockGrid.get((float)Math.floor(vCamera.x+x+cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z-cameraCollisionPadding));
            Block b4 = blockGrid.get((float)Math.floor(vCamera.x+x-cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y+y+yy)+"_"+(float)Math.floor(vCamera.z+z-cameraCollisionPadding));
            if (b1!=null) {return false;}
            if (b2!=null) {return false;}
            if (b3!=null) {return false;}
            if (b4!=null) {return false;}
        }
        return true;
    }

    void move() {
        Vector speed = new Vector(xSpeed,0,zSpeed);
        if (Vector.length(speed)>MOVESPEED) {
            speed = Vector.multiply(Vector.normalize(speed),MOVESPEED);
            xSpeed = speed.x;
            zSpeed = speed.z;
        }
        
        float tempY = vCameraSpeed.y;
        if (xSpeed==0&&zSpeed==0) {
            return;
        }
        vCameraSpeed.y = 0;
        vCameraSpeed = Vector.multiply(Vector.normalize(speed),Vector.length(speed));
        vCameraSpeed.y=tempY;

        if (FLYING_MODE||checkCollisionSquare(vCameraSpeed.x,0,0)) {
            vCamera.x+=vCameraSpeed.x;
        }
        /*if (FLYING_MODE||checkCollisionSquare(0,vCameraSpeed.y,0)) {
            vCamera.y+=vCameraSpeed.y;
        }*/
        if (FLYING_MODE||checkCollisionSquare(0,0,vCameraSpeed.z)) {
            vCamera.z+=vCameraSpeed.z;
        }
    }

    void friction(Vector v,float friction) {
        /*System.out.println(vCameraSpeed);
        vCameraSpeed.x=Math.signum(vCameraSpeed.x)>0?(vCameraSpeed.x-friction)<0?0:vCameraSpeed.x-friction:(vCameraSpeed.x+friction)>0?0:vCameraSpeed.x+friction;
        //vCameraSpeed.y=Math.signum(vCameraSpeed.y)>0?(vCameraSpeed.y-vCameraFriction)<0?0:vCameraSpeed.y-vCameraFriction:(vCameraSpeed.y+vCameraFriction)>0?0:vCameraSpeed.y+vCameraFriction;
        vCameraSpeed.z=Math.signum(vCameraSpeed.z)>0?(vCameraSpeed.z-friction)<0?0:vCameraSpeed.z-friction:(vCameraSpeed.z+friction)>0?0:vCameraSpeed.z+friction;*/
        float xRatio = 0f;
        float zRatio = 0f;
        if (xSpeed==0&&zSpeed==0) {
            return;
        }

        float absXSpeed = Math.abs(xSpeed);
        float absZSpeed = Math.abs(zSpeed);

        if (absXSpeed>absZSpeed) {
            zRatio = absZSpeed/absXSpeed;
            xRatio = 1 - zRatio;
        } else {
            xRatio = absXSpeed/absZSpeed;
            zRatio = 1 - xRatio;
        }
        xSpeed=Math.signum(xSpeed)>0?(xSpeed-friction*xRatio)<0?0:xSpeed-friction*xRatio:(xSpeed+friction*xRatio)>0?0:xSpeed+friction*xRatio;
        zSpeed=Math.signum(zSpeed)>0?(zSpeed-friction*zRatio)<0?0:zSpeed-friction*zRatio:(zSpeed+friction*zRatio)>0?0:zSpeed+friction*zRatio;
    }

    public void runGameLoop() {

        if (!FLYING_MODE) {
            move();
            if (SigRenderer.currentStaircase!=null) {
                boolean found=false;
                for (int yy=0;yy<cameraHeight;yy++) {
                    Block b1 = blockGrid.get((float)Math.floor(vCamera.x+cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y-0.5f+yy)+"_"+(float)Math.floor(vCamera.z+cameraCollisionPadding));
                    Block b2 = blockGrid.get((float)Math.floor(vCamera.x-cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y-0.5f+yy)+"_"+(float)Math.floor(vCamera.z+cameraCollisionPadding));
                    Block b3 = blockGrid.get((float)Math.floor(vCamera.x+cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y-0.5f+yy)+"_"+(float)Math.floor(vCamera.z-cameraCollisionPadding));
                    Block b4 = blockGrid.get((float)Math.floor(vCamera.x-cameraCollisionPadding)+"_"+(float)Math.floor(vCamera.y-0.5f+yy)+"_"+(float)Math.floor(vCamera.z-cameraCollisionPadding));
                    if (b1!=null && b1.block instanceof Staircase) {found=true;break;}
                    if (b2!=null && b2.block instanceof Staircase) {found=true;break;}
                    if (b3!=null && b3.block instanceof Staircase) {found=true;break;}
                    if (b4!=null && b4.block instanceof Staircase) {found=true;break;}
                }
                if (!found) {
                    SigRenderer.currentStaircase=null;
                }
            }
            if (checkCollisionSquare(0,fallSpd-gravity,0)&&SigRenderer.currentStaircase==null) {
                fallSpd=Math.max(-maxCameraSpeed.y,fallSpd-gravity);
                friction(vCameraSpeed,0.004f); //Air friction.
            } else {
                if (!(wHeld||sHeld||aHeld||dHeld)) {
                    friction(vCameraSpeed,MOVESPEED/4);
                }
                if (fallSpd<0) {
                    vCamera.y=(float)Math.floor(vCamera.y);
                    fallSpd=0;
                    jumpsAvailable=1;
                }
            }

            if (fallSpd!=0) {
                if (fallSpd>0) {
                    if (checkCollisionSquare(0,fallSpd+0.5f,0)) {
                        vCamera.y+=fallSpd;
                    } else {
                        fallSpd=0;
                    }
                } else {
                    vCamera.y+=fallSpd;
                }
            }

            if (spaceHeld&&jumpsAvailable==1&&fallSpd==0&&(!checkRawCollision(0,-gravity,0))) {
                jumpsAvailable=0;
                fallSpd=JUMP_HEIGHT;
                currentStaircase=null;
            }
        }

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
            Vector newDir = Vector.normalize(new Vector(vLookDir.x,0,vLookDir.z));
            Vector forward = Vector.multiply(newDir,MOVESPEED);
            if (!FLYING_MODE) {
                forward.y=0;
            }
            if (wLast&&wHeld) {
                if (FLYING_MODE) {
                    vCamera = Vector.add(vCamera,forward);
                }
                addSpeed(forward);
               // move(MOVESPEED);
            }
            if (!wLast&&sHeld) {
                if (FLYING_MODE) {
                    vCamera = Vector.subtract(vCamera,forward);
                }
                addSpeed(Vector.multiply(forward,-1));
                //move(MOVESPEED);
            }
        }
        if (aLast&&aHeld) {
            Vector leftStrafe = Vector.multiply(Matrix.MultiplyVector(Matrix.MakeRotationY((float)-Math.PI/2), vLookDir),MOVESPEED);
            leftStrafe.y=0;
            if (FLYING_MODE) {
                vCamera = Vector.add(vCamera,leftStrafe);
            }
            addSpeed(leftStrafe);
            //move(MOVESPEED);
        }
        if (!aLast&&dHeld) {
            Vector rightStrafe = Vector.multiply(Matrix.MultiplyVector(Matrix.MakeRotationY((float)Math.PI/2), vLookDir),MOVESPEED);
            rightStrafe.y=0;
            if (FLYING_MODE) {
                vCamera = Vector.add(vCamera,rightStrafe);
            }
            addSpeed(rightStrafe);
            //move(MOVESPEED);
        }
        if (answer!=null) {
            if (answer.e.getButton()==MouseEvent.BUTTON1) {
                int dirVal=0;
                if (answer.t.b.block instanceof Cube) {
                    dirVal=answer.t.dir;
                } else {
                    int[] directions = new int[]{BlockType.FRONT,BlockType.RIGHT,BlockType.BACK,BlockType.LEFT};
                    dirVal=answer.t.dir;
                    if (dirVal!=BlockType.TOP&&dirVal!=BlockType.BOTTOM) {
                        int index=-1;
                        for (int i=0;i<directions.length;i++) {
                            if (dirVal==directions[i]) {
                                index=i;
                                break;
                            }
                        }
                        dirVal=directions[(index+answer.t.b.getFacingDirection().ordinal())%4];
                    }
                }

                BlockDefinition bl = null;
                switch (selectedMode) {
                    case 1:{
                        bl = new BlockDefinition(Staircase.class,BlockType.ICE);
                    }break;
                    default:{
                        bl = new BlockDefinition(Cube.class,BlockType.DIRT);
                    }break;
                }

                switch (dirVal) {
                    case BlockType.FRONT:{
                        addBlock(Vector.add(answer.t.b.pos,new Vector(0,0,-1)),bl.cl,bl.type,FacingDirection.SOUTH);
                    }break;
                    case BlockType.BACK:{
                        addBlock(Vector.add(answer.t.b.pos,new Vector(0,0,1)),bl.cl,bl.type,FacingDirection.SOUTH);
                    }break;
                    case BlockType.LEFT:{
                        addBlock(Vector.add(answer.t.b.pos,new Vector(-1,0,0)),bl.cl,bl.type,FacingDirection.SOUTH);
                    }break;
                    case BlockType.RIGHT:{
                        addBlock(Vector.add(answer.t.b.pos,new Vector(1,0,0)),bl.cl,bl.type,FacingDirection.SOUTH);
                    }break;
                    case BlockType.TOP:{
                        addBlock(Vector.add(answer.t.b.pos,new Vector(0,1,0)),bl.cl,bl.type,FacingDirection.SOUTH);
                    }break;
                    case BlockType.BOTTOM:{
                        addBlock(Vector.add(answer.t.b.pos,new Vector(0,-1,0)),bl.cl,bl.type,FacingDirection.SOUTH);
                    }break;
                }
            } else 
            if (answer.e.getButton()==MouseEvent.BUTTON2) {
                answer.t.b.rotateClockwise();
            } else 
            if (answer.e.getButton()==MouseEvent.BUTTON3) {
                removeBlock(answer.t.b.pos);
            }
            answer=null;
        }
    }

    public static void addBlock(Vector pos,Class<?> meshType,BlockType type,FacingDirection facingDir) {
        Block b;
        try {
            b = new Block(pos,(Mesh)meshType.getConstructor(BlockType.class).newInstance(type),FacingDirection.SOUTH);
            b.setFacingDirection(facingDir);
            blockGrid.put(pos.x+"_"+pos.y+"_"+pos.z,b);
            b.updateFaces();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void removeBlock(Vector pos) {
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y+1)+"_"+pos.z)) {
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y+1)+"_"+pos.z).neighbors.DOWN=false;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y-1)+"_"+pos.z)) {
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y-1)+"_"+pos.z).neighbors.UP=false;
        }
        if (SigRenderer.blockGrid.containsKey((pos.x-1)+"_"+(pos.y)+"_"+pos.z)) {
            SigRenderer.blockGrid.get((pos.x-1)+"_"+(pos.y)+"_"+pos.z).neighbors.RIGHT=false;
        }
        if (SigRenderer.blockGrid.containsKey((pos.x+1)+"_"+(pos.y)+"_"+pos.z)) {
            SigRenderer.blockGrid.get((pos.x+1)+"_"+(pos.y)+"_"+pos.z).neighbors.LEFT=false;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z+1))) {
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z+1)).neighbors.BACKWARD=false;
        }
        if (SigRenderer.blockGrid.containsKey(pos.x+"_"+(pos.y)+"_"+(pos.z-1))) {
            SigRenderer.blockGrid.get(pos.x+"_"+(pos.y)+"_"+(pos.z-1)).neighbors.FORWARD=false;
        }
        blockGrid.remove(pos.x+"_"+pos.y+"_"+pos.z);
    }

    SigRenderer(JFrame f) {
        SigRenderer.frame=f;
        //cube = new Mesh(OBJReader.ReadOBJFile("teapot.obj",false));
        Random r = new Random(438107);
        for (int x=0;x<64;x++) {
            for (int z=0;z<64;z++) {
                addBlock(new Vector(x,0,z),Cube.class,BlockType.SOIL_WET,FacingDirection.SOUTH);
                //addBlock(new Vector(x,1,z),Staircase.class,BlockType.JUNGLE_PLANK,FacingDirection.SOUTH);
                //addBlock(new Vector(x,2,z),Staircase.class,BlockType.SPRUCE_PLANK,FacingDirection.SOUTH);
                /*for (int y=1;y<r.nextInt(5);y++) {
                    addBlock(new Vector(x,y,z),BlockType.PLANKS,FacingDirection.SOUTH);
                }*/
                /*if (r.nextInt(2)<1) {
                    switch (r.nextInt(7)) {
                        case 1:{
                            addBlock(new Vector(x,1,z),BlockType.FURNACE,FacingDirection.values()[r.nextInt(FacingDirection.values().length)]);
                        }break;
                        case 2:{
                            addBlock(new Vector(x,1,z),BlockType.PUMPKIN,FacingDirection.values()[r.nextInt(FacingDirection.values().length)]);
                        }break;
                        case 3:{
                            addBlock(new Vector(x,1,z),BlockType.CRAFTING_TABLE,FacingDirection.values()[r.nextInt(FacingDirection.values().length)]);
                        }break;
                    }
                }*/
                /*
                if (Math.random()<=0.5) {
                    addBlock(new Vector(x,y,z),BlockType.GLASS);
                } else {
                    addBlock(new Vector(x,y,z),BlockType.SNOW_DIRT);
                }*/
            }
        }
        addBlock(new Vector(31,1,31),Plant.class,BlockType.valueOf("WHEAT_"+(r.nextInt(7))),FacingDirection.SOUTH);
        /*addBlock(new Vector(31,2,32),Staircase.class,BlockType.PLANKS,FacingDirection.EAST);
        addBlock(new Vector(31,3,33),Staircase.class,BlockType.PLANKS,FacingDirection.WEST);
        addBlock(new Vector(31,4,34),Staircase.class,BlockType.PLANKS,FacingDirection.SOUTH);
        addBlock(new Vector(31,5,35),Staircase.class,BlockType.PLANKS,FacingDirection.SOUTH);*/

        for (int x=0;x<64;x++) {
            for (int y=1;y<5;y++) {
                /*
                if (x%8>2&&x%8<6&&y>1&&y<4) {
                    addBlock(new Vector(x,y,16),BlockType.GLASS);
                } else {
                    addBlock(new Vector(x,y,16),BlockType.FURNACE);
                }*/
            }
        }

        panel = new Panel();

        f.getContentPane().addMouseListener(this);
        f.getContentPane().addMouseMotionListener(this);
        f.addKeyListener(this);
        f.getContentPane().addMouseWheelListener(this);
        f.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        f.add(panel,BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        invisibleCursor = f.getToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),new Point(),null);

        panel.setCursor(invisibleCursor);
        f.setVisible(true);
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        f.setLocation((screen.getDisplayMode().getWidth()-SigRenderer.SCREEN_WIDTH)/2,(screen.getDisplayMode().getHeight()-SigRenderer.SCREEN_HEIGHT)/2);
        panel.init();

        new Thread() {
            public void run(){
                while (true) {
                    long startTime = System.nanoTime();
                    runGameLoop();
                    panel.repaint();
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

            myRobot = new Robot();

            final int BLOCK_WIDTH = 128;
            final int BLOCK_HEIGHT = 128;

            BufferedImage img = ImageIO.read(new File("textures.png"));
            WritableRaster r = img.getRaster();
            for (TextureType tt : TextureType.values()) {
                int[] pixelData = new int[tt.texWidth*BLOCK_WIDTH*tt.texHeight*BLOCK_HEIGHT];
                Texture tex = new Texture(pixelData,tt.texWidth*BLOCK_WIDTH,tt.texHeight*BLOCK_HEIGHT,tt);
                int startX=tt.texX*BLOCK_WIDTH;
                int startY=tt.texY*BLOCK_HEIGHT;
                for (int x=0;x<tt.texWidth*BLOCK_WIDTH;x++) {
                    for (int y=0;y<tt.texHeight*BLOCK_HEIGHT;y++) {
                        int[] pixel = r.getPixel(x+startX,y+startY,new int[4]);
                        pixelData[x+y*tt.texWidth*BLOCK_WIDTH]=pixel[2]+(pixel[1]<<8)+(pixel[0]<<16)+(pixel[3]<<24);
                        if (pixel[3]!=255) { 
                            if (pixel[3]>0) {
                                tex.hasTranslucency=true;
                            } else {
                                tex.hasTransparency=true;
                            }
                        }
                    }
                }
                blockTextures.put(tt,tex);
            }

            JFrame f = new JFrame("SigRenderer");
            new SigRenderer(f);
        } catch (IOException | AWTException e) {
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
        controlCamera(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        controlCamera(e);
    }

    private void controlCamera(MouseEvent e) {
        Point middle = new Point((int)(panel.getLocationOnScreen().x+panel.getWidth()/2), (int)(panel.getLocationOnScreen().y+panel.getHeight()/2));
        //System.out.println((middle.x-e.getXOnScreen())+","+(middle.y-e.getYOnScreen()));
        int diffX=Math.max(-100,Math.min(100,e.getXOnScreen()-middle.x));
        int diffY=-Math.max(-100,Math.min(100,e.getYOnScreen()-middle.y));
        yaw+=diffX*TURNSPEED*1.5;
        pitch=(float)Math.max(-Math.PI/2+0.01f,Math.min(Math.PI/2-0.01f,pitch+diffY*TURNSPEED));
        if (windowActive) {
            myRobot.mouseMove(middle.x,middle.y);
        }
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
                wLast=true;
                wHeld=true;
            }break;
            case KeyEvent.VK_D:{
                aLast=false;
                dHeld=true;
            }break;
            case KeyEvent.VK_A:{
                aLast=true;
                aHeld=true;
            }break;
            case KeyEvent.VK_S:{
                wLast=false;
                sHeld=true;
            }break;
            case KeyEvent.VK_E:{
                eHeld=true;
            }break;
            case KeyEvent.VK_Q:{
                qHeld=true;
            }break;
            case KeyEvent.VK_SPACE:{
                spaceHeld=true;
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
                wLast=false;
                wHeld=false;
            }break;
            case KeyEvent.VK_D:{
                aLast=true;
                dHeld=false;
            }break;
            case KeyEvent.VK_A:{
                aLast=false;
                aHeld=false;
            }break;
            case KeyEvent.VK_S:{
                wLast=true;
                sHeld=false;
            }break;
            case KeyEvent.VK_E:{
                eHeld=false;
            }break;
            case KeyEvent.VK_Q:{
                qHeld=false;
            }break;
            case KeyEvent.VK_SPACE:{
                spaceHeld=false;
            }break;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        selectedMode+=Math.signum(e.getWheelRotation());
        System.out.println("Mode "+selectedMode+".");
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        windowActive=true;
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        windowActive=false;
    }
}
