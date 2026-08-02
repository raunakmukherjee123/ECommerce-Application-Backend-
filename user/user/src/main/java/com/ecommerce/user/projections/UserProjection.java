package com.ecommerce.user.projections;

import com.ecommerce.user.enums.UserRole;

public interface UserProjection {
    public String getPassword();
    public String getFirstName();
    public String getLastName();
    public String getEmail();
    public String getPhone();
    public UserRole getRole();
    public AddressProjection getAddress();
}
