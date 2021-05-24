package com.shambonik.meat.services;

import com.shambonik.meat.dto.ChangeRole;
import com.shambonik.meat.models.Order;
import com.shambonik.meat.models.Role;
import com.shambonik.meat.models.User;
import com.shambonik.meat.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    public List<User> getUsers(){
        return userRepo.findAll();
    }

    public User getUserById(long id){
        return userRepo.findById(id);
    }

    public String changeUserRole(User currentUser, long id, ChangeRole role){
        if(currentUser.getId()!=id) {
            User user = userRepo.findById(id);
            user.setRoles(Collections.singleton(role.getRole()));
            userRepo.save(user);
            for (Object principal : sessionRegistry.getAllPrincipals()) {
                if (principal instanceof User) {
                    User userPrincipal = (User) principal;
                    if (userPrincipal.getUsername().equals(user.getUsername())) {
                        for (SessionInformation information : sessionRegistry.getAllSessions(userPrincipal, true)) {
                            information.expireNow();
                        }
                    }
                }
            }
        }
        return "redirect:/admin/users";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public void saveUser(User originalUser, User user){
        originalUser.setData(user.getName(), user.getAddress(), user.getEmail(), user.getPhone());
        userRepo.save(originalUser);
    }

    public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if (userFromDb != null) {
            return false;
        }
        user.setActive(true);
        if(user.getAdmin()!= null && user.getAdmin().equals("true"))
            user.setRoles(Collections.singleton(Role.ADMIN));
        else
            user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return true;
    }
}
