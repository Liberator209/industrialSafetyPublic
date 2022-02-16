/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Mobile;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.co.abrain.IndustrialSafety.Employee.Employee;
import kr.co.abrain.IndustrialSafety.Employee.EmployeeRepository;
import reactor.core.publisher.Mono;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Service
public class MobileService {
	private EmployeeRepository employeeRepository;
	private JavaMailSender emailSender;
	
	/**
	 * initiate password encoder
	 * @return
	 */
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}
	
	MobileService(EmployeeRepository employeeRepository, JavaMailSender emailSender){
		this.employeeRepository = employeeRepository;
		this.emailSender = emailSender;
	}
	
	/**
	 * Random Password generator
	 * @return
	 */
	public String generateRandomPassword() {
		char[] set = new char[] {
				'0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9', 'A', 'B',
				'C', 'D', 'E', 'F', 'G', 'H',
				'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
				'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
				'!', '@', '#', '$', '%', '^', '&'};
		
		StringBuffer pwd = new StringBuffer();
		SecureRandom pr = new SecureRandom();
		pr.setSeed(new Date().getTime());
		
		int idx = 0;
		int len =set.length;
		
		for (int i=0; i<8; i++) {
			idx=pr.nextInt(len);
			pwd.append(set[idx]);
		}
		
		return pwd.toString();
	}
	
	/**
	 * make Employee object by requested data,
	 * find employee by requested data's employee id,
	 * if exists, error return
	 * or return employee object except password
	 * @param registerData
	 * @return if employee id == exists ? error : saved employee data except password
	 */
	public Mono<Object> register(Register registerData){
		Employee newEmp = new Employee();
		newEmp.setDevice(registerData.getUser_mac_addr());
		newEmp.setEmail(registerData.getUser_email());
		newEmp.setId(registerData.getUser_employee_id());
		newEmp.setLicense(registerData.getWork_type() == 0 ? false : true);
		newEmp.setName(registerData.getUser_name());
		newEmp.setPw(passwordEncoder().encode(registerData.getUser_pw()));
		newEmp.setWork_equipment(registerData.getWork_equipment());
		
		Mono<Object> returnStatement = 
		this.employeeRepository.findById(registerData.getUser_employee_id())
			.map(listed -> {
				return new RegisterError(0, listed.getId());
			});
		
		return returnStatement.switchIfEmpty(this.employeeRepository.save(newEmp));
	}
	
	/**
	 * find employee by requested data's employee id,
	 * if exists, check password correction,
	 * if valid, return employee object except password
	 * or return error
	 * @param loginData
	 * @return 	 if valid, return employee object except password or return error
	 */
	public Mono<Object> login(Login loginData){
		Mono<Object> tryLogin = 
				this.employeeRepository.findById(loginData.getUser_employee_id())
					.filter(listed -> passwordEncoder().matches(loginData.getUser_pw(), listed.getPw()))
					.map(success -> {
						success.setToken(loginData.getUser_token());
						this.employeeRepository.save(success).subscribe();
						success.setPw("");
						return success;
					});
		return tryLogin
				.switchIfEmpty(Mono.just(new LoginError(1, loginData.getUser_employee_id())));
	}
	
	/**
	 * find employee by requested data's employee id,
	 * if exists, check password correction,
	 * if valid, make random password and send mail,
	 * save renewal password after encoding
	 * @param passwordRenewal
	 * @return if valid return renewed employee object or error
	 */
	public Mono<Object> passwordRenewal(PasswordRenewal passwordRenewal){
		Mono<Object> tryRenewal = 
		this.employeeRepository.findById(passwordRenewal.getUser_employee_id())
			.filter(listed -> listed.getToken().equals(passwordRenewal.getUser_token()))
			.map(validToken -> {
				String newPwd = generateRandomPassword();
				SimpleMailMessage message = new SimpleMailMessage();
     			message.setTo(validToken.getEmail());
     			message.setSubject("산업안전서비스 - 비밀번호 초기화 안내");
     			message.setText(newPwd);
     			emailSender.send(message);
     			
     			validToken.setPw(passwordEncoder().encode(newPwd));
     			return this.employeeRepository.save(validToken).map(s-> {
     				s.setPw("");
     				return s;
     			});
			});
		
		return tryRenewal
				.switchIfEmpty(Mono.just(new PasswordRenewalError(0, passwordRenewal.getUser_employee_id())));
			
	}
	/**
	 * check password is correct
	 * @param confirmPassword
	 * @return if valid return employee except password or error
	 */
	public Mono<Object> confirmPassword(ConfirmPassword confirmPassword){
		Mono<Object> isValidPw = 
				this.employeeRepository.findById(confirmPassword.getUser_employee_id())
				.filter(listed -> passwordEncoder().matches(confirmPassword.getUser_pw(), listed.getPw()))
				.map(valid -> {
					valid.setPw("");
					return valid;
				});
		return isValidPw
				.switchIfEmpty(Mono.just("invalid password or employee id"));
	}
	
	/**
	 * find employee by requested data's employee id,
	 * if exists, check password correction,
	 * if valid, return employee updated object except password
	 * @param updateData
	 * @return if valid return renewed employee except password or error
	 */
	public Mono<Object> updateData(UpdateData updateData){
		Mono<Object> updateTry =
				this.employeeRepository.findById(updateData.getUser_employee_id())
				.filter(listed -> passwordEncoder().matches(updateData.getUser_pw(), listed.getPw()))
				.map(valid -> {
					valid.setName(updateData.getUser_name());
					valid.setEmail(updateData.getUser_email());
					valid.setPw(passwordEncoder().encode(updateData.getNew_pw()));
					valid.setLicense(updateData.getWork_type() == 0 ? false : true);
					valid.setWork_type(updateData.getWork_type());
					this.employeeRepository.save(valid).subscribe();
					valid.setPw("");
					return valid;
				});
		
		return updateTry
				.switchIfEmpty(Mono.just(new UpdateDataError(0, updateData.getUser_employee_id())));
	}
}
