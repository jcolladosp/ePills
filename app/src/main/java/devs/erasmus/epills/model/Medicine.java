package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;


/**
 * Created by jcolladosp on 28/10/2017.
 * Represents all pills, medicines blah.
 */

public class Medicine extends DataSupport {
    private long id;

    private String name;
    private String description;
    private String image;

    public Medicine(String name, String description, String image) {

        this.name = name;
        this.image = image;
        this.description = description;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage(){
        return this.image;
    }
}
