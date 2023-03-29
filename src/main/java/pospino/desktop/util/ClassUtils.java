package pospino.desktop.util;

public final class ClassUtils {

    private ClassUtils() {
    }

    // https://www.geeksforgeeks.org/get-name-of-current-method-being-executed-in-java/
    public static String getExecutingMethodName() {
        return new Exception().getStackTrace()[1].getMethodName();
    }

}
