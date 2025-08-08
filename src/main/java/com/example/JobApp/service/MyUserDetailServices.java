package com.example.JobApp.service;

import com.example.JobApp.model.User;
import com.example.JobApp.model.UserPrincipal;
import com.example.JobApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailServices implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=repo.findByUsername(username);
        if (user==null){
            System.out.println("User 404");
            throw new UsernameNotFoundException("User 204");
        }
        return new UserPrincipal(user);
    }
}
