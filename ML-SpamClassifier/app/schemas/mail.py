from enum import Enum
from pydantic import BaseModel
class Tipo(str, Enum):
    spam = "SPAM"
    ham = "HAM"


class MailBase(BaseModel):
    message: str
    
class MailInput(MailBase):
    pass

class MailOutput(MailBase):
    tipo: Tipo
    pass 
