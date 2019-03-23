#include <SoftwareSerial.h>
SoftwareSerial GSMport(4, 5); // RX, TX пины gsm

void setup(){
Serial.begin(9600);  //скорость порта
delay(5000); //время на инициализацию GSM модуля
Serial.println("GPRS test");
GSMport.begin(57600);
delay(200);
GSMport.println("AT+CMEE=2");
delay(200);
gprs_init(); // инициализация gps 
delay(3000);
}





void loop() {
  if (GSMport.available())
    Serial.write(GSMport.read());
  if (Serial.available())
    GSMport.write(Serial.read());
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

  GSMport.println("AT+HTTPSSL=1");
  Serial.println("GPRG init complete");
} 


String ReadGSM() {  //функция чтения данных от GSM модуля
  int c;
  String v;
  while (GSMport.available()) {  //сохраняем входную строку в переменную v
    c = GSMport.read();
    v += char(c);
    delay(10);
  }
  return v;
}
