package server.custom.auction;

public class AuctionPoint {
    private int characterid;
    private long point;
    private long point_sell;
    private long point_buy;

    public int getCharacterid() {
        return characterid;
    }

    public void setCharacterid(int characterid) {
        this.characterid = characterid;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getPoint_sell() {
        return point_sell;
    }

    public void setPoint_sell(long point_sell) {
        this.point_sell = point_sell;
    }

    public long getPoint_buy() {
        return point_buy;
    }

    public void setPoint_buy(long point_buy) {
        this.point_buy = point_buy;
    }
    
}
