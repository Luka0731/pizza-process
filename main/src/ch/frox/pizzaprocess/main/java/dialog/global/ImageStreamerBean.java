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



@Named("imageStreamerBean")
@ApplicationScoped
public class ImageStreamerBean {
    private final ImageService imageService = Registry.get(ImageService.class);



    // |----- actions -----|

    public StreamedContent getImage() {
        FacesContext faces = FacesContext.getCurrentInstance();
        if (faces.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) return emptyImage();

        String imageId = faces.getExternalContext().getRequestParameterMap().get("imageId");
        if (imageId == null) return emptyImage();

        Image image = imageService.getById(imageId);
        byte[] data = image.getData();
        return DefaultStreamedContent
            .builder()
            .contentType(mimeTypeOf(image.getFileExtension()))
            .stream(() -> new ByteArrayInputStream(data))
            .build();
    }



    // |----- helper methods -----|

    private static String mimeTypeOf(String fileExtension) {
        if (fileExtension == null) {
            return "application/octet-stream";
        }
        return switch (fileExtension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }

    private static StreamedContent emptyImage() {
        return DefaultStreamedContent
            .builder()
            .stream(() -> new ByteArrayInputStream(new byte[0]))
            .build();
    }
}