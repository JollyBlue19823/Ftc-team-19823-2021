package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@Config
@TeleOp(name="Decentswerve", group="Linear Opmode")
public class Decentswerve extends LinearOpMode {


    private AnalogInput BLE = null;
    private AnalogInput BRE = null;
    private AnalogInput FLE = null;
    private AnalogInput FRE = null;

    private CRServo BLT = null;
    private CRServo BRT = null;
    private CRServo FLT = null;
    private CRServo FRT = null;

    private DcMotorEx BLD = null;
    private DcMotorEx BRD = null;
    private DcMotorEx FLD = null;
    private DcMotorEx FRD = null;

    private CRServo INS = null;
    private Servo INFL = null;
    private Servo OTD = null;
    private DcMotorEx OTE = null;

    private Servo DROP = null;

    FtcDashboard dashboard;

    public static double Kp = 0.2;
    public static double Ki = 0;
    public static double Kd = 0.0001;
    public static double Kf = 0;

    public static double BLTreference = 0;
    double BLTintegralSum = 0;
    double BLTlastError = 0;
    double BLTerror = 0;

    public static double BRTreference = 0;
    double BRTintegralSum = 0;
    double BRTlastError = 0;
    double BRTerror = 0;

    public static double FLTreference = 0;
    double FLTintegralSum = 0;
    double FLTlastError = 0;
    double FLTerror = 0;

    public static double FRTreference = 0;
    double FRTintegralSum = 0;
    double FRTlastError = 0;
    double FRTerror = 0;

    public static double tolerance = 0;

    ElapsedTime BLTtimer =  new ElapsedTime();
    ElapsedTime BRTtimer =  new ElapsedTime();
    ElapsedTime FLTtimer =  new ElapsedTime();
    ElapsedTime FRTtimer =  new ElapsedTime();

    double BLP = 0;
    double BRP = 0;
    double FLP = 0;
    double FRP = 0;

    double x1 = 0;
    double y1 = 0;
    public static double INFP = 0.5;
    public static double OTDP = 0.5;



    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        BLE = hardwareMap.get(AnalogInput.class, "BLE");
        BRE = hardwareMap.get(AnalogInput.class, "BRE");
        FLE = hardwareMap.get(AnalogInput.class, "FLE");
        FRE = hardwareMap.get(AnalogInput.class, "FRE");

        BLD = hardwareMap.get(DcMotorEx.class, "BLD");
        BRD = hardwareMap.get(DcMotorEx.class, "BRD");
        FLD = hardwareMap.get(DcMotorEx.class, "FLD");
        FRD = hardwareMap.get(DcMotorEx.class, "FRD");

        BLT = hardwareMap.get(CRServo.class, "BLT");
        BRT = hardwareMap.get(CRServo.class, "BRT");
        FLT = hardwareMap.get(CRServo.class, "FLT");
        FRT = hardwareMap.get(CRServo.class, "FRT");

        INS = hardwareMap.get(CRServo.class, "INS");
        INFL = hardwareMap.get(Servo.class, "INFL");
        OTD = hardwareMap.get(Servo.class, "OTD");
        OTE = hardwareMap.get(DcMotorEx.class,"OTE");

        DROP = hardwareMap.get(Servo.class, "DROP");

        BLT.setDirection(CRServo.Direction.REVERSE);
        BRT.setDirection(CRServo.Direction.REVERSE);
        FLT.setDirection(CRServo.Direction.REVERSE);
        FRT.setDirection(CRServo.Direction.REVERSE);

        dashboard = FtcDashboard.getInstance();


        OTD.setPosition(0.35);
        DROP.setPosition(0.5);


        waitForStart();

        while (opModeIsActive()) {

            y1 = gamepad1.right_stick_y;
            x1 = gamepad1.left_stick_x*179.9999999;

            INFP = Range.clip(INFP,0.1,0.9);

            if (gamepad1.x){
                y1 = gamepad1.right_stick_y;
                x1 = gamepad1.left_stick_x*179.9999999;

                BRTreference = -45;
                BLTreference = 45;
                FRTreference = 45;
                FLTreference = -45;

                BLD.setPower(y1*-1);
                BRD.setPower(y1);
                FRD.setPower(y1);
                FLD.setPower(y1*-1);
            }
            else if (gamepad1.dpad_right){
                y1 = gamepad1.right_stick_y;
                x1 = gamepad1.left_stick_x*179.9999999;

                BRTreference = 18;
                BLTreference = 18;
                FRTreference = -35;
                FLTreference = -35;

                BLD.setPower(y1);
                BRD.setPower(y1);
                FRD.setPower(y1);
                FLD.setPower(y1);
            }
            else if (gamepad1.dpad_left){
                y1 = gamepad1.right_stick_y;
                x1 = gamepad1.left_stick_x*179.9999999;

                BRTreference = -18;
                BLTreference = -18;
                FRTreference = 35;
                FLTreference = 35;

                BLD.setPower(y1);
                BRD.setPower(y1);
                FRD.setPower(y1);
                FLD.setPower(y1);
            }
            else {
                y1 = gamepad1.right_stick_y;
                x1 = gamepad1.left_stick_x*179.9999999;

                BLTreference = x1;
                BRTreference = x1;
                FLTreference = x1;
                FRTreference = x1;

                BLD.setPower(y1);
                BRD.setPower(y1);
                FRD.setPower(y1);
                FLD.setPower(y1);
            }

            if (gamepad1.a){
                INFP = 0.9;
            }
            if (!gamepad1.a) {
                if (INFP != 0.1){
                    INFP -= 0.029;
                }
            }
            INFL.setPosition(INFP);
            if (gamepad1.y){
                OTDP = 0;
            }
            if (!gamepad1.y){
                    OTDP = 0.35;
            }
            OTD.setPosition(OTDP);

            //depositing pos = 0
            //resting pos = 0.35
            //init pos = 0.1
            double OTEV = 0;
            OTEV = gamepad1.right_trigger*-0.75;
            if (gamepad1.right_bumper){
                OTEV = -1;
            }
            OTE.setPower(OTEV);


            BLP = BLE.getVoltage() * 74.16;
            BRP = BRE.getVoltage() * 74.16;
            FLP = FLE.getVoltage() * 74.16;
            FRP = FRE.getVoltage() * 74.16;

            if(INFP>0.2){
                INS.setPower(1);}
            else if(gamepad1.b){
                INS.setPower(-0.25);}
            else{
                INS.setPower(0);}



            if(BLP <= -180) {
                BLP += 360;
            }
            if(BLP > 180) {
                BLP -= 360;
            }
            if(BRP <= -180) {
                BRP += 360;
            }
            if(BRP > 180) {
                BRP -= 360;
            }
            if(FLP <= -180) {
                FLP += 360;
            }
            if(FLP > 180) {
                FLP -= 360;
            }
            if(FRP <= -180) {
                FRP += 360;
            }
            if(FRP > 180) {
                FRP -= 360;
            }







            BLTerror = AngleUnit.normalizeDegrees(BLTreference - BLP);
            if (Math.abs(BLTerror) > tolerance ) {
                BLTerror = AngleUnit.normalizeDegrees(BLTreference - BLP);
                 double BLTderivative = (BLTerror - BLTlastError) / BLTtimer.seconds();
                 BLTintegralSum = BLTintegralSum + (BLTerror * BLTtimer.seconds());
                 double BLTout = (Kp*BLTerror)+(Kd*BLTderivative)+(Ki*BLTintegralSum)+(Kf*Math.signum(BLTerror));
                 BLT.setPower(BLTout/10);

                 BLTlastError = BLTerror;
                 BLTtimer.reset();

            }
            if (Math.abs(BLTerror)<tolerance){
                BLT.setPower(0);
            }


            BRTerror = AngleUnit.normalizeDegrees(BRTreference- BRP);
            if (Math.abs(BRTerror) > tolerance ) {
                BRTerror = AngleUnit.normalizeDegrees(BRTreference- BRP);
                double BRTderivative = (BRTerror - BRTlastError) / BRTtimer.seconds();
                BRTintegralSum = BRTintegralSum + (BRTerror * BRTtimer.seconds());
                double BRTout = (Kp*BRTerror)+(Kd*BRTderivative)+(Ki*BRTintegralSum)+(Kf*Math.signum(BRTerror));
                BRT.setPower(BRTout/10);

                BRTlastError = BRTerror;
                BRTtimer.reset();

            }
            if (Math.abs(BRTerror)<tolerance){
                BRT.setPower(0);
            }


            FLTerror = AngleUnit.normalizeDegrees(FLTreference - FLP);
            if (Math.abs(FLTerror) > tolerance ) {
                FLTerror = AngleUnit.normalizeDegrees(FLTreference - FLP);

                double FLTderivative = (FLTerror - FLTlastError) / FLTtimer.seconds();
                FLTintegralSum = FLTintegralSum + (FLTerror * FLTtimer.seconds());
                double FLTout = (Kp*FLTerror)+(Kd*FLTderivative)+(Ki*FLTintegralSum)+(Kf*Math.signum(FLTerror));
                FLT.setPower(FLTout/10);

                FLTlastError = FLTerror;
                FLTtimer.reset();

            }
            if (Math.abs(FLTerror)<tolerance){
                FLT.setPower(0);
            }


            FRTerror = AngleUnit.normalizeDegrees(FRTreference - FRP);
            if (Math.abs(FRTerror) > tolerance ) {

                FRTerror = AngleUnit.normalizeDegrees(FRTreference - FRP);

                double FRTderivative = (FRTerror - FRTlastError) / FRTtimer.seconds();
                FRTintegralSum = FRTintegralSum + (FRTerror * FRTtimer.seconds());
                double FRTout = (Kp*FRTerror)+(Kd*FRTderivative)+(Ki*FRTintegralSum)+(Kf*Math.signum(FRTerror));
                FRT.setPower(FRTout/10);

                FRTlastError = FRTerror;
                FRTtimer.reset();

            }
            if (Math.abs(FRTerror)<tolerance){
                FRT.setPower(0);
            }
            telemetry.addData("BLTreference",BLTreference);
            telemetry.addData("BRTreference",BRTreference);
            telemetry.addData("FLTreference",FLTreference);
            telemetry.addData("FRTreference",FRTreference);
            telemetry.addData("BLP",BLP);
            telemetry.addData("BRP",BRP);
            telemetry.addData("FLP",FLP);
            telemetry.addData("FRP",FRP);
            telemetry.addData("OTE",OTE.getCurrentPosition());
            telemetry.addData("OTEV",OTEV);
            telemetry.addData("G",gamepad1.right_trigger);
            telemetry.update();
        }
    }
    }
