/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * Debug - Small debugging tools, to keep debugging simple.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

public class Debug {
    /**
     * If an instance is needed, make it a simpleton.
     */
    private static Debug ourInstance = new Debug();
    public static Debug getInstance() {
        return ourInstance;
    }

    /**
     * Private constructor - static class.
     */
    private Debug() {
    }

    /**
     * out - Simple debug message display, for one-liner messages.
     * @param msg - More detailed message.
     */
    public static void out(String msg) {
        System.out.println(msg);
    }

    /**
     * out - Simple debug message display, for simple messages.
     * @param title - Error title line.
     * @param msg - More detailed message.
     */
    public static void out(String title, String msg) {
        System.out.println(title);
        System.out.println(msg);
    }

    /**
     * out - Simple debug message display, for Exceptions.
     * @param title - Error title line.
     * @param e - The exception, to display its message.
     */
    public static void out(String title, Exception e) {
        System.out.println(title);
        System.out.println(e.getMessage());
    }

}
