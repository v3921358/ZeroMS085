package server.custom.auction1;

public enum AuctionState1 {
    下架(0), 上架(1), 已售(2);
    private final int state;

    private AuctionState1(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public static AuctionState1 getState(int state) {
        for (AuctionState1 as : values()) {
            if (as.state == state) {
                return as;
            }
        }
        return null;
    }

}
