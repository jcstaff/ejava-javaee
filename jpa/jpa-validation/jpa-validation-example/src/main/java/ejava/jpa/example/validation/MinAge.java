package ejava.jpa.example.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy={MinAgeValidator.class})
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface MinAge {
	String message() default "too young";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default{};
	int minAge() default 0;
}
