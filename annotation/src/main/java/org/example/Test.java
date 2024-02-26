
public class Test {
    private static int testingField = 42;

    public static void main(String[] args) {
        if (args.length > 0) {
            testingField = 33;
        }
        System.out.println("Hello world: " + testingField);
    }
}
