package sig;

import java.awt.event.MouseEvent;

public class MouseHandler {
    public MouseEvent e;
    public Block b;
    public MouseHandler(MouseEvent e,Block b) {
        this.e=e;
        this.b=b;
    }
}
