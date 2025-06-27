package fr.abes.baconprovider.utils;


import fr.abes.baconprovider.entity.ProviderPackageDeleted;
import fr.abes.baconprovider.dto.ProviderPackageDeletedDTO;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WebDtoMapper {

    private final UtilsMapper mapper;

    public WebDtoMapper(UtilsMapper mapper) {
        this.mapper = mapper;
    }

    @Bean
    public void converterProviderPackageDeletedToProviderPackageDeletedDTO() {
        Converter<ProviderPackageDeleted, ProviderPackageDeletedDTO> myConverter = new Converter<ProviderPackageDeleted, ProviderPackageDeletedDTO>() {
            public ProviderPackageDeletedDTO convert(MappingContext<ProviderPackageDeleted, ProviderPackageDeletedDTO> context) {
                ProviderPackageDeleted source = context.getSource();
                return new ProviderPackageDeletedDTO(source.getPackageField(),  source.getProvider(), (source.getDateS() == null ) ? "null" : source.getDateS().toString());
            }
        };
        mapper.addConverter(myConverter);
    }
}