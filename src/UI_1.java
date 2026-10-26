import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UI_1 {

    // A plain file next to the program. Change the name if you like.
    private static final Path FILE = Path.of("student_list.txt");

    private UI_1() {}   // static helper class: nobody should create one

    /** The file the list is saved in. */
    public static Path path() {
        return FILE;
    }

    /** True if a saved list exists and is not empty. */
    public static boolean exists() {
        try {
            return Files.exists(FILE) && Files.size(FILE) > 0;
        } catch (IOException ex) {
            return false;   // if we can't read it, treat it as "no list"
        }
    }

    /** Deletes the saved list. Does nothing if there isn't one. */
    public static void clear() throws IOException {
        Files.deleteIfExists(FILE);
    }
}