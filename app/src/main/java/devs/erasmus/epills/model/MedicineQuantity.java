package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class MedicineQuantity extends DataSupport {

    private IntakeMoment intakeMoment;
    private Medicine medicine;

    public MedicineQuantity(IntakeMoment intakeMoment, Medicine medicine) {

        this.intakeMoment = intakeMoment;
        this.medicine = medicine;
    }

    public IntakeMoment getIntakeMoment() {
        return intakeMoment;
    }

    public void setIntakeMoment(IntakeMoment intakeMoment) {
        this.intakeMoment = intakeMoment;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
}
