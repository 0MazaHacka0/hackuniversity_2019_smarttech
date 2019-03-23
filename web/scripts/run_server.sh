#!/usr/bin/env bash

python manage.py migrate --no-input
python manage.py collectstatic --no-input
gunicorn api.wsgi -b 0.0.0.0:8000