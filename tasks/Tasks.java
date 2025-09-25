public class Tasks {

    public static final int IT = 150;
    public static final long LIMIT = 100000000;

    public static void main(String[] args) {
        double sum = 0, start, end;

        start = System.currentTimeMillis();
        for (int i = 0; i <= IT; i++) {
            sum += 0;
            for (long j = 0; j <= LIMIT; j++) {
                sum += j;
            }
        }
        end = System.currentTimeMillis();

        System.out.println("The sum is: " + sum);
        System.out.println("Execution time: " + (end - start) + " ms");
    }
}