package myorg.javaeeex.web;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;

import myorg.javaeeex.ejb.RegistrarRemote;

/**
 * This class is used to define the injection type for RegistrarRemote
 * types
 */
public class RegistrarWebConfig {
    @Produces
    @EJB
    public RegistrarRemote registrar;
}
