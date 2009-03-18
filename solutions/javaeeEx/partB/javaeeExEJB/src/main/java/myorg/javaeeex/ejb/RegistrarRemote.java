package myorg.javaeeex.ejb;

import javax.ejb.Remote;

@Remote
public interface RegistrarRemote {
    void ping();
}
