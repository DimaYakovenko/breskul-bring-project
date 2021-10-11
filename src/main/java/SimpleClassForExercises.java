import java.util.function.Supplier;

public class SimpleClassForExercises {
    public static void main(String[] args) {
        Supplier<String> supplier = () -> "From nothing to success";
        System.out.println(supplier.get());
    }
}
