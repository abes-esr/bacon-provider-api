package fr.abes.baconprovider.controller;

import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.service.FileService;
import fr.abes.baconprovider.service.ProviderService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class BaconProviderController {
    private final ProviderService providerService;

    private final FileService fileService;

    public BaconProviderController(ProviderService providerService, FileService fileService) {
        this.providerService = providerService;
        this.fileService = fileService;
    }

    @GetMapping(value = "/providers", produces = "application/octet-stream;charset=UTF-8")
    public ResponseEntity<Resource> getProviders() throws IOException, IllegalAccessException {
        File file = File.createTempFile("provider", ".csv");
        fileService.writeHeaders(file, Provider.class);
        List<Provider> providers = providerService.getProviders();
        for (Provider provider : providers) {
            fileService.writeLine(file, provider);
        }

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "force-download"));
        headers.setContentDispositionFormData("attachment", "provider.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
