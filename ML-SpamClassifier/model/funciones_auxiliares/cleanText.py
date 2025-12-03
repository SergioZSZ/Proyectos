import re
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer

'''
nltk.download("stopwords")
nltk.download("WordNetLemmatizer")
'''
STOPWORDS_ENGLISH = stopwords.words("english")
lem = WordNetLemmatizer()

def clean_text_ML(text):
    #minus
    text = text.lower()
    #urls
    text = re.sub(r"http\S+|www\.\S+", " ", text)
    #nums
    text = re.sub(r"\d+", " ", text)
    #cualquier cosa que no sea palabra o espacio
    text = re.sub(r"[^\w\s]", " ", text)
    #espacios duplicados
    text = re.sub(r"\s+", " ", text).strip()
    #quitar stopwords y lematizar si dejas

    tokens = []
    for tok in text.split():
        if tok in STOPWORDS_ENGLISH:
            continue
        tok = lem.lemmatize(tok)
        tokens.append(tok)

    return " ".join(tokens)


def clean_text_MLP(text):
    #minus
    text = text.lower()
    #urls
    text = re.sub(r"http\S+|www\.\S+", " ", text)
    #nums
    text = re.sub(r"\d+", " ", text)
    #cualquier cosa que no sea palabra o espacio
    text = re.sub(r"[^\w\s]", " ", text)
    #espacios duplicados
    text = re.sub(r"\s+", " ", text).strip()
    #quitar stopwords y lematizar si dejas
    
    return text
