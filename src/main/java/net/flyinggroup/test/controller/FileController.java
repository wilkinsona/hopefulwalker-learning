package net.flyinggroup.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1alpha1/files")
public class FileController {

    private final Logger log = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<FileInfo> uploadFile(@RequestPart("files") Flux<FilePart> files) {
        return files.log().filter(part -> StringUtils.hasLength(part.filename()))
                .flatMap(part -> Mono.just(new FileInfo(part.filename())))
                .doOnNext(fileInfo -> log.debug(fileInfo.toString()));
    }

}
