package Lampemm.Util;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

import java.io.IOException;

/**
 * Created by Mark on 8/20/17.
 */
public class PotentiometerReadTest {

    static Console console = new Console();
    private static SpiDevice spi;
    private static final short ADC_CHANNEL_COUNT = 8;  // MCP3004=4, MCP3008=8

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting");
        try {
            spi = SpiFactory.getInstance(SpiChannel.CS0);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        while (console.isRunning()) {
            read();
            Thread.sleep(1000);
        }
        console.emptyLine();

    }

    /**
     * Read data via SPI bus from MCP3002 chip.
     * @throws IOException
     */
    public static void read() throws IOException, InterruptedException {
        int conversion_value = getConversionValue((short) 0);
        console.print(String.format(" | %04d", conversion_value)); // print 4 digits with leading zeros
    }

    /**
     * Communicate to the ADC chip via SPI to get single-ended conversion value for a specified channel.
     * @param channel analog input channel on ADC chip
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public static int getConversionValue(short channel) throws IOException {

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
