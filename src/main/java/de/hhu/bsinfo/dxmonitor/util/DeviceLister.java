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

    public static ArrayList<String> getIBs() {
        ArrayList<String> out = new ArrayList<>();

        File file = new File(SYS_IB);
        if(!file.exists()) {
            return out;
        }

        for(File ibDevice : file.listFiles()) {
            out.add(ibDevice.getName());
        }

        return out;
    }

    public static ArrayList<String> getNICs() {
        return getContent(PROC_NICS, s -> s.substring(0, s.indexOf(':')).trim());
    }

    public static ArrayList<String> getDisks() {
        return getContent(PROC_DISKS, s -> s.substring(s.lastIndexOf(' ')).trim());
    }


    private static ArrayList<String> getContent(final String p_path, final Function<String, String> p_filterFunction) {
        String[]  fileContent = null;
        try {
            fileContent = ProcSysFileReader.readCompleteFileOnce(p_path).split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fileContent != null;

        ArrayList<String> out = new ArrayList<>();
        for (int i = 2; i < fileContent.length; i++) {
            if(fileContent[i].isEmpty() || fileContent[i].equals(" ")) {
                continue;
            }
            out.add(p_filterFunction.apply(fileContent[i]));
        }

        return out;
    }

}
