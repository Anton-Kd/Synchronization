import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    static final int NUMBER_OF_ROUTES = 1000;
    static int processedNumber;

    public static void main(String[] args) throws InterruptedException {

        String[] routes = new String[NUMBER_OF_ROUTES];
        for (int i = 0; i < routes.length; i++) {
            routes[i] = generateRoute("RLRFR", 100);
        }

        for (String string : routes) {
            new Thread(() -> {
                int count = 0;
                for (int j = 0; j < string.length(); j++) {
                    String right = String.valueOf(string.charAt(j));
                    if (right.equals("R"))
                        count++;
                }

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(count, 1, Integer::sum);
                    processedNumber++;
                    sizeToFreq.notify();
                }
            }).start();
        }

        new Thread(() -> {
            synchronized (sizeToFreq) {

                while (processedNumber < NUMBER_OF_ROUTES) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                int maxKey = 0;
                int maxValue = 0;

                for (int i : sizeToFreq.keySet()) {
                    if (sizeToFreq.get(i) > maxValue) {
                        maxKey = i;
                        maxValue = sizeToFreq.get(i);
                    }
                }

                System.out.printf("Самое частое колличество повторений %d (встретилось %d раз)\n", maxKey, maxValue);
                sizeToFreq.remove(maxKey);
                System.out.println("Другие размеры:");
                for (int keys : sizeToFreq.keySet()) {
                    System.out.printf("- %d (%d раз) \n", keys, sizeToFreq.get(keys));
                }
            }
        }).start();
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


