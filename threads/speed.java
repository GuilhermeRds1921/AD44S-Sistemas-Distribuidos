public class speed {
    public static void main(String[] args) {
        int ini = 11;
        int fim = 21;
        
        Worker worker1 = new Worker(0, ini);
        Worker worker2 = new Worker(ini, fim);

        worker1.start();
        worker2.start();
    }
}

class Worker extends Thread {
    private int start;
    private int end;

    public Worker(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            System.out.println("i = " + i);
        }
    }
}