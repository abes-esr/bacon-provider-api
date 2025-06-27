package fr.abes.baconprovider.exception;

import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ExceptionControllerHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<Object> buildResponseEntity(ApiReturnError apiReturnError) {
        return new ResponseEntity<>(apiReturnError, apiReturnError.getStatus());
    }

    /**
     * Erreur dans la validité des paramètres de la requête
     *
     * @param ex : l'exception catchée
     * @return l'objet du message d'erreur
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        String error = "Erreur dans les paramètres de la requête";
        log.debug(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(FileException.class)
    protected ResponseEntity<Object> handleFileException(FileException ex) {
        String error = "Erreur dans le fichier en entrée";
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(IllegalDatabaseOperation.class)
    protected ResponseEntity<Object> handleIllegalDatabaseOperationException(IllegalDatabaseOperation ex) {
        String error = "Erreur de sauvegarde dans la base de données";
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler({IOException.class, CsvException.class, IllegalAccessException.class})
    protected ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        String error = "Erreur inconnu, merci de contacter les administrateurs ";
        ex.printStackTrace();
        return buildResponseEntity(new ApiReturnError(HttpStatus.I_AM_A_TEAPOT, error, ex));
    }
}
