package nextstep.subway.controller.path;

import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.service.line.LineService;
import nextstep.subway.service.path.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping(value = "/paths")
    public ResponseEntity<PathResponse> getPath(@RequestParam("source") Long source,  @RequestParam("target") Long target) {
        PathResponse path = pathService.getShortestPath(source, target);
        return ResponseEntity.ok().body(path);
    }

}
