package ejava.jpa.example.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinAgeValidator implements ConstraintValidator<MinAge, Date>{
	int minAge;

	@Override
	public void initialize(MinAge constraint) {
		this.minAge = constraint.minAge();
	}

	@Override
	public boolean isValid(Date date, ConstraintValidatorContext ctx) {
		if (date==null) { return true; }
		//get today's date
		Calendar now = new GregorianCalendar();
		//determine min date that meets age requirement
		now.add(Calendar.YEAR, -1*minAge);
		//get calendate date of object
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		//return whether min date is after provided date
		return now.after(cal);
	}
}
