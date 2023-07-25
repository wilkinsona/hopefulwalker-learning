package net.flyinggroup.test.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@WebFluxTest(controllers = {FileController.class})
class FileControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void uploadFile() throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        List<Path> paths = addTempFiles(builder, "files", 2);
        webTestClient.post()
                .uri("/api/v1alpha1/files/")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(FileInfo.class)
                .hasSize(paths.size());
        deleteTempFiles(paths);
    }

    private List<Path> addTempFiles(MultipartBodyBuilder builder, String name, int count) throws IOException {
        List<Path> paths = new ArrayList<>();
        Path path;
        for (int i = 0; i < count; i++) {
            path = Files.createTempFile("file", ".tmp");
            paths.add(path);
            builder.part(name, new FileSystemResource(path));
        }
        return paths;
    }

    private void deleteTempFiles(List<Path> paths) throws IOException {
        for (Path path : paths) Files.deleteIfExists(path);
    }
}