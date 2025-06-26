package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public Profile getProfile(Principal principal){
       try {
           // get the currently logged in username
           String userName = principal.getName();
           // find database user by userId
           User user = userDao.getByUserName(userName);
           if (user == null){
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
           }
           int userId = user.getId();

           Profile profile = profileDao.getProfileByUserId(userId);
           if (profile == null){
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opp ..... our bad");
           }
           return profile;
       }catch (Exception e){
           System.err.println("Error getting user profile: " + e.getMessage());
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
       }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile createProfile(@RequestBody Profile profile, Principal principal){
        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            if (user == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
            }
            int userId = user.getId();

//           ensuring the profile is created for the authenticated user
            profile.setUserId(userId);

//           checking the profile already exist
            Profile existingProfile = profileDao.getProfileByUserId(userId);
            if (existingProfile != null){
                throw  new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists.");
            }
            Profile createdProfile = profileDao.create(profile);
            return createdProfile;

        }catch (Exception e){
            System.err.println("Error updating profile: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad. Could not add profile.");
        }
    }

    @PutMapping
    public void updateProfile(@RequestBody Profile profile, Principal principal){
       try {
           // get the currently logged in username
           String userName = principal.getName();
           // find database user by userId
           User user = userDao.getByUserName(userName);
           if (user == null){
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found for authenticated principal.");
           }
           int userId = user.getId();
           profile.setUserId(userId); //making sure the logged user is the updated user
//        checking if anything exist in profile
           Profile existingProfile = profileDao.getProfileByUserId(userId);
           if (existingProfile == null){
               throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "User Profile Empty");
           }
           profileDao.updateProfile(profile);
       }catch (Exception e){
           System.err.println("Error updating user profile: " + e.getMessage());
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
       }
    }
}
