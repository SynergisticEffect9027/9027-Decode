package org.firstinspires.ftc.teamcode.Drive;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Turret Motor TEST")
public class TurretMotorTEST extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftTurretMotor;
    private DcMotor rightTurretMotor;

    @Override
    public void runOpMode() {
        leftTurretMotor = hardwareMap.get(DcMotor.class, "leftTurretMotor");
        rightTurretMotor = hardwareMap.get(DcMotor.class, "rightTurretMotor");

        leftTurretMotor.setDirection(DcMotor.Direction.FORWARD);
        rightTurretMotor.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.left_trigger > 0.0000 ) {
                leftTurretMotor.setPower(1.0);
                rightTurretMotor.setPower(1.0);
            }
            else if (gamepad1.left_trigger > 0.000 && gamepad1.left_trigger < 0.001) {
                leftTurretMotor.setPower(0);
                rightTurretMotor.setPower(0);
            }

        }
    }
}