package sig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sig.utils.OBJReader;

public class Mesh {
    List<Triangle> triangles = new ArrayList<>();
    Mesh(List<Triangle> tris) {
        this.triangles=tris;
    }
    Mesh(String obj) {
        this.triangles=OBJReader.ReadOBJFile(obj,false);
    }
    Mesh(String obj,String tex) {
        this.triangles=OBJReader.ReadOBJFile(obj,true);
        Texture te = new Texture(new File(tex));
        for (Triangle t : triangles) {
            t.tex=te;
        }
    }
    Mesh(BlockType type) {;
    }
}
