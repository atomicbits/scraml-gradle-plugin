package io.atomicbits.scraml.gradleplugin.util;

import java.util.List;

/**
 * Created by peter on 18/10/17.
 */
public class ListUtils {

    static public String mkString(List<String> list, String delimiter) {
        return mkStringHelper(list, delimiter, new StringBuilder());
    }

    static private String mkStringHelper(List<String> list, String delimiter, StringBuilder sb) {

        if (list == null || list.isEmpty()) {
            return sb.toString();
        } else if (list.size() == 1) {
            String head = list.get(0);
            return sb.append(head).toString();
        } else {
            String head = list.get(0);
            List<String> tail = list.subList(1, list.size());
            return mkStringHelper(tail, delimiter, sb.append(head).append(delimiter));
        }

    }


}
