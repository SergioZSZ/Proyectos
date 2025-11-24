import joblib
import sys


MODEL_PATH = "models/SpamModelLR.joblib"  
#Cargar modelo
model = joblib.load(MODEL_PATH)

#Ejemplos de correos
mensajes = [
    "WIN a free prize now! Click the link to claim.",
    "Hola, te mando el documento actualizado.",
    "Limited offer! Get your reward before midnight.",
    "Tu pedido ha sido enviado y llegará mañana.",
    "Congratulations, you've been selected for a special promotion!",
    "Reunión confirmada para mañana a las 10.",
    "URGENT! Your account will be suspended unless you verify now.",
    "Puedes revisar este archivo cuando tengas tiempo.",
    "You won a cash prize! Reply with your info.",
    "Recordatorio: tienes una cita médica el jueves.",
    "Claim your free voucher before it expires.",
    "El paquete está listo para recoger en la oficina.",
    "Don't miss this chance to earn money fast!",
    "Gracias por tu ayuda con el proyecto.",
    "Important update: your bank account requires verification.",
    "Aquí tienes el resumen que pediste el otro día.",
    "FREE entry to the event! Register now.",
    "La reunión semanal se pasa al viernes.",
    "You've been chosen for an exclusive money reward!",
    "¿Puedes enviarme el informe cuando esté listo?"
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
    


