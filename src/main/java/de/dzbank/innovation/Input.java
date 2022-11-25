package de.dzbank.innovation;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
//@Table(name = "known_adds")
@NamedQuery(name = "Inputs.findAll", query = "SELECT f FROM Input f", hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
@Cacheable   
public class Input 
{
 	@Id
    @SequenceGenerator(name = "addSequence", sequenceName = "known_add_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "addSequence")  
	private int id;
	
	@Column(length = 40, unique = false)
	private int num1;
	@Column(length = 40, unique = false)
	private int num2;

	public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public int getNum1() 
	{
		return num1;
	}
	public void setNum1(int num1) 
	{
		this.num1 = num1;
	}
	public int getNum2() 
	{
		return num2;
	}
	public void setNum2(int num2) 
	{
		this.num2 = num2;
	}

}