package org.frcteam2910.common.motion;

public final class TrapezoidalMotionProfile extends MotionProfile {
	private final Constraints constraints;
	private final Goal start, end;

	private final double endAccelerationTime, endFullSpeedTime, endDecelerationTime;

	public TrapezoidalMotionProfile(Goal start, Goal end,
	                                Constraints constraints) {
		this.constraints = constraints;
		this.start = start;
		this.end = end;

		double cutoffBegin = start.velocity / constraints.maxAcceleration;
		double cutoffDistBegin = cutoffBegin * cutoffBegin * constraints.maxAcceleration / 2;

		double cutoffEnd = end.velocity / constraints.maxAcceleration;
		double cutoffDistEnd = cutoffEnd * cutoffEnd * constraints.maxAcceleration / 2;

		double fullTrapezoidDist = cutoffDistBegin + (end.position - start.position) + cutoffDistEnd;
		double accelerationTime = constraints.maxVelocity / constraints.maxAcceleration;

		double fullSpeedDist = fullTrapezoidDist - accelerationTime * accelerationTime * constraints.maxAcceleration;

		// Handle profiles where the max velocity is never reached
		if (fullSpeedDist < 0) {
			accelerationTime = Math.sqrt(fullTrapezoidDist / constraints.maxAcceleration);
			fullSpeedDist = 0;
		}

		endAccelerationTime = accelerationTime - cutoffBegin;
		endFullSpeedTime = endAccelerationTime + fullSpeedDist / constraints.maxVelocity;
		endDecelerationTime = endFullSpeedTime + accelerationTime - cutoffEnd;
	}

	@Override
	public State calculate(double time) {
		double acceleration;
		double velocity;
		double position;

		if (time < endAccelerationTime) {
			acceleration = constraints.maxAcceleration;
			velocity = start.velocity + time * constraints.maxAcceleration;
			position = start.position + (start.velocity + time * constraints.maxAcceleration / 2) * time;
		} else if (time < endFullSpeedTime) {
			acceleration = 0;
			velocity = constraints.maxVelocity;
			position = start.position + (start.velocity + endAccelerationTime * constraints.maxAcceleration / 2) *
					endAccelerationTime + constraints.maxVelocity * (time - endAccelerationTime);
		} else if (time <= endDecelerationTime) {
			acceleration = -constraints.maxAcceleration;
			velocity = end.velocity + (endDecelerationTime - time) * constraints.maxAcceleration;
			double timeLeft = endDecelerationTime - time;
			position = end.position - (end.velocity + timeLeft * constraints.maxAcceleration / 2) * timeLeft;
		} else {
			acceleration = 0;
			velocity = end.velocity;
			position = end.position;
		}

		return new State(time, position, velocity, acceleration);
	}

	@Override
	public double getDuration() {
		return endDecelerationTime;
	}
}
