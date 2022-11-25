package de.dzbank.innovation;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



 @Path("/sub")
public class SubtractionController {
    @POST
    @Produces(MediaType.TEXT_PLAIN)
	public String subForm(Input input)
	{
		return Integer.toString(input.getNum1()-input.getNum2());
	}

} 



//https://examples.javacodegeeks.com/spring-boot-microservices-example/
//https://www.tutussfunny.com/how-to-add-two-numbers-using-spring-boot/
//https://quarkus.io/guides/validation
//https://spring.io/guides/gs/handling-form-submission/