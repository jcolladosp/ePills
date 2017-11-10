package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class Receipt extends DataSupport {

    private ArrayList<IntakeMoment> intakeMomentList;

    public Receipt() {

        intakeMomentList = new ArrayList<>();

    }
    public void addIntakeMoment(IntakeMoment intakeMoment) {
        intakeMomentList.add(intakeMoment);
    }

    public void removeIntakeMoment(IntakeMoment o) {
        intakeMomentList.remove(o);
    }

    public ArrayList<IntakeMoment> getIntakeMomentList() {
        return intakeMomentList;
    }
}
