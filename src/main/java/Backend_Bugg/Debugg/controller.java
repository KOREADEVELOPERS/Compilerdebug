package Backend_Bugg.Debugg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = "*")
public class controller {

    @Autowired
    private services codeService;

    @PostMapping("/run")
    public attributes runCode(@RequestBody String code) {
        return codeService.compileAndRun(code);
    }
}
