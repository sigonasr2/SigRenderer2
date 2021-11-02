package sig.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sig.Triangle;
import sig.Vector;

public class OBJReader {
    public static List<Triangle> ReadOBJFile(String f) {
        String[] data = FileUtils.readFromFile(f);
        List<float[]> vertices = new ArrayList<>();
        List<Triangle> tris = new ArrayList<>();
        for (String s : data) {
            String[] split = s.split(Pattern.quote(" "));
            if (split[0].equalsIgnoreCase("v")) {
                vertices.add(new float[]{Float.parseFloat(split[1]),Float.parseFloat(split[2]),Float.parseFloat(split[3])});
            } else
            if (split[0].equalsIgnoreCase("f")) {
                tris.add(new Triangle(
                    new Vector(
                        vertices.get(Integer.parseInt(split[1])-1)[0],
                        vertices.get(Integer.parseInt(split[1])-1)[1],
                        vertices.get(Integer.parseInt(split[1])-1)[2]),
                    new Vector(
                        vertices.get(Integer.parseInt(split[2])-1)[0],
                        vertices.get(Integer.parseInt(split[2])-1)[1],
                        vertices.get(Integer.parseInt(split[2])-1)[2]),
                    new Vector(
                        vertices.get(Integer.parseInt(split[3])-1)[0],
                        vertices.get(Integer.parseInt(split[3])-1)[1],
                        vertices.get(Integer.parseInt(split[3])-1)[2])));
            }
        }
        return tris;
    }
}
