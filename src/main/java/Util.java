public class Util {
    public static String readFromPropertyOrEnv(String name) {
        String tokenProperty = System.getProperty(name);
        return (tokenProperty == null || tokenProperty.isEmpty()) ? System.getenv(name) : tokenProperty;
    }
}
