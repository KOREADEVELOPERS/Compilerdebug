package Backend_Bugg.Debugg;

import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class services {

    /**
     * Compile and run Java code with optional Scanner input.
     *
     * @param code  Java source code
     * @param input Scanner input (newline separated)
     * @return attributes object with output or error
     */
    public attributes compileAndRunWithInput(String code, String input) {
        try {
            // Step 1: Save user code to a temporary file
            String fileName = "UserProgram.java";
            Files.write(Paths.get(fileName), code.getBytes());

            // Step 2: Compile code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return new attributes("JavaCompiler not found. Make sure JDK is installed.", "ERROR");
            }

            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            int compileResult = compiler.run(null, null, errStream, fileName);

            if (compileResult != 0) {
                return new attributes("Compilation Error:\n" + errStream.toString(), "ERROR");
            }

            // Step 3: Run compiled code with input
            ProcessBuilder pb = new ProcessBuilder("java", "UserProgram");
            pb.redirectErrorStream(true); // Merge stdout & stderr
            Process process = pb.start();

            // Send Scanner input
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(input);
                if (!input.endsWith("\n")) writer.newLine();
                writer.flush();
            }

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor(); // wait until program finishes
            return new attributes(output.toString(), "SUCCESS");

        } catch (Exception e) {
            return new attributes("Runtime Error: " + e.getMessage(), "ERROR");
        }
    }
}
