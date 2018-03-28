package inulock.io.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

final public class GpioControl {
	final private GpioController gpio = GpioFactory.getInstance();
	private GpioPinDigitalOutput pinR, pinY, pinG, pinRelay;

	public GpioControl() {
		initGPIO();
	}

	protected void finalize() throws Throwable {
		finishGPIO();
	}

	private void initGPIO() {
		pinR = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "LED R",
				PinState.LOW);
		pinY = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "LED Y",
				PinState.LOW);
		pinG = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "LED G",
				PinState.LOW);
		pinRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Relay",
				PinState.HIGH);
		pinR.setShutdownOptions(Boolean.TRUE, PinState.LOW);
		pinY.setShutdownOptions(Boolean.TRUE, PinState.LOW);
		pinG.setShutdownOptions(Boolean.TRUE, PinState.LOW);
		pinRelay.setShutdownOptions(Boolean.TRUE, PinState.LOW);
	}

	public void finishGPIO() {
		gpio.shutdown();
	}

	public void openDoor() throws InterruptedException {
		pinRelay.toggle();
		Thread.sleep(100);
		pinRelay.toggle();
	}

	public void setLED(char color, boolean status) {
		switch (color) {
		case 'R':
			pinR.setState(status);
			break;
		case 'Y':
			pinY.setState(status);
			break;
		case 'G':
			pinG.setState(status);
			break;
		}
	}
}
