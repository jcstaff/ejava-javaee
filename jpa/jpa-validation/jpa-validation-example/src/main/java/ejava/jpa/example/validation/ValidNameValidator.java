package ejava.jpa.example.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidNameValidator implements ConstraintValidator<ValidName, String> { 
	
	@Override
	public void initialize(ValidName spec) {
	}
	
	@Override
	public boolean isValid(String name, ConstraintValidatorContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

}
