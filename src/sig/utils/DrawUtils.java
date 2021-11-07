package sig.utils;

import java.awt.Color;
import java.util.Arrays;
import java.awt.image.BufferedImage;

import sig.MouseHandler;
import sig.SigRenderer;
import sig.Texture;
import sig.Triangle;

public class DrawUtils {
    final public static int NORMAL_RENDERING = 0;
    final public static int IGNORE_TRANSLUCENT_RENDERING = 1;
    final public static int TRANSLUCENT_ONLY_RENDERING = 2;
    static void drawLine(int[] canvas,int sx,int ex,int ny,int col) {
        for (int i=sx;i<=ex;i++) {
            Draw(canvas,i,ny,col);
        }
    }
    public static void TexturedTriangle(int[] canvas,
    int x1, int y1, float u1,float v1,float w1,
    int x2, int y2, float u2,float v2,float w2,
    int x3, int y3, float u3,float v3,float w3,
    Texture texture, int colorMult,Triangle ref) {
        TexturedTriangle(canvas,x1,y1,u1,v1,w1,x2,y2,u2,v2,w2,x3,y3,u3,v3,w3,texture,colorMult,ref,NORMAL_RENDERING);
    }
    public static void TexturedTriangle(int[] canvas,
            int x1, int y1, float u1,float v1,float w1,
            int x2, int y2, float u2,float v2,float w2,
            int x3, int y3, float u3,float v3,float w3,
            Texture texture, int colorMult,Triangle ref,
            int rendering_state
    ) {
		if (y2<y1) {int t=y1;y1=y2;y2=t;t=x1;x1=x2;x2=t;float u=u1;u1=u2;u2=u;float v=v1;v1=v2;v2=v;float w=w1;w1=w2;w2=w;}
		if (y3<y1) {int t=y1;y1=y3;y3=t;t=x1;x1=x3;x3=t;float u=u1;u1=u3;u3=u;float v=v1;v1=v3;v3=v;float w=w1;w1=w3;w3=w;}
		if (y3<y2) {int t=y2;y2=y3;y3=t;t=x2;x2=x3;x3=t;float u=u2;u2=u3;u3=u;float v=v2;v2=v3;v3=v;float w=w2;w2=w3;w3=w;}

        int dy1=y2-y1;
        int dx1=x2-x1;
        float dv1=v2-v1;
        float du1=u2-u1;
        float dw1=w2-w1;
        int dy2=y3-y1;
        int dx2=x3-x1;
        float dv2=v3-v1;
        float du2=u3-u1;
        float dw2=w3-w1;
        float tex_u,tex_v,tex_w;

        float dax_step=0,dbx_step=0,
            du1_step=0,dv1_step=0,dw1_step=0,
            du2_step=0,dv2_step=0,dw2_step=0;

        if (dy1!=0) {dax_step=dx1/((float)Math.abs(dy1));}
        if (dy2!=0) {dbx_step=dx2/((float)Math.abs(dy2));}

        if (dy1!=0) {du1_step=du1/((float)Math.abs(dy1));}
        if (dy1!=0) {dv1_step=dv1/((float)Math.abs(dy1));}
        if (dy1!=0) {dw1_step=dw1/((float)Math.abs(dy1));}
        if (dy2!=0) {du2_step=du2/((float)Math.abs(dy2));}
        if (dy2!=0) {dv2_step=dv2/((float)Math.abs(dy2));}
        if (dy2!=0) {dw2_step=dw2/((float)Math.abs(dy2));}

        if (dy1!=0) {
            for (int i=y1;i<=y2;i++) {
                int ax=(int)(x1+((float)(i-y1))*dax_step);
                int bx=(int)(x1+((float)(i-y1))*dbx_step);

                float tex_su=u1+((float)(i-y1))*du1_step;
                float tex_sv=v1+((float)(i-y1))*dv1_step;
                float tex_sw=w1+((float)(i-y1))*dw1_step;
                float tex_eu=u1+((float)(i-y1))*du2_step;
                float tex_ev=v1+((float)(i-y1))*dv2_step;
                float tex_ew=w1+((float)(i-y1))*dw2_step;

                if (ax>bx) {
                    int t=ax;ax=bx;bx=t;
                    float u=tex_su;tex_su=tex_eu;tex_eu=u;
                    float v=tex_sv;tex_sv=tex_ev;tex_ev=v;
                    float w=tex_sw;tex_sw=tex_ew;tex_ew=w;
                }

                tex_u=tex_su;
                tex_v=tex_sv;
                tex_w=tex_sw;

                float tstep = 1.0f/((float)(bx-ax));
                float t=0.0f;

                for (int j=ax;j<bx;j++) {
                    tex_u=(1.0f-t)*tex_su+t*tex_eu;
                    tex_v=(1.0f-t)*tex_sv+t*tex_ev;
                    tex_w=(1.0f-t)*tex_sw+t*tex_ew;
                    if (SigRenderer.temp_request!=null) {
                        if (tex_w>SigRenderer.depthBuffer_noTransparency[i*SigRenderer.SCREEN_WIDTH+j]) {
                            SigRenderer.depthBuffer_noTransparency[i*SigRenderer.SCREEN_WIDTH+j]=tex_w;
                            if (j==SigRenderer.temp_request.getX()&&i==SigRenderer.temp_request.getY()) {
                                SigRenderer.tempAnswer=new MouseHandler(SigRenderer.temp_request,ref);
                            }
                        }
                    }
                    if (tex_w>SigRenderer.depthBuffer[i*SigRenderer.SCREEN_WIDTH+j]) {
                        int col = texture.getColor(tex_u/tex_w,tex_v/tex_w,colorMult/255f);
                        if (((col&0xFF000000)>>>24)!=0) {
                            if (((col&0xFF000000)>>>24)!=255) {
                                if (rendering_state==TRANSLUCENT_ONLY_RENDERING||
                                    rendering_state==NORMAL_RENDERING) {
                                        Draw(canvas,j,i,col);
                                        SigRenderer.depthBuffer[i*SigRenderer.SCREEN_WIDTH+j] = tex_w;
                                        SigRenderer.depthBuffer_tri[i*SigRenderer.SCREEN_WIDTH+j] = ref.unmodifiedTri;
                                        SigRenderer.translucencyBuffer[i*SigRenderer.SCREEN_WIDTH+j] = true;
                                }
                            } else {
                                if (rendering_state!=TRANSLUCENT_ONLY_RENDERING) {
                                    Draw(canvas,j,i,col);
                                    SigRenderer.depthBuffer[i*SigRenderer.SCREEN_WIDTH+j] = tex_w;
                                    SigRenderer.depthBuffer_tri[i*SigRenderer.SCREEN_WIDTH+j] = ref.unmodifiedTri;
                                }
                            }
                        }
                    } 
                    t+=tstep;
                }
            }
        }

        dy1=y3-y2;
        dx1=x3-x2;
        dv1=v3-v2;
        du1=u3-u2;
        dw1=w3-w2;
        if (dy1!=0) {dax_step=dx1/((float)Math.abs(dy1));}
        if (dy2!=0) {dbx_step=dx2/((float)Math.abs(dy2));}
        du1_step=0f;
        dv1_step=0f;
        if (dy1!=0) {du1_step=du1/((float)Math.abs(dy1));}
        if (dy1!=0) {dv1_step=dv1/((float)Math.abs(dy1));}
        if (dy1!=0) {dw1_step=dw1/((float)Math.abs(dy1));}

        if (dy1!=0) {
            for (int i=y2;i<=y3;i++) {
                int ax=(int)(x2+((float)(i-y2))*dax_step);
                int bx=(int)(x1+((float)(i-y1))*dbx_step);

                float tex_su=u2+((float)(i-y2))*du1_step;
                float tex_sv=v2+((float)(i-y2))*dv1_step;
                float tex_sw=w2+((float)(i-y2))*dw1_step;
                float tex_eu=u1+((float)(i-y1))*du2_step;
                float tex_ev=v1+((float)(i-y1))*dv2_step;
                float tex_ew=w1+((float)(i-y1))*dw2_step;

                if (ax>bx) {
                    int t=ax;ax=bx;bx=t;
                    float u=tex_su;tex_su=tex_eu;tex_eu=u;
                    float v=tex_sv;tex_sv=tex_ev;tex_ev=v;
                    float w=tex_sw;tex_sw=tex_ew;tex_ew=w;
                }

                tex_u=tex_su;
                tex_v=tex_sv;
                tex_w=tex_sw;

                float tstep = 1.0f/((float)(bx-ax));
                float t=0.0f;

                for (int j=ax;j<bx;j++) {
                    tex_u=(1.0f-t)*tex_su+t*tex_eu;
                    tex_v=(1.0f-t)*tex_sv+t*tex_ev;
                    tex_w=(1.0f-t)*tex_sw+t*tex_ew;
                    if (SigRenderer.temp_request!=null) {
                        if (tex_w>SigRenderer.depthBuffer_noTransparency[i*SigRenderer.SCREEN_WIDTH+j]) {
                            SigRenderer.depthBuffer_noTransparency[i*SigRenderer.SCREEN_WIDTH+j]=tex_w;
                            if (j==SigRenderer.temp_request.getX()&&i==SigRenderer.temp_request.getY()) {
                                SigRenderer.tempAnswer=new MouseHandler(SigRenderer.temp_request,ref);
                            }
                        }
                    }
                    if (tex_w>SigRenderer.depthBuffer[i*SigRenderer.SCREEN_WIDTH+j]) {
                        int col = texture.getColor(tex_u/tex_w,tex_v/tex_w,colorMult/255f);
                        if (((col&0xFF000000)>>>24)!=0) {
                            if (((col&0xFF000000)>>>24)!=255) {
                                if (rendering_state==TRANSLUCENT_ONLY_RENDERING||
                                    rendering_state==NORMAL_RENDERING) {
                                        Draw(canvas,j,i,col);
                                        SigRenderer.depthBuffer[i*SigRenderer.SCREEN_WIDTH+j] = tex_w;
                                        if (rendering_state!=TRANSLUCENT_ONLY_RENDERING) {
                                            SigRenderer.depthBuffer_tri[i*SigRenderer.SCREEN_WIDTH+j] = ref.unmodifiedTri;
                                        }
                                        SigRenderer.translucencyBuffer[i*SigRenderer.SCREEN_WIDTH+j] = true;
                                }
                            } else {
                                if (rendering_state!=TRANSLUCENT_ONLY_RENDERING) {
                                    Draw(canvas,j,i,col);
                                    SigRenderer.depthBuffer[i*SigRenderer.SCREEN_WIDTH+j] = tex_w;
                                    SigRenderer.depthBuffer_tri[i*SigRenderer.SCREEN_WIDTH+j] = ref.unmodifiedTri;
                                }
                            }
                        }
                    }
                    t+=tstep;
                }
            }
        }
    }
    public static void FillTriangle(int[] canvas,int x1, int y1, int x2, int y2, int x3, int y3, int col)
	{
		int t1x=0, t2x=0, y=0, minx=0, maxx=0, t1xp=0, t2xp=0;
		boolean changed1 = false;
		boolean changed2 = false;
		int signx1=0, signx2=0, dx1=0, dy1=0, dx2=0, dy2=0;
		int e1=0, e2=0;
		// Sort vertices
		if (y1>y2) {int t=y1;y1=y2;y2=t;t=x1;x1=x2;x2=t;}
		if (y1>y3) {int t=y1;y1=y3;y3=t;t=x1;x1=x3;x3=t;}
		if (y2>y3) {int t=y2;y2=y3;y3=t;t=x2;x2=x3;x3=t;}

		t1x = t2x = x1; y = y1;   // Starting points
		dx1 = (int)(x2 - x1); if (dx1<0) { dx1 = -dx1; signx1 = -1; }
		else signx1 = 1;
		dy1 = (int)(y2 - y1);

		dx2 = (int)(x3 - x1); if (dx2<0) { dx2 = -dx2; signx2 = -1; }
		else signx2 = 1;
		dy2 = (int)(y3 - y1);

		if (dy1 > dx1) {   // swap values
			int t=dx1;dx1=dy1;dy1=t;
			changed1 = true;
		}
		if (dy2 > dx2) {   // swap values
            int t=dy2;dy2=dx2;dx2=t;
			changed2 = true;
		}

		e2 = (int)(dx2 >> 1);
		// Flat top, just process the second half
        boolean goNext=false;
		if (y1 == y2) goNext=true;
        if (!goNext) {
            e1 = (int)(dx1 >> 1);

            for (int i = 0; i < dx1;) {
                t1xp = 0; t2xp = 0;
                if (t1x<t2x) { minx = t1x; maxx = t2x; }
                else { minx = t2x; maxx = t1x; }
                // process first line until y value is about to change
                loop3:
                while (i<dx1) {
                    i++;
                    e1 += dy1;
                    while (e1 >= dx1) {
                        e1 -= dx1;
                        if (changed1) t1xp = signx1;//t1x += signx1;
                        else break loop3;
                    }
                    if (changed1) break;
                    else t1x += signx1;
                }
                // Move line
                // process second line until y value is about to change
                loop2:
                while (true) {
                    e2 += dy2;
                    while (e2 >= dx2) {
                        e2 -= dx2;
                        if (changed2) t2xp = signx2;//t2x += signx2;
                        else break loop2;
                    }
                    if (changed2)     break;
                    else              t2x += signx2;
                }
                if (minx>t1x) minx = t1x; if (minx>t2x) minx = t2x;
                if (maxx<t1x) maxx = t1x; if (maxx<t2x) maxx = t2x;
                drawLine(canvas,minx, maxx, y,col);    // Draw line from min to max points found on the y
                                            // Now increase y
                if (!changed1) t1x += signx1;
                t1x += t1xp;
                if (!changed2) t2x += signx2;
                t2x += t2xp;
                y += 1;
                if (y == y2) break;

            }
        }
		// Second half
		dx1 = (int)(x3 - x2); if (dx1<0) { dx1 = -dx1; signx1 = -1; }
		else signx1 = 1;
		dy1 = (int)(y3 - y2);
		t1x = x2;

		if (dy1 > dx1) {   // swap values
            int t=dy1;dy1=dx1;dx1=t;
			changed1 = true;
		}
		else changed1 = false;

		e1 = (int)(dx1 >> 1);

		for (int i = 0; i <= dx1; i++) {
			t1xp = 0; t2xp = 0;
			if (t1x<t2x) { minx = t1x; maxx = t2x; }
			else { minx = t2x; maxx = t1x; }
			// process first line until y value is about to change
            loop3:
			while (i<dx1) {
				e1 += dy1;
				while (e1 >= dx1) {
					e1 -= dx1;
					if (changed1) { t1xp = signx1; break; }//t1x += signx1;
					else break loop3;
				}
				if (changed1) break;
				else   	   	  t1x += signx1;
				if (i<dx1) i++;
			}
            // process second line until y value is about to change
            loop2:
            while (t2x != x3) {
                e2 += dy2;
                while (e2 >= dx2) {
                    e2 -= dx2;
                    if (changed2) t2xp = signx2;
                    else break loop2;
                }
                if (changed2)     break;
                else              t2x += signx2;
            }
			if (minx>t1x) minx = t1x; if (minx>t2x) minx = t2x;
			if (maxx<t1x) maxx = t1x; if (maxx<t2x) maxx = t2x;
			drawLine(canvas,minx, maxx, y,col);   										
			if (!changed1) t1x += signx1;
			t1x += t1xp;
			if (!changed2) t2x += signx2;
			t2x += t2xp;
			y += 1;
			if (y>y3) return;
		}
	}
    public static void DrawTriangle(int[]canvas,int x1,int y1,int x2,int y2,int x3,int y3,int col) {
        DrawLine(canvas,x1,y1,x2,y2,col);
        DrawLine(canvas,x2,y2,x3,y3,col);
        DrawLine(canvas,x3,y3,x1,y1,col);
    }
    public static void DrawLine(int[] canvas,int x1,int y1,int x2,int y2,int col) {
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
    public static void Draw(int[] canvas,int x,int y,int col) {
        if (x>=0&&y>=0&&x<SigRenderer.SCREEN_WIDTH&&y<SigRenderer.SCREEN_HEIGHT) {
            //System.out.println(x+","+y);
            int alpha = col>>>24;
            if (alpha>0&&alpha<255) {
                float ratio = alpha/255f;
                int prev_col = canvas[x+y*SigRenderer.SCREEN_WIDTH];
                int prev_r=(prev_col&0xFF),prev_g=(prev_col&0xFF00)>>>8,prev_b=(prev_col&0xFF0000)>>>16;
                int r=(col&0xFF),g=(col&0xFF00)>>>8,b=(col&0xFF0000)>>>16;

                int new_r=(int)(ratio*r+(1-ratio)*prev_r);
                int new_g=(int)(ratio*g+(1-ratio)*prev_g);
                int new_b=(int)(ratio*b+(1-ratio)*prev_b);
                
                canvas[x+y*SigRenderer.SCREEN_WIDTH]=new_r+(new_g<<8)+(new_b<<16)+(col&0xFF000000);
            } else {
                canvas[x+y*SigRenderer.SCREEN_WIDTH]=col;
            }
        }
    }
}
