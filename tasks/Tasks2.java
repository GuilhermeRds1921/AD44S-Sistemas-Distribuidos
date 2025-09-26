public class Tasks2 {

    public static final int IT = 150;
    public static final long LIMIT = 1000000;

    public static void main(String[] args) {
        double start, end;

        start = System.currentTimeMillis();

        Worker t1 = new Worker(0, LIMIT / 2, IT/2);
        Worker t2 = new Worker(LIMIT / 2 + 1, LIMIT, IT/2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        end = System.currentTimeMillis();

        System.out.println("The sum is: " + (t1.getSum() + t2.getSum()));
        System.out.println("Execution time: " + (end - start) + " ms");
    }
}

class Worker extends Thread {
    private long start, end, it;
    private double sum;

    public Worker(long start, long end, long it) {
        this.start = start;
        this.end = end;
        this.it = it;
    }

    public double getSum() {
        return sum;
    }

    public void run(){
        for (int i = 0; i <= it; i++) {
            sum = 0;
            for (long j = start; j <= end; j++) {
                sum += j;
            }
        }
    }
}