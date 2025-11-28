package fr.abes.baconprovider.utils;

import fr.abes.baconprovider.entity.Provider;
import jakarta.annotation.PostConstruct;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {
    private final UtilsMapper mapper;

    public ProviderMapper(UtilsMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void converterTableauStringToProvider(){
        Converter<String[], Provider> myConverter = new Converter<String[], Provider>() {
            public Provider convert(MappingContext<String[], Provider> mappingContext) {
                String[] ligneCsv = mappingContext.getSource();
                for (int i = 0; i < ligneCsv.length-1; i++) {
                    if(ligneCsv[i].isEmpty()){
                        ligneCsv[i] = null;
                    }
                }
                Provider provider = new Provider();
                if (ligneCsv[0] != null)
                    provider.setIdtProvider(Integer.parseInt(ligneCsv[0]));
                provider.setProvider(ligneCsv[1]);
                provider.setNomContact(ligneCsv[2]);
                provider.setPrenomContact(ligneCsv[3]);
                provider.setMailContact(ligneCsv[4]);
                provider.setDisplayName(ligneCsv[5]);
                return provider;
            }
        };
        mapper.addConverter(myConverter);
    }
}
