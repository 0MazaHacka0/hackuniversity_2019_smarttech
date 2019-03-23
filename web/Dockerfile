FROM python:3.7

ENV PYTHONUNBUFFERED 1

RUN apt-get update && \
    apt-get install -y postgresql-client && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt /config/
RUN pip install -r /config/requirements.txt

RUN mkdir /src
WORKDIR /src