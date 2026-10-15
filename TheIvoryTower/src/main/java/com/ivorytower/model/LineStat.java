package com.ivorytower.model;

public class LineStat {
    private int lineNo;
    private int count;
    private boolean mine;

    public LineStat() {}

    public LineStat(int lineNo, int count, boolean mine) {
        this.lineNo = lineNo;
        this.count = count;
        this.mine = mine;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
}