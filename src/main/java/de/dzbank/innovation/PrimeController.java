package de.dzbank.innovation;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



 @Path("/prime")
public class PrimeController {
    @POST
    @Produces(MediaType.TEXT_PLAIN)
	public String addForm(Input input)
	{
		int n = input.getNum1()+input.getNum2();
		//int b = n*4;
		//return Integer.toString(b);

		int i,m=0,flag=0; 
		String a = "";     
		m=n/2;      
		if(n==0||n==1){  
			a="is not prime number";       
		}else{  
		 for(i=2;i<=m;i++){      
		  if(n%i==0){   
			flag=1;   
			a="is not prime number";          
		   break;      
		  }      
		 }      
		 if(flag==0)  { a= "is  prime number"; }  
		}//end of else 
		return a;

	}

} 



//https://examples.javacodegeeks.com/spring-boot-microservices-example/
//https://www.tutussfunny.com/how-to-add-two-numbers-using-spring-boot/
//https://quarkus.io/guides/validation
//https://spring.io/guides/gs/handling-form-submission/
