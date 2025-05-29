import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    static final int NUMBER_OF_ROUTES = 1000;
    static int maxKey = 0;
    static int maxValue = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread countingThread;
        Thread countMaximum;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_ROUTES; i++) {
            countingThread = new Thread(() -> {
                int count = countSymbol_R(generateRoute("RLRFR", 100));
                synchronized (sizeToFreq) {
                    sizeToFreq.merge(count, 1, Integer::sum);
                    sizeToFreq.notify();
                }
            });
            countingThread.start();
            threads.add(countingThread);
            countingThread.interrupt();
        }

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
        countMaximum.start();
        for (Thread thread : threads) {
            thread.join();
        }
        countMaximum.interrupt();
    }

    public static int countSymbol_R(String route) {
        int count = 0;
        for (int i = 0; i < route.length(); i++) {
            String right = String.valueOf(route.charAt(i));
            if (right.equals("R"))
                count++;
        }
        return count;
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


