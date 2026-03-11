package com.ecommerce.user.controller;


import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

private final UserService userService;
//private static Logger logger= LoggerFactory.getLogger(UserController.class);



    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest)
    {
       return ResponseEntity.ok(userService.addUser(userRequest));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers()
    {
        return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id,@RequestHeader("loggedInUser") String username)
    {
//        logger.info("Fetching user for id: {}",id);
         log.info("Fetching user for id: {}",id);

        System.out.println("Username is "+username);

        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestBody UserRequest userRequest)
    {
        boolean updated=userService.updateUser(id, userRequest);

        if(updated)
        {
            return ResponseEntity.ok("User updated");
        }
        return ResponseEntity.notFound().build();
    }
}
