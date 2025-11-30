import joblib
import os

def evaluar_y_guardar(nombre_archivo, ejemplos, modelo):
    """
    ejemplos = [("HAM", "mensaje..."), ("SPAM","mensaje..."), ...]
    Modelo = sklearn (LinearSVC, LogisticRegression, etc.)
    """

    # Sacamos solo los mensajes (X)
    mensajes = [msg for label, msg in ejemplos]

    # Predicción del modelo sklearn
    preds_bin = modelo.predict(mensajes)

    # Crear carpeta si no existe
    os.makedirs("model/examples", exist_ok=True)
    f = open(f"model/examples/{nombre_archivo}.txt", "w", encoding="utf-8")

    # Contadores
    ham_ok = ham_bad = spam_ok = spam_bad = 0

    for (label_real, msg), pred in zip(ejemplos, preds_bin):

        # A tu modelo sklearn SPAM = 1, HAM = 0
        label_pred = "SPAM" if pred == 1 else "HAM"

        # Contar aciertos/fallos
        if label_real == "HAM":
            if label_pred == "HAM":
                ham_ok += 1
            else:
                ham_bad += 1
        else:  # real = SPAM
            if label_pred == "SPAM":
                spam_ok += 1
            else:
                spam_bad += 1

        f.write("================================\n")
        f.write(f"Real: {label_real}\n")
        f.write(f"Predicción: {label_pred}\n")
        f.write(f"Mensaje: {msg}\n\n")

    # Resultados finales
    total = len(ejemplos)
    aciertos = ham_ok + spam_ok
    porcentaje = (aciertos / total) * 100

    f.write("\n\n=========== RESULTADOS ===========\n")
    f.write(f"HAM acertados: {ham_ok}\n")
    f.write(f"HAM fallados: {ham_bad}\n")
    f.write(f"SPAM acertados: {spam_ok}\n")
    f.write(f"SPAM fallados: {spam_bad}\n")
    f.write(f"Acierto total: {porcentaje:.2f}%\n")
    f.close()
    
    
    
model = joblib.load("model/models/modelML.joblib")


# ====================================================
# EJEMPLOS FÁCILES (30/30)
# ====================================================
faciles = [
    ("HAM", "Hi John, just confirming our meeting for tomorrow."),
    ("SPAM", "WIN a free iPhone today! Click here to claim your prize."),

    ("HAM", "Thanks for sending the report, I’ll review it this afternoon."),
    ("SPAM", "Get rich fast with this simple online trick!"),

    ("HAM", "Can you share the updated schedule when you have time?"),
    ("SPAM", "Limited-time offer: 90% off all products!"),

    ("HAM", "The package has arrived at the office, you can pick it up."),
    ("SPAM", "Congratulations, you've been selected for a cash reward!"),

    ("HAM", "Let me know if you need help with the presentation."),
    ("SPAM", "Your account will be suspended unless you click here now."),

    ("HAM", "Great work on the project, the client was very happy."),
    ("SPAM", "Claim your free crypto bonus before midnight!"),

    ("HAM", "I attached the notes from today’s call, please check them."),
    ("SPAM", "You won a vacation to the Caribbean! Confirm your details."),

    ("HAM", "The server maintenance completed successfully this morning."),
    ("SPAM", "Act now! Only a few vouchers left."),

    ("HAM", "Lunch at 1 PM still works for me, see you there."),
    ("SPAM", "This miracle pill burns fat without exercise."),

    ("HAM", "Here are the photos from yesterday's event."),
    ("SPAM", "Earn €500/day working from home, no experience needed."),

    ("HAM", "I will be working remotely this afternoon."),
    ("SPAM", "Update your banking information to avoid penalties."),

    ("HAM", "The meeting room is booked from 11 to 12 as requested."),
    ("SPAM", "Your password is expiring, verify your account now."),

    ("HAM", "Thanks for your quick response earlier."),
    ("SPAM", "Get instant access to thousands of movies for free."),

    ("HAM", "Please review the attached draft when you have a moment."),
    ("SPAM", "FINAL NOTICE: unpaid fees must be resolved today."),

    ("HAM", "Your order will be delivered tomorrow between 10 and 12."),
    ("SPAM", "Don't miss out on your FREE gift card, confirm now."),

    ("HAM", "Could you update the spreadsheet with the latest numbers?"),
    ("SPAM", "Your device is infected! Run an immediate system scan."),

    ("HAM", "I uploaded the documents to the shared drive."),
    ("SPAM", "Double your Bitcoin instantly with this promo link."),

    ("HAM", "Let me know if you prefer the PDF or the Word version."),
    ("SPAM", "This exclusive offer ends in 2 hours, act quickly!"),

    ("HAM", "The office printer is working again, IT fixed it."),
    ("SPAM", "Confirm your email to avoid permanent deactivation.")
]

# ====================================================
# EJEMPLOS DIFÍCILES
# ====================================================
dificiles = [
    ("HAM", "Hi team, the compliance department requires a quick review of the attached policy update."),
    ("SPAM", "Your account has been temporarily restricted due to unusual login activity. Please verify your identity."),

    ("HAM", "The security certificate on the analytics dashboard expired this morning. IT is renewing it."),
    ("SPAM", "The document you attempted to access requires re-authentication. Log in through the updated security portal."),

    ("HAM", "Could you verify whether the supplier responded to the revised purchase order from last week?"),
    ("SPAM", "Your payroll information could not be validated. Update your banking details to avoid payment delays."),

    ("HAM", "Reminder: VPN access will be intermittent tonight due to scheduled updates."),
    ("SPAM", "A confidential message has been assigned to your inbox. Access requires verification."),
    
    ("HAM", "We detected delays in the email queue; some outgoing messages may arrive late."),
    ("SPAM", "The company-wide security audit found inconsistencies in your profile. Resolve them immediately."),

    ("HAM", "Thanks for updating the financial forecast. Please also review the new expense entries."),
    ("SPAM", "Your mailbox storage is over quota. Increase your capacity using the admin panel."),

    ("HAM", "Legal needs confirmation on clause revisions for the new contract draft."),
    ("SPAM", "Your encrypted files could not be restored without confirming your security credentials."),

    ("HAM", "IT informed us that access to archived emails may be slow while indexing completes."),
    ("SPAM", "The IT department detected a breach in your session. Reset your password now."),

    ("HAM", "I updated the permissions on the shared drive; you should have write access."),
    ("SPAM", "The invoice submitted yesterday has been rejected. Re-upload your credentials to continue."),

    ("HAM", "Please confirm if the data export process finished successfully on your end."),
    ("SPAM", "Your multi-factor authentication token has expired. Generate a new token through the secure gateway."),

    ("HAM", "Analytics flagged an unusual spike in traffic, but operations confirmed it was expected."),
    ("SPAM", "Due to suspicious activity, your SSO session has been terminated. Restore access through the link."),

    ("HAM", "Could you recheck the formatting of the quarterly report? Some headers appear misaligned."),
    ("SPAM", "Immediate action required: your compliance forms are incomplete. Submit verification details now."),

    ("HAM", "The authentication token for the dev environment expired. I requested a new one from IT."),
    ("SPAM", "Your access to the corporate dashboard has been limited pending security confirmation."),

    ("HAM", "Let me know if the file uploaded correctly to the secure drop portal."),
    ("SPAM", "A restricted document has been shared with you, but your identity could not be confirmed."),

    ("HAM", "The internal monitoring system reported elevated load earlier, but it's stable now."),
    ("SPAM", "Your HR profile contains outdated information. Update your credentials to avoid lockout.")
]

# ====================================================
# EJECUCIÓN
# ====================================================
evaluar_y_guardar("faciles", faciles, model)
evaluar_y_guardar("dificiles", dificiles, model)

