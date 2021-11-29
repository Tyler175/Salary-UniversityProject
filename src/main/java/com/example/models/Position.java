package com.example.models;

import com.example.views.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "positions")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.UI.class)
    private Long id;

    @JsonView(View.UI.class)
    private String name;

    @JsonView(View.UI.class)
    private float salary;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private Set<WorkerInfo> workerInfos = new HashSet<>();

    public Position(){

    }

    public Position(Long id, String name, float salary, Set<WorkerInfo> workerInfos) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.workerInfos = workerInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(id, position.id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<WorkerInfo> getWorkerInfos() {
        return workerInfos;
    }

    public void setWorkerInfos(Set<WorkerInfo> workerInfos) {
        this.workerInfos = workerInfos;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }
}
