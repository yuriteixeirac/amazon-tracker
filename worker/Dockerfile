FROM mcr.microsoft.com/playwright/python:v1.57.0-noble

WORKDIR /worker

COPY requirements.txt requirements.txt

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

ENTRYPOINT ["python", "main.py"]
