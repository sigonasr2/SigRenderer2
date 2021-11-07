package sig;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

public class Texture{
    int[] tex;
    int width;
    int height;
    public boolean hasTransparency=false;

    public Texture(File f) {
        try {
            BufferedImage i = ImageIO.read(f);
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            width=i.getWidth();
            height=i.getHeight();
            tex = new int[width*height];
            WritableRaster r = i.getRaster();
            for (int x=0;x<width;x++) {
                for (int y=0;y<height;y++) {
                    int[] pixel = r.getPixel(x,y,new int[3]);
                    tex[x+y*width]=pixel[2]+(pixel[1]<<8)+(pixel[0]<<16);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int SOUTH = 0;
    public static final int EAST = 2;
    public static final int NORTH = 4;
    public static final int WEST = 6;
    public static final int TOP = 8;
    public static final int BOTTOM = 10;

    public Texture(int[] tex,int width,int height) {
        this.tex=tex;
        this.width=width;
        this.height=height;
    }

    public int getColor(float u,float v,float mult) {
        int sx = (int)(u*width-1f);
        int sy = (int)(v*height-1f);
        if (sx<0||sx>=width||sy<0||sy>=height) {
            return 0;
        } else {
            int indice = (int)(u*width-1)+(int)(v*height-1)*width;
            //return tex[indice];
            return (int)((tex[indice]&0xFF)*mult) + ((int)(((tex[indice]&0xFF00)>>>8)*mult)<<8) + ((int)(((tex[indice]&0xFF0000)>>>16)*mult)<<16) + ((((tex[indice]&0xFF000000)>>>24))<<24);
            /*Color newCol = new Color(tex.getRGB(sx,sy));
            return new Color((newCol.getRed()/255f)*mult,(newCol.getGreen()/255f)*mult,(newCol.getBlue()/255f)*mult);*/
        }
    }
    
}
