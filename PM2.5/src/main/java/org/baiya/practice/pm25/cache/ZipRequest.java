package org.baiya.practice.pm25.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipInputStream;

public class ZipRequest {

    protected String request(String url) {
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setReadTimeout(60000);
            InputStream inputStream = urlConnection.getInputStream();
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);

            if (zipInputStream.getNextEntry() != null) {
                InputStreamReader reader = new InputStreamReader(zipInputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                StringBuilder builder = new StringBuilder();
                char[] chars = new char[8192];
                while (true) {
                    int count = bufferedReader.read(chars);
                    if (count <= 0) {
                        bufferedReader.close();
                        inputStream.close();
                        return builder.toString();
                    }
                    builder.append(chars, 0, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
