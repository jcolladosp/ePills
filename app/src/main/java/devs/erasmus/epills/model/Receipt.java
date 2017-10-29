package devs.erasmus.epills.model;

import java.util.ArrayList;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class Receipt {
    private int id;
    private ArrayList<IntakeMoment> intakeMomentList;
    private User user;

    public Receipt(int id,IntakeMoment intakeMoment,User user) {
        this.id = id;
        intakeMomentList = new ArrayList<>();
        intakeMomentList.add(intakeMoment);
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
