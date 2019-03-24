#include <SoftwareSerial.h>
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x27,16,2);
#include <ArduinoJson.h>

char buf[130];
SoftwareSerial GSMport(4, 5); // RX, TX пины gsm
long previousMillis = 0;  
long interval = 5000;
int c = 101;
String _response = "";


StaticJsonDocument<300> doc; 

void setup(){
lcd.init(); 
lcd.backlight();
lcd.setCursor(0,0);
lcd.print("Init...");




delay(50);
Serial.begin(19200);  //скорость порта
delay(5000); //время на инициализацию GSM модуля
Serial.println("GPRS test");
GSMport.begin(57600);
delay(200);
//GSMport.println("AT+CMEE=2");
//delay(200);
gprs_init(); // инициализация gps 
delay(5000);
}


void loop() {
//  unsigned long currentMillis = millis();
//  if(currentMillis - previousMillis > interval) 
//    previousMillis = currentMillis;   

      gprs_send();  //Запрос на сервер

   _response = ReadGSM2();   //Чтение строки в буфер
  Serial.println(_response); 
  
  int index_start, index_end;                                 //Переменные начала и конца подстроки
  index_start = _response.indexOf("{");                       //Начало строки от {  
  index_end = _response.indexOf("}");                         //Конец строки до }
  _response = _response.substring(index_start,index_end+1); //Выделение нужной строки
  //Serial.println(_response); 
                                  
  DeserializationError error = deserializeJson(doc, _response);
  
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.c_str());
    return;
  }
  const char* end = doc["end"];
  int id = doc["id"];
  const char* start = doc["start"];
  Serial.println(start);
  Serial.println(end);
  lcd.setCursor(0,0); 
  lcd.print(start); 
  lcd.setCursor(0,1); 
  lcd.print(end);
  
  

//  lcd.clear();
//  lcd.print(_response);                                       
  
  _response ="";     //Обнуление буфера
       
  Serial.println("Send done");
  delay(2000);
    

    
}



void gprs_init() {  //Процедура начальной инициализации GSM модуля
  int d = 500;
  int ATsCount = 7;
  String ATs[] = {  //массив АТ команд
    "AT+SAPBR=3,1,\"CONTYPE\",\"GPRS\"",  //Установка настроек подключения
    "AT+SAPBR=3,1,\"APN\",\"internet.tele2.ru\"",
    "AT+SAPBR=3,1,\"USER\",\"tele2\"",
    "AT+SAPBR=3,1,\"PWD\",\"tele2\"",
    "AT+SAPBR=1,1",  //Устанавливаем GPRS соединение
    "AT+HTTPINIT",  //Инициализация http сервиса
    "AT+HTTPPARA=\"CID\",1"  //Установка CID параметра для http сессии
  };
  int ATsDelays[] = {6, 1, 1, 1, 3, 3, 1}; //массив задержек
  Serial.println("GPRG init start");
  for (int i = 0; i < ATsCount; i++) {
    Serial.println(ATs[i]);  //посылаем в монитор порта
    GSMport.println(ATs[i]);  //посылаем в GSM модуль
    delay(d * ATsDelays[i]);
    Serial.println(ReadGSM());  //показываем ответ от GSM модуля
    delay(d);
  }
  Serial.println("GPRG init complete");
} 


String ReadGSM2() {  //функция чтения данных от GSM модуля
  String v;
  while (v.length()<=125) {  //сохраняем входную строку в переменную v
    
    if(GSMport.available()>0)
    v += (char)GSMport.read();
    
  }
  return v;
}

//______________________________________________

String ReadGSM() {  //функция чтения данных от GSM модуля
  String v;
  while (GSMport.available()>0) {  //сохраняем входную строку в переменную v
    v += (char)GSMport.read();
  }
  return v;
}

int readline(int readch, char *buffer, int len) {
    static int pos = 0;
    int rpos;

    if (readch > 0) {
        switch (readch) {
            case '\r': // Ignore CR
                break;
            case '\n': // Return on new-line
                rpos = pos;
                pos = 0;  // Reset position index ready for next time
                return rpos;
            default:
                if (pos < len-1) {
                    buffer[pos++] = readch;
                    buffer[pos] = 0;
                }
        }
    }
    return 0;
}



//}

void gprs_send() {  //Процедура отправки данных на сервер
  int d = 500;
  Serial.println("Send start");
  Serial.println("setup url");
  GSMport.println("AT+HTTPPARA=\"URL\",\"http://89.108.65.120:5000/semen/test\"");      
  delay(d*4);
  Serial.println(ReadGSM());
  Serial.println("GET url");
  GSMport.println("AT+HTTPACTION=0");
  delay(d * 8);
  Serial.println(ReadGSM());
  GSMport.println("AT+HTTPREAD");
  
}
