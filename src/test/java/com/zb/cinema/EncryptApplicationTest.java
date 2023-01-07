package com.zb.cinema;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.zb.cinema.config.JasyptConfig;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class EncryptApplicationTest extends JasyptConfig {

	@Test
	public void jasypt_test() {
	    // given
		String url = "jdbc:mysql://222.108.32.89:3306/pro";
		String userName = "pro";
		String password = "1234";
		String jwtTokenKey = "Y2luZW1hLXByb2plY3Qtand0LXNlY3JldC1rZXkK";

	    // when
		String encryptedURL = jasyptEncrypt(url);
		String encryptedUserName = jasyptEncrypt(userName);
		String encryptedPw = jasyptEncrypt(password);
		String encryptedJwtTokenKey = jasyptEncrypt(jwtTokenKey);

		System.out.println("DB URL 암호화 된 값 :::: " + encryptedURL);
		System.out.println("DB userName 암호화 된 값 :::: " + encryptedUserName);
		System.out.println("DB userPW 암호화 된 값 :::: " + encryptedPw);
		System.out.println("jwt token key 암호화 된 값 :::: " + encryptedJwtTokenKey);

		System.out.println("url 복호화 :::::::: " + jasyptDecrypt(encryptedURL));
		System.out.println("name 복호화 :::::::: " + jasyptDecrypt(encryptedUserName));
		System.out.println("pw 복호화 :::::::: " + jasyptDecrypt(encryptedPw));
		System.out.println("jwt 복호화 :::::::: " + jasyptDecrypt(encryptedJwtTokenKey));

	    // then
		assertThat(url).isEqualTo(jasyptDecrypt(encryptedURL));
		assertThat(userName).isEqualTo(jasyptDecrypt(encryptedUserName));
		assertThat(password).isEqualTo(jasyptDecrypt(encryptedPw));
		assertThat(jwtTokenKey).isEqualTo(jasyptDecrypt(encryptedJwtTokenKey));
	}

	private String jasyptEncrypt(String input) {
		String key = "5678";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(key);
		return encryptor.encrypt(input);
	}

	private String jasyptDecrypt(String input){
		String key = "5678";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(key);
		return encryptor.decrypt(input);
	}

}
