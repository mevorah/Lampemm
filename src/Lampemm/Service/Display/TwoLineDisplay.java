package Lampemm.Service.Display;

import Lampemm.Model.CurrentPlayback;
import Lampemm.Model.DisplayableTime;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Lcd;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.System.exit;

/**
 * Created by Mark on 8/11/17.
 */
public class TwoLineDisplay implements MusicDisplay {

    // http://pi4j.com/pins/model-zero-rev1.html

    private final static int LCD_ROWS = 2;
    private final static int LCD_COLUMNS = 16;
    private final static int LCD_ROW_ONE = 0;
    private final static int LCD_ROW_TWO = 1;
    private final GpioLcdDisplay lcd;

    private final static int TIME_LENGTH = 5;
    private final static String ELLIPSES = "..";
    private final static int ELLIPSES_LENGTH = ELLIPSES.length();
    private final static int MAX_TITLE_LENGTH = LCD_COLUMNS - ELLIPSES_LENGTH;
    private final static int MAX_ARTIST_LENGTH = LCD_COLUMNS - TIME_LENGTH - ELLIPSES_LENGTH;

    private String currentTitle = "";
    private String currentArtist = "";

    private static final TwoLineDisplay instance = new TwoLineDisplay();

    private int lcdHandle;

    private TwoLineDisplay() {
        final GpioController gpio = GpioFactory.getInstance();

        System.out.println("Setting up lcd");
        this.lcd = new GpioLcdDisplay(LCD_ROWS,    // number of row supported by LCD
                LCD_COLUMNS,       // number of columns supported by LCD
                RaspiPin.GPIO_09,  // LCD RS pin
                RaspiPin.GPIO_08,  // LCD strobe pin
                RaspiPin.GPIO_26,  // LCD data bit D4
                RaspiPin.GPIO_27,  // LCD data bit D5
                RaspiPin.GPIO_28,  // LCD data bit D6
                RaspiPin.GPIO_29); // LCD data bit D7

        lcd.clear();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static TwoLineDisplay getInstance() {
        return instance;
    }

    public void setStatus(String status) {
        lcd.write(status);
    }

    public void setDisplayForCurrentPlayback(CurrentPlayback currentPlayback) {
        final String title = currentPlayback.getTitle();
        final String artist = currentPlayback.getArtists();
        final String time = new DisplayableTime(currentPlayback.getTimeElapsed()).toString();

        checkNotNull(title);
        checkNotNull(artist);
        checkNotNull(time);

        if (!title.equals(currentTitle) || !artist.equals(currentArtist)) {
            lcd.clear();
        }

        if (!title.equals(currentTitle)) {
            currentTitle = title;
            setTitle(title);
        }

        if (!artist.equals(currentArtist)) {
            currentArtist = artist;
            setArtist(artist);
        }

        setTime(time);
    }

    @Override
    public void setTitle(String title) {
        checkNotNull(title);

        StringBuilder stringBuilder = new StringBuilder();
        if (title.length() > MAX_TITLE_LENGTH) {
            stringBuilder.append(title.substring(0, MAX_TITLE_LENGTH));
            stringBuilder.append(ELLIPSES);
        } else {
            stringBuilder.append(title);
        }

        lcd.write(LCD_ROW_ONE, 0, stringBuilder.toString());
    }

    @Override
    public void setArtist(String artist) {
        checkNotNull(artist);

        StringBuilder stringBuilder = new StringBuilder();
        if (artist.length() > MAX_ARTIST_LENGTH) {
            stringBuilder.append(artist.substring(0, MAX_ARTIST_LENGTH));
            stringBuilder.append(ELLIPSES);
        } else {
            stringBuilder.append(artist);
        }

        lcd.write(LCD_ROW_TWO, 0, stringBuilder.toString());
    }

    @Override
    public void setTime(String displayableTimeElapsed) {
        checkNotNull(displayableTimeElapsed);
        lcd.write(LCD_ROW_TWO, LCD_COLUMNS - TIME_LENGTH, displayableTimeElapsed);
    }
}
