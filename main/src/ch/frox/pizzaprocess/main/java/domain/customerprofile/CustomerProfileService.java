package ch.frox.pizzaprocess.main.java.domain.customerprofile;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericService;
import ch.ivyteam.ivy.security.IUser;



public class CustomerProfileService extends GenericService<CustomerProfile, UUID, CustomerProfileRepository> {

    public CustomerProfile getByIUser(IUser user) {
        return repository.findByCustomerReference(user.getSecurityMemberId()).orElseGet(() -> {
            CustomerProfile customerProfile = new CustomerProfile();
            customerProfile.setFullName(user.getFullName());
            customerProfile.setEmail(user.getEMailAddress());
            return customerProfile;
        });
    }

    @Override
    public CustomerProfile save(CustomerProfile customerProfile) {
        String customerReference = customerProfile.getCustomerReference();
        if (customerReference == null) return super.save(customerProfile);

        return repository
            .findByCustomerReference(customerReference)
            .map(( existingCustomerProfile ) -> {
                existingCustomerProfile.pasteIn(customerProfile);
                return super.update(existingCustomerProfile);
            })
            .orElseGet(() -> {
                return super.save(customerProfile);
            });
    }
}