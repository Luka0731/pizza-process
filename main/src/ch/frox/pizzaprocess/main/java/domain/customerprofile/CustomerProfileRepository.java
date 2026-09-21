package ch.frox.pizzaprocess.main.java.domain.customerprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericRepository;



public class CustomerProfileRepository extends GenericRepository<CustomerProfile, UUID> {

    public Optional<CustomerProfile> findByCustomerReference(String customerReference) {
        List<CustomerProfile> customerProfiles = entityManager
            .createQuery("SELECT c FROM CustomerProfile c WHERE c.customerReference = :customerReference")
            .setParameter("customerReference", customerReference)
            .getResults();
        return customerProfiles.stream().findFirst();
    }
}