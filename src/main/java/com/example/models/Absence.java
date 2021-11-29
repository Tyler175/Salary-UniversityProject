package com.example.models;

import com.example.views.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Objects;

@Entity
@Table(name = "absences")
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.UI.class)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "workerInfo_user", nullable=false)
    private WorkerInfo workerInfo;

    @JsonView(View.UI.class)
    private Calendar startDate;

    @JsonView(View.UI.class)
    private Calendar endDate;

    @JsonView(View.UI.class)
    private String reason;

    public Absence(){

    }

    public Absence(Long id, WorkerInfo workerInfo, Calendar startDate, Calendar endDate, String reason) {
        this.id = id;
        this.workerInfo = workerInfo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Absence absence = (Absence) o;
        return Objects.equals(id, absence.id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkerInfo getWorkerInfo() {
        return workerInfo;
    }

    public void setWorkerInfo(WorkerInfo workerInfo) {
        this.workerInfo = workerInfo;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
