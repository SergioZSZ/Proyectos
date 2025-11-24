

import re
import string
from nltk.corpus import stopwords           #lista de las palabras inutiles
from nltk.stem import WordNetLemmatizer     #transformar a palabras base(going ->go)

STOPWORDS_ENGLISH = set(stopwords.words("english"))
lemmatizer =WordNetLemmatizer()

###FUNCION PROPIA PARA LIMPIAR TEXTO

def clean_text(text: str)->str:
    #convertimos en texto si no lo es
    if not isinstance(text,str):
        text = str(text)
    
#1º pasamos a minusculas
    text = text.lower()

#2º eliminamos URLS por patrones(los puntos hay que poner \ para que entren en el patron y no puede haber espacios)
    text = re.sub(r"http\S+|www\.\S+"," ",text)
    
#3º eliminamos emails por patron
    text = re.sub(r"\S+@\S+\.\S+"," ",text)
    
#4º eliminamos numeros
    text = re.sub(r"\d+"," ",text)
    
#5º eliminamos html(borra cualquier contenido entre < > lo de dentro)
    text = re.sub(r"<.*?>", " ", text)
    
#6º eliminamos signos de puntuacion
    text = text.translate(str.maketrans("", "", string.punctuation))
    
#7º eliminamos espacios duplicados y .strip para si hay espacios al principio o final
    text = re.sub(r"\s+", " ", text).strip()

#8º convertimos en tokens(palabras en un array) el string
    tokens = text.split()

#9º AHORA quitamos las stopwords(palabras que no sirven para nada) y lematizamos
    cleaned_tokens = []
    for tok in tokens:
        if tok in STOPWORDS_ENGLISH:    #si es una stopword no la añadimos
            continue
        lemma = lemmatizer.lemmatize(tok)
        cleaned_tokens.append(lemma)
        
    return " ".join(cleaned_tokens) # ["win", "free", "offer"] → "win free offer"
    

    