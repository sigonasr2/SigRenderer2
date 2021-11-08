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
}
