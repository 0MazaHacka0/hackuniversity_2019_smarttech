FROM python:3.7

MAINTAINER "Dmitry Abakumov <killerinshadow2@gmail.com>"

RUN mkdir src
WORKDIR src

COPY app.py .
COPY requirements.txt .

RUN pip install -r requirements.txt

CMD ["python", "app.py"]
