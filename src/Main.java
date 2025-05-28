import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    static final int NUMBER_OF_ROUTES = 1000;
    static int maxKey = 0;
    static int maxValue = 0;

    public static void main(String[] args) throws InterruptedException {

        String[] routes = new String[NUMBER_OF_ROUTES];
        for (int i = 0; i < routes.length; i++) {
            routes[i] = generateRoute("RLRFR", 100);
        }
        Thread countingThread;
        Thread countMaximum;
        countingThread = new Thread(() -> {
            for (String route : routes) {

                int count = 0;
                for (int j = 0; j < route.length(); j++) {
                    String right = String.valueOf(route.charAt(j));
                    if (right.equals("R"))
                        count++;
                }
                synchronized (sizeToFreq) {
                    sizeToFreq.merge(count, 1, Integer::sum);
                    sizeToFreq.notify();
                }
            }
        });

        countMaximum = new Thread(() -> {
            synchronized (sizeToFreq) {
                while (!Thread.interrupted()) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    for (int i : sizeToFreq.keySet()) {
                        if (sizeToFreq.get(i) > maxValue) {
                            maxKey = i;
                            maxValue = sizeToFreq.get(i);
                            System.out.printf("Новый лидер %d (встретилось %d раз)\n", maxKey, maxValue);
                        }
                    }

                }
            }
        });
        countingThread.start();
        countMaximum.start();
        countingThread.join();
        countingThread.interrupt();
        countMaximum.interrupt();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}


