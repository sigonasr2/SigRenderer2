package sig.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sig.Triangle;
import sig.Vector;
import sig.Vector2;

public class OBJReader {
    public static List<Triangle> ReadOBJFile(String f,boolean textured) {
        String[] data = FileUtils.readFromFile(f);
        List<float[]> vertices = new ArrayList<>();
        List<float[]> texs = new ArrayList<>();
        List<Triangle> tris = new ArrayList<>();
        for (String s : data) {
            String[] split = s.split(Pattern.quote(" "));
            if (split[0].equalsIgnoreCase("v")) {
                vertices.add(new float[]{Float.parseFloat(split[1]),Float.parseFloat(split[2]),Float.parseFloat(split[3])});
            } else
            if (split[0].equalsIgnoreCase("vt")) {
                float u=Math.min(1,Math.max(0,Float.parseFloat(split[1])));
                float v=Math.min(1,Math.max(0,Float.parseFloat(split[2])));
                texs.add(new float[]{u,v});
            } else
            if (split[0].equalsIgnoreCase("f")) {
                if (textured) {
                    String[] spl1=split[1].split(Pattern.quote("/"));
                    String[] spl2=split[2].split(Pattern.quote("/"));
                    String[] spl3=split[3].split(Pattern.quote("/"));
                    Triangle tri = new Triangle(
                        new Vector(
                            vertices.get(Integer.parseInt(spl1[0])-1)[0],
                            vertices.get(Integer.parseInt(spl1[0])-1)[1],
                            vertices.get(Integer.parseInt(spl1[0])-1)[2]),
                        new Vector(
                            vertices.get(Integer.parseInt(spl2[0])-1)[0],
                            vertices.get(Integer.parseInt(spl2[0])-1)[1],
                            vertices.get(Integer.parseInt(spl2[0])-1)[2]),
                        new Vector(
                            vertices.get(Integer.parseInt(spl3[0])-1)[0],
                            vertices.get(Integer.parseInt(spl3[0])-1)[1],
                            vertices.get(Integer.parseInt(spl3[0])-1)[2]),
                        new Vector2(
                            texs.get(Integer.parseInt(spl1[1])-1)[0],
                            texs.get(Integer.parseInt(spl1[1])-1)[1]),
                        new Vector2(
                            texs.get(Integer.parseInt(spl2[1])-1)[0],
                            texs.get(Integer.parseInt(spl2[1])-1)[1]),
                        new Vector2(
                            texs.get(Integer.parseInt(spl3[1])-1)[0],
                            texs.get(Integer.parseInt(spl3[1])-1)[1]));
                    tris.add(tri);
                } else {
                    String[] spl1=split[1].split(Pattern.quote("/"));
                    String[] spl2=split[2].split(Pattern.quote("/"));
                    String[] spl3=split[3].split(Pattern.quote("/"));
                    Triangle tri = new Triangle(
                        new Vector(
                            vertices.get(Integer.parseInt(spl1[0])-1)[0],
                            vertices.get(Integer.parseInt(spl1[0])-1)[1],
                            vertices.get(Integer.parseInt(spl1[0])-1)[2]),
                        new Vector(
                            vertices.get(Integer.parseInt(spl2[0])-1)[0],
                            vertices.get(Integer.parseInt(spl2[0])-1)[1],
                            vertices.get(Integer.parseInt(spl2[0])-1)[2]),
                        new Vector(
                            vertices.get(Integer.parseInt(spl3[0])-1)[0],
                            vertices.get(Integer.parseInt(spl3[0])-1)[1],
                            vertices.get(Integer.parseInt(spl3[0])-1)[2]));
                    tris.add(tri);
                }
            }
        }
        return tris;
    }
}
