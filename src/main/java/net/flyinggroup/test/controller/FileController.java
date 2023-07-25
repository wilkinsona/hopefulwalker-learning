package net.flyinggroup.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1alpha1/files")
public class FileController {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<FileInfo> uploadFile(@RequestPart("files") Flux<FilePart> files) {
        return files.log().filter(part -> StringUtils.isNotBlank(part.filename()))
                .flatMap(part -> Mono.just(new FileInfo(part.filename())))
                .doOnNext(fileInfo -> log.debug(fileInfo.toString()));
    }

}
