package fr.abes.baconprovider.controller;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fr.abes.baconprovider.configuration.Constants;
import fr.abes.baconprovider.entity.Provider;
import fr.abes.baconprovider.dto.ProviderPackageDeletedDTO;
import fr.abes.baconprovider.exception.FileException;
import fr.abes.baconprovider.exception.IllegalDatabaseOperation;
import fr.abes.baconprovider.service.FileService;
import fr.abes.baconprovider.service.ProviderPackageDeletedService;
import fr.abes.baconprovider.service.ProviderService;
import fr.abes.baconprovider.utils.UtilsMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class BaconProviderController {
    private final ProviderService providerService;

    private final FileService fileService;

    private final UtilsMapper mapper;
    private final ProviderPackageDeletedService providerPackageDeletedService;

    public BaconProviderController(ProviderService providerService, FileService fileService, UtilsMapper mapper, ProviderPackageDeletedService providerPackageDeletedService) {
        this.providerService = providerService;
        this.fileService = fileService;
        this.mapper = mapper;
        this.providerPackageDeletedService = providerPackageDeletedService;
    }

    @Operation(
            summary = "Permet de récuperer un fichier CSV contenant tout les Providers.",
            description = "La methode récupere tout les providers se trouvant dans la table PROVIDER de Bacon, et construit un fichier CSV avec."
    )
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

    @Operation(
            summary = "Permet d'envoyer des Providers afin de les sauvegarders.",
            description = "La methode envoi un fichier CSV contenant des providers afin de les sauvegarders dans la table PROVIDER de la base Bacon."
    )
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

    @GetMapping(value = "/provider-package-deleted")
    public List<ProviderPackageDeletedDTO> getProviderPackageDeleted(@RequestParam Optional<String> provider) {
        List<ProviderPackageDeletedDTO> listProviderPackageDeleted;
        if(provider.isPresent()) {
            listProviderPackageDeleted = providerPackageDeletedService.getProviderPackageDeletedDTOListByProvider(provider.get());
        }else {
            listProviderPackageDeleted = providerPackageDeletedService.getProviderPackageDeletedDTOList();
        }
        return listProviderPackageDeleted;
    }


}
