package ejava.examples.jndischeduler.ejb;

import javax.ejb.Remote;

import ejava.examples.jndischeduler.Scheduler;

@Remote
public interface AidSchedulerRemote extends Scheduler {
}
