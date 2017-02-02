
package org.usfirst.frc.team4950.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

import org.usfirst.frc.team4950.robot.autoplay.FlusherThread;
import org.usfirst.frc.team4950.robot.autoplay.Translator;
import org.usfirst.frc.team4950.robot.autoplay.UpdaterThread;
import org.usfirst.frc.team4950.robot.commands.ExampleCommand;
import org.usfirst.frc.team4950.robot.subsystems.DriveTrain;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	public static final DriveTrain driveTrain = new DriveTrain();
	public static OI oi;
	public static UpdaterThread updater;
	public static FlusherThread flusher;
	public static AnalogGyro gyro;
	SensorThread sense;
	PrintStream out;
	public static Translator translate;

	public static boolean isRecordingForAutoPlay = true;
	
	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();
	
	public static ArrayBlockingQueue<String> tempData;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = new OI();
		//gyro = new AnalogGyro();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		Map<String, Supplier<Command>> systemsMap = new HashMap<>();
		systemsMap.put("EXAMPLE_SUBSYSTEM", () -> driveTrain.getCurrentCommand());
		
		sense = new SensorThread(10);
		translate = new Translator();
		
		
		if(isRecordingForAutoPlay) {
			tempData = new ArrayBlockingQueue<String>(200);
		}
		
		ServerSocket server;
		try {
			server = new ServerSocket(8080);
			Socket socket = server.accept();
			OutputStream rawOut = socket.getOutputStream();
			out = new PrintStream(rawOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
		updater = new UpdaterThread(systemsMap);
		flusher = new FlusherThread(out);
		sense = new SensorThread(10);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
		
		if(!isRecordingForAutoPlay)
			translate.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		sense.start();
		if(isRecordingForAutoPlay) {
			updater.start();
			flusher.start();
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
