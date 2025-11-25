package org.firstinspires.ftc.teamcode.Drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Continuous Servo TEST")
public class ContinuousServoTEST extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private CRServo testServoleft;
    private CRServo testServoRight;

    @Override
    public void runOpMode() {
        testServoleft = hardwareMap.get(CRServo.class, "testServoleft");
        testServoRight = hardwareMap.get(CRServo.class,"testServoRight");

        testServoleft.resetDeviceConfigurationForOpMode();
        testServoRight.resetDeviceConfigurationForOpMode();


        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.a) {
                testServoleft.setPower(1.0);
                testServoRight.setPower(-1.0);
            } else if (gamepad1.b) {
                testServoleft.setPower(-1.0);
                testServoRight.setPower(1.0);
            } else {
                testServoleft.setPower(0.0);
                testServoRight.setPower(0.0);
            }

            }
        }
    }