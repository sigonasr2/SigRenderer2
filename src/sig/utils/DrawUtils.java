package sig.utils;

import java.awt.Color;

import sig.SigRenderer;

public class DrawUtils {
    public static void DrawTriangle(int[]canvas,int x1,int y1,int x2,int y2,int x3,int y3,Color col) {
        DrawLine(canvas,x1,y1,x2,y2,col);
        DrawLine(canvas,x2,y2,x3,y3,col);
        DrawLine(canvas,x3,y3,x1,y1,col);
    }
    public static void DrawLine(int[] canvas,int x1,int y1,int x2,int y2,Color col) {
        int x,y,dx,dy,dx1,dy1,px,py,xe,ye,i;
        dx=x2-x1;dy=y2-y1;
        dx1=Math.abs(dx);dy1=Math.abs(dy);
        px=2*dy1-dx1;py=2*dx1-dy1;
        if (dy1<=dx1) {
            if (dx>=0) {
                x=x1;y=y1;xe=x2;
            } else {
                x=x2;y=y2;xe=x1;
            }
            Draw(canvas,x,y,col);
            for (i=0;x<xe;i++) {
                x=x+1;
                if (px<0) {
                    px=px+2*dy1;
                } else {
                    if ((dx<0&&dy<0)||(dx>0&&dy>0)) {
                        y=y+1;
                    } else {
                        y=y-1;
                    }
                    px=px+2*(dy1-dx1);
                }
                Draw(canvas,x,y,col);
            }
        } else {
            if (dy>=0) {
                x=x1;y=y1;ye=y2;
            } else {
                x=x2;y=y2;ye=y1;
            }
            Draw(canvas,x,y,col);
            for (i=0;y<ye;i++) {
                y=y+1;
                if (py<=0) {
                    py=py+2*dx1;
                } else {
                    if ((dx<0&&dy<0)||(dx>0&&dy>0)) {
                        x=x+1;
                    } else {
                        x=x-1;
                    }
                    py=py+2*(dx1-dy1);
                }
                Draw(canvas,x,y,col);
            }
        }
    }
    public static void Draw(int[] canvas,int x,int y,Color col) {
        if (x>=0&&y>=0&&x<SigRenderer.SCREEN_WIDTH&&y<SigRenderer.SCREEN_HEIGHT) {
            //System.out.println(x+","+y);
            canvas[x+y*SigRenderer.SCREEN_WIDTH]=col.getRGB();
        }
    }
}
