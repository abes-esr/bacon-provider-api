package fr.abes.baconprovider.repository;

import fr.abes.baconprovider.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByProvider(String provider);

    List<Provider> findAllByOrderByProvider();
}
