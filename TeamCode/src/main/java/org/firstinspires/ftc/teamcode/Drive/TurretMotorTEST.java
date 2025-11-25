package org.firstinspires.ftc.teamcode.Drive;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Turret Motor TEST")
public class TurretMotorTEST extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private DcMotorEx leftTurretMotor;
    private DcMotorEx rightTurretMotor;
    private CRServo testServoleft;
    private CRServo testServoRight;
    private double ticksPerRev = 28;

    @Override
    public void runOpMode() {
        leftTurretMotor = hardwareMap.get(DcMotorEx.class, "leftTurretMotor");
        rightTurretMotor = hardwareMap.get(DcMotorEx.class, "rightTurretMotor");
        testServoleft = hardwareMap.get(CRServo.class, "testServoleft");
        testServoRight = hardwareMap.get(CRServo.class,"testServoRight");

        testServoleft.resetDeviceConfigurationForOpMode();
        testServoRight.resetDeviceConfigurationForOpMode();

        leftTurretMotor.setDirection(DcMotor.Direction.FORWARD);
        rightTurretMotor.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.left_trigger > 0.0000) {
                leftTurretMotor.setVelocity((5000*ticksPerRev)/60);
                rightTurretMotor.setVelocity((5000*ticksPerRev)/60);
            } else if (gamepad1.left_bumper) {
                leftTurretMotor.setVelocity((2600*ticksPerRev)/60);
                rightTurretMotor.setVelocity((2600*ticksPerRev)/60);
            } else {
                leftTurretMotor.setPower(0);
                rightTurretMotor.setPower(0);
            }
            if (gamepad1.right_trigger > 0.000000) {
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