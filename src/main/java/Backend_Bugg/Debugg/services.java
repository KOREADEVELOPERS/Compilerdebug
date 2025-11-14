package Backend_Bugg.Debugg;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

@Service
public class services {

    private static final int TIME_LIMIT_SECONDS = 2;      // Time limit like JDoodle
    private static final int OUTPUT_LIMIT_LINES = 200;    // Output limit

    public attributes compileAndRun(String code) {
        try {
            // Step 1: Create temp folder
            File folder = new File("temp");
            if (!folder.exists()) folder.mkdir();

            // Step 2: Save user code
            String fileName = "UserProgram.java";
            Files.write(Paths.get("temp/" + fileName), code.getBytes());

            // Step 3: Compile code
            Process compileProcess = Runtime.getRuntime().exec(
                    "javac temp/UserProgram.java"
            );

            compileProcess.waitFor();

            // If compile error
            String compileErrors = new String(compileProcess.getErrorStream().readAllBytes());
            if (!compileErrors.isEmpty()) {
                return new attributes("Compilation Error:\n" + compileErrors, "ERROR");
            }

            // Step 4: Run code with ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "temp", "UserProgram");
            pb.redirectErrorStream(true);
            Process runProcess = pb.start();

            // Step 5: Capture output with timeout
            ExecutorService executor = Executors.newSingleThreadExecutor();

            Future<String> future = executor.submit(() ->
                    readProcessOutput(runProcess)
            );

            try {
                String output = future.get(TIME_LIMIT_SECONDS, TimeUnit.SECONDS);
                return new attributes(output, "SUCCESS");

            } catch (TimeoutException e) {
                runProcess.destroyForcibly();
                return new attributes(
                        "⏳ Time Limit Exceeded (possible infinite loop)",
                        "ERROR"
                );
            } finally {
                executor.shutdownNow();
            }

        } catch (Exception e) {
            return new attributes("Runtime Error: " + e.getMessage(), "ERROR");
        }
    }

    // Output reader with line limit
    private String readProcessOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        int count = 0;

        while ((line = reader.readLine()) != null) {
            if (count >= OUTPUT_LIMIT_LINES) {
                output.append("\n⚠ Output truncated...");
                break;
            }
            output.append(line).append("\n");
            count++;
        }

        return output.toString();
    }
}
