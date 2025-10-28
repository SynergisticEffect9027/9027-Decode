package org.firstinspires.ftc.teamcode.Drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 */
@TeleOp(name="Robot Centric Drive", group="Linear OpMode")
public class RobotCentricDrive extends LinearOpMode {

    // Declare servos and motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotor intakeMotor = null;
    private CRServo flywheel;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        flywheel = hardwareMap.get(CRServo.class, "Flywheel");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");


        //Setting motor direction and config servo for continuous movement.
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        flywheel.resetDeviceConfigurationForOpMode();

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
                lateral = lateral * 0.55;
                yaw = yaw * 0.55;
            }
            if (gamepad1.left_trigger > 0.000 && gamepad1.left_trigger < 0.001) {
                axial = axial / 0.55;
                lateral = lateral / 0.55;
                yaw = yaw / 0.55;
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

            if (gamepad1.a) {
                flywheel.setPower(1.0);
            } else if (gamepad1.b) {
                flywheel.setPower(-1.0);
            } else {
                flywheel.setPower(0.0);
            }

            //Change these once we get Dominic's equation for velocity power.
            if (gamepad1.x) {
                intakeMotor.setPower(1.0);
            } else if (gamepad1.y) {
                intakeMotor.setPower(-1.0);
            } else {
                intakeMotor.setPower(0.0);
            }

                // Show the elapsed game time and wheel power.
                telemetry.addData("Status", "Run Time: " + runtime.toString());
                telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
                telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
                telemetry.update();
            }
        }
    }
