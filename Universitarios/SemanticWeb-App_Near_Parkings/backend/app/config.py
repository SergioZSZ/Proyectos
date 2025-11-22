from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    APP_NAME: str
    DATABASE_URL: str

    
# Indicamos a Pydantic que lea el archivo .env
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")
    
# Instancia global para usar las variables entorno de .env
settings = Settings()