package Lampemm.Util;

import com.pi4j.io.gpio.*;

import static com.pi4j.io.gpio.RaspiPin.GPIO_23;

/**
 * Created by Mark on 8/20/17.
 */
public class MotorTest {
    public static void main(String[] args) {
        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput PWMA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "PwmA");
        final GpioPinDigitalOutput AIN2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "Ain2");
        final GpioPinDigitalOutput AIN1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10, "Ain1");
        final GpioPinDigitalOutput STBY = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "Stby");

        PWMA.high();
        AIN2.low();
        AIN1.high();
        STBY.high();
    }
}
