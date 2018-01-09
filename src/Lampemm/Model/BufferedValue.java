package Lampemm.Model;

public class BufferedValue {
    private final float upper;
    private final float lower;

    public BufferedValue(float value, float buffer) {
        this.upper = value + buffer;
        this.lower = value - buffer;
    }

    public boolean isWithinBoundsInclusive(float valueToCheck) {
        return valueToCheck >= lower && valueToCheck <= upper;
    }
}
