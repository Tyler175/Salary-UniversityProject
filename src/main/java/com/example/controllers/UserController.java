package com.example.controllers;

import com.example.models.Absence;
import com.example.models.Position;
import com.example.models.User;
import com.example.models.WorkerInfo;
import com.example.repository.AbsenceRepository;
import com.example.repository.UserRepository;
import com.example.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @JsonView(View.UI.class)
    @GetMapping
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @JsonView(View.UI.class)
    @GetMapping(params = "name")
    public List<User> getUsers(@RequestParam("name") String name){
        if (name.isEmpty()) return new ArrayList<>();
        return userRepository.findAllByFirstNameStartingWithOrLastNameStartingWithOrPatronymicStartingWith(name, name, name);
    }

    @JsonView(View.UI.class)
    @GetMapping("{id}")
    public User getOne(@PathVariable("id") User user) {
        return user;
    }


    @PutMapping("{id}")
    public User putUser(@PathVariable("id") User userFromDb, @RequestBody User user){

        BeanUtils.copyProperties(user, userFromDb,"id", "password", "workerInfo");
        if (user.getWorkerInfo() != null){
            BeanUtils.copyProperties(user.getWorkerInfo(), userFromDb.getWorkerInfo(), "id", "user","absences");
            if (user.getWorkerInfo().getAbsences() != null) {
                user.getWorkerInfo().getAbsences().forEach(i -> i.setWorkerInfo(userFromDb.getWorkerInfo()));
                absenceRepository.saveAll(user.getWorkerInfo().getAbsences());
            }
        }

        return userRepository.save(userFromDb);
    }

    @PostMapping
    public User addWorker(@RequestBody User user) {
        return userRepository.save(user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") User user) {
        userRepository.delete(user);
    }


    //CHANGE USER PASSWORD
    @PutMapping("{id}/{password}")
    public Boolean changePassword(@PathVariable("id") User userFromDb, @PathVariable("password") String password, @RequestBody User user){
        if (new BCryptPasswordEncoder().matches(password, userFromDb.getPassword())){
            userFromDb.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userRepository.save(userFromDb);
            return true;
        }
        return false;
    }

    //UPLOAD PROFILE PICTURE
    @PostMapping("/upload/{id}")
    public User putProfilePicture(@PathVariable("id") User userFromDb, @RequestBody MultipartFile profilePicture) throws IOException {

        if(profilePicture != null && !profilePicture.getOriginalFilename().isEmpty()){
            File uploadFolder = new File(uploadPath);
            if (!uploadFolder.exists()){
                uploadFolder.mkdir();
            }
            String resultFileName = UUID.randomUUID().toString() + '.' + profilePicture.getOriginalFilename();
            profilePicture.transferTo(new File(uploadPath + '/'+ resultFileName));

            File oldImage = new File(uploadPath + '/'+ userFromDb.getProfilePicture());

            oldImage.delete();
            userFromDb.setProfilePicture(resultFileName);
        }

        return userRepository.save(userFromDb);
    }

    //ABSENCES HANDLER
    private boolean isAbsenceBetweenDates(Date startDate, Date endDate, Absence absence){
        return !absence.getEndDate().getTime().before(startDate)  && !absence.getStartDate().getTime().after(endDate);

    }

    @GetMapping("/absences")
    public Set<User> getAbsences(@RequestParam(value="userId", required=false) User userFromDb, @RequestParam(value = "position", required=false) Position positionFromDb, @RequestParam("startDate") String stringStartDate, @RequestParam("endDate") String stringEndDate) throws ParseException {



        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(stringStartDate.replaceAll("Z$", "+0000")));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date startDate = cal.getTime();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(stringEndDate.replaceAll("Z$", "+0000")));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();


        Set<User> result = new HashSet<>();
        if (userFromDb != null && userFromDb.getWorkerInfo() != null) result.add(userFromDb);
        else result = new HashSet<>(userRepository.findAllByWorkerInfoIsNotNull());

        //you shouldn't iterate result list three times, rewrite pls
        if (positionFromDb != null) result = result.stream().filter(i -> i.getWorkerInfo().getPosition().equals(positionFromDb)).collect(Collectors.toSet());



        result.forEach(user -> {
            user.getWorkerInfo().setAbsences(user.getWorkerInfo().getAbsences().stream().filter(i -> isAbsenceBetweenDates(startDate, endDate, i)).collect(Collectors.toSet()));
        });

        result = result.stream().filter(i -> i.getWorkerInfo().getAbsences().size()>0).collect(Collectors.toSet());

        return result;
    }

    @DeleteMapping("/absences/{id}")
    public void deleteAbsence(@PathVariable("id") Absence absence) {
        absenceRepository.delete(absence);
    }

}
