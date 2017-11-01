package devs.erasmus.epills.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class User {
    private String name;
    private Date birthDate;
    private ArrayList<IntakeMoment> intakeMomentList;

    public User(String name, Date birthDate) {
        this.name = name;
        this.birthDate = birthDate;
        intakeMomentList = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void  addIntakeMoment(IntakeMoment intakeMoment) {
        intakeMomentList.add(intakeMoment);
    }

    public void  removeIntakeMoment(IntakeMoment o) {
         intakeMomentList.remove(o);
    }
}
