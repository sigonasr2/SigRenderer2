package sig;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Color;

public class Texture{
    BufferedImage tex;

    public Texture(File f) {
        try {
            this.tex = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Color getColor(float u,float v) {
        int sx = (int)(u*tex.getWidth()-1f);
        int sy = (int)(v*tex.getHeight()-1f);
        if (sx<0||sx>=tex.getWidth()||sy<0||sy>=tex.getHeight()) {
            return new Color(0,0,0,0);
        } else {
            return new Color(tex.getRGB(sx,sy));
        }
    }
    
}
