package fr.abes.baconprovider.controller;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fr.abes.baconprovider.configuration.Constants;
import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.exception.FileException;
import fr.abes.baconprovider.exception.IllegalDatabaseOperation;
import fr.abes.baconprovider.service.FileService;
import fr.abes.baconprovider.service.ProviderService;
import fr.abes.baconprovider.utils.UtilsMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    @PostMapping(value = "/providers", produces = "application/octet-stream;charset=UTF-8", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public void postProviders(@Parameter(description = "File to upload") @RequestPart(value = "file")
                                  @Schema(type = "string", format = "binary")MultipartFile file) throws IOException, FileException, CsvException, IllegalDatabaseOperation {
        File tmpFile = new File("provider.csv");

        file.transferTo(Path.of(tmpFile.toURI()));

        //vérification du fichier
        fileService.checkCsvFile(tmpFile, Provider.class);

        CSVReader reader = new CSVReaderBuilder(new FileReader(tmpFile)).withCSVParser(new CSVParserBuilder().withSeparator(Constants.SEPARATOR).build()).build();
        //lecture à vide la ligne d'en tête
        reader.readNext();
        List<Provider> listeProvider = mapper.mapList(reader.readAll(),Provider.class);

        providerService.saveListProvider(listeProvider);
        reader.close();
    }

}
