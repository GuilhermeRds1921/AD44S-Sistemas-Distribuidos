public class run {
    public static void main(String[] args) {
        Thread worker = new Thread(new Worker());
        worker.start();
    }
}

class Worker implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 100000000; i++) {
            System.out.println("i = " + i);
        }
    }
}
