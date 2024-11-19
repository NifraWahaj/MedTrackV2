package com.example.medtrack;


public class FormattedText {
    private String text;
    private String link;       // For clickable links

    private String imageBase64;  // Store image as Base64 string
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;
    private boolean isStrikethrough;
    private float relativeSize;
    private int headingLevel;
    private int alignment;




    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean isBold) {
        this.isBold = isBold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean isItalic) {
        this.isItalic = isItalic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public boolean isStrikethrough() {
        return isStrikethrough;
    }

    public void setStrikethrough(boolean isStrikethrough) {
        this.isStrikethrough = isStrikethrough;
    }

    public float getRelativeSize() {
        return relativeSize;
    }

    public void setRelativeSize(float relativeSize) {
        this.relativeSize = relativeSize;
    }

    public int getHeadingLevel() {
        return headingLevel;
    }

    public void setHeadingLevel(int headingLevel) {
        this.headingLevel = headingLevel;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
}

