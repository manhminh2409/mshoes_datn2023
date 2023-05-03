package com.mshoes.mshoes.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderGenerator {

	public static void main(String[] args) throws NoSuchAlgorithmException {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		System.out.println(passwordEncoder.encode("123456"));

		Date date = new Date();
		System.out.println(date);
	}
}
