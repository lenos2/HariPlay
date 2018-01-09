package zw.co.hariplay.hariplay.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    public static ArrayList<String> getImagePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isFile() && accept(listfiles[i])){

                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    private static final String[] okFileVideoExtensions =  new String[] {"mp4", "m4a","webm"};
    private static boolean acceptedVideo(File file)
    {
        for (String extension : okFileVideoExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }
    public static ArrayList<String> getVideoPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isFile() && acceptedVideo(listfiles[i])){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    private static final String[] okFileImageExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
    private static boolean accept(File file)
    {
        for (String extension : okFileImageExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;
    }
}
