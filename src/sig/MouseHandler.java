package sig;

import java.awt.event.MouseEvent;

public class MouseHandler {
    public MouseEvent e;
    public Triangle t;
    public MouseHandler(MouseEvent e,Triangle t) {
        this.e=e;
        this.t=t;
    }
}
