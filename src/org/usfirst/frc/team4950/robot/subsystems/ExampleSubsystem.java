package org.usfirst.frc.team4950.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.usfirst.frc.team4950.robot.Database;
import org.usfirst.frc.team4950.robot.Robot;
import org.usfirst.frc.team4950.robot.RobotMap;
import org.usfirst.frc.team4950.robot.commands.ExampleCommand;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ExampleSubsystem extends Subsystem {
	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	public static CANTalon leftMotor;
	private static ReentrantReadWriteLock readWriteLock;
	public ExampleSubsystem() {
		leftMotor = new CANTalon(RobotMap.leftMotor);
		readWriteLock = new ReentrantReadWriteLock();
	}
	public void initDefaultCommand() {
		if(Robot.isRecordingForAutoPlay)
			setDefaultCommand(new ExampleCommand());
	}
	
	public void power(double p) {
		readWriteLock.readLock().lock();
		try {
			leftMotor.set(p);
		}
		finally {
			readWriteLock.readLock().unlock();
		}
	}
}
