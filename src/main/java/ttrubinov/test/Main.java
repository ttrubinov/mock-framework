package ttrubinov.test;

public class Main {
    public static void main(String[] args) {
        var mockedMain = Mock.mock(Main.class);
//        Mock.when(mockedMain.doSth());
        System.out.println(mockedMain.doSth());
    }

    public String doSth() {
        return "Hello";
    }
}