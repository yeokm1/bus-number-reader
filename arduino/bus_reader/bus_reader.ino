#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

#include <LiquidCrystal.h> //Replaces the LiquidCrystal.h
#include <EEPROM.h>
#include <Wire.h>



#define LED_13_PIN 13
#define UP A15
#define DOWN A14
#define RESET A13
#define CONTRAST_PIN 44  //Make sure this is on a PWM pin. I'm using the Arduino Mega 2560 R3 so pin number will differ for others.
#define BACKLIGHT_PIN 46   //Make sure this is on a PWM pin
#define BACKLIGHT_BRIGHTNESS 0   //PWM value  I don't need the backlight so set to 0
#define CONTRAST 0             //PWM value  You may need to change this value

#define LCD_CHAR_LENGTH 16

//LCD wiring without POT from http://engineeringlearning.blogspot.sg/2013/10/interfacing-lcd-without-potentiometer.html
LiquidCrystal lcd(26, 30, 34, 38, 42, 49);
int display_number;

void setName(int number){
  
  char str[10];
  
  sprintf(str,"MKMS %d",number);
  ble_set_name(str);
  
  // Init. and start BLE library.
  ble_begin();

}


void setup(){
  Serial.begin(9600);
  display_number = EEPROM.read(0);
  //Disable LED Pin 13 (Optional). I just hate the constant LED light.
  pinMode(LED_13_PIN, OUTPUT);
  digitalWrite(LED_13_PIN, LOW);
  
  pinMode(UP, INPUT);
  pinMode(DOWN, INPUT);
  pinMode(RESET, INPUT);
  
  //Start LCD
  pinMode(CONTRAST_PIN, OUTPUT);
  pinMode(BACKLIGHT_PIN, OUTPUT);
  analogWrite(CONTRAST_PIN,CONTRAST);
  analogWrite(BACKLIGHT_PIN,BACKLIGHT_BRIGHTNESS);
  lcd.begin(LCD_CHAR_LENGTH, 2);
  char str[3];
  sprintf(str,"%d",display_number);
  printThisOnLCDLine(str, 1);
  char dis[16];
  sprintf(dis,"Broadcasting %d",display_number);
  printThisOnLCDLine(dis, 0);
  setName(display_number);
}

void loop(){
  char str[3];
  
  if (digitalRead(UP) == HIGH) {
    sprintf(str,"%d",++display_number);  
    printThisOnLCDLine(str, 1);
  } else if (digitalRead(DOWN) == HIGH) {
    sprintf(str,"%d",--display_number); 
    printThisOnLCDLine(str, 1);
  } else if (digitalRead(RESET) == HIGH) {
    char dis[16];
    printThisOnLCDLine("                ", 0);
    setName(display_number);
    sprintf(dis,"Broadcasting %d",display_number);
    printThisOnLCDLine(dis, 0);
    if(display_number < 256)
      EEPROM.write(0, display_number);
  }
  ble_do_events();
  delay(100); 
}

void printThisOnLCDLine(String text, int line){

  lcd.setCursor (0, line);  
  lcd.print(text);
}







