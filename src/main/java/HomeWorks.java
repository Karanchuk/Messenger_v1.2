import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeWorks {
    public static void main(String[] args) {
        //hw4task1();
        System.out.println(Arrays.toString(hw6task2(new int[] { 1, 4, 4, 2, 3, 4, 1, 7})));
        System.out.println(hw6task3(new int[] {4,4}));
    }

    public static void hw4task1() {
        /**
         * 1. Создать три потока, каждый из которых выводит
         * определенную букву (A, B и C) 5 раз (порядок – ABСABСABС).
         * Используйте wait/notify/notifyAll.
         */
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        final Object mon = new Object();
        final char[] currentLetter = {'A'};

        char[] letters = {'A', 'B', 'C'};

        for (char letter : letters) {
            executorService.execute(() -> {
                synchronized (mon) {
                    try {
                        for (int i = 0; i < 5; i++) {
                            while (currentLetter[0] != letter) {
                                mon.wait();
                            }
                            System.out.println(letter);
                            int index = Arrays.binarySearch(letters, letter);
                            currentLetter[0] = letters[(index == (letters.length - 1) ? 0 : (index + 1))];
                                    mon.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static int[] hw6task2(int[] array) {

        /**
         * 2. Написать метод, которому в качестве аргумента
         * передается не пустой одномерный целочисленный массив.
         * Метод должен вернуть новый массив, который получен путем
         * вытаскивания из исходного массива элементов, идущих после
         * последней четверки. Входной массив должен содержать хотя бы
         * одну четверку, иначе в методе необходимо выбросить RuntimeException.
         * Написать набор тестов для этого метода (по 3-4 варианта входных данных).
         * Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
         */
        Integer lastFour = null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 4) {
                lastFour = i;
            }
        }
        if (lastFour == null) {
            throw new RuntimeException("There are no 4 here");
        }

        return Arrays.stream(array).skip((lastFour + 1)).toArray();
    }

    public static boolean hw6task3(int[] array) {

        /**
         * 3. Написать метод, который проверяет состав
         * массива из чисел 1 и 4. Если в нем нет хоть
         * одной четверки или единицы, то метод вернет
         * false; Если в массиве есть числа отличные от
         * 1 и 4, то метод выбрасывает RuntimeException;
         * Написать набор тестов для этого метода (по 3-4 варианта входных данных).
         */
        if (!Arrays.stream(array).filter(a -> a != 1 && a != 4).findFirst().isEmpty() || array.length == 0) {
            throw new RuntimeException("In addition to 1 and 4 there are other numbers");
        }
        return !Arrays.stream(array).filter(a -> a == 1).findFirst().isEmpty()
                && !Arrays.stream(array).filter(a -> a == 4).findFirst().isEmpty();
    }
}
