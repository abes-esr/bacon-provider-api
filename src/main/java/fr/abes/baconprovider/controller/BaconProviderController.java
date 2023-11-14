package fr.abes.baconprovider.controller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.FileException;
import fr.abes.baconprovider.service.FileService;
import fr.abes.baconprovider.service.ProviderService;
import fr.abes.baconprovider.utils.UtilsMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final UtilsMapper mapper;

    public BaconProviderController(ProviderService providerService, FileService fileService, UtilsMapper mapper) {
        this.providerService = providerService;
        this.fileService = fileService;
        this.mapper = mapper;
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

    @PostMapping(value = "/providers", produces = "application/octet-stream;charset=UTF-8")
    public void postProviders(MultipartFile file) throws IOException, FileException, CsvException {
        File tmpFile = new File("provider.csv");
        file.transferTo(tmpFile);

        //vérification du fichier
        fileService.checkCsvFile(tmpFile, Provider.class);

        CSVReader reader = new CSVReader(new FileReader(tmpFile));
        List<Provider> listeProvider = mapper.mapList(reader.readAll(),Provider.class);

        providerService.saveListProvider(listeProvider);
        reader.close();
    }

    private File createTmpFileFromMultipart(MultipartFile file) throws IOException {
        File tmpFile = new File("providers.csv");

        return tmpFile;
    }
}
