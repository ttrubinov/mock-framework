package mock;

public class MyClass {
    public MyClass() {
    }

    public String aboba() {
        return "Aboba";
    }

    public String aboba(String text) {
        return "Aboba " + text;
    }

    public int inc(int x) {
        return x + 1;
    }

    public static int incc(int x) {
        return x + 1;
    }

    public boolean isOdd(int x) {
        return x % 2 == 1;
    }

    public void some(int x, int y) {
    }
}