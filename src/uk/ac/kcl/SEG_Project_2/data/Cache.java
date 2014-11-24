package uk.ac.kcl.SEG_Project_2.data;

import android.content.Context;
import uk.ac.kcl.SEG_Project_2.constants.C;

import java.io.File;

/**
 * Created by ravi on 24/11/14.
 */

public class Cache {

    public static boolean hasData(Context context, WorldBankApiRequest request) {

        String hash = request.createHash();

        File cacheFolder = context.getFilesDir();
        File[] cacheFiles = cacheFolder.listFiles();

        long currentTime = System.currentTimeMillis()/1000;

        for (File f : cacheFiles) {

            String name = f.getName();
            if (name.startsWith("cache." + hash)) {

                String[] nameParts = name.split("\\.");

                if (nameParts.length != 3) continue;

                long timeStamp;
                try {
                    timeStamp = Long.parseLong(nameParts[2]);
                }
                catch (NumberFormatException e) {

                    continue;

                }


                if(currentTime-timeStamp <= C.CACHE_EXPIRY){

                    return  true;

                }
                else {

                    f.delete();

                }

            }


        }

        return false;


    }




}

