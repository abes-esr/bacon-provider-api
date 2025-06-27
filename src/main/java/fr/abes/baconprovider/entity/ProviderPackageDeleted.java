package fr.abes.baconprovider.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "PROVIDER_PACKAGE_DELETED")
public class ProviderPackageDeleted {
    @Id
    @Column(name = "ID_PROVIDER_PACKAGE_DELETED", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "\"PACKAGE\"", length = 100)
    private String packageField;

    @Size(max = 200)
    @Column(name = "PROVIDER", length = 200)
    private String provider;

    @ColumnDefault("null")
    @Column(name = "DATE_S")
    private LocalDate dateS;

}