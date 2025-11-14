package Backend_Bugg.Debugg;

import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.*;

@Service
public class services {

    public attributes compileAndRunWithInput(String code, String input) {
        try {
            // Step 1: Save user code to a file
            String fileName = "UserProgram.java";
            Files.write(Paths.get(fileName), code.getBytes());

            // Step 2: Compile code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            int result = compiler.run(null, null, errStream, fileName);

            if (result != 0) {
                return new attributes("Compilation Error:\n" + errStream, "ERROR");
            }

            // Step 3: Run compiled code with input
            ProcessBuilder pb = new ProcessBuilder("java", "UserProgram");
            pb.redirectErrorStream(true); // merge stdout & stderr
            Process process = pb.start();

            // Send Scanner input to process
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(input);
                if (!input.endsWith("\n")) writer.newLine(); // make sure last line ends with newline
                writer.flush();
            }

            // Read process output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) output.append(line).append("\n");

            process.waitFor(); // wait until process finishes

            return new attributes(output.toString(), "SUCCESS");

        } catch (Exception e) {
            return new attributes("Runtime Error: " + e.getMessage(), "ERROR");
        }
    }
}
