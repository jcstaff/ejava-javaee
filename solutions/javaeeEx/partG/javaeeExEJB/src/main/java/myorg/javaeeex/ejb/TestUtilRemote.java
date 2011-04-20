package myorg.javaeeex.ejb;

import javax.ejb.Remote;

import myorg.javaeeex.bl.TestUtil;

@Remote
public interface TestUtilRemote extends TestUtil {
}
