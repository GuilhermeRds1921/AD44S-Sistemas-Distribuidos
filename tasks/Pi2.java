public class Pi {
    public static final int IT = 30000000;
    public static double pi = 0.0;

    public static void main(String[] args){
        double start, end;

        start = System.currentTimeMillis();

        Worker t1 = new Worker();
        Worker t2 = new Worker();

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pi = t1.getSum() + t2.getSum();
        pi *= 4.0;

        end = System.currentTimeMillis();

        System.out.println("The value of pi is: " + pi);
    }
}

class Worker extends Thread {
    private long start, end, it;
    private double sum;

    public Worker(long start) {
        this.start = start;
        this.end = Pi.IT / 2;
        this.it = Pi.IT / 2;
        this.sum = 0.0;
    }

        public double getSum() {
        return sum;
    }

    for(long i = 0; i < it; i++){
            if(i % 2 == 0){
                sum += (1.0 / (2.0 * i + 1.0));
            } else {
                sum -= (1.0 / (2.0 * i + 1.0));
            }
    }
}