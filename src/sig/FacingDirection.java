package sig;

public enum FacingDirection {
    SOUTH,
    WEST,
    NORTH,
    EAST;

    static FacingDirection[] orderList = new FacingDirection[]{SOUTH,WEST,NORTH,EAST};
    FacingDirection clockwise() {
        return orderList[(this.ordinal()+1)%orderList.length];
    }
    FacingDirection counterClockwise() {
        return orderList[Math.floorMod((this.ordinal()-1),orderList.length)];
    }
    boolean isOpposite(FacingDirection dir) {
        return this.ordinal()!=dir.ordinal()&&this.ordinal()%2==dir.ordinal()%2;
    }
    public static boolean isFacingEachOther(Block b1, Block b2) {
        float xDiff=b1.pos.x-b2.pos.x;
        float zDiff=b1.pos.z-b2.pos.z;
        if (Math.abs(xDiff)+Math.abs(zDiff)==1) {
            return (xDiff==-1&&b1.getFacingDirection()==WEST&&b2.getFacingDirection()==EAST)||
                (xDiff==1&&b1.getFacingDirection()==EAST&&b2.getFacingDirection()==WEST)||
                (zDiff==-1&&b1.getFacingDirection()==NORTH&&b2.getFacingDirection()==SOUTH)||
                (zDiff==1&&b1.getFacingDirection()==SOUTH&&b2.getFacingDirection()==NORTH);
        } else {
            return false;
        }
    }
    public static boolean isFacingAwayFromEachOther(Block b1, Block b2) {
        float xDiff=b1.pos.x-b2.pos.x;
        float zDiff=b1.pos.z-b2.pos.z;
        if (Math.abs(xDiff)+Math.abs(zDiff)==1) {
            return (xDiff==-1&&b1.getFacingDirection()==EAST&&b2.getFacingDirection()==WEST)||
                (xDiff==1&&b1.getFacingDirection()==WEST&&b2.getFacingDirection()==EAST)||
                (zDiff==-1&&b1.getFacingDirection()==SOUTH&&b2.getFacingDirection()==NORTH)||
                (zDiff==1&&b1.getFacingDirection()==NORTH&&b2.getFacingDirection()==SOUTH);
        } else {
            return false;
        }
    }
}
