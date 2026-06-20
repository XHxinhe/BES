package cn.tesseract.bes.command;

import java.io.*;
import java.util.*;

public final class HomeData {
    public static final String DEFAULT_HOME = "home";
    private static final Map<String, Map<String, double[]>> homes = new HashMap<>();
    private static final Map<String, String> tpaRequests = new HashMap<>();
    private static final Map<String, double[]> lastPositions = new HashMap<>();
    private static final Set<String> autoAcceptTpa = new HashSet<>();
    private static final String HOMES_FILE = "mite_homes.txt";
    private static final String LAST_POSITIONS_FILE = "mite_last_positions.txt";
    private static final String TPA_AUTO_FILE = "mite_tpa_auto.txt";
    private static boolean loaded = false;
    private static boolean lastPositionsDirty = false;

    private HomeData() {
    }

    public static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        loadHomes();
        loadLastPositions();
        loadTpaAuto();
    }

    private static void loadHomes() {
        try {
            File file = new File(HOMES_FILE);
            if (!file.exists()) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        if (!line.isEmpty()) {
                            String[] strArrSplit = line.split(",");
                            try {
                                if (strArrSplit.length >= 6) {
                                    putHome(strArrSplit[0], strArrSplit[1], Double.parseDouble(strArrSplit[2]), Double.parseDouble(strArrSplit[3]), Double.parseDouble(strArrSplit[4]), (int) Double.parseDouble(strArrSplit[5]));
                                } else if (strArrSplit.length == 5 && isNumeric(strArrSplit[1])) {
                                    putHome(strArrSplit[0], DEFAULT_HOME, Double.parseDouble(strArrSplit[1]), Double.parseDouble(strArrSplit[2]), Double.parseDouble(strArrSplit[3]), (int) Double.parseDouble(strArrSplit[4]));
                                } else if (strArrSplit.length == 4) {
                                    putHome(strArrSplit[0], DEFAULT_HOME, Double.parseDouble(strArrSplit[1]), Double.parseDouble(strArrSplit[2]), Double.parseDouble(strArrSplit[3]), 0);
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    } else {
                        bufferedReader.close();
                        return;
                    }
                } catch (Throwable th) {
                    bufferedReader.close();
                    throw th;
                }
            }
        } catch (Throwable th2) {
            System.err.println("[CheatMod] load homes failed: " + th2);
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void putHome(String str, String str2, double d, double d2, double d3, int i) {
        Map<String, double[]> map = homes.computeIfAbsent(str, k -> new HashMap<>());
        map.put(str2, new double[]{d, d2, d3, i});
    }

    private static void saveHomes() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(HOMES_FILE));
            try {
                for (Map.Entry<String, Map<String, double[]>> entry : homes.entrySet()) {
                    String key = entry.getKey();
                    for (Map.Entry<String, double[]> entry2 : entry.getValue().entrySet()) {
                        double[] value = entry2.getValue();
                        printWriter.println(key + "," + entry2.getKey() + "," + value[0] + "," + value[1] + "," + value[2] + "," + ((int) value[3]));
                    }
                }
                printWriter.close();
            } catch (Throwable th) {
                printWriter.close();
                throw th;
            }
        } catch (Throwable th2) {
            System.err.println("[CheatMod] save homes failed: " + th2);
        }
    }

    public static synchronized boolean hasHome(String str, String str2) {
        ensureLoaded();
        Map<String, double[]> map = homes.get(str);
        return map != null && map.containsKey(str2);
    }

    public static synchronized void setHome(String str, String str2, double d, double d2, double d3, int i) {
        ensureLoaded();
        putHome(str, str2, d, d2, d3, i);
        saveHomes();
    }

    public static synchronized double[] getHome(String str, String str2) {
        ensureLoaded();
        Map<String, double[]> map = homes.get(str);
        if (map == null) {
            return null;
        }
        return map.get(str2);
    }

    public static synchronized List<String> listHomes(String str) {
        ensureLoaded();
        Map<String, double[]> map = homes.get(str);
        if (map == null) {
            return Collections.emptyList();
        }
        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        Collections.sort(arrayList);
        return arrayList;
    }

    public static synchronized int homeCount(String str) {
        ensureLoaded();
        Map<String, double[]> map = homes.get(str);
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public static synchronized double[] getOnlyHome(String str) {
        ensureLoaded();
        Map<String, double[]> map = homes.get(str);
        if (map == null || map.size() != 1) {
            return null;
        }
        return map.values().iterator().next();
    }

    public static synchronized void setTpaRequest(String str, String str2) {
        tpaRequests.put(str, str2);
    }

    public static synchronized String getTpaRequest(String str) {
        return tpaRequests.get(str);
    }

    public static synchronized void clearTpaRequest(String str) {
        tpaRequests.remove(str);
    }

    /* JADX WARN: Finally extract failed */
    private static void loadLastPositions() {
        try {
            File file = new File(LAST_POSITIONS_FILE);
            if (!file.exists()) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        String[] strArrSplit = line.split(",");
                        if (strArrSplit.length == 5) {
                            try {
                                lastPositions.put(strArrSplit[0], new double[]{Double.parseDouble(strArrSplit[1]), Double.parseDouble(strArrSplit[2]), Double.parseDouble(strArrSplit[3]), Double.parseDouble(strArrSplit[4])});
                            } catch (NumberFormatException e) {
                            }
                        }
                    } else {
                        bufferedReader.close();
                        return;
                    }
                } catch (Throwable th) {
                    bufferedReader.close();
                    throw th;
                }
            }
        } catch (Throwable th2) {
            System.err.println("[CheatMod] load last positions failed: " + th2);
        }
    }

    private static void saveLastPositions() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(LAST_POSITIONS_FILE));
            try {
                for (Map.Entry<String, double[]> entry : lastPositions.entrySet()) {
                    double[] value = entry.getValue();
                    printWriter.println(entry.getKey() + "," + value[0] + "," + value[1] + "," + value[2] + "," + ((int) value[3]));
                }
                printWriter.close();
            } catch (Throwable th) {
                printWriter.close();
                throw th;
            }
        } catch (Throwable th2) {
            System.err.println("[CheatMod] save last positions failed: " + th2);
        }
    }

    public static synchronized void setLastPosition(String str, double d, double d2, double d3, int i) {
        ensureLoaded();
        lastPositions.put(str, new double[]{d, d2, d3, i});
        lastPositionsDirty = true;
    }

    public static synchronized double[] getLastPosition(String str) {
        ensureLoaded();
        return lastPositions.get(str);
    }

    public static synchronized void flushLastPositions() {
        if (!lastPositionsDirty) {
            return;
        }
        saveLastPositions();
        lastPositionsDirty = false;
    }

    private static void loadTpaAuto() {
        try {
            File file = new File(TPA_AUTO_FILE);
            if (!file.exists()) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        String strTrim = line.trim();
                        if (!strTrim.isEmpty()) {
                            autoAcceptTpa.add(strTrim);
                        }
                    } else {
                        bufferedReader.close();
                        return;
                    }
                } catch (Throwable th) {
                    bufferedReader.close();
                    throw th;
                }
            }
        } catch (Throwable th2) {
            System.err.println("[CheatMod] load tpa auto failed: " + th2);
        }
    }

    private static void saveTpaAuto() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(TPA_AUTO_FILE));
            try {
                for (String s : autoAcceptTpa) {
                    printWriter.println(s);
                }
                printWriter.close();
            } catch (Throwable th) {
                printWriter.close();
                throw th;
            }
        } catch (Throwable th2) {
            System.err.println("[CheatMod] save tpa auto failed: " + th2);
        }
    }

    public static synchronized boolean isAutoAcceptTpa(String str) {
        ensureLoaded();
        return autoAcceptTpa.contains(str);
    }

    public static synchronized boolean toggleAutoAcceptTpa(String str) {
        boolean z;
        ensureLoaded();
        if (autoAcceptTpa.contains(str)) {
            autoAcceptTpa.remove(str);
            z = false;
        } else {
            autoAcceptTpa.add(str);
            z = true;
        }
        saveTpaAuto();
        return z;
    }
}
