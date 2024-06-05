package ru.netology.graphics.image;

public class Schema implements TextColorSchema{
    @Override
    public char convert(int color) {
        char [] ch = new char [] {'#', '$', '@', '%', '*', '+', '-', '\''};
        return ch[7 - color/(255/7)];
    }
}
