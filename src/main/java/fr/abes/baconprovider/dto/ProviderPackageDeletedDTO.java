package fr.abes.baconprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProviderPackageDeletedDTO {
//( package, provider, date).
    private String packageName;
    private String provider;
    private String date;

}
