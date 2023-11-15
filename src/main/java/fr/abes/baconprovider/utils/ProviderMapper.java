package fr.abes.baconprovider.utils;

import fr.abes.baconprovider.entity.Provider;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProviderMapper {
    private final UtilsMapper mapper;

    public ProviderMapper(UtilsMapper mapper) {
        this.mapper = mapper;
    }

    @Bean
    public void converterTableauStringToProvider(){
        Converter<String[], Provider> myConverter = new Converter<String[], Provider>() {
            @Override
            public Provider convert(MappingContext<String[], Provider> mappingContext) {
                String[] ligneCsv = mappingContext.getSource();
                for (int i = 0; i < ligneCsv.length-1; i++) {
                    if(ligneCsv[i].equals("")){
                        ligneCsv[i] = null;
                    }
                }
                Provider provider = new Provider();
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
