package event.logging.transformer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TestRealPipelines {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRealPipelines.class);

    private static final Path GENERATED_PATH = Paths.get("./pipelines/generated");

    private MockSystemService systemService = new MockSystemService();

    @BeforeEach
    public void setup() throws IOException {
        systemService.reset();

        if (Files.exists(GENERATED_PATH)) {
            Assertions.assertThat(GENERATED_PATH).isDirectory();
            SchemaGenerator.emptyDirectory(GENERATED_PATH);
        }
    }

    @Test
    public void testMain() throws Exception {
        TestUtil.runGenerator(
                systemService,
                0,
                "pipelines",
                "../event-logging.xsd");

        //two pipelines in config so should result in 2 files
        Assertions.assertThat((long) TestUtil.listFiles(GENERATED_PATH, Stream::count))
                .isGreaterThanOrEqualTo(2L);

        TestUtil.forEachFile(GENERATED_PATH, file -> {
            Assertions.assertThat(file)
                    .isRegularFile();
            Assertions.assertThat(file.getFileName().toString())
                    .endsWith(".xsd");
        });
    }
}
