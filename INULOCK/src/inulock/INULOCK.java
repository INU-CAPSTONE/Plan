package inulock;

import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

import inulock.io.gpio.GpioControl;
import inulock.qr.QRScanner;

;

public class INULOCK {
	public static void main(String[] args) throws InterruptedException, FailedToRunRaspistillException {
		GpioControl gpio = new GpioControl();
		QRScanner scan = new QRScanner();
		String result;

		for (;;) {
			result = scan.takeInstantSnap();
			if (result != null)
				break;
		}

		System.out.println("Decode QR = " + result + "\n");
		gpio.finishGPIO();
	}
}
