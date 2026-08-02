package com.ecommerce.user.service;


import com.ecommerce.user.dto.AddressDto;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.models.Address;
import com.ecommerce.user.models.User;
import com.ecommerce.user.projections.UserProjection;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
   // private final KeyCloakAdminService keyCloakAdminService;

    public String addUser(UserRequest userRequest)
    {
//        String token= keyCloakAdminService.getAdminAccessToken();
//        String keyCloakUserId=keyCloakAdminService.createUser(token,userRequest);

        User user=new User();

        updateUserFromRequest(user,userRequest);

        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        userRepository.save(user);
        return "User added";

//        System.out.println("Method called");
//
//        User user = new User();
//        updateUserFromRequest(user, userRequest);
//
//        User savedUser = userRepository.save(user);
//
//        System.out.println("Saved user: " + savedUser);
//        System.out.println("Total users: " + userRepository.count());
//
//        return "User added";
    }

    public List<UserResponse> fetchAllUsers()
    {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse fetchUser(Long id) {
        UserProjection userProjection=userRepository.findUserById(id);

        if(userProjection==null)
        {
            throw new UsernameNotFoundException("No user found for id= "+id);
        }

        return mapToUserResponseFromUserProjection(userProjection);

    }

    public boolean updateUser(Long id, UserRequest userRequest)
    {
        return userRepository.findById(String.valueOf(id))
                .map(existingUser ->{
                        updateUserFromRequest(existingUser,userRequest);
                        userRepository.save(existingUser);
                        return true;
                }).orElse(false);
    }

    private UserResponse mapToUserResponse(User user)
    {
        UserResponse userResponse=new UserResponse();

        userResponse.setFirstName(user.getFirstName());
        userResponse.setEmail(user.getEmail());
        userResponse.setLastName(user.getLastName());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());

        Address address=user.getAddress();
        AddressDto addressDto=new AddressDto();

        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setStreet(address.getStreet());
        addressDto.setCountry(address.getCountry());
        addressDto.setZipcode(address.getZipcode());

        userResponse.setAddressDto(addressDto);

        return userResponse;
    }

    private UserResponse mapToUserResponseFromUserProjection(UserProjection userProjection)
    {
        UserResponse userResponse=new UserResponse();

        userResponse.setFirstName(userProjection.getFirstName());
        userResponse.setEmail(userProjection.getEmail());
        userResponse.setLastName(userProjection.getLastName());
        userResponse.setPhone(userProjection.getPhone());
        userResponse.setRole(userProjection.getRole());

        AddressDto addressDto=new AddressDto();

        addressDto.setCity(userProjection.getAddress().getCity());
        addressDto.setState(userProjection.getAddress().getState());
        addressDto.setStreet(userProjection.getAddress().getStreet());
        addressDto.setCountry(userProjection.getAddress().getCountry());
        addressDto.setZipcode(userProjection.getAddress().getZipcode());

        userResponse.setAddressDto(addressDto);

        return userResponse;
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        AddressDto addressDto=userRequest.getAddressDto();
        Address address=new Address();

        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setStreet(addressDto.getStreet());
        address.setCountry(addressDto.getCountry());
        address.setZipcode(addressDto.getZipcode());

        user.setAddress(address);
    }
}
