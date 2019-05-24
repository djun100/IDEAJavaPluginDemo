package com.cy.javaplugin.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

/**
 * Created by cy on 2017/6/22.
 */
public class UtilCmd {
    public static ArrayList<String> exec(String cmd) {
        ArrayList<String> results=new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            InputStreamReader ir = new InputStreamReader(process.getInputStream(),"UTF-8");
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                results.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
