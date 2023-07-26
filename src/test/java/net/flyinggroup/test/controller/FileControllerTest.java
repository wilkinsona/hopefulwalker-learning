package net.flyinggroup.test.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import net.flyinggroup.test.TestApplication;

@SpringJUnitConfig(TestApplication.class)
class FileControllerTest {

    @Autowired
    ApplicationContext context;

    @Test
    void uploadFile() throws IOException {
        WebTestClient webTestClient = WebTestClient.bindToApplicationContext(context).build();
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