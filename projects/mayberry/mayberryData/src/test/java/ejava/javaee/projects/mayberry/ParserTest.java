package ejava.javaee.projects.mayberry;

import ejava.javaee.projects.mayberry.Parser;
import gov.ojp.it.jxdm._3_0.AddressType;
import gov.ojp.it.jxdm._3_0.DecalType;
import gov.ojp.it.jxdm._3_0.ImageType;
import gov.ojp.it.jxdm._3_0.Person;
import gov.ojp.it.jxdm._3_0.PersonNameTextType;
import gov.ojp.it.jxdm._3_0.PersonNameType;
import gov.ojp.it.jxdm._3_0.PersonPhysicalDetailsType;
import gov.ojp.it.jxdm._3_0.ReferenceType;
import gov.ojp.it.jxdm._3_0.ResidenceType;
import gov.ojp.it.jxdm._3_0.StreetType;
import gov.ojp.it.jxdm._3_0.TextType;
import gov.ojp.it.jxdm._3_0.VehicleRegistration;
import gov.ojp.it.jxdm._3_0.VehicleType;
import gov.ojp.it.jxdm._3_0_3.proxy.xsd._1.Base64Binary;
import gov.ojp.it.jxdm._3_0_3.proxy.xsd._1.Date;

import info.ejava.mayberry._1.Dmv;
import info.ejava.mayberry._1.PersonsType;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class ParserTest extends TestCase {
    Log log = LogFactory.getLog(ParserTest.class);

    public void testParse() throws Exception {
        try {
            Parser parser = new Parser(
                    new Class[] { Dmv.class },
                    Parser.getSampleData());

            String elements[] = new String[] {"Person", "VehicleRegistration" };
            for (Object o=parser.getObject(elements);
                o != null; o=parser.getObject(elements)) {
                if (o instanceof Person) {
                    log((Person)o);     
                }
                else if (o instanceof VehicleRegistration) {
                    log((VehicleRegistration)o);                         
                }
            }
        }
        catch (Exception ex) {
            log.fatal("error in parser", ex);
            fail("goodbye:" + ex);
        }
    }
    
    private void log(Person p) {
        log.info("person=" + p.getId());
        log(p.getPersonName());
        log.info("   dob=" + p.getPersonBirthDate().getValue().getYear());
        log(p.getPersonPhysicalDetails());
        logResidences(p.getResidence());
    }
    
    private void logResidences(List<ResidenceType> residences) {
        for(ResidenceType res : residences) {
            log.info(" +residence=" + getValue(res.getResidenceStartDate()));    
            log.info("    end=" + getValue(res.getResidenceEndDate()));    
            log(res.getLocationAddress());
        }        
    }

    private void log(AddressType addr) {
        if (addr != null) {
            log(addr.getLocationStreet());   
            log.info("   city=" + addr.getLocationCityName().getValue());
            log.info("   state=" + addr.getLocationStateCodeUSPostalService().getValue());
            log.info("   zip=" + addr.getLocationPostalCodeID().getID().getValue());
        }
        else {
            log.info("no address");
        }
    }

    private void log(StreetType street) {
        if (street != null) {
            log.info("    street no=" + street.getStreetNumberText().getValue());
            log.info("    street name=" + street.getStreetName().getValue());
        }
        else {
            log.info("no street");
        }
    }

    private String getValue(Date date) {
        return (date != null) ? date.getValue().toString() : null;
    }

    private void log(PersonNameType name) {
        if (name != null) {
            log.info("    first=" + getText(name.getPersonGivenName()));
            log.info("    middle=" + getText(name.getPersonMiddleName()));
            log.info("    last=" + getText(name.getPersonSurName()));
            log.info("    last=" + getText(name.getPersonSuffixName()));
        }
        else {
            log.info("no name supplied");
        }
    }
    
    private String getText(TextType name) {
        return (name != null) ? name.getValue() : "not supplied";
    }    
    
    
    private void log(PersonPhysicalDetailsType pd) {
        if (pd != null) {
            log.info("   height=" + pd.getPersonHeightMeasure().getValue());
            log.info("   weight=" + pd.getPersonWeightMeasure().getValue());
            log.info("   race=" + pd.getPersonRaceCode().getValue());
            log.info("   eyes=" + pd.getPersonEyeColorCode().getValue());
            log.info("   hair=" + pd.getPersonHairColorText().getValue());
            log.info("   sex=" + pd.getPersonSexCode().getValue());
            log(pd.getPersonDigitalImage());
        }
        else {
            log.info("no physical details");
        }
    }
    
    private void log(ImageType image) {
        if (image != null) {
            log.info("   photo= supplied");
            log(image.getBinaryObjectBase64());
        }
        else {
            log.info("no image");
        }
    }
    
    private void log(Base64Binary binary) {
        if (binary != null) {
            log.info("    length=" + binary.getValue().length);
        }
        else {
            log.info("no binary value");
        }
    }
    
    private void log(VehicleRegistration r) {
        log.info("registration=" + r.getId());
        log.info("    tag=" + r.getVehicleLicensePlateID().getID().getValue());
        log(r.getVehicle());
        log(r.getVehicleRegistrationDecal());
    }
    
    private void log(VehicleType v) {
        if (v != null) {
            logOwners(v.getPropertyOwnerPerson());
            log.info("    make=" + v.getVehicleMakeCode().getValue());
            log.info("    model=" + v.getVehicleModelCode().getValue());
            log.info("    year=" + v.getVehicleModelYearDate().getValue().getYear());
            log.info("    color=" + v.getVehicleColorPrimaryCode().getValue());            
        }
        else {
            log.info("no vehicle");
        }
    }
    
    private void logOwners(List<ReferenceType> refs) {
        for(ReferenceType ref : refs) {
            Object owner = ref.getRef();
            if (owner instanceof Person) {
                log.info("    owner=" + ((Person)owner).getId());
            }
            else {
                log.info("unknown owner type:" + owner.getClass().getName());
            }
        }
    }

    private void log(DecalType decal) {
        if (decal != null) {
            //log.info("    exp month=" + decal.getDecalMonthDate().getValue().getTime());
            //log.info("    exp year=" + decal.getDecalYearDate().getValue().getTime());
            log.info("    exp month=" + decal.getDecalMonthDate().getValue().getMonth());
            log.info("    exp year=" + decal.getDecalYearDate().getValue().getYear());
        }
        else {
            log.info("no decal");
        }
    }

}
