package org.firstinspires.ftc.teamcode.Drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Passthrough+Launcher TEST")
public class IntakePassthrough extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor intakeMotor = null;
    private CRServo testServo;

    @Override
    public void runOpMode() {
        testServo = hardwareMap.get(CRServo.class, "testServo");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        testServo.resetDeviceConfigurationForOpMode();
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);


        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.a) {
                testServo.setPower(1.0);
            } else if (gamepad1.b) {
                testServo.setPower(-1.0);
            } else {
                testServo.setPower(0.0);
            }
            if (gamepad1.x) {
                intakeMotor.setPower(1.0);
            } else if (gamepad1.y) {
                intakeMotor.setPower(-1.0);
            } else {
                intakeMotor.setPower(0.0);
            }

        }
    }
}