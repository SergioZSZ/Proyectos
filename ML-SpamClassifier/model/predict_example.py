import joblib
import sys


MODEL_PATH = "model/models/SpamModelLR.joblib"  
#Cargar modelo
model = joblib.load(MODEL_PATH)

#Ejemplos de correos
mensajes = [
    "WIN a free prize now! Click the link to claim.",
    "Hi, I'm sending you the updated document.",
    "Limited offer! Get your reward before midnight.",
    "Your order has been shipped and will arrive tomorrow.",
    "Congratulations, you have been selected for a special promotion!",
    "Meeting confirmed for tomorrow at 10 AM.",
    "URGENT! Your account will be suspended unless you verify now.",
    "You can review this file whenever you have time.",
    "You won a cash prize! Reply with your information.",
    "Reminder: you have a medical appointment on Thursday.",
    "Claim your free voucher before it expires.",
    "The package is ready for pickup at the office.",
    "Don't miss this chance to earn money fast!",
    "Thank you for your help with the project.",
    "Important update: your bank account requires verification.",
    "Here is the summary you asked for the other day.",
    "FREE entry to the event! Register now.",
    "The weekly meeting is moved to Friday.",
    "You have been chosen for an exclusive money reward!",
    "Can you send me the report when it is ready?"
]

#Predecir
preds = model.predict(mensajes)

#Mostrar resultados
sys.stdout = open("Predicciones de Ejemplo.txt","w")
for text, pred in zip(mensajes, preds):
    label = "SPAM" if pred == 1 else "HAM"
    print("================================")
    print("Mensaje:", text)
    print("Predicción:", label,"\n")
    


