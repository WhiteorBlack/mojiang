package cn.idcby.jiajubang.Bean;

public class PublishClass {
    private String name;
    private int resId;
    private int pos;

    public PublishClass(String name, int resId,int pos) {
        this.name = name;
        this.resId = resId;
        this.pos=pos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
