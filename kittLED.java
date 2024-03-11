package frc.CompBot2022;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class kittLED {

  AddressableLED kittEye_leds = new AddressableLED(1);
  AddressableLEDBuffer kittEye_buf = new AddressableLEDBuffer(250);

  int step = 0;
  int underglowStep = 0;
  int intensity;
  boolean increasing = true;
  double factor;
  double calc;

  public void LEDInit() {
    kittEye_leds.setLength(kittEye_buf.getLength());
    kittEye_leds.setData(kittEye_buf);
    kittEye_leds.start();

  }

  public void LEDPeriodic(String colorChoice) {
    String color = colorChoice.toLowerCase();
    if (color.contains("red")) {
      // Red KITT lights
      if (step == 0) {
        kittEye_buf.setRGB(step, 255, 0, 0);
        kittEye_buf.setRGB(step + 1, 50, 0, 0);
        kittEye_buf.setRGB(step + 2, 10, 0, 0);
        kittEye_buf.setRGB(step + 3, 5, 0, 0);
        kittEye_buf.setRGB(step + 4, 0, 0, 0);
      } else if (step == 1) {
        kittEye_buf.setRGB(step + 2, 0, 0, 0);
        kittEye_buf.setRGB(step + 1, 5, 0, 0);
        kittEye_buf.setRGB(step, 255, 0, 0);
        kittEye_buf.setRGB(step - 1, 50, 0, 0);
      } else if (step == 2) {
        kittEye_buf.setRGB(step, 255, 0, 0);
        kittEye_buf.setRGB(step - 1, 50, 0, 0);
        kittEye_buf.setRGB(step - 2, 10, 0, 0);
      } else if (step == 3) {
        kittEye_buf.setRGB(step, 255, 0, 0);
        kittEye_buf.setRGB(step - 1, 50, 0, 0);
        kittEye_buf.setRGB(step - 2, 10, 0, 0);
        kittEye_buf.setRGB(step - 3, 5, 0, 0);
      } else if (step < 24) {// 23
        kittEye_buf.setRGB(step, 255, 0, 0);
        kittEye_buf.setRGB(step - 1, 50, 0, 0);
        kittEye_buf.setRGB(step - 2, 10, 0, 0);
        kittEye_buf.setRGB(step - 3, 5, 0, 0);
        kittEye_buf.setRGB(step - 4, 0, 0, 0);
      } else if (step == 24) {// 22
        kittEye_buf.setRGB(step - 1, 50, 0, 0);
        kittEye_buf.setRGB(step - 2, 255, 0, 0);
        kittEye_buf.setRGB(step - 3, 5, 0, 0);
        kittEye_buf.setRGB(step - 4, 0, 0, 0);
      } else if (step == 25) {// step 25, led 21
        kittEye_buf.setRGB(step - 2, 10, 0, 0);
        kittEye_buf.setRGB(step - 3, 50, 0, 0);
        kittEye_buf.setRGB(step - 4, 255, 0, 0);
      } else if (step < 45) { // steps 26-47, leds 22-0
        kittEye_buf.setRGB(49 - step, 0, 0, 0);
        kittEye_buf.setRGB(48 - step, 5, 0, 0);
        kittEye_buf.setRGB(47 - step, 10, 0, 0);
        kittEye_buf.setRGB(46 - step, 50, 0, 0);
        kittEye_buf.setRGB(45 - step, 255, 0, 0);
      }

      // Underglow Red
      for (int i = 56; i < 128; i++) {
        kittEye_buf.setRGB(i, intensity, 0, 0);
      }

      if (intensity == 255) {
        increasing = false;
      }
      if (intensity == 0) {
        increasing = true;
      }

      if (increasing) {
        intensity += 5;
      } else {
        intensity -= 5;
      }

    } else if (color.contains("blue")) {
      // Blue KITT lights
      if (step == 0) {
        kittEye_buf.setRGB(step, 0, 0, 255);
        kittEye_buf.setRGB(step + 1, 0, 0, 50);
        kittEye_buf.setRGB(step + 2, 0, 0, 10);
        kittEye_buf.setRGB(step + 3, 0, 0, 5);
        kittEye_buf.setRGB(step + 4, 0, 0, 0);
      } else if (step == 1) {
        kittEye_buf.setRGB(step + 2, 0, 0, 0);
        kittEye_buf.setRGB(step + 1, 0, 0, 5);
        kittEye_buf.setRGB(step, 0, 0, 255);
        kittEye_buf.setRGB(step - 1, 0, 0, 50);
      } else if (step == 2) {
        kittEye_buf.setRGB(step, 0, 0, 255);
        kittEye_buf.setRGB(step - 1, 0, 0, 50);
        kittEye_buf.setRGB(step - 2, 0, 0, 10);
      } else if (step == 3) {
        kittEye_buf.setRGB(step, 0, 0, 255);
        kittEye_buf.setRGB(step - 1, 0, 0, 50);
        kittEye_buf.setRGB(step - 2, 0, 0, 10);
        kittEye_buf.setRGB(step - 3, 0, 0, 5);
      } else if (step < 24) {// 23
        kittEye_buf.setRGB(step, 0, 0, 255);
        kittEye_buf.setRGB(step - 1, 0, 0, 50);
        kittEye_buf.setRGB(step - 2, 0, 0, 10);
        kittEye_buf.setRGB(step - 3, 0, 0, 5);
        kittEye_buf.setRGB(step - 4, 0, 0, 0);
      } else if (step == 24) {// 22
        kittEye_buf.setRGB(step - 1, 0, 0, 50);
        kittEye_buf.setRGB(step - 2, 0, 0, 255);
        kittEye_buf.setRGB(step - 3, 0, 0, 5);
        kittEye_buf.setRGB(step - 4, 0, 0, 0);
      } else if (step == 25) {// step 25, led 21
        kittEye_buf.setRGB(step - 2, 0, 0, 10);
        kittEye_buf.setRGB(step - 3, 0, 0, 50);
        kittEye_buf.setRGB(step - 4, 0, 0, 255);
      } else if (step < 45) { // steps 26-47, leds 22-0
        kittEye_buf.setRGB(49 - step, 0, 0, 0);
        kittEye_buf.setRGB(48 - step, 0, 0, 5);
        kittEye_buf.setRGB(47 - step, 0, 0, 10);
        kittEye_buf.setRGB(46 - step, 0, 0, 50);
        kittEye_buf.setRGB(45 - step, 0, 0, 255);

      }

      // Blue Underglow
      for (int i = 56; i < 128; i++) {
        kittEye_buf.setRGB(i, 0, 0, intensity);
      }

      if (intensity == 255) {
        increasing = false;
      }
      if (intensity == 0) {
        increasing = true;
      }

      if (increasing) {
        intensity += 5;
      } else {
        intensity -= 5;
      }

    }

    kittEye_leds.setData(kittEye_buf);

    step++;
    if (step > 44) {
      step = 0;
    }
  }

  public void LEDTeleopBack(double ShooterBackRPM, double targetRPM) {
    factor = targetRPM / 17;
    calc = Math.round(ShooterBackRPM / factor);
    SmartDashboard.putNumber("factor", factor);
    SmartDashboard.putNumber("calc", calc);
    if ((targetRPM - 100) > ShooterBackRPM) {
      for (int i = 0; i < calc; i++) {
        kittEye_buf.setRGB(40-i, 255, 0, 0);
      }
    }else if((targetRPM + 200) < ShooterBackRPM){
      for (int i = 0; i < 17; i++) {
        kittEye_buf.setRGB(i+24, 255, 0, 0);
      }
    }else{
      for (int i = 0; i < 17; i++) {
        kittEye_buf.setRGB(i+24, 0, 255, 0);
      }
    }

  }

  public void LEDTeleopFront(double ShooterFrontRPM, double targetRPM) {
    factor = targetRPM / 17;
    calc = Math.round(ShooterFrontRPM / factor);
    SmartDashboard.putNumber("factor", factor);
    SmartDashboard.putNumber("calc", calc);
    if ((targetRPM - 100) > ShooterFrontRPM) {
      for (int i = 0; i < calc; i++) {
        kittEye_buf.setRGB(i+143, 255, 0, 0);
      }
    }else if((targetRPM + 200) < ShooterFrontRPM){
      for (int i = 0; i < 17; i++) {
        kittEye_buf.setRGB(i+143, 255, 0, 0);
      }
    }else{
      for (int i = 0; i < 17; i++) {
        kittEye_buf.setRGB(i+143, 0, 255, 0);
      }
    }

  }

  public void LEDShooterReset(){
    for (int i = 0; i < 17; i++) {
      kittEye_buf.setRGB(i+24, 0, 0, 0);
      kittEye_buf.setRGB(i+143, 0, 0, 0);
    }
    
  }

  public void LEDlineup(boolean linedup){
    if (linedup){
      for (int i = 0; i < 15; i++) {
        kittEye_buf.setRGB(i+41, 0, 0, 255);
        kittEye_buf.setRGB(i+128, 0, 0, 255);
      }
    }else{
      for (int i = 0; i < 15; i++) {
        kittEye_buf.setRGB(i+41, 0, 0, 0);
        kittEye_buf.setRGB(i+128, 0, 0, 0);
      }      
    }
  }
}
