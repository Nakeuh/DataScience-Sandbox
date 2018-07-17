package utils;

import java.util.List;

public class Util {

    /**
     * Equals function for Lists of Double
     * @param list1
     * @param list2
     * @return
     */
    public static boolean equals(List<Double> list1, List<Double> list2) {
        Boolean equals = true;
        for (int i = 0; i < list1.size(); i++) {
            equals = equals && list1.get(i).equals(list2.get(i));
        }
        return equals;
    }
}
