package ejava.examples.jndischeduler.ejb;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class HospitalEJB 
    extends SchedulerBase implements HospitalLocal, HospitalRemote {
    public String getName() { return "HospitalEJB"; }

    @Resource
    protected void setSessionContext(SessionContext ctx) {
        super.ctx = ctx;
    }
}
