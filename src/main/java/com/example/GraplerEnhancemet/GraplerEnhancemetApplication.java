package com.example.GraplerEnhancemet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@OpenAPIDefinition(
	       info= @Info ( title = "Grappler Enhancement - Module 1",
	             version="1.0.0",
	             description="The project is to define the structure of APIs in Role Based Access Controll and Hierarchy",
	             termsOfService="https://grappler.innogent.in/dashboard"  ,
	       		      contact = @Contact ( name = "Niraj , Himanshu, Vanshika" , email ="niraj.kaushal@innogent.in ,himanshu.rathore@innogent.in , vanshika.yadav@innogent.in " ) ,
	             license = @License ( name = "licence",url="https://github.com/Himanshurathore9977/Grapler_Enhancement")

       )
)
@EnableTransactionManagement
public class GraplerEnhancemetApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraplerEnhancemetApplication.class, args);
		System.out.println("Started Successfully");
	}
}
