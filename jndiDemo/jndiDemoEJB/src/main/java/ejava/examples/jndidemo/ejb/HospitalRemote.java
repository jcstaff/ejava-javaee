package ejava.examples.jndidemo.ejb;

import javax.ejb.Remote;

import ejava.examples.jndidemo.Scheduler;

@Remote
public interface HospitalRemote extends Scheduler {
}
