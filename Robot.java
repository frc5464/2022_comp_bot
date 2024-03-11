// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.CompBot2022;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;

import org.photonvision.PhotonCamera;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
//import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kTwoAuto = "Two Ball Auto";
  private static final String kThreeAuto = "Three Ball Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");

  double[] xAverage = { 0, 0, 0, 0, 0 };
  double[] yAverage = { 0, 0, 0, 0, 0 };

  double limelight_offset_Y = -1;
  double limelight_offset_X = 0;

  double shooterFrontRPM = 0;
  double shooterBackRPM = 0;

  // Class objects
  kittLED LED = new kittLED();

  // Drive Motors
  CANSparkMax leftRear = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax leftFront = new CANSparkMax(6, MotorType.kBrushless);
  CANSparkMax rightRear = new CANSparkMax(4, MotorType.kBrushless);
  CANSparkMax rightFront = new CANSparkMax(5, MotorType.kBrushless);

  CANSparkMax ClimbRotateLeft = new CANSparkMax(7, MotorType.kBrushless);
  CANSparkMax ClimbRotateRight = new CANSparkMax(8, MotorType.kBrushless);

  // Shooter Motors
  CANSparkMax ShooterFront = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkMax ShooterBack = new CANSparkMax(1, MotorType.kBrushless);

  // Climber Extend
  CANSparkMax climbExtendLeft = new CANSparkMax(10, MotorType.kBrushless);
  CANSparkMax climbExtendRight = new CANSparkMax(9, MotorType.kBrushless);

  SparkMaxPIDController shooterBackPID;
  RelativeEncoder shooterBackEncoder;

  SparkMaxPIDController shooterFrontPID;
  RelativeEncoder shooterFrontEncoder;

  double kP_front, kI_front, kD_front, kIz_front, kFF_front, kMaxOutput_front, kMinOutput_front, maxRPM_front;
  double kP_back, kI_back, kD_back, kIz_back, kFF_back, kMaxOutput_back, kMinOutput_back, maxRPM_back;

  // Motors (Intake, Hopper, and Climber)
  WPI_TalonSRX hopper = new WPI_TalonSRX(14);
  WPI_TalonSRX intakeSpin = new WPI_TalonSRX(16);
  WPI_TalonSRX intakeLift = new WPI_TalonSRX(15);

  MecanumDrive drive = new MecanumDrive(leftFront, rightFront, leftRear, rightRear);

  Joystick stick = new Joystick(0);
  Joystick stick2 = new Joystick(1);
  // Joystick testStick = new Joystick(2);

  Timer kitt_clock = new Timer();
  Timer autoclock = new Timer();
  Timer autoshooterclock = new Timer();

  DigitalInput LimitIntakeUp = new DigitalInput(2);
  DigitalInput LimitClimbExtendUp = new DigitalInput(1);
  DigitalInput LimitClimbRotateUpRight = new DigitalInput(0);
  DigitalInput LimitClimbRotateUpLeft = new DigitalInput(6);
  DigitalInput LimitIntakeDown = new DigitalInput(4);
  DigitalInput LimitClimbExtendDown = new DigitalInput(3);
  DigitalInput LimitClimbRotateDown = new DigitalInput(5);

  // Photon
  PhotonCamera camera = new PhotonCamera("autocam");

  // Variables
  double x;
  double y;
  double area;
  double yScaled;
  double i = 0.0;
  int index = 0;
  double xAverageCalc;
  double yAverageCalc;

  double BackRef = 0.0;
  double FrontRef = 0.0;

  boolean IsClimberDown = false;
  boolean IsIntakeDown = false;

  // Joystick 1 Axis Inputs
  double axisForward_1;
  double axisRight_1;
  double axisRot_1;
  double axis4rth_1;
  double axisLeftTrigger_1;
  double axisRightTrigger_1;

  // Joystick 1 Button Inputs
  boolean ButtonA_1;
  boolean ButtonB_1;
  boolean ButtonX_1;
  boolean ButtonY_1;
  boolean BumperLeft_1;
  boolean BumperRight_1;

  // Joystick 2 Axis Inputs
  double axisForward_2;
  double axisRight_2;
  double axisRot_2;
  double axis4rth_2;
  double axisLeftTrigger_2;
  double axisRightTrigger_2;

  // Joystick 2 Button Inputs
  boolean ButtonA_2;
  boolean ButtonB_2;
  boolean ButtonX_2;
  boolean ButtonY_2;
  boolean BumperLeft_2;
  boolean BumperRight_2;

  double target_rpm_front = 3900;
  double target_rpm_back = 4100;

  double shooter_front_error;
  double shooter_front_power = -0.5;

  double shooter_back_error;
  double shooter_back_power = 0.5;

  boolean linedup = false;

  double off = 10;
  double dist;

  // Automous varibles
  double phase = 0.0;
  // DigitalInput breakbeam = new DigitalInput(0);
  AHRS navx = new AHRS();

  String colorChoice = "red";

  // Distance sensor
  //Counter m_LIDAR = new Counter(7);
  AnalogInput UltraSonic = new AnalogInput(0);

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("2 Ball Auto", kTwoAuto);
    m_chooser.addOption("3 Ball Auto", kThreeAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    LED.LEDInit();
    kitt_clock.start();

    ShooterBack.restoreFactoryDefaults();
    ShooterFront.restoreFactoryDefaults();
    leftRear.setInverted(true);

    ShooterFront.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100);
    ShooterBack.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100);
    leftFront.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100);
    leftRear.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100);
    rightFront.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100);
    rightRear.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 100);
    shooterBackPID = ShooterBack.getPIDController();
    shooterFrontPID = ShooterFront.getPIDController();

    shooterBackEncoder = ShooterBack.getEncoder();
    shooterFrontEncoder = ShooterFront.getEncoder();

    kP_back = 0.00006;
    kI_back = 0.0;
    kD_back = 0.0;
    kIz_back = 0;
    kFF_back = 0.00018;
    kMaxOutput_back = 1;
    kMinOutput_back = -1;
    maxRPM_back = 5700;

    kP_front = 0.00006;
    kI_front = 0.0;
    kD_front = 0.0;
    kIz_front = 0;
    kFF_front = 0.00018;
    kMaxOutput_front = 1;
    kMinOutput_front = -1;
    maxRPM_front = 5700;

    shooterBackPID.setP(kP_back);
    shooterBackPID.setI(kI_back);
    shooterBackPID.setD(kD_back);
    shooterBackPID.setIZone(kIz_back);
    shooterBackPID.setFF(kFF_back);
    shooterBackPID.setOutputRange(kMinOutput_back, kMaxOutput_back);

    shooterFrontPID.setP(kP_front);
    shooterFrontPID.setI(kI_front);
    shooterFrontPID.setD(kD_front);
    shooterFrontPID.setIZone(kIz_front);
    shooterFrontPID.setFF(kFF_front);
    shooterFrontPID.setOutputRange(kMinOutput_front, kMaxOutput_front);

    SmartDashboard.putNumber("P gain - BACK", kP_back);
    SmartDashboard.putNumber("I gain - BACK", kI_back);
    SmartDashboard.putNumber("D gain - BACK", kD_back);
    SmartDashboard.putNumber("I zone - BACK", kIz_back);
    SmartDashboard.putNumber("Feed forward - BACK", kFF_back);
    SmartDashboard.putNumber("Max Output - BACK", kMaxOutput_back);
    SmartDashboard.putNumber("Min Output - BACK", kMinOutput_back);

    SmartDashboard.putNumber("P gain - FRONT", kP_front);
    SmartDashboard.putNumber("I gain - FRONT", kI_front);
    SmartDashboard.putNumber("D gain - FRONT", kD_front);
    SmartDashboard.putNumber("I zone - FRONT", kIz_front);
    SmartDashboard.putNumber("Feed forward - FRONT", kFF_front);
    SmartDashboard.putNumber("Max Output - FRONT", kMaxOutput_front);
    SmartDashboard.putNumber("Min Output - FRONT", kMinOutput_front);

    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1);

    navx.calibrate();

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and
   * test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

    colorChoice = DriverStation.getAlliance().toString();

    // SmartDashboard Info Readout

    // SmartDashboard.putBoolean("Limit Switch Intake Up", LimitIntakeUp.get());
    // SmartDashboard.putBoolean("Limit Switch Climb Extend Up", LimitClimbExtendUp.get());
    // SmartDashboard.putBoolean("Limit Switch Climb Rotate Up Right", LimitClimbRotateUpRight.get());
    // SmartDashboard.putBoolean("Limit Switch Climb Rotate Up Left", LimitClimbRotateUpLeft.get());
    // SmartDashboard.putBoolean("Limit Switch Intake Down", LimitIntakeDown.get());
    // SmartDashboard.putBoolean("Limit Switch Climb Extend Down", LimitClimbExtendDown.get());
    // SmartDashboard.putBoolean("Limit Switch Climb Rotate Down", LimitClimbRotateDown.get());
    SmartDashboard.putNumber("phase", phase);
    // SmartDashboard.putNumber("camera pipline", camera.getPipelineIndex());
    SmartDashboard.putNumber("Limelight Y (Up and Down) Offset:\n 'Greater the Y offset, Lower the Speed'",
        limelight_offset_Y);
    SmartDashboard.putNumber("Limelight X (Side to Side) Offset: 'Greater the X offset, the more Right we will shoot'",
        limelight_offset_X);
    // SmartDashboard.putNumber("CAN bus graph", ShooterFront.getBusVoltage());
    SmartDashboard.putNumber("Ultrasonic", UltraSonic.getVoltage());

    if (kitt_clock.get() > 0.02) {
      LED.LEDPeriodic(colorChoice);
      kitt_clock.reset();
    }

    area = ta.getDouble(0.0);


    SmartDashboard.putBoolean("Lined Up", linedup);

    SmartDashboard.putNumber("limelightX", x);
    SmartDashboard.putNumber("limelightY", y);
    SmartDashboard.putNumber("limelightArea", area);
    SmartDashboard.putNumber("Y Scaled", yScaled);

    SmartDashboard.putNumber("Yaw", navx.getYaw()+180);

    // if (m_LIDAR.get() < 1)
    //   dist = 0;
    // else
    //   dist = (m_LIDAR.getPeriod() * 1000000.0 / 10.0) - off; // convert to distance. sensor is high 10 us for every
    //                                                          // centimeter.
    // SmartDashboard.putNumber("Distance", dist); // put the distance on the dashboard

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    autoclock.stop();
    autoclock.reset();
    autoclock.start();
    phase = 0;
    navx.zeroYaw();

    IsClimberDown = false;
    IsIntakeDown = false;
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    double GyroY = navx.getYaw() + 180;
    switch (m_autoSelected) {
      case kThreeAuto:
        // Constantly Updated Variables
        shooterBackRPM = shooterBackEncoder.getVelocity();
        shooterFrontRPM = shooterFrontEncoder.getVelocity();
        x = tx.getDouble(0.0) + limelight_offset_X;
        y = ty.getDouble(0.0) + limelight_offset_Y;
        FrontRef = (3350 - 20 * (y));
        BackRef = (4850 - 100 * (y));

        // Intake Lowering Code
        if (!LimitIntakeDown.get() && autoclock.get() <= 3 && !IsIntakeDown) {
          intakeLift.set(-1.0);
        } else {
          intakeLift.set(0.0);
          IsIntakeDown = true;
        }

        // Climber Lowering Code
        if (!LimitClimbRotateDown.get() && autoclock.get() <= 5 && !IsClimberDown) {
        ClimbRotateLeft.set(-1.0);
        ClimbRotateRight.set(-1.0);
        } else {
        ClimbRotateLeft.set(0.0);
        ClimbRotateRight.set(0.0);
        IsClimberDown = true;
        }

        // 0: Driving Forward To First Ball And Intaking
        if (phase == 0) {
          drive.driveCartesian(0.0, 0.5, 0.0);
          if (autoclock.get() >= 0.5) {
            intakeSpin.set(1.0);
          }
          if (autoclock.get() >= 1) {
            phase = 1;
          }
        }

        // 1: Stop Moving And Keep Intaking
        if (phase == 1) {
          drive.driveCartesian(0.0, 0.0, 0.0);
          if (autoclock.get() >= 2) {
            phase = 2;
          }
        }

        // 2: Turn Around And Stop Intaking
        if (phase == 2) {
          drive.driveCartesian(0.0, 0.0, 0.5);
          if (GyroY > 340) {
            phase = 3;
            intakeSpin.set(0.0);
          }
        }

        // 3: Line Up And Get To The Right Shooting Speed
        if (phase == 3) {
          LED.LEDTeleopBack(shooterBackRPM, BackRef);
          LED.LEDTeleopFront(shooterFrontRPM, FrontRef);
          shooterBackPID.setReference(BackRef, CANSparkMax.ControlType.kVelocity);
          shooterFrontPID.setReference(FrontRef, CANSparkMax.ControlType.kVelocity);
          NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
          drive.driveCartesian(0.0, 0.0, x / 55);
          if ((shooterFrontRPM > FrontRef - 200) &&
              (shooterBackRPM > BackRef - 200) &&
              (shooterFrontRPM < FrontRef + 200) &&
              (shooterBackRPM < BackRef + 200) &&
              (Math.abs(x) < 2) && (x != 0)) {
            autoclock.reset();
            autoclock.start();
            phase = 4;
          }
        }

        // 4: Turn On Hopper, Maintain Shooting Speed
        if (phase == 4) {
          hopper.set(-1.0);
          LED.LEDTeleopBack(shooterBackRPM, BackRef);
          LED.LEDTeleopFront(shooterFrontRPM, FrontRef);
          shooterBackPID.setReference(BackRef, CANSparkMax.ControlType.kVelocity);
          shooterFrontPID.setReference(FrontRef, CANSparkMax.ControlType.kVelocity);
          if (autoclock.get() > 1.5) {
            phase = 5;
          }
        }

        // 5: Turn Off Shooter/Hopper Motors And Turn Around
        if (phase == 5) {
          hopper.set(0.0);
          ShooterBack.set(0.0);
          ShooterFront.set(0.0);
          LED.LEDShooterReset();

          drive.driveCartesian(0.0, 0.0, 0.4);
          if (GyroY > 130 && GyroY < 300) {
            phase = 6;
            autoclock.reset();
            autoclock.start();
          }
        }     

        // 6: Drive Forward Towards Ball And Spin Intake
        if (phase == 6) {
          double rotationSpeed;
          double speed;
          var result = camera.getLatestResult();
          // Turn Towards Ball
          if(autoclock.get() < 0.3){
            speed = autoclock.get() * 3.333
          }else if(autoclock.get() < 1.3){
            speed = 1.0;
          }else{
            speed = 0.2;
          }
          if (result.hasTargets() == true) {
            SmartDashboard.putNumber("Result", result.getBestTarget().getYaw());
            rotationSpeed = result.getBestTarget().getYaw() / 90;
            drive.driveCartesian(0.0, speed, rotationSpeed);
          } else {
            // Don't Turn If No Ball
            rotationSpeed = 0;
            drive.driveCartesian(0.0, speed, rotationSpeed);
          }
          // Spin Intake
          if (autoclock.get() > 1) {
            intakeSpin.set(1.0);
          }
          if ((autoclock.get() > 4 || UltraSonic.getAverageVoltage() < 2.5) && UltraSonic.getAverageVoltage() != 0 && speed != 1) {
            phase = 6.5;
          }
        }

        // 6.5: Slow Down When Close To Wall
        if (phase == 6.5) {
            intakeSpin.set(1.0);
            drive.driveCartesian(0.0, 0.2, 0.0);
            if (UltraSonic.getAverageVoltage() < 0.8) {
              phase = 7;            
              autoclock.reset();
              autoclock.start();
            }
        }

        // 7: Rotate To Face Target With 3rd Ball (and 4rth)
        if (phase == 7) {
          if (autoclock.get() > 1.5) {
            intakeSpin.set(1.0);
            drive.driveCartesian(0.0, 0.0, 0.3);
            if (GyroY > 330) {
              intakeSpin.set(0.0);
              drive.driveCartesian(0.0, 0.0, 0.0);
              phase = 8;
            }
          }
        }

        // 8: Line Up To Target And Get To Right Distance
        if (phase == 8) {
          NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
          drive.driveCartesian(0.0, -((y - 5.5) / 10), x / 55);
          if ((Math.abs(x) < 2.0) && (6.0 > y) && (y > 5.0) && y != 0) {
            phase = 9;
          }
        }

        // 9: Set Shooting Speed And Turn On Hopper
        if (phase == 9) {
          LED.LEDTeleopBack(shooterBackRPM, BackRef);
          LED.LEDTeleopFront(shooterFrontRPM, FrontRef);
          shooterBackPID.setReference(BackRef, CANSparkMax.ControlType.kVelocity);
          shooterFrontPID.setReference(FrontRef, CANSparkMax.ControlType.kVelocity);
          if ((shooterFrontRPM > FrontRef - 200) &&
              (shooterBackRPM > BackRef - 200) &&
              (shooterFrontRPM < FrontRef + 200) &&
              (shooterBackRPM < BackRef + 200)){
            hopper.set(-1.0);
            autoclock.reset();
            autoclock.start();
            phase = 10;
          }

        }

        // 10: Wait For Shooting To Finish
        if (phase == 10) {
          if (autoclock.get() > 2.5) {
            phase = 11;
          }
        }

        // 11: Turn Everything Off
        if (phase == 11) {
          drive.stopMotor();
          hopper.set(0.0);
          LED.LEDShooterReset();
          shooterBackPID.setReference(0.0, CANSparkMax.ControlType.kVoltage);
          shooterFrontPID.setReference(0.0, CANSparkMax.ControlType.kVoltage);
        }

        break;
      case kTwoAuto:
      default:

        shooterBackRPM = shooterBackEncoder.getVelocity();
        shooterFrontRPM = shooterFrontEncoder.getVelocity();

        x = tx.getDouble(0.0) + limelight_offset_X;
        y = ty.getDouble(0.0) + limelight_offset_Y;
        FrontRef = (3350 - 20 * (y));
        BackRef = (4850 - 100 * (y));

        // Changed from limit switch to opperated by time
        if (!LimitIntakeDown.get() && autoclock.get() <= 3 && !IsIntakeDown) {
          intakeLift.set(-1.0);
        } else {
          intakeLift.set(0.0);
          IsIntakeDown = true;
        }

        // Changed from limit switch to opperated by time
        if (!LimitClimbRotateDown.get() && autoclock.get() <= 5 && !IsClimberDown) {
        ClimbRotateLeft.set(-1.0);
        ClimbRotateRight.set(-1.0);
        } else {
        ClimbRotateLeft.set(0.0);
        ClimbRotateRight.set(0.0);
        IsClimberDown = true;
        }

        // 0: backing up/intake on
        if (phase == 0) {
          drive.driveCartesian(0.0, 0.5, 0.0);
          if (autoclock.get() >= 0.5) {
            intakeSpin.set(1.0);
          }
          if (autoclock.get() >= 1) {
            phase = 1;
          }
        }

        // 1: not expected jake
        if (phase == 1) {
          drive.driveCartesian(0.0, 0.0, 0.0);
          if (autoclock.get() >= 2) {
            phase = 2;
          }
        }

        // 2: turn around
        if (phase == 2) {
          drive.driveCartesian(0.0, 0.0, 0.5);
          if (GyroY > 340) {
            phase = 3;
            intakeSpin.set(0.0);
          }
        }

        // 3: line up limelight/shooter rpm control
        if (phase == 3) {
          LED.LEDTeleopBack(shooterBackRPM, BackRef);
          LED.LEDTeleopFront(shooterFrontRPM, FrontRef);
          shooterBackPID.setReference(BackRef, CANSparkMax.ControlType.kVelocity);
          shooterFrontPID.setReference(FrontRef, CANSparkMax.ControlType.kVelocity);
          NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
          drive.driveCartesian(0.0, 0.0, x / 55);
          if ((shooterFrontRPM > FrontRef - 200) &&
              (shooterBackRPM > BackRef - 200) &&
              (shooterFrontRPM < FrontRef + 200) &&
              (shooterBackRPM < BackRef + 200) &&
              (Math.abs(x) < 2) && (x != 0)) {
            autoclock.reset();
            autoclock.start();
            phase = 4;
          }
        }

        // 4: hopper on to shoot
        if (phase == 4) {
          hopper.set(-1.0);
          LED.LEDTeleopBack(shooterBackRPM, BackRef);
          LED.LEDTeleopFront(shooterFrontRPM, FrontRef);
          shooterBackPID.setReference(BackRef, CANSparkMax.ControlType.kVelocity);
          shooterFrontPID.setReference(FrontRef, CANSparkMax.ControlType.kVelocity);
          if (autoclock.get() > 1.5) {
            phase = 11;
          }
        }

        // 11: off
        if (phase == 11) {
          if(autoclock.get() < 2.5){
            drive.driveCartesian(0.0, -0.5, 0.0);
          }else{
            drive.stopMotor();
            hopper.set(0.0);
            LED.LEDShooterReset();
            shooterBackPID.setReference(0.0, CANSparkMax.ControlType.kVoltage);
            shooterFrontPID.setReference(0.0, CANSparkMax.ControlType.kVoltage);
          }
        }
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    if (colorChoice.toLowerCase().contains("red")) {
      camera.setPipelineIndex(0);
    } else {
      camera.setPipelineIndex(1);
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    double p_back = SmartDashboard.getNumber("P gain - BACK", kP_back);
    double i_back = SmartDashboard.getNumber("I gain - BACK", kI_back);
    double d_back = SmartDashboard.getNumber("D gain - BACK", kD_back);
    double iz_back = SmartDashboard.getNumber("I zone - BACK", kIz_back);
    double ff_back = SmartDashboard.getNumber("Feed forward - BACK", kFF_back);
    double max_back = SmartDashboard.getNumber("Max Output - BACK", kMaxOutput_back);
    double min_back = SmartDashboard.getNumber("Min Output - BACK", kMinOutput_back);

    double p_front = SmartDashboard.getNumber("P gain - FRONT", kP_front);
    double i_front = SmartDashboard.getNumber("I gain - FRONT", kI_front);
    double d_front = SmartDashboard.getNumber("D gain - FRONT", kD_front);
    double iz_front = SmartDashboard.getNumber("I zone - FRONT", kIz_front);
    double ff_front = SmartDashboard.getNumber("Feed forward - FRONT", kFF_front);
    double max_front = SmartDashboard.getNumber("Max Output - FRONT", kMaxOutput_front);
    double min_front = SmartDashboard.getNumber("Min Output - FRONT", kMinOutput_front);

    if ((p_back != kP_back)) {
      shooterBackPID.setP(p_back);
      kP_back = p_back;
    }
    if ((i_back != kI_back)) {
      shooterBackPID.setI(i_back);
      kI_back = i_back;
    }
    if ((d_back != kD_back)) {
      shooterBackPID.setD(d_back);
      kD_back = d_back;
    }
    if ((iz_back != kIz_back)) {
      shooterBackPID.setIZone(iz_back);
      kIz_back = iz_back;
    }
    if ((ff_back != kFF_back)) {
      shooterBackPID.setFF(ff_back);
      kFF_back = ff_back;
    }
    if ((max_back != kMaxOutput_back) || (min_back != kMinOutput_back)) {
      shooterBackPID.setOutputRange(min_back, max_back);
      kMinOutput_back = min_back;
      kMaxOutput_back = max_back;
    }

    if ((p_front != kP_front)) {
      shooterFrontPID.setP(p_front);
      kP_front = p_front;
    }
    if ((i_front != kI_front)) {
      shooterFrontPID.setI(i_front);
      kI_front = i_front;
    }
    if ((d_front != kD_front)) {
      shooterFrontPID.setD(d_front);
      kD_front = d_front;
    }
    if ((iz_front != kIz_front)) {
      shooterFrontPID.setIZone(iz_front);
      kIz_front = iz_front;
    }
    if ((ff_front != kFF_front)) {
      shooterFrontPID.setFF(ff_front);
      kFF_front = ff_front;
    }
    if ((max_front != kMaxOutput_front) || (min_front != kMinOutput_front)) {
      shooterFrontPID.setOutputRange(min_front, max_front);
      kMinOutput_front = min_front;
      kMaxOutput_front = max_front;
    }

    shooterBackRPM = shooterBackEncoder.getVelocity();
    shooterFrontRPM = shooterFrontEncoder.getVelocity();

    SmartDashboard.putNumber("ProcessVariable - Back", shooterBackRPM);
    SmartDashboard.putNumber("ProcessVariable - Front", shooterFrontRPM);

    SmartDashboard.putNumber("Front Reference", FrontRef);
    SmartDashboard.putNumber("Back Reference", BackRef);

    // Update Stick 1 Input Variables
    axisForward_1 = stick.getRawAxis(1);
    axisRight_1 = -stick.getRawAxis(0);
    axisRot_1 = -stick.getRawAxis(4);
    axis4rth_1 = stick.getRawAxis(5);
    axisRightTrigger_1 = stick.getRawAxis(3);
    axisLeftTrigger_1 = stick.getRawAxis(2);

    ButtonA_1 = stick.getRawButton(1);
    ButtonB_1 = stick.getRawButton(2);
    ButtonX_1 = stick.getRawButton(3);
    ButtonY_1 = stick.getRawButton(4);
    BumperLeft_1 = stick.getRawButton(5);
    BumperRight_1 = stick.getRawButton(6);

    // Update Stick 2 Input Variables
    axisForward_2 = stick2.getRawAxis(1);
    axisRight_2 = -stick2.getRawAxis(0);
    axisRot_2 = -stick2.getRawAxis(4);
    axis4rth_2 = stick2.getRawAxis(5);
    axisRightTrigger_2 = stick2.getRawAxis(3);
    axisLeftTrigger_2 = stick2.getRawAxis(2);

    ButtonA_2 = stick2.getRawButton(1);
    ButtonB_2 = stick2.getRawButton(2);
    ButtonX_2 = stick2.getRawButton(3);
    ButtonY_2 = stick2.getRawButton(4);
    BumperLeft_2 = stick2.getRawButton(5);
    BumperRight_2 = stick2.getRawButton(6);

    FrontRef = (3350 - 20 * (y));
    BackRef = (4850 - 100 * (y));

    LED.LEDlineup(linedup);

    // RPM Calibration
    if (stick.getRawButtonPressed(8)) {
      target_rpm_front += 100;
    } else if (stick.getRawButtonPressed(7)) {
      target_rpm_front -= 100;
    }
    if (stick2.getRawButtonPressed(8)) {
      target_rpm_back += 100;
    } else if (stick2.getRawButtonPressed(7)) {
      target_rpm_back -= 100;
    }

    // Shooting Code
    if (ButtonX_2 == true) {
      LED.LEDTeleopBack(shooterBackRPM, BackRef);
      LED.LEDTeleopFront(shooterFrontRPM, FrontRef);

      shooterBackPID.setReference(BackRef, CANSparkMax.ControlType.kVelocity);
      shooterFrontPID.setReference(FrontRef, CANSparkMax.ControlType.kVelocity);

    } else if (ButtonB_2 == true) {
      ShooterFront.set(1.0);
      ShooterBack.set(1.0);
    } else if (axisForward_2 > 0.05) {
      ShooterFront.set(axisForward_2);
      ShooterBack.set(axisForward_2);
    } else {
      shooterBackPID.setReference(0.0, CANSparkMax.ControlType.kVoltage);
      shooterFrontPID.setReference(0.0, CANSparkMax.ControlType.kVoltage);
      LED.LEDShooterReset();
    }

    // Hopper
    if (BumperRight_2 == true) {
      hopper.set(-1.0);
    } else if (BumperLeft_2 == true) {
      hopper.set(1.0);
    } else if (axis4rth_2 > 0.05 || axis4rth_2 < -0.05) {
      hopper.set(axis4rth_2);
    } else {
      hopper.set(0.0);
    }

    // Intake Lift
    if (BumperRight_1 == true && !LimitIntakeUp.get()) {
      intakeLift.set(1.0);
    } else if (BumperLeft_1 == true && !LimitIntakeDown.get()) {
      intakeLift.set(-1.0);
    } else {
      intakeLift.set(0.0);
    }

    // Intake Spin
    if (axisRightTrigger_1 > 0.1) {
      intakeSpin.set(
          axisRightTrigger_1);
    } else if (ButtonA_1 == true) {
      intakeSpin.set(-1.0);
    } else {
      intakeSpin.set(0.0);
    }

    // Climb extend
    if (ButtonA_2 == true && !LimitClimbExtendDown.get()) {
      climbExtendLeft.set(0.5);
      climbExtendRight.set(-0.5);
    } else if (ButtonY_2 == true && !LimitClimbExtendUp.get()) {
      climbExtendLeft.set(-0.5);
      climbExtendRight.set(0.5);
    } else {
      climbExtendLeft.set(0.0);
      climbExtendRight.set(0.0);
    }

    // Climb Rotate Right
    if (axisLeftTrigger_2 > 0.05 && !LimitClimbRotateDown.get()) {
      ClimbRotateRight.set(-axisLeftTrigger_2);
    } else if (axisRightTrigger_2 > 0.05 && !LimitClimbRotateUpRight.get()) {
      ClimbRotateRight.set(axisRightTrigger_2);
    } else {
      ClimbRotateRight.set(0.0);
    }

    // Climb Rotate Left
    if (axisLeftTrigger_2 > 0.05 && !LimitClimbRotateDown.get()) {
      ClimbRotateLeft.set(-axisLeftTrigger_2);
    } else if (axisRightTrigger_2 > 0.05 && !LimitClimbRotateUpLeft.get()) {
      ClimbRotateLeft.set(axisRightTrigger_2);
    } else {
      ClimbRotateLeft.set(0.0);
    }

    // LED controls
    if (stick.getRawButton(9)) {
      colorChoice = "red";
    } else if (stick.getRawButton(10)) {
      colorChoice = "blue";
    }

    // if(x == 0.0 && y == 0.0){
    // yScaled = 0.0;
    // }else{
    // yScaled = (y+25)/50-1;
    // }

    // Drive Code
    if (axisLeftTrigger_1 > 0.1) {
      x = tx.getDouble(0.0) + limelight_offset_X;
      y = ty.getDouble(0.0) + limelight_offset_Y;
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
      if ((x > 1.0 || x < -1.0)) {
        drive.driveCartesian(-axisRight_1, -axisForward_1, x / 55);
        linedup = false;
      } else if (x != 0) {
        drive.driveCartesian(-axisRight_1, -axisForward_1, 0.0);
        linedup = true;
      }
    } else {
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1);
      drive.driveCartesian(-axisRight_1, -axisForward_1, -axisRot_1 * 0.5);
      linedup = false;
    }

    // photon (temporal)
    // var result = camera.getLatestResult();
    // var photonyaw = result.getBestTarget().getYaw();
    // SmartDashboard.putNumber("camerayaw", photonyaw);
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }
}
