package com.example.controllers;

import com.example.models.Award;
import com.example.models.Position;
import com.example.models.PremiumBudget;
import com.example.models.User;
import com.example.repository.AwardRepository;
import com.example.repository.BudgetRepository;
import com.example.repository.PositionRepository;
import com.example.repository.UserRepository;
import com.example.views.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/salary")
public class SalaryController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BudgetRepository budgetRepository;

    @Autowired
    AwardRepository awardRepository;

    @GetMapping(params = "month")
    public List<UserDTO> getPositions(@RequestParam("month") String stringMonth) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(stringMonth.replaceAll("Z$", "+0000")));
        Date month = cal.getTime();

        LocalDate date = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 1);

        List<UserDTO> result = new ArrayList<>();
        int index = 0;
        List<User> workers = userRepository.findAllByWorkerInfoIsNotNull();
        workers.forEach(item->{
                    result.add(index, new UserDTO());
                    UserDTO el = result.get(index);
                    BeanUtils.copyProperties(item, el);
                    el.setAbsenceDays(item.getWorkerInfo().missedDays(month));
                    el.setName(item.getLastName()+' '+item.getFirstName()+' ' + item.getPatronymic());
                    el.setPosition(item.getWorkerInfo().getPosition().getName());
                    el.setSalary(item.getWorkerInfo().calculateSalary(month));
                    el.setAward(item.getWorkerInfo().getAwards().stream()
                            .filter(i -> LocalDate.of(i.getMonth().get(Calendar.YEAR),i.getMonth().get(Calendar.MONTH)+1,1).equals(date))
                            .findAny().orElse(new Award()));
                });


        return result;
    }

    @GetMapping(value = "/budget",params = "month")
    public PremiumBudget getBudget(@RequestParam("month") String stringMonth) throws ParseException{
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(stringMonth.replaceAll("Z$", "+0000")));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        budgetRepository.findAll();


        return budgetRepository.findByMonth(cal).orElse(new PremiumBudget());
    }

    @PutMapping("/awards")
    public Award putAward (@RequestBody UserDTO userDTO){

        User userFromDb = userRepository.findById(userDTO.getId()).orElse(new User());
        if (userDTO.getAward().getId()!=null){
            System.out.println("1");
            Award awardFromDb = awardRepository.findById(userDTO.getAward().getId()).orElse(new Award());
            BeanUtils.copyProperties(userDTO.getAward(), awardFromDb, "workerInfo");

            return awardRepository.save(awardFromDb);
        }
        System.out.println("2");
        userDTO.getAward().setWorkerInfo(userFromDb.getWorkerInfo());
        return awardRepository.save(userDTO.getAward());
    }
}
