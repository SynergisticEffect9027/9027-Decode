package org.firstinspires.ftc.teamcode.Drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import android.util.Size;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import java.util.List;

/*
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 */
@TeleOp(name="Robot Centric Drive (BLUE)", group="Linear OpMode")
public class RobotCentricDriveBlue extends LinearOpMode {

    // Declare servos and motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor intakeMotor = null;
    private DcMotorEx leftTurretMotor;
    private DcMotorEx rightTurretMotor;
    private DcMotor turretMotor;
    private CRServo flywheel;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private final double kP = 0.002;
    private final double kI = 0.0;
    private final double kD = 0.0005;
    private double previousError = 0;
    private double integral = 0;
    private Boolean turretEnable = true;
    private double ticksPerRev = 28;


    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        turretMotor = hardwareMap.get(DcMotor.class, "turretMotor");
        flywheel = hardwareMap.get(CRServo.class, "Flywheel");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        leftTurretMotor = hardwareMap.get(DcMotorEx.class, "leftTurretMotor");
        rightTurretMotor = hardwareMap.get(DcMotorEx.class, "rightTurretMotor");


        //Setting motor direction and config servo for continuous movement.
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        leftTurretMotor.setDirection(DcMotor.Direction.REVERSE);
        rightTurretMotor.setDirection(DcMotor.Direction.FORWARD);
        flywheel.resetDeviceConfigurationForOpMode();

        leftTurretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightTurretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftTurretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightTurretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        aprilTag = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .setCameraResolution(new Size(640, 480))
                .build();


        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        //Get Henry's code to smash together for turret autolocking and other things that I just don't think about.
        //Meet with drivers on control preferences.
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

//            //This is the dampener code. The robot will slow down by 45% when the left trigger is pressed
            if (gamepad1.left_trigger > 0.000) {
                axial = axial * 0.55;
                lateral = lateral * 0.65;
                yaw = yaw * 0.65;
            }
            if (gamepad1.left_trigger > 0.000 && gamepad1.left_trigger < 0.001) {
                axial = axial / 0.55;
                lateral = lateral / 0.65;
                yaw = yaw / 0.65;
            }

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower = axial - lateral + yaw;
            double backRightPower = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower /= max;
                frontRightPower /= max;
                backLeftPower /= max;
                backRightPower /= max;
            }

            // Send calculated power to wheels
            frontLeftDrive.setPower(Math.pow(frontLeftPower, 3));
            frontRightDrive.setPower(Math.pow(frontRightPower, 3));
            backLeftDrive.setPower(Math.pow(backLeftPower, 3));
            backRightDrive.setPower(Math.pow(backRightPower, 3));

            if (gamepad2.b){
                flywheel.setPower(-1.0);
            }
            if (gamepad2.right_trigger > 0.000000) {
                flywheel.setPower(1.0);
            } else {
                flywheel.setPower(0.0);
            }

            //Change these once we get Dominic's equation for velocity power.
            if (gamepad2.x) {
                intakeMotor.setPower(1.0);
            } else if (gamepad2.y) {
                intakeMotor.setPower(-1.0);
            } else {
                intakeMotor.setPower(0.0);
            }
            if (gamepad2.left_trigger > 0.0000) {
                leftTurretMotor.setVelocity((5000*ticksPerRev)/60);
                rightTurretMotor.setVelocity((5000*ticksPerRev)/60);
            } else if (gamepad2.left_bumper) {
                leftTurretMotor.setVelocity((2600*ticksPerRev)/60);
                rightTurretMotor.setVelocity((2600*ticksPerRev)/60);
            } else {
                leftTurretMotor.setPower(0);
                rightTurretMotor.setPower(0);
            }

//            List<AprilTagDetection> detections = aprilTag.getDetections();
//            if  (gamepad2.b){
//                turretEnable = !turretEnable;
//            }
//
//            if (turretEnable) {
//                AprilTagDetection redGoalTag = null;
//
//                for (AprilTagDetection tag : detections) {
//                    if (tag.id == 20) {
//                        redGoalTag = tag;
//                        break;
//                    }
//                }
//
//                if (redGoalTag != null) {
//                    double targetX = redGoalTag.center.x;
//                    double imageCenterX = 640 / 2.0;
//                    double error = targetX - imageCenterX;
//
//                    integral += error;
//                    double derivative = error - previousError;
//
//                    double output = (kP * error) + (kI * integral) + (kD * derivative);
//                    output = Math.max(-0.3, Math.min(0.3, output));
//
//                    turretMotor.setPower(output);
//                    previousError = error;
//                    telemetry.addData("Error", error);
//                } else {
//                    turretMotor.setPower(0);
//                    telemetry.addLine("No target detected");
//                }
//            } else {
//                if (gamepad2.dpad_left) {
//                    turretMotor.setPower(-0.3);
//                } else if (gamepad2.dpad_right) {
//                    turretMotor.setPower(0.3);
//                } else {
//                    turretMotor.setPower(0);
//                }
//            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("Turrent Velocity Left", leftTurretMotor.getVelocity());
            telemetry.addData("Turrent Velocity Right", rightTurretMotor.getVelocity());
            telemetry.update();
        }
    }
}
