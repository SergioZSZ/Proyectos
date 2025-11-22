FROM python:3.12-slim

WORKDIR /app

# Dependencias de sistema (útil para compilar algunas libs)
RUN apt-get update && apt-get install -y build-essential && rm -rf /var/lib/apt/lists/*

# Instalar dependencias Python
COPY requirements.txt .
RUN pip install -r requirements.txt

# Copiar el código de la app
COPY . .



# Comando de arranque
CMD ["uvicorn", "app.main:app", "--reload", "--host", "0.0.0.0", "--port", "8000"]
