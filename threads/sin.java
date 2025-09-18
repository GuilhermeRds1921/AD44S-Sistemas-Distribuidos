public class sin {
    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.start();
        Worker worker2 = new Worker();
        worker2.start();
    }

}

class Worker extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100000000; i++) {
            System.out.println("i = " + i);
        }
    }
}
