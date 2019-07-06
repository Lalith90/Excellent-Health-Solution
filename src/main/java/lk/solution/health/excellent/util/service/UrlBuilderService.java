package lk.solution.health.excellent.util.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class UrlBuilderService {
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
}
