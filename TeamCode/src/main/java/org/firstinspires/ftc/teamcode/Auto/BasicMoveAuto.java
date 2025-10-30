package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

    /*
     * This OpMode illustrates the concept of driving a path based on encoder counts.
     * The code is structured as a LinearOpMode
     *
     * The code REQUIRES that you DO have encoders on the wheels,
     *   otherwise you would use: RobotAutoDriveByTime;
     *
     *  This code ALSO requires that the drive Motors have been configured such that a positive
     *  power command moves them forward, and causes the encoders to count UP.
     *
     *   The desired path in this example is:
     *   - Drive forward for 48 inches
     *   - Spin right for 12 Inches
     *   - Drive Backward for 24 inches
     *   - Stop and close the claw.
     *
     *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
     *  that performs the actual movement.
     *  This method assumes that each movement is relative to the last stopping place.
     *  There are other ways to perform encoder based moves, but this method is probably the simplest.
     *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
     */

    @Autonomous(name="Basic Drive Forward", group="Robot")
    public class BasicMoveAuto extends LinearOpMode {

        private ElapsedTime runtime = new ElapsedTime();
        private DcMotor frontLeftDrive = null;
        private DcMotor frontRightDrive = null;
        private DcMotor backLeftDrive = null;
        private DcMotor backRightDrive = null;

        // Calculate the COUNTS_PER_INCH for your specific drive train.
        // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
        // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
        // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
        // This is gearing DOWN for less speed and more torque.
        // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
        static final double     COUNTS_PER_MOTOR_REV    = 537.6 ;    // eg: TETRIX Motor Encoder
        static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
        static final double     WHEEL_DIAMETER_INCHES   = 3.9 ;     // For figuring circumference
        static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                (WHEEL_DIAMETER_INCHES * 3.1415);
        static final double     DRIVE_SPEED             = 0.3;
        static final double     TURN_SPEED              = 0.4;

        @Override
        public void runOpMode() {

            // Initialize the drive system variables.
            frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
            frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
            backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
            backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

            // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
            // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
            // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
            frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
            backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
            backRightDrive.setDirection(DcMotor.Direction.REVERSE);

            frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Send telemetry message to indicate successful Encoder reset
            telemetry.addData("Starting at",  "%7d :%7d",
                    frontLeftDrive.getCurrentPosition(),
                    frontRightDrive.getCurrentPosition(),
                    backLeftDrive.getCurrentPosition(),
                    backRightDrive.getCurrentPosition());
            telemetry.update();
            waitForStart();

            // Step through each leg of the path,
            // Note: Reverse movement is obtained by setting a negative distance (not speed)
            encoderDrive(DRIVE_SPEED,  20,  20, 3);
            //encoderDrive(TURN_SPEED,   5, -5, 0.15);
            //encoderDrive(DRIVE_SPEED, -6, -6, 0.235);

            telemetry.addData("Path", "Complete");
            telemetry.update();
            sleep(1000);  // pause to display final telemetry message.
        }

        /*
         *  Method to perform a relative move, based on encoder counts.
         *  Encoders are not reset as the move is based on the current position.
         *  Move will stop if any of three conditions occur:
         *  1) Move gets to the desired position
         *  2) Move runs out of time
         *  3) Driver stops the OpMode running.
         */
        public void encoderDrive(double speed,
                                 double leftInches, double rightInches,
                                 double timeoutS) {
            int frontLeftDriveTarget;
            int frontRightDriveTarget;
            int backLeftDriveTarget;
            int backRightDriveTarget;

            // Ensure that the OpMode is still active
            if (opModeIsActive()) {

                // Determine new target position, and pass to motor controller
                frontLeftDriveTarget = frontLeftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
                frontRightDriveTarget = frontRightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
                backLeftDriveTarget = backLeftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
                backRightDriveTarget = backRightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
                frontLeftDrive.setTargetPosition(frontLeftDriveTarget);
                frontRightDrive.setTargetPosition(frontRightDriveTarget);
                backLeftDrive.setTargetPosition(backLeftDriveTarget);
                backRightDrive.setTargetPosition(backRightDriveTarget);

                // Turn On RUN_TO_POSITION
                frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // reset the timeout time and start motion.
                runtime.reset();
                frontLeftDrive.setPower(Math.abs(speed));
                frontRightDrive.setPower(Math.abs(speed));
                backLeftDrive.setPower(Math.abs(speed));
                backRightDrive.setPower(Math.abs(speed));

                // keep looping while we are still active, and there is time left, and both motors are running.
                // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
                // its target position, the motion will stop.  This is "safer" in the event that the robot will
                // always end the motion as soon as possible.
                // However, if you require that BOTH motors have finished their moves before the robot continues
                // onto the next step, use (isBusy() || isBusy()) in the loop test.
                while (opModeIsActive() &&
                        (runtime.seconds() < timeoutS) &&
                        (frontLeftDrive.isBusy() && frontRightDrive.isBusy() && backLeftDrive.isBusy() && backRightDrive.isBusy())) {

                    // Display it for the driver.
                    telemetry.addData("Running to",  " %7d :%7d", frontLeftDriveTarget,  frontRightDriveTarget, backLeftDriveTarget, backRightDriveTarget);
                    telemetry.addData("Currently at",  " at %7d :%7d",
                            frontLeftDrive.getCurrentPosition(), frontRightDrive.getCurrentPosition(), backLeftDrive.getCurrentPosition(), backRightDrive.getCurrentPosition());
                    telemetry.update();
                }

                // Stop all motion;
                frontLeftDrive.setPower(0);
                frontRightDrive.setPower(0);
                backLeftDrive.setPower(0);
                backRightDrive.setPower(0);

                // Turn off RUN_TO_POSITION
                frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                sleep(250);   // optional pause after each move.
            }
        }
    }

