package com.ivorytower.model;

public class Quote {
    private Long poemId;
    private String poemTitle;
    private Long authorId;
    private String authorName;
    private int lineNo;
    private String lineText;
    private int count;

    public Quote() {}

    public Long getPoemId() {
        return poemId;
    }

    public void setPoemId(Long poemId) {
        this.poemId = poemId;
    }

    public String getPoemTitle() {
        return poemTitle;
    }

    public void setPoemTitle(String poemTitle) {
        this.poemTitle = poemTitle;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public String getLineText() {
        return lineText;
    }

    public void setLineText(String lineText) {
        this.lineText = lineText;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}