package com.socialLink.Repositories;

import com.socialLink.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    UserModel getUserByEmail(String userEmail);
}
