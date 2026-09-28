package ch.frox.pizzaprocess.main.java.dialog.global;

import java.io.ByteArrayInputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import jakarta.inject.Named;



@Named("imageBean")
@ApplicationScoped
public class ImageBean {
    private static final ImageService imageService = Registry.get(ImageService.class);



    public StreamedContent getImage() {
        FacesContext faces = FacesContext.getCurrentInstance();
        if (faces.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) return emptyImage();

        String imageId = faces.getExternalContext().getRequestParameterMap().get("imageId");
        if (imageId == null || imageId.isBlank()) return emptyImage();

        Image image = imageService.getById(imageId);

        byte[] data = image.getData();
        return DefaultStreamedContent
            .builder()
            .contentType(image.getFileExtension())
            .stream(() -> new ByteArrayInputStream(data))
            .build();
    }



    // |----- helper methods -----|

    private static StreamedContent emptyImage() {
        return DefaultStreamedContent
            .builder()
            .stream(() -> new ByteArrayInputStream(new byte[0]))
            .build();
    }
}