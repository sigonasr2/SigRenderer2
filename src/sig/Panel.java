package sig;
import javax.swing.JPanel;

import sig.utils.DrawUtils;

import java.awt.Graphics;
import java.awt.Color;

import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.image.ColorModel;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.util.Queue;

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
    Thread[] workerThread = new Thread[9];
    String[][] keySets = new String[9][];
    ConcurrentLinkedQueue<Triangle> newTris = new ConcurrentLinkedQueue<>();
    List<Triangle> accumulatedTris = new ArrayList<Triangle>();
    public static ConcurrentLinkedQueue<Triangle> accumulatedTris1 = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Triangle> accumulatedTris2 = new ConcurrentLinkedQueue<>();
    boolean renderFirst=false;
    boolean lastRender=renderFirst;
    boolean renderNeeded=false;
    long profileStartTime;

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
        SigRenderer.depthBuffer_tri = new Triangle[width*height];
        SigRenderer.translucencyBuffer = new boolean[width*height];
        SigRenderer.depthBuffer_noTransparency = new float[width*height];
    }
    /**
    * Do your draws in here !!
    * pixel is your canvas!
    */
    public /* abstract */ void render(){
        int[] p = pixel; // this avoid crash when resizing
        //a=h/w

        if (SigRenderer.PROFILING) {
            profileStartTime = System.currentTimeMillis();
        }

        final int h=SigRenderer.SCREEN_HEIGHT;
        if(p.length != width * height) return;        
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                p[ x + y * width] = 0;
                SigRenderer.depthBuffer[x+y*width]=0;
                SigRenderer.depthBuffer_tri[x+y*width]=null;
                SigRenderer.translucencyBuffer[x+y*width]=false;
                SigRenderer.depthBuffer_noTransparency[x+y*width]=0;
            }
        }   

        //accumulatedTris2.clear();

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
        vTarget = Vector.add(Vector.add(SigRenderer.vCamera,SigRenderer.vCameraOffset),SigRenderer.vLookDir);

        Matrix matCamera = Matrix.PointAt(Vector.add(SigRenderer.vCamera,SigRenderer.vCameraOffset), vTarget, vUp);
        Matrix matView = Matrix.QuickInverse(matCamera);

        
        int pieces = SigRenderer.blockGrid.size()/8;
        int currentPiece = 0;
        int currentSet=0;
        for (String key : SigRenderer.blockGrid.keySet()) {
            if (currentPiece==0) {
                keySets[currentSet] = new String[pieces];
            }
            keySets[currentSet][currentPiece]=key;
            currentPiece++;
            if (currentPiece>=pieces) {
                currentPiece=0;
                currentSet++;
            }
        }

        boolean threadsDone=false;
        for (int i=0;i<workerThread.length;i++) {
            if (workerThread[i]!=null&&workerThread[i].isAlive()) {
                threadsDone=true;
                break;
            }
        }
        if (!threadsDone) {
            renderFirst=!renderFirst;
            if (renderFirst) {
                accumulatedTris2.clear();
            } else {
                accumulatedTris1.clear();
            }
            final Matrix matWorld2 = matWorld;
            for (int i=0;i<9;i++) {
                final int id=i;
                workerThread[i] = new Thread(){
                    @Override
                    public void run() {
                        ConcurrentLinkedQueue<Triangle> newTris = new ConcurrentLinkedQueue<>();
                        if (keySets[id]!=null) {
                            for (String key : keySets[id]) {
                                if (key!=null) {
                                    Block b = SigRenderer.blockGrid.get(key);
                                    if (b!=null) {
                                        for (Triangle t : b.block.prepareRender(b)) {
                                            prepareTriForRender(matWorld2, matView, t, newTris);
                                        }
                                    }
                                }
                            }
                        }
                        if (renderFirst) {
                            accumulatedTris2.addAll(newTris);
                        } else {
                            accumulatedTris1.addAll(newTris);
                        }
                    }
                };
                workerThread[i].start();
            }
        }

/*
        if (workerThread==null||(!workerThread.isAlive()&&renderNeeded)) {
            renderFirst=!renderFirst;
            final Matrix matWorld2 = matWorld;
            workerThread = new Thread() {
                @Override
                public void run() {
                    ConcurrentLinkedQueue<Triangle> newTris = new ConcurrentLinkedQueue<>();
                    for (String key : SigRenderer.blockGrid.keySet()) {
                        Block b = SigRenderer.blockGrid.get(key);
                        for (Triangle t : b.block.prepareRender(b)) {
                            prepareTriForRender(matWorld2, matView, t, newTris);
                        }
                    }
                    if (renderFirst) {
                        accumulatedTris2.clear();
                        accumulatedTris2.addAll(newTris);
                    } else {
                        accumulatedTris1.clear();
                        accumulatedTris1.addAll(newTris);
                    }
                    renderNeeded=false;
                }
            };
            workerThread.start();
        }
        if (SigRenderer.PROFILING) {
            System.out.println((System.currentTimeMillis()-profileStartTime)+"ms");profileStartTime=System.currentTimeMillis();
        }
        /*Collections.sort(accumulatedTris, new Comparator<Triangle>() {
            @Override
            public int compare(Triangle t1, Triangle t2) {
                float z1=(t1.A.z+t1.B.z+t1.C.z)/3f;
                float z2=(t2.A.z+t2.B.z+t2.C.z)/3f;
                return (z1<z2)?1:(z1==z2)?0:-1;
            }
        });*/
        SigRenderer.tempAnswer=null;
        long totalTime=0;
        long startTime2=0;
        Collection<Triangle> currentRender = (renderFirst)?accumulatedTris1:accumulatedTris2;
        List<Triangle> newTris = new ArrayList<>();
        for (Triangle t:currentRender) {
            prepareTriForRender(matWorld, matView, t.unmodifiedTri, newTris, true);
        }
        currentRender=newTris;
        Vector newCamera = Vector.add(SigRenderer.vCamera,SigRenderer.vCameraOffset);
        for (Triangle t : currentRender) {
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
            
            if (SigRenderer.PROFILING) {
                startTime2 = System.nanoTime();
            }
            for (Triangle tt : triList) {
                if (tt.tex!=null) {
                    if (renderFirst) {
                        tt.unmodifiedTri.nextRenderTime=System.currentTimeMillis()+50;
                    } else {
                        tt.unmodifiedTri.nextRenderTime2=System.currentTimeMillis()+50;
                    }
                    SigRenderer.temp_request=SigRenderer.request;
                    RenderTriangle(p,tt,DrawUtils.NORMAL_RENDERING,newCamera);
                } else {
                    DrawUtils.FillTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,tt.getColor());
                }
                if (SigRenderer.WIREFRAME) {
                    DrawUtils.DrawTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,Color.WHITE.getRGB());
                }
            }
            if (SigRenderer.PROFILING) {
                totalTime+=System.nanoTime()-startTime2;
            }
        }
        renderNeeded=true;
        boolean translucencyDetected=false;
        for (int x=0;x<SigRenderer.SCREEN_WIDTH;x++) {
            for (int y=0;y<SigRenderer.SCREEN_HEIGHT;y++) {
                if (SigRenderer.translucencyBuffer[x+y*SigRenderer.SCREEN_WIDTH]) {
                    translucencyDetected=true;
                    SigRenderer.depthBuffer[x+y*SigRenderer.SCREEN_WIDTH]=0;
                    //SigRenderer.depthBuffer_tri[x+y*SigRenderer.SCREEN_WIDTH]=null;
                }
                Triangle t = SigRenderer.depthBuffer_tri[x+y*SigRenderer.SCREEN_WIDTH];
                if (t!=null) {
                    t.nextRenderTime=t.nextRenderTime2=-1;
                }
            }
        }
        if (translucencyDetected) {
            Collections.sort((ArrayList<Triangle>)currentRender, new Comparator<Triangle>() {
                @Override
                public int compare(Triangle t1, Triangle t2) {
                    float z1=(t1.A.z+t1.B.z+t1.C.z)/3f;
                    float z2=(t2.A.z+t2.B.z+t2.C.z)/3f;
                    return (z1<z2)?1:(z1==z2)?0:-1;
                }
            });
            for (Triangle t : currentRender) {
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
                
                if (SigRenderer.PROFILING) {
                    startTime2 = System.nanoTime();
                }
                for (Triangle tt : triList) {
                    if (tt.tex!=null) {
                        RenderTriangle(p,tt,DrawUtils.IGNORE_TRANSLUCENT_RENDERING,newCamera);
                    } else {
                        DrawUtils.FillTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,tt.getColor());
                    }
                    if (SigRenderer.WIREFRAME) {
                        DrawUtils.DrawTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,Color.WHITE.getRGB());
                    }
                }
                if (SigRenderer.PROFILING) {
                    totalTime+=System.nanoTime()-startTime2;
                }
            }
            for (Triangle t : currentRender) {
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
                
                if (SigRenderer.PROFILING) {
                    startTime2 = System.nanoTime();
                }
                for (Triangle tt : triList) {
                    if (tt.tex!=null) {
                        tt.unmodifiedTri.nextRenderTime=tt.unmodifiedTri.nextRenderTime2=-1;
                        RenderTriangle(p,tt,DrawUtils.TRANSLUCENT_ONLY_RENDERING,newCamera);
                    } else {
                        DrawUtils.FillTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,tt.getColor());
                    }
                    if (SigRenderer.WIREFRAME) {
                        DrawUtils.DrawTriangle(p,(int)tt.A.x,(int)tt.A.y,(int)tt.B.x,(int)tt.B.y,(int)tt.C.x,(int)tt.C.y,Color.WHITE.getRGB());
                    }
                }
                if (SigRenderer.PROFILING) {
                    totalTime+=System.nanoTime()-startTime2;
                }
            }
            
            for (int x=0;x<SigRenderer.SCREEN_WIDTH;x++) {
                for (int y=0;y<SigRenderer.SCREEN_HEIGHT;y++) {
                    Triangle t = SigRenderer.depthBuffer_tri[x+y*SigRenderer.SCREEN_WIDTH];
                    if (t!=null) {
                        t.nextRenderTime=t.nextRenderTime2=-1;
                    }
                }
            }
        }

        SigRenderer.request=null;
        SigRenderer.answer=SigRenderer.tempAnswer;
        if (SigRenderer.PROFILING) {
            System.out.println((totalTime/1000000f)+"ms/"+(System.currentTimeMillis()-profileStartTime)+"ms");
        }
    }
    
    private void prepareTriForRender(Matrix matWorld, Matrix matView, Triangle t, Collection<Triangle> accumulatedTris) {
        prepareTriForRender(matWorld,matView,t,accumulatedTris,false);
    }

    private void prepareTriForRender(Matrix matWorld, Matrix matView, Triangle t, Collection<Triangle> accumulatedTris, boolean buffer) {
        if (!buffer||(buffer&&t.nextRenderTime<=System.currentTimeMillis())) {
            Triangle triProjected = new Triangle(),triPreTransform=new Triangle(),triTransformed=new Triangle(),triViewed=new Triangle(),triRotation = new Triangle();

            matWorld = Matrix.MakeTranslation(-0.5f,0,-0.5f);

            triPreTransform.A = Matrix.MultiplyVector(matWorld,t.A);
            triPreTransform.B = Matrix.MultiplyVector(matWorld,t.B);
            triPreTransform.C = Matrix.MultiplyVector(matWorld,t.C);
            t.copyExtraDataTo(triPreTransform);
            triPreTransform.unmodifiedTri=t;

            if (t.b!=null) {
                if (!(t.b.block instanceof Cube)) {
                    matWorld = Matrix.MakeRotationY((float)(t.b.getFacingDirection().ordinal()*(Math.PI/2)));
                
                    triTransformed.A = Matrix.MultiplyVector(matWorld,triPreTransform.A);
                    triTransformed.B = Matrix.MultiplyVector(matWorld,triPreTransform.B);
                    triTransformed.C = Matrix.MultiplyVector(matWorld,triPreTransform.C);
                    triPreTransform.copyExtraDataTo(triTransformed);
                } else {
                    triTransformed=triPreTransform;
                }
            }

            if (t.b!=null) {
                matWorld = Matrix.MakeTranslation(t.b.pos.x+0.5f,t.b.pos.y,t.b.pos.z+0.5f);
            }
            
            triRotation.A = Matrix.MultiplyVector(matWorld,triTransformed.A);
            triRotation.B = Matrix.MultiplyVector(matWorld,triTransformed.B);
            triRotation.C = Matrix.MultiplyVector(matWorld,triTransformed.C);
            triTransformed.copyExtraDataTo(triRotation);

            Vector normal=new Vector(),line1=new Vector(),line2=new Vector();
            line1 = Vector.subtract(triRotation.B,triRotation.A);
            line2 = Vector.subtract(triRotation.C,triRotation.A);

            normal = Vector.crossProduct(line1,line2);
            normal = Vector.normalize(normal);

            Vector center = Vector.divide(Vector.add(triRotation.A,Vector.add(triRotation.B,triRotation.C)),3);

            Vector newCamera = Vector.add(SigRenderer.vCamera,SigRenderer.vCameraOffset);
            
            Vector cameraRay = Vector.subtract(center,newCamera);

            float distSquared = ((triRotation.b.pos.x-newCamera.x)*(triRotation.b.pos.x-newCamera.x)+
            (triRotation.b.pos.y-newCamera.y)*(triRotation.b.pos.y-newCamera.y)+
            (triRotation.b.pos.z-newCamera.z)*(triRotation.b.pos.z-newCamera.z));

            if (Vector.dotProduct(normal,cameraRay)<0&&Vector.dotProduct(cameraRay,SigRenderer.vLookDir)>-0.2f&&distSquared<4096) {
                Vector lightDir = Vector.multiply(SigRenderer.vLookDir, -1);
                lightDir = Vector.normalize(lightDir);

                //System.out.println(-Vector.dotProduct(normal,Vector.normalize(cameraRay)));
                float dp = 0.1f;
                if (t.b!=null) {
                    dp = Math.max(0.1f,Math.min(1,(1f/((triRotation.b.pos.x-newCamera.x)*(triRotation.b.pos.x-newCamera.x)+
                    (triRotation.b.pos.y-newCamera.y)*(triRotation.b.pos.y-newCamera.y)+
                    (triRotation.b.pos.z-newCamera.z)*(triRotation.b.pos.z-newCamera.z))*64)))*0.5f+Math.max(0.1f,Math.min(1,1-Vector.dotProduct(normal,SigRenderer.vLookDir)))*0.5f;
                } else {
                    dp = Math.max(0.1f,Vector.dotProduct(lightDir,normal));
                }
                /*Vector center = Vector.divide(Vector.add(triTransformed.A,Vector.add(triTransformed.B,triTransformed.C)),3);
                Vector cameraRay2 = Vector.subtract(center,newCamera);
                float dp = Math.max(0.1f,Math.min(1,(1f/((cameraRay2.x-center.x)*(cameraRay2.x-center.x)+
                (cameraRay2.y-center.y)*(cameraRay2.y-center.y)+
                (cameraRay2.z-center.z)*(cameraRay2.z-center.z))*4)));*/
                /*float dp = Math.max(0.1f,Math.min(1,(1f/((triTransformed.b.pos.x-newCamera.x)*(triTransformed.b.pos.x-newCamera.x)+
                (triTransformed.b.pos.y-newCamera.y)*(triTransformed.b.pos.y-newCamera.y)+
                (triTransformed.b.pos.z-newCamera.z)*(triTransformed.b.pos.z-newCamera.z))*4)));*/

                triViewed.A = Matrix.MultiplyVector(matView,triRotation.A);
                triViewed.B = Matrix.MultiplyVector(matView,triRotation.B);
                triViewed.C = Matrix.MultiplyVector(matView,triRotation.C);
                triRotation.copyExtraDataTo(triViewed);
                triViewed.setColor((0)+(0<<8)+((int)(dp*255)<<16));

                int clippedTriangles = 0;
                Triangle[] clipped = new Triangle[]{new Triangle(),new Triangle()};

                clippedTriangles = Triangle.ClipAgainstPlane(new Vector(0,0,0.01f),new Vector(0,0,1), triViewed, clipped);
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
                    triProjected.b=clipped[i].b;
                    triProjected.unmodifiedTri=clipped[i].unmodifiedTri;
                    triProjected.dir=clipped[i].dir;

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
            } else {
                t.nextRenderTime=System.currentTimeMillis()+50;
            }
        }
    }    

    public void RenderTriangle(int[] pixels,Triangle tt,int renderMode,Vector newCamera) {
        float distSquared = ((tt.b.pos.x-newCamera.x)*(tt.b.pos.x-newCamera.x)+
        (tt.b.pos.y-newCamera.y)*(tt.b.pos.y-newCamera.y)+
        (tt.b.pos.z-newCamera.z)*(tt.b.pos.z-newCamera.z));
        int finalResolution = SigRenderer.MAPLV2_RESOLUTION;
        if (distSquared<SigRenderer.MAPLV1_DISTANCE) {
            finalResolution=SigRenderer.RESOLUTION;
        } else
        if (distSquared<SigRenderer.MAPLV2_DISTANCE) {
            finalResolution=SigRenderer.MAPLV1_RESOLUTION;
        }
        DrawUtils.TexturedTriangle(pixels, 
            (int)tt.A.x,(int)tt.A.y,tt.T.u,tt.T.v,tt.T.w,
            (int)tt.B.x,(int)tt.B.y,tt.U.u,tt.U.v,tt.U.w,
            (int)tt.C.x,(int)tt.C.y,tt.V.u,tt.V.v,tt.V.w,
        tt.tex,(tt.col&0xFF0000)>>16,tt,renderMode,finalResolution);
    }

    public void repaint() {
        super.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        startTime = System.nanoTime();
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