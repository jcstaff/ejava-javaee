package myorg.javaeeex.ejb;

import javax.ejb.Local;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Person;

@Local
public interface RegistrarLocal extends Registrar {

    Person getPersonById(long id)
	    throws RegistrarException;
	Person getPersonByIdHydrated(long id)
	    throws RegistrarException;
}
