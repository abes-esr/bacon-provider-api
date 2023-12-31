package fr.abes.baconprovider.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "PROVIDER")
@Getter
@Setter
@NoArgsConstructor
public class Provider implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-provider")
    @SequenceGenerator(name = "seq-provider", sequenceName = "PROVIDER_SEQ", allocationSize = 1)
    @Column(name = "IDT_PROVIDER")
    private Integer idtProvider;
    @Column(name = "PROVIDER")
    private String provider;
    @Column(name = "NOM_CONTACT")
    private String nomContact;
    @Column(name = "PRENOM_CONTACT")
    private String prenomContact;
    @Column(name = "MAIL_CONTACT")
    private String mailContact;
    @Column(name = "DISPLAY_NAME")
    private String displayName;


    public Provider(String provider) {
        this.provider = provider;
        //on ne connait pas le display name à l'avance, on l'initialise au provider pour éviter une erreur not null dans la table
        this.displayName = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equals(idtProvider, provider.idtProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idtProvider);
    }

    @Override
    public String toString() {
        return this.idtProvider + " / " + this.provider;
    }

}
