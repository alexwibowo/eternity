package org.isolution.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * User: alex
 * Date: 22/11/2007
 * Time: 21:38:40
 * Copyright: Insight Solution
 */
public class StreamUtils {

    public static String[] readLines(InputStream is) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        int count = 0;

        String line=reader.readLine();

        while (line != null) {
            if (line != null) {
                lines.add(line);
                count++;
            }
            line = reader.readLine();
        }

        reader.close();

        System.out.println("Read :"+count + " lines");
        return lines.toArray(new String[lines.size()]);
    }
}
