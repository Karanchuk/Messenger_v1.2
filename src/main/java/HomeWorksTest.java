import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeWorksTest {

    private HomeWorks homeWorks;

    @BeforeEach
    public void init() {
        homeWorks = new HomeWorks();
    }

    @Test
    void hw6task2() {
        Assertions.assertArrayEquals(new int[] {5} , homeWorks.hw6task2(new int[] {1, 2, 3, 4, 5}));
        Assertions.assertArrayEquals(new int[]{3,2,1}, homeWorks.hw6task2(new int[]{1,2,3,4,5,4,3,2,1}));
        Assertions.assertArrayEquals(new int[]{}, homeWorks.hw6task2(new int[]{1,2,2,3,3,4}));
        Assertions.assertThrows(RuntimeException.class, () -> homeWorks.hw6task2(new int[]{3,3,2,2,1,1,0,0}));
    }

    @Test
    void hw6task3() {
        Assertions.assertFalse(homeWorks.hw6task3(new int[] {4,4,4}));
        Assertions.assertFalse(homeWorks.hw6task3(new int[] {1,1,1,1}));
        Assertions.assertTrue(homeWorks.hw6task3(new int[] {1,4,4,4,1,4,4,1,1,4,1}));
        Assertions.assertTrue(homeWorks.hw6task3(new int[] {4,1,1,1,1}));
        Assertions.assertThrows(RuntimeException.class, () -> homeWorks.hw6task3(new int[] {1,2,3}));
        Assertions.assertThrows(RuntimeException.class, () -> homeWorks.hw6task3(new int[] {}));
    }
}