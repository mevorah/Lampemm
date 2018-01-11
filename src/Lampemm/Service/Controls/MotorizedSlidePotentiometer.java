package Lampemm.Service.Controls;

import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import java.io.IOException;

/**
 * Created by Mark on 8/11/17.
 */
public class MotorizedSlidePotentiometer {

    private static final float MAX_RESISTANCE = 988; //True max: 1023
    private static final float MIN_RESISTANCE = 9;
    private static final float TOLERANCE = 5;

    private SpiDevice spi;
    private static final short CHANNEL_ONE = 1;

    private boolean interuppted = false;

    // Motor Driver
    final GpioPinDigitalOutput pwmA;
    final GpioPinDigitalOutput ain1;
    final GpioPinDigitalOutput ain2;
    final GpioPinDigitalOutput stby;
    final GpioController gpio = GpioFactory.getInstance();

    private static final MotorizedSlidePotentiometer instance = new MotorizedSlidePotentiometer();

    // Rate at which potentiometer is moving
    private float currentNaturalPosition = 0;
    private float nextNaturalPosition = 0;

    private MotorizedSlidePotentiometer() {
        System.out.println("Setting up potentiometer");
        try {
            spi = SpiFactory.getInstance(SpiChannel.CS0);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        this.pwmA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "PwmA");
        this.ain1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_30, "Ain1");
        this.ain2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "Ain2");
        this.stby = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "Stby");
    }

    public static MotorizedSlidePotentiometer getInstance() {
        return instance;
    }

    /**
     * Allows client to stop motor movement mid movement
     */
    public void setInterupt() {
        interuppted = true;
    }

    /**
     * Set the potentiometer's position to the fraction defined in the
     * position.
     *
     * Ex. .5 => half way
     *
     * @param position
     */
    public void setPosition(float position) throws IOException {
        float target =  getResistanceForPosition(position);

        // Set current resistance, and next resistance during playback
        this.currentNaturalPosition = this.nextNaturalPosition;
        this.nextNaturalPosition = target;

        float low = target - TOLERANCE;
        float high = target + TOLERANCE;

        int value = getValue(CHANNEL_ONE);

        while ((value < low || value > high) && !interuppted) {
            value = getValue(CHANNEL_ONE);
            if (value < low) {
                pwmA.high();
                ain2.low();
                ain1.high();
                stby.high();
            } else if (value > high) {
                pwmA.high();
                ain2.high();
                ain1.low();
                stby.high();
            } else {
                break;
            }

            if (interuppted) {
                System.out.println("Motor movement interupted");
            }
        }

        interuppted = false;
        pwmA.low();
        ain2.low();
        ain1.low();
        stby.high();
    }

    /**
     * Return the natural interval for the song playing i.e the difference
     * in resistance
     * @return
     */
    public float getNaturalInterval() {
        return currentNaturalPosition - nextNaturalPosition;
    }

    /**
     * Returns the actual interval of resistance.
     *
     * 0 -> No manual tracking
     *
     * @return
     */
    public float getActualInterval() {
        return currentNaturalPosition - getResistance();
    }

    /**
     * Get the potentiometer's position as a fraction.
     *
     * Ex. half way => .5
     *
     * @return
     */
    public float getPosition() throws IOException {
        float currentPositionResistance = getResistance();
        float position = getPositionForResistance(currentPositionResistance);
        return position;
    }

    public float getPositionForResistance(float resistance) {
        return (MAX_RESISTANCE - resistance) / (MAX_RESISTANCE - MIN_RESISTANCE);
    }

    public float getResistanceForPosition(float position) {
        return MAX_RESISTANCE - (position * (MAX_RESISTANCE - MIN_RESISTANCE));
    }

    public float getResistance() {
        try {
            return (float) getValue(CHANNEL_ONE);
        } catch (IOException e) {
            System.out.println("Error getting current resistance reading. Failed with:" + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Communicate to the ADC chip via SPI to get single-ended conversion value for a specified channel.
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public int getValue(final int channel) throws IOException {

        // create a data buffer and initialize a conversion request payload
        byte data[] = new byte[] {
                (byte) 0b00000001,                              // first byte, start bit
                (byte)(0b10000000 |( ((channel & 7) << 4))),    // second byte transmitted -> (SGL/DIF = 1, D2=D1=D0=0)
                (byte) 0b00000000                               // third byte transmitted....don't care
        };

        // send conversion request to ADC chip via SPI channel
        byte[] result = spi.write(data);

        // calculate and return conversion value from result bytes
        int value = (result[1]<< 8) & 0b1100000000; //merge data[1] & data[2] to get 10-bit result
        value |=  (result[2] & 0xff);
        return value;
    }
}
