package org.frcteam2910.common.robot.drivers;

import edu.wpi.first.wpilibj.AnalogGyro;
import org.frcteam2910.common.drivers.Gyroscope;

public final class AnalogGyroscope extends Gyroscope {

	private final AnalogGyro gyro;

	public AnalogGyroscope(int analogPort) {
		this(new AnalogGyro(analogPort));
	}

	public AnalogGyroscope(AnalogGyro gyro) {
		this.gyro = gyro;
	}

	@Override
	public void calibrate() {
		gyro.calibrate();
	}

	@Override
	public double getUnadjustedAngle() {
		return gyro.getAngle();
	}

	@Override
	public double getUnadjustedRate() {
		return gyro.getRate();
	}
}
