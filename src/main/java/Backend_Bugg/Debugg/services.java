package Backend_Bugg.Debugg;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.*;

@Service
public class services {

    public attributes compileAndRun(String code) {
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

            // Step 3: Run compiled code
            Process process = Runtime.getRuntime().exec("java UserProgram");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) output.append(line).append("\n");
            while ((line = errReader.readLine()) != null) output.append(line).append("\n");

            return new attributes(output.toString(), "SUCCESS");

        } catch (Exception e) {
            return new attributes("Runtime Error: " + e.getMessage(), "ERROR");
        }
    }
}
