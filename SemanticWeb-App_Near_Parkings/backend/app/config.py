import os
from pydantic_settings import BaseSettings, SettingsConfigDict

#en local pilla las de mi .env y en fly las de .env SECRETO que genera, todo bien mientras no suba
#mi .env a github, que entonces si usara ese
class Settings(BaseSettings):
    APP_NAME: str = "Near_Parkings"
    DATABASE_URL: str

    

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")
    
# Instancia global para usar las variables entorno de .env
settings = Settings()