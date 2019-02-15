package nz.shelto.banyan.radix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class Main {

    public static long LINE_NUM = -1;

    public static List<String> FILE_CONTENTS = null;
    public static void main(final String... args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("/words.txt").toURI());
        FILE_CONTENTS = Files.readAllLines(path);
        LINE_NUM =  Files.lines(path).count();
        SpringApplication.run(Main.class, args);
    }
}
