package com.banking.net_banking_system;

//import com.banking.net_banking_system.utils.TransferData;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class NetBankingSystemApplication {
//		@Value("${next_gen.jwt.secret}")
//		String secretKey ="fafiepgrggt8e4gt41b8r7t4HGrgr8edrgf" ;
//
//		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//
//		String verificationToken = Jwts.builder()
//				.subject("NXT_GEN")
//				.signWith(key)
//				.compact();
//
//
//		public NetBankingSystemApplication(){
//			System.out.println("veficationtoekn  : "+verificationToken);
//		}


	public static void main(String[] args) {


//		TransferData transferData = new TransferData();
		SpringApplication.run(NetBankingSystemApplication.class, args);


	}


}
