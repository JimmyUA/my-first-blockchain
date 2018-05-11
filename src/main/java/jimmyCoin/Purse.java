package jimmyCoin;

import java.util.ArrayList;
import java.util.List;

public class Purse {
    public static void main(String[] args) {
        float totalPurse = 100.0f;
        int playersAmount = 44;

        List<Float> distributed = new ArrayList<>();
        float purse;
        for (int i = playersAmount; i > 0; i--) {
            purse = (float) (0.2 * totalPurse * Math.pow(i, -0.9207));
            distributed.add(purse);
//            totalPurse = totalPurse - purse;
        }

        distributed.forEach(System.out::println);

        float sum = distributed.stream().reduce((f1, f2) -> f1 + f2).get();
        System.out.println("Sum: " + sum);
        System.out.println(distributed.get(23));
    }
}

