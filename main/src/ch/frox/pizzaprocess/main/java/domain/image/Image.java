package ch.frox.pizzaprocess.main.java.domain.image;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name = "image")
@Getter @Setter
public class Image extends GenericEntity<String> {
    @NotBlank
    @Size(max = 7)
    @Column(name = "file_extension", length = 7, nullable = false)
    private String fileExtension;

    @NotEmpty 
    @Column(name = "data", nullable = false, columnDefinition = "bytea")
    private byte[] data;
}