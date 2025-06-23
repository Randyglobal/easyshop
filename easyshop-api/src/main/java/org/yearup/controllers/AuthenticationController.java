package org.yearup.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.authentication.LoginDto;
import org.yearup.models.authentication.LoginResponseDto;
import org.yearup.models.authentication.RegisterUserDto;
import org.yearup.models.User;
import org.yearup.models.authentication.UserDto;
import org.yearup.security.jwt.JWTFilter;
import org.yearup.security.jwt.TokenProvider;

@RestController
@CrossOrigin
//@PreAuthorize("permitAll()")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private UserDao userDao;
    private ProfileDao profileDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, PasswordEncoder passwordEncoder, UserDao userDao, ProfileDao profileDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        try
        {
            User user = userDao.getByUserName(loginDto.getUsername());

            if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return new ResponseEntity<>(new LoginResponseDto(jwt, user), httpHeaders, HttpStatus.OK);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterUserDto newUser) {

        try
        {
            boolean exists = userDao.exists(newUser.getUsername());
            if (exists)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exists.");
            }
//            encrypting password before sending
//            String hashedPassword = passwordEncoder.encode(newUser.getPassword());

            // create user
            User user = userDao.create(new User(0, newUser.getUsername(), newUser.getPassword(), newUser.getRole()));

            // create profile
            Profile profile = new Profile();
            profile.setUserId(user.getId());
            profileDao.create(profile);

            UserDto responseUserDto = new UserDto(user.getId(), user.getUsername(), user.getRole());
            return new ResponseEntity<>(responseUserDto, HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            System.err.println("Registration error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

}

