package ch.frox.pizzaprocess.main.java.domain.image;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;
import java.util.TreeSet;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.system.EntityNotFoundException;
import ch.frox.pizzaprocess.main.java.core.exception.system.TechnicalException;
import ch.frox.pizzaprocess.main.java.core.exception.user.ValidationException;
import ch.frox.pizzaprocess.main.java.core.validation.Validater;
import ch.ivyteam.ivy.environment.Ivy;




// NOTE: images are special compared to the other entities, thats why ImageService does not extend GenericService
public class ImageService {
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = new TreeSet<>(Set.of("jpg", "jpeg", "png", "gif", "webp"));
    private static final int MAX_BYTES = 2 * 1024 * 1024;
    private final ImageRepository repository;

    public ImageService() {
        repository = Registry.get(ImageRepository.class);
    }
    


    public Image getById(String id) {
        Image image = repository.findById(id);
        if (image == null) throw new EntityNotFoundException(Image.class, id);
        return image;
    }

    public Image save(byte[] data, String fileExtension) {
        if (data == null || data.length == 0) {
            throw new ValidationException("image", "The picture is empty.");
        }
        if (data.length > MAX_BYTES) {
            throw new ValidationException("image", "The picture can be 2 MB at most.");
        }
        String extension = fileExtension == null ? "" : fileExtension.toLowerCase();
        if (!ALLOWED_FILE_EXTENSIONS.contains(extension)) {
            throw new ValidationException("image", "'" + fileExtension + "' pictures are not supported. Use one of: " + String.join(", ", ALLOWED_FILE_EXTENSIONS) + ".");
        }

        String id = sha256Of(data);
        Image existing = repository.findById(id);
        if (existing != null) {
            Ivy.log().trace("image '" + id + "." + extension + "' has already been stored, the existing one gets reused");
            return existing;
        }

        Image image = new Image();
        image.setId(id);
        image.setFileExtension(extension);
        image.setData(data);
        Validater.of(image).throwIfAny();
        return repository.save(image);
    }

    public void deleteIfUnused(Image image) {
        long usages = repository.countUsages(image.getId());
        if (usages > 0) {
            Ivy.log().trace("image " + image.getId() + " is still used " + usages + " times, it won't be deleted");
            return;
        }
        Image current = repository.findById(image.getId());
        if (current == null) return;
        repository.delete(current);
        Ivy.log().trace("image " + image.getId() + " was unused and got deleted");
    }



    // |----- helper methods -----|

    private static String sha256Of(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(messageDigest.digest(data));
        } catch (NoSuchAlgorithmException ex) {
            throw new TechnicalException("the jvm does not provide 'SHA-256', images cannot get an id", ex);
        }
    }
}