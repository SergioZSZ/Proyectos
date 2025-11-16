import sys
import os
from logging.config import fileConfig
from sqlalchemy import engine_from_config, pool
from alembic import context

# --- Añadir la carpeta del proyecto al path (para poder importar app.*)
sys.path.append(os.path.abspath('.'))

# --- Importar settings y Base
from app.config import settings
from app.db import Base
from app import models  # Importa todos los modelos para que Alembic los detecte

# --- Configuración de Alembic
config = context.config

# --- Cargar la URL de la base de datos desde settings (tu .env)
config.set_main_option("sqlalchemy.url", settings.DATABASE_URL)

# --- Configurar logging (opcional, viene por defecto)
if config.config_file_name is not None:
    fileConfig(config.config_file_name)

# --- Aquí se le dice a Alembic qué metadata usar
target_metadata = Base.metadata

# --- Función para modo "offline" (no ejecuta SQL directamente)
def run_migrations_offline():
    url = config.get_main_option("sqlalchemy.url")
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
    )

    with context.begin_transaction():
        context.run_migrations()

# --- Función para modo "online" (ejecuta SQL real)
def run_migrations_online():
    connectable = engine_from_config(
        config.get_section(config.config_ini_section),
        prefix="sqlalchemy.",
        poolclass=pool.NullPool,
    )

    with connectable.connect() as connection:
        context.configure(connection=connection, target_metadata=target_metadata)

        with context.begin_transaction():
            context.run_migrations()

# --- Ejecutar según modo
if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
