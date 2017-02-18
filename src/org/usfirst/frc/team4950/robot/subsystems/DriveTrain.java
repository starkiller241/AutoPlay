package org.usfirst.frc.team4950.robot.subsystems;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.usfirst.frc.team4950.robot.Database;
import org.usfirst.frc.team4950.robot.Robot;
import org.usfirst.frc.team4950.robot.RobotMap;
import org.usfirst.frc.team4950.robot.commands.RecordForAutoPlay;
import org.usfirst.frc.team4950.robot.Database;
import org.usfirst.frc.team4950.robot.Robot;
import org.usfirst.frc.team4950.robot.RobotMap;
import org.usfirst.frc.team4950.robot.commands.Drive;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {
    
	private CANTalon leftFrontCAN;
	private CANTalon rightFrontCAN;
	private CANTalon leftBackCAN;
	private CANTalon rightBackCAN;

	private RobotDrive drive;
	private static ReentrantReadWriteLock readWriteLock;
	private ReentrantReadWriteLock rwLock;
	
	public DriveTrain() {
		super();
		
		leftFrontCAN = new CANTalon(RobotMap.leftFrontMotor);
		rightFrontCAN = new CANTalon(RobotMap.rightFrontMotor);
		leftBackCAN = new CANTalon(RobotMap.leftBackMotor);
		rightBackCAN = new CANTalon(RobotMap.rightBackMotor);
		
		drive = new RobotDrive(leftFrontCAN, leftBackCAN, rightFrontCAN, rightBackCAN);
		
		drive.setMaxOutput(.70);
		drive.setInvertedMotor(MotorType.kFrontLeft, true);
		drive.setInvertedMotor(MotorType.kRearLeft, true);
		drive.setInvertedMotor(MotorType.kFrontRight, true);
		drive.setInvertedMotor(MotorType.kRearRight, true);
	}

    public void initDefaultCommand() {
    	if(Robot.isRecordingForAutoPlay)
    		setDefaultCommand(new RecordForAutoPlay());
    }
    
    public void drive(double left, double right) {
    	drive.tankDrive(left, right);
		
		rwLock = new ReentrantReadWriteLock();
	}

    public void driveArcade(double speed, double rotate) {
    	drive.arcadeDrive(speed, rotate);
	}
    
    public double getLPow() {
    	rwLock.readLock().lock();
    	try {
        	return leftFrontCAN.get();
    	}
    	finally {
    		rwLock.readLock().unlock();
    	}
    }
    
    public double getRPow() {
    	rwLock.readLock().lock();
    	try {
        	return rightFrontCAN.get();
    	}
    	finally {
    		rwLock.readLock().unlock();
    	}
    }
}
