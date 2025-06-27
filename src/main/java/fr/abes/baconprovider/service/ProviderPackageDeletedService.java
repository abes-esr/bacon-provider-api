package fr.abes.baconprovider.service;

import fr.abes.baconprovider.entity.ProviderPackageDeleted;
import fr.abes.baconprovider.dto.ProviderPackageDeletedDTO;
import fr.abes.baconprovider.repository.ProviderPackageDeletedRepository;
import fr.abes.baconprovider.utils.UtilsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProviderPackageDeletedService {
    private final UtilsMapper webDtoMapper;
    private final ProviderPackageDeletedRepository dao;

    public ProviderPackageDeletedService(ProviderPackageDeletedRepository dao,
                                         UtilsMapper webDtoMapper) {
        this.dao = dao;
        this.webDtoMapper = webDtoMapper;
    }

    public List<ProviderPackageDeletedDTO> getProviderPackageDeletedDTOList() {
        List<ProviderPackageDeleted> providerPackageDeletedList = dao.findAllByOrderByDateSDesc();
        return webDtoMapper.mapList(providerPackageDeletedList, ProviderPackageDeletedDTO.class);
    }

    public List<ProviderPackageDeletedDTO> getProviderPackageDeletedDTOListByProvider(String provider) {
        List<ProviderPackageDeleted> providerPackageDeletedList = dao.findAllByProviderOrderByDateSDesc(provider.toUpperCase());
        return webDtoMapper.mapList(providerPackageDeletedList, ProviderPackageDeletedDTO.class);
    }
}
