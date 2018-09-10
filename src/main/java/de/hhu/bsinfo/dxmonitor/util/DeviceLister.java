package de.hhu.bsinfo.dxmonitor.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Helper class which lists (ib, nics, disk) devices
 *
 * @author Burak Akguel, burak.akguel@hhu.de, 23.11.2017
 */
public class DeviceLister {
    private static final String PROC_NICS = "/proc/net/dev";
    private static final String PROC_DISKS = "/proc/partitions";
    private static final String SYS_IB = "/sys/class/infiniband";

    /**
     * Returns a list of available infiniband devices
     *
     * @return list of ib devs
     */
    public static ArrayList<String> getIBs() {
        ArrayList<String> out = new ArrayList<>();

        File file = new File(SYS_IB);
        if (!file.exists()) {
            return out;
        }

        for (File ibDevice : file.listFiles()) {
            out.add(ibDevice.getName());
        }

        return out;
    }

    /**
     * Returns a list of all available nics
     *
     * @return List with NICs
     * @throws IOException
     *         if the file could not be found or read
     */
    public static ArrayList<String> getNICs() throws IOException {
        return getContent(PROC_NICS, s -> s.substring(0, s.indexOf(':')).trim());
    }

    /**
     * Returns a list of all disks
     *
     * @return List with disks
     * @throws IOException
     *         if the file could not be found or read
     */
    public static ArrayList<String> getDisks() throws IOException {
        return getContent(PROC_DISKS, s -> s.substring(s.lastIndexOf(' ')).trim());
    }

    /**
     * Helper method to read and parse certain proc files
     *
     * @param p_path
     *         File path
     * @param p_filterFunction
     *         A function to filter each file in its own way
     * @return List of filtered entries from a given path
     * @throws IOException
     *         if the file could not be found or read
     */
    private static ArrayList<String> getContent(final String p_path, final Function<String, String> p_filterFunction)
            throws IOException {
        String[] fileContent;
        fileContent = ProcSysFileReader.readCompleteFileOnce(p_path).split("\n");

        ArrayList<String> out = new ArrayList<>();
        for (int i = 2; i < fileContent.length; i++) {
            if (fileContent[i].isEmpty() || fileContent[i].equals(" ")) {
                continue;
            }
            out.add(p_filterFunction.apply(fileContent[i]));
        }

        return out;
    }

}
