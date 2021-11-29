package com.example.controllers;

import com.example.models.ERole;
import com.example.models.Role;
import com.example.models.User;
import com.example.models.WorkerInfo;
import com.example.payload.request.LoginRequest;
import com.example.payload.request.SignupRequest;
import com.example.payload.response.JwtResponse;
import com.example.payload.response.MessageResponse;
import com.example.repository.PositionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.repository.WorkerInfoRepository;
import com.example.security.jwt.JwtUtils;
import com.example.security.services.UserDetailsImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	WorkerInfoRepository workerInfoRepository;

	@Autowired
	PositionRepository positionRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
												 userDetails.getId(),
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User signUpRequest) {
		int i = 1;
		for (ERole role: ERole.values()) {
			if (!roleRepository.existsByName(role)){

				Role newRole = new Role(role);
				newRole.setId(i);
				roleRepository.save(newRole);

				i+=1;
			}
		}




		if (userRepository.existsByEmailIgnoreCase(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Такой email уже используется!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		BeanUtils.copyProperties(signUpRequest, user, "password", "email", "roles", "workerInfo");

		Set<Role> roles = new HashSet<>();

		for (ERole role: ERole.values()) {
			if (signUpRequest.getRoles().stream().anyMatch(item -> item.getName().equals(role))){
				roles.add(roleRepository.findByName(role)
						.orElseThrow(() -> new RuntimeException("Ошибка: Роль не найдена.")));
			}
		}



//		add user with admin-role if database doesnt contain any users
//
//		System.out.println(userRepository.isTableEmpty());
//		if (userRepository.isTableEmpty() == 0) {
//			Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//					.orElseThrow(() -> new RuntimeException("Ошибка: Роль не найдена."));
//			roles.add(adminRole);
//		}
		user.setRoles(roles);

		userRepository.save(user);
		if (user.hasRole("ROLE_USER") && !user.hasRole("ROLE_ACCOUNTANT")){
			WorkerInfo workerInfo = new WorkerInfo();
			BeanUtils.copyProperties(signUpRequest.getWorkerInfo(), workerInfo);
			workerInfo.setUser(user);



			user.setWorkerInfo(workerInfoRepository.save(workerInfo));
			userRepository.save(user);
		}

		return ResponseEntity.ok(user);
	}
}
