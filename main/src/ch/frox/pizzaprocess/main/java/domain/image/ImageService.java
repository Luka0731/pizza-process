package ch.frox.pizzaprocess.main.java.domain.image;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.EntityNotFoundException;
import ch.frox.pizzaprocess.main.java.core.exception.TechnicalException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException.Violation;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;
import ch.ivyteam.ivy.environment.Ivy;



// images are speical data compared ot other, thats the reason for ImageService not inheriting GenericService and taking in pure data in its save method
public class ImageService {
    private final ImageRepository repository;

    public ImageService() {
        repository = Registry.get(ImageRepository.class);
    }


    
    public Image getById(String id) {
        Image entity = repository.findById(id);
        if (entity == null) throw new EntityNotFoundException(Image.class, id);
        return repository.findById(id);
    }

    public Image save(byte[] data, String fileExtension) {
        // data inside check
        if (data == null || data.length == 0) {
            throw new ValidationException(new Violation("data", "the image has no data"));
        }
        // image with same data allready in check
        String id = encodeToSha256(data);
        if (repository.existsById(id)) {
            Ivy.log().trace("image '" + id + "." + fileExtension + "' has allready been stored. existing image will get reused");
            return repository.findById(id);
        }
        // creat image entity
        Image image = new Image();
        image.setId(id);
        image.setFileExtension(fileExtension);
        image.setData(data);
        // validator
        ValidationUtil.validateElseThrow(image);
        // save image in db
        return repository.save(image);
    }

    public void deleteByIdIfUnused(String id) {    
        long usages = repository.countUsages(id);
        
        if (usages > 0) {
            Ivy.log().trace("image " + id + " is still used " + usages + " times, it wont be deleted");
            return;
        }
        
        repository.deleteById(id);
        Ivy.log().trace("image " + id + " was unused deleted");
    }



    // |--- hellper methods ---|

    private String encodeToSha256(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(messageDigest.digest(data));
        } catch (NoSuchAlgorithmException ex) {
            throw new TechnicalException("the jvm does not provide 'SHA-256', images cannot get an id", ex);
        }
    }
}