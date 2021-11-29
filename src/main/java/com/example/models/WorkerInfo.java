package com.example.models;

import com.example.views.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.*;

@Entity
@Table(name = "workers_info")
public class WorkerInfo {

    static final Set<DayOfWeek> businessDays = Set.of(
            MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.UI.class)
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "workerInfo")
    private User user;

    @JsonView(View.UI.class)
    private float salary;

    @JsonView(View.UI.class)
    private int maritalStatus;

    @JsonView(View.UI.class)
    private int children;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable=false)
    @JsonView(View.UI.class)
    private Position position;

    @OneToMany(mappedBy = "workerInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Absence> absences;

    @OneToMany(mappedBy = "workerInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Award> awards;

    public WorkerInfo(){

    }

    public WorkerInfo(Long id, User user, float salary, int maritalStatus, int children, Position position, Set<Absence> absences, Set<Award> awards) {
        this.id = id;
        this.user = user;
        this.salary = salary;
        this.maritalStatus = maritalStatus;
        this.children = children;
        this.position = position;
        this.absences = absences;
        this.awards = awards;
    }

    public double calculateSalary(Date month){

        Calendar cal = Calendar.getInstance();
        cal.setTime(month);

        LocalDate startDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 1);
        LocalDate endDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.getActualMaximum(Calendar.DAY_OF_MONTH));



        long dayInMonth = startDate.datesUntil(endDate.plusDays(1))
                .filter(t -> businessDays.contains(t.getDayOfWeek())).count();
        long missedDays = missedDays(month);
       return (position.getSalary()*(dayInMonth-missedDays)+missedDays*position.getSalary()*0.5)/dayInMonth;



    }

    public long missedDays(Date month){

        Calendar cal = Calendar.getInstance();
        cal.setTime(month);

        LocalDate startDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 1);
        LocalDate endDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        long count = 0;
        for (Absence absence:absences
        ) {
            //      |max(A, C) - min(B, D)|

            count += startDate.datesUntil(endDate.plusDays(1))
                    .filter(t -> businessDays.contains(t.getDayOfWeek())
                            && !t.isBefore(LocalDate.of(absence.getStartDate().get(Calendar.YEAR), absence.getStartDate().get(Calendar.MONTH)+1, absence.getStartDate().get(Calendar.DAY_OF_MONTH)))
                            && !t.isAfter(LocalDate.of(absence.getEndDate().get(Calendar.YEAR), absence.getEndDate().get(Calendar.MONTH)+1, absence.getEndDate().get(Calendar.DAY_OF_MONTH)))).count();

//            if (start.before(absence.getEndDate()) && end.after(absence.getStartDate())){
//                count+= Math.abs(Math.max(start.getTimeInMillis(),absence.getStartDate().getTimeInMillis()) - Math.min(end.getTimeInMillis(),absence.getEndDate().getTimeInMillis()));
//
////                if (start.after(absence.getStartDate())) count += Duration.between(LocalDateTime.from(start.toInstant()), LocalDateTime.from(absence.getEndDate().toInstant())).toDays();
////                if (end.before(absence.getEndDate())) count += Duration.between(LocalDateTime.from(start.toInstant()), LocalDateTime.from(absence.getEndDate().toInstant())).toDays();
//            };
        }
        return count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public int getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(int maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Set<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(Set<Absence> absences) {
        this.absences = absences;
    }

    public Set<Award> getAwards() {
        return awards;
    }

    public void setAwards(Set<Award> awards) {
        this.awards = awards;
    }


}
