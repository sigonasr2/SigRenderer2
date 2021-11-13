package sig;

public class BlockDefinition {
    Class<?> cl;
    BlockType type;
    BlockDefinition(Class<?> cl,BlockType type) {
        this.cl=cl;
        this.type=type;
    }
}
