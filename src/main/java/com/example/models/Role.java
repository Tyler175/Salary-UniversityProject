package com.example.models;

import com.example.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(View.UI.class)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	@JsonView(View.UI.class)
	private ERole name;

	public Role() {

	}

	public Role(ERole name) {
		this.name = name;
	}

	public Role(String name) {
		this.name = ERole.valueOf(name);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = ERole.valueOf(name);
	}
}