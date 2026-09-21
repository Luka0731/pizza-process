package ch.frox.pizzaprocess.main.java.domain.customerprofile;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericService;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;
import ch.frox.pizzaprocess.main.java.core.validationgroup.OnAuthentication;
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
        if (customerReference == null) {
            return super.save(customerProfile);
        }

        CustomerProfile loggedInCustomerProfile = repository.findByCustomerReference(customerReference).orElse(customerProfile);
        ValidationUtil.validateElseThrow(loggedInCustomerProfile, OnAuthentication.class);
        return loggedInCustomerProfile.getId() == null ? repository.save(loggedInCustomerProfile) : repository.update(loggedInCustomerProfile); // update: ?
    }
}