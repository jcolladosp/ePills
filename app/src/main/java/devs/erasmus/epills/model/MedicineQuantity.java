package devs.erasmus.epills.model;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class MedicineQuantity {
    private int id;
    private IntakeMoment intakeMoment;
    private Medicine medicine;

    public MedicineQuantity(int id, IntakeMoment intakeMoment, Medicine medicine) {
        this.id = id;
        this.intakeMoment = intakeMoment;
        this.medicine = medicine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
