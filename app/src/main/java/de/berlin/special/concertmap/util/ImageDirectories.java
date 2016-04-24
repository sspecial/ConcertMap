package de.berlin.special.concertmap.util;

import java.io.File;

/**
 * Created by Saeed on 23-Apr-16.
 */
public class ImageDirectories {

    public static void organize(){
        /*
        File dir = new File(Utility.imageDirPath());
        if (dir.exists()) {
            for (File imFile : dir.listFiles()) {
                imFile.delete();
            }
            dir.delete();
        }
        */
        File artistDir = new File(Utility.IMAGE_DIR_ARTIST);
        if (!artistDir.exists()) {
            artistDir.mkdirs();
        }

        File eventDir = new File(Utility.IMAGE_DIR_EVENT);
        if (!eventDir.exists()) {
            eventDir.mkdirs();
        }

        File dailyDir = new File(Utility.IMAGE_DIR_DAILY);

        if (!dailyDir.exists()) {
            dailyDir.mkdirs();
        } else {
            if (dailyDir.listFiles() != null) {
                for (File imFile : dailyDir.listFiles()) {
                    if (!Utility.imageDirToday().equals(imFile.getAbsolutePath())) {
                        if (imFile.isDirectory()) {
                            for (File image : imFile.listFiles()) {
                                image.delete();
                            }
                            imFile.delete();
                        } else {
                            imFile.delete();
                        }
                    }
                }
            }
        }

        File todayDir = new File(Utility.imageDirToday());
        if (!todayDir.exists()) {
            todayDir.mkdirs();
        }
    }
}
