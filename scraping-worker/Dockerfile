FROM python:3.10.19-slim-bookworm

WORKDIR /worker/

COPY requirements.txt requirements.txt

RUN pip install -r requirements.txt

COPY . .

ENTRYPOINT [ "python3", "main.py" ]