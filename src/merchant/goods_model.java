package merchant;

public class goods_model {
    private int acc_id, good_id, good_num, good_price;
    private String characters_name;
    private long createData;

    public goods_model(int acc_id, int good_id, int good_num, int good_price, String characters_name, long createData) {
        this.acc_id = acc_id;
        this.good_id = good_id;
        this.good_num = good_num;
        this.good_price = good_price;
        this.characters_name = characters_name;
        this.createData = createData;
    }

    public int getGood_id() {
        return good_id;
    }

    public void setGood_id(int good_id) {
        this.good_id = good_id;
    }

    public int getGood_num() {
        return good_num;
    }

    public void setGood_num(int good_num) {
        this.good_num = good_num;
    }

    public boolean gainGood_num(int good_num) {
        int update_num = this.good_num + good_num;
        if (update_num < 0){
            return false;
        }
        this.good_num = update_num;
        return true;
    }

    public int getGood_price() {
        return good_price;
    }

    public void setGood_price(int good_price) {
        this.good_price = good_price;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public String getCharacters_name() {
        return characters_name;
    }

    public void setCharacters_name(String characters_name) {
        this.characters_name = characters_name;
    }

    public long getCreateData() {
        return createData;
    }

    public void setCreateData(long createData) {
        this.createData = createData;
    }
}
