package com.example.pidev.Service;


import com.example.pidev.Repository.RoleRepository;
import com.example.pidev.Repository.UserRepository;
import com.example.pidev.entity.Gender;
import com.example.pidev.entity.Role1;
import com.example.pidev.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ServletContext context;

    @Autowired
    private JavaMailSender javaMailSender;

    public void initRolesAndUser(){
        // Role adminRole=new Role();
        // adminRole.setRoleName("Admin");
        //adminRole.setRoleDescription("Admin role");
        //roleRepository.save(adminRole);

        // Role etudiantRole=new Role();
        //etudiantRole.setRoleName("Project manager");
        //etudiantRole.setRoleDescription("Project manager role");
        //roleRepository.save(etudiantRole);

        //Ajout de l'admin dans la base
        User adminUser = new User();
        adminUser.setFileName("yahyacv.jpg");
        adminUser.setPrenom("yahya");
        adminUser.setNom("jday");
        adminUser.setEmail("mohamedyahya.jday@esprit.tn");
        adminUser.setPassword(getEncodedPassword("yahya"));
        adminUser.setUserName("yahyajday");
        adminUser.setGender(Gender.MALE);
        adminUser.setPhoneNumber(51809395);
        adminUser.setRole1(Role1.Admin);
        //Set<Role> adminRoles = new HashSet<>();
        //adminRoles.add(adminRole);
        //adminUser.setRole(adminRoles);
        userRepository.save(adminUser);


    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }



    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }


    public ResponseEntity<Map<String, String>> registerNewUser(User user) throws JsonProcessingException {
        try {

            // Role role = roleRepository.findById("Admin").get();
            String token = RandomString.make(30);
            user.setToken(token);
            //Set<Role> userRoles = new HashSet<>();
            //userRoles.add(role);
            //user.setRole(userRoles);
            user.setPassword(getEncodedPassword(user.getPassword()));
            User savedUser = userRepository.save(user);

            if (savedUser != null) {
                // Send activation email to user
                String activationLink = "http://localhost:8090" + "/activate/" + token;
                String emailSubject = "Activate Your Account";
                String emailBody = "Dear " + savedUser.getUserName() + ",<br><br>" +
                        "Please click on the following link to activate your account:<br><br>" +
                        "<a href=\"" + activationLink + "\">" + activationLink + "</a><br><br>" +
                        "Best regards,<br>The Cloudestino Team";

                sendEmail(savedUser.getEmail(), emailSubject, emailBody);

                Map<String, String> response = new HashMap<>();
                response.put("message", "ok");
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // count all users with role admin
        //long count = userRepository.countAllByRole("Admin");
    }









    public byte[] getPhoto(String userName) throws Exception{
        User user   = userRepository.findById(userName).get();
        return Files.readAllBytes(Paths.get(context.getRealPath("/Images/")+user.getFileName()));
    }

    public User updateUser(User user,String userName) {
        User existingUser = userRepository.findById(userName).orElse(null);
        existingUser.setUserName(user.getUserName());
        existingUser.setNom(user.getNom());
        existingUser.setPrenom(user.getPrenom());
        existingUser.setEmail(user.getEmail());
        existingUser.setGender(user.getGender());
        existingUser.setRole(user.getRole());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(existingUser);
    }
    public boolean ifEmailExist(String mail){
        return userRepository.existsByEmail(mail);
    }

    public Optional<User> getUserByMail(String mail){
        return userRepository.findByEmail(mail);
    }



    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public String deleteUser(String userName) {
        userRepository.deleteById(userName);
        return "removed !! " + userName;
    }


    public User GetUserByUsername(String userName){
        return  userRepository.findById(userName).get();
    }



    public String updateToken(String token) throws UsernameNotFoundException {
        User user = userRepository.findByToken(token);
        if (user != null) {
            user.setToken(null);
            userRepository.save(user);
            return null;
        } else {
            throw new UsernameNotFoundException("Could not find any User with the token");
        }
    }



    public void updatePassword(User user, String newPassword) {

        user.setPassword(getEncodedPassword(newPassword));

        userRepository.save(user);
    }


    // webcam for fraud detection














}

