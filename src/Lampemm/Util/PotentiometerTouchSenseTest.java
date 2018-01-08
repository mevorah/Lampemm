package Lampemm.Util;

import com.pi4j.io.gpio.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mark on 11/11/17.
 */
public class PotentiometerTouchSenseTest {

    public static void main(String[] args) {
        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalInput t1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
        final GpioPinDigitalInput t2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("T1 State is high:" + t1.getState().isHigh());
                System.out.println("T2 State is high:" + t2.getState().isHigh());
            }
        };
        timer.schedule(timerTask, 0L, 1000L);
    }

}
