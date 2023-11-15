package fr.abes.baconprovider.configuration;

public class Constants {

    public static final String FILE_EXCEPTION_WRONG_EXTENSION = "L'extension du fichier doit être .csv";
    public static final String FILE_EXCEPTION_MISSING_HEADER = "Une ligne d'en-tête doit être présente dans le fichier";
    public static final String FILE_EXCEPTION_MISSING_COLUMN = "La colonne %s n'est pas présente dans l'en-tête";
    public static final String FILE_EXCEPTION_WRONG_NB_COLUMN = "Une des lignes du fichier ne contient le même nombre de colonnes que la table ";
    public static final String FILE_EXCEPTION_ERROR_READ = "Impossible de lire le fichier";

    public static final char SEPARATOR = ';';
}
