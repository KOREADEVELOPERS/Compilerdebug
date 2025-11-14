package Backend_Bugg.Debugg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = "*")
public class controller {

    @Autowired
    private services codeService;

//    @PostMapping("/run")
//    public attributes runCode(@RequestBody String code) {
//        return codeService.compileAndRun(code);
//    }
@PostMapping("/run")
public attributes runCode(@RequestBody Map<String,String> body) {
    String code = body.get("code");
    String input = body.getOrDefault("input","");
    return codeService.compileAndRunWithInput(code,input);
}

}
