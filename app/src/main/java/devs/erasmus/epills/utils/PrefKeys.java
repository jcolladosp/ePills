package devs.erasmus.epills.utils;

public enum PrefKeys {
    NAME("ePillsPreferences"),
    FIRST_TIME("first_time");

    private String value;

    PrefKeys(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
