package de.conti.tires.mandi.container.util;

import de.conti.tires.mandi.backend.user.UserEntity;
import de.conti.tires.mandi.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

//    public String loggedInEmail(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByUserName(authentication.getName())
//                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
//
//        return user.getEmail();
//    }

    public UUID loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getUuid();
    }

    public UserEntity loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user;

    }


}