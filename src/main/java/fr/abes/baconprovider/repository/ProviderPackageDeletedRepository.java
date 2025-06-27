package fr.abes.baconprovider.repository;

import fr.abes.baconprovider.entity.ProviderPackageDeleted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderPackageDeletedRepository extends JpaRepository<ProviderPackageDeleted, Long> {
    List<ProviderPackageDeleted> findAllByOrderByDateSDesc();

    List<ProviderPackageDeleted> findAllByProviderOrderByDateSDesc(String provider);
}
