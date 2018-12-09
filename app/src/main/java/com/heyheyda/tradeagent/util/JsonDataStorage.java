package com.heyheyda.tradeagent.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class JsonDataStorage {

    private static final String FILE_NAME_FORMAT = "json_storage_%s.jds";

    /**
     * write object into file
     * @return true if success
     */
    public static boolean writeToFile(String key, Object object, Context context) throws IOException {
        final String fileName = String.format(FILE_NAME_FORMAT, key);

        //convert to string
        Gson gson = new Gson();
        String objectJsonString = gson.toJson(object);

        //write file
        if (context != null) {
            Writer writer = null;
            try {
                FileOutputStream fos = context.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(fos);
                writer.write(objectJsonString);
                writer.flush();
            } catch (FileNotFoundException e) {
                Log.printStackTrace(e);
                return false;
            } finally {
                //close stream
                if (writer != null) {
                    writer.close();
                }
            }
        }

        return true;
    }

    /**
     * read target class from file
     * @return null if failed
     */
    @Nullable
    public static  <T> T readFromFile(String key, Class<T> classOfT, Context context) throws IOException {
        final String fileName = String.format(FILE_NAME_FORMAT, key);

        T object = null;
        if (context != null) {
            Reader reader = null;
            try {
                //read file
                FileInputStream fis = context.getApplicationContext().openFileInput(fileName);
                reader = new InputStreamReader(fis);

                //convert into object
                Gson gson = new Gson();
                object = gson.fromJson(reader, classOfT);
            } catch (FileNotFoundException e) {
                Log.printStackTrace(e);
                return null;
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }

        return object;
    }
}
