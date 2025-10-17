package org.firstinspires.ftc.teamcode.Drive;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Intake Motor TEST")
public class IntakeMotorTEST extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor intakeMotor;

    @Override
    public void runOpMode() {
        intakeMotor = hardwareMap.get(DcMotor.class, "testServo");


        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.a) {
                intakeMotor.setPower(1.0);
            }
            else if (gamepad1.b) {
                intakeMotor.setPower(-1.0);
            } else {
                intakeMotor.setPower(0.0);
            }

        }
    }
}