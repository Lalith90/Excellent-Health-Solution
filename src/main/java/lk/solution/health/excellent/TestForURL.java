package lk.solution.health.excellent;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Controller
public class TestForURL {
    private final Environment environment;

    public TestForURL(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/test1")
    public void doSomething(UriComponentsBuilder uriComponentsBuilder) {
   /*     URI someNewUriBasedOnCurrentRequest = uriComponentsBuilder
                .replacePath(null)
                .replaceQuery(null)
                .pathSegment("some", "new", "path")
                .build().toUri();*/
        //... output -> http://localhost:8090/some/new/path

              URI someNewUriBasedOnCurrentRequest = uriComponentsBuilder
                .build().toUri();
        //... output -> http://localhost:8090


        System.out.println(someNewUriBasedOnCurrentRequest);
    }
    @GetMapping("/test2")
    public void getURL(HttpServletRequest request){
        String fullURL = request.getRequestURL().toString();
        System.out.println(fullURL); //outPut -> http://localhost:8090/test2
        System.out.println( fullURL.substring(0, StringUtils.ordinalIndexOf(fullURL, "/", 3))); // outPut -> http://localhost:8090
    }
    @GetMapping("/env")
    public void testEnvermentFact(){
        String a = environment.getProperty("spring.mail.username");
        System.out.println(a);
    }
}
