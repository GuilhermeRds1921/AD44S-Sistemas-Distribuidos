public class Pi {
    public static void main(String[] args){
        long IT = 30000000;
        double pi = 0.0;
        
        for(long i = 0; i < IT; i++){
            if(i % 2 == 0){
                pi += (1.0 / (2.0 * i + 1.0));
            } else {
                pi -= (1.0 / (2.0 * i + 1.0));
            }
        }
        pi *= 4.0;
        System.out.println("The value of pi is: " + pi);
    }
}