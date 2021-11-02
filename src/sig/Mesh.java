package sig;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    List<Triangle> triangles = new ArrayList<>();
    Mesh(List<Triangle> tris) {
        this.triangles=tris;
    }
}
