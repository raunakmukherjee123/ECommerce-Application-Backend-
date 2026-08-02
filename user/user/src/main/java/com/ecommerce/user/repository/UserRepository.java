package com.ecommerce.user.repository;


import com.ecommerce.user.models.User;
import com.ecommerce.user.projections.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByFirstName(String username);

//    @Query(value = """
//            SELECT
//                    u.password AS password,
//                    u.firstName AS firstName,
//                    u.lastName AS lastName,
//                    u.email AS email,
//                    u.phone AS phone,
//                    u.role AS role
//                FROM User u
//                WHERE u.id = :id
//            """)
//    UserProjection findUserById(@Param("id") Long id);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    UserProjection findUserById(@Param("id") Long id);
}
