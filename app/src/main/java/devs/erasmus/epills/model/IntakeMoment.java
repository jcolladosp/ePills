package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jcolladosp on 28/10/2017.
 * Represents the intake of a specific medicine on a specific day in a week at a certain time. It may be repeated in the next week or it may not be repeated.
 */

public class IntakeMoment extends DataSupport {

    private Calendar intakeDate;
    private Receipt receiptId;
    private Medicine medicineId;
    private int quantity;
    private int alarmRequestCode;
    /**
     * Represents the weekDay for this certain event. Mon = 0, ..., Sun = 7.
     */
    private int weekDay;

    public IntakeMoment(Calendar intakeDate, Receipt receiptId, Medicine medicineId, int quantity, int alarmRequestCode, int weekDay) {
        this.intakeDate = intakeDate;
        this.receiptId = receiptId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.alarmRequestCode = alarmRequestCode;
        this.weekDay = weekDay;
    }

    public Calendar getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Calendar intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Receipt getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Receipt receiptId) {
        this.receiptId = receiptId;
    }

    public Medicine getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Medicine medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAlarmRequestCode() {
        return alarmRequestCode;
    }

    public void setAlarmRequestCode(int alarmRequestCode) {
        this.alarmRequestCode = alarmRequestCode;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }
}
