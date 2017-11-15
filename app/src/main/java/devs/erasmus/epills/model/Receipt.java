package devs.erasmus.epills.model;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by jcolladosp on 28/10/2017.
 * To bundle all intakeMoments, that are created together.
 */

public class Receipt extends DataSupport {

    private long id;

    public Receipt() {
        this.id = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
