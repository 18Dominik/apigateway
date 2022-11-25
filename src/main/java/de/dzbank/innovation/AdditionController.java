import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;



import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



 @Path("/add")
public class AdditionController {

	@Inject
    EntityManager em;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public String addForm(Input input)
	{
		em.persist(input);
		return Integer.toString(input.getNum1()+input.getNum2());
	}

	@GET
    public List<Input> get() {
        return em.createNamedQuery("Inputs.findAll", Input.class)
                .getResultList();
    }

} 



//https://examples.javacodegeeks.com/spring-boot-microservices-example/
//https://www.tutussfunny.com/how-to-add-two-numbers-using-spring-boot/
//https://quarkus.io/guides/validation
//https://spring.io/guides/gs/handling-form-submission/

//run postgresql via docker container: https://hevodata.com/learn/docker-postgresql/

//amazon coretto: https://aws.amazon.com/de/corretto/?filtered-posts.sort-by=item.additionalFields.createdDate&filtered-posts.sort-order=desc
//java jdk: https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html 