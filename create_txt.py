import wikipedia
import os
import random

# pip install wikipedia

def genera_contenuto_da_wiki(titolo_ricerca: str, numero_frasi: int = 1) -> str:
    try:
        # Imposta la lingua italiana
        wikipedia.set_lang("it")
        riassunto = wikipedia.summary(titolo_ricerca, sentences=numero_frasi)
        
        return riassunto
    except wikipedia.exceptions.PageError:
        return f"Pagina non trovata per l'argomento: {titolo_ricerca}."
    except wikipedia.exceptions.DisambiguationError as e:
        return genera_contenuto_da_wiki(e.options[0], numero_frasi)
    except Exception as e:
        return f"Si è verificato un errore: {e}"

# Lista di argomenti da cui estrarre i contenuti
argomenti_wiki = [
#    "Python", 
#    "Java",
#    "Nazione", 
#    "Web", 
#    "Acqua",
#    "Energia", 
#    "Rivoluzione",
#    "Marte",
#    "Supermercato",
#    "Film"
    "Pranzo",
    "Cibo",
    "Bottiglia",
    "Libro",
    "Computer",
    "Carta",
    "DNA",
    "Televisione",
    "Pioggia",
    "Università"
]

def crea_file_wiki(cartella_output: str = "docs", numero_file: int = 100):
    
    if not os.path.exists(cartella_output):
        os.makedirs(cartella_output)
    
    # Genera 100 file, prendendo casualmente da uno dei 10 argomenti
    for i in range(1, numero_file + 1):
        argomento = random.choice(argomenti_wiki)
        contenuto_logico = genera_contenuto_da_wiki(argomento, numero_frasi=random.randint(1, 3))
        
        # Rimuove caratteri non validi dal nome del file
        nome_base = argomento.replace(' ', '_').replace('(', '').replace(')', '')[:20] 
        nome_file = os.path.join(cartella_output, f"{i:03d}_{nome_base}.txt")
        
        try:
            with open(nome_file, 'w', encoding='utf-8') as f:
                f.write(f"{argomento}\n")
                f.write("----------------------------------------------------------\n\n")
                f.write(contenuto_logico)

            if i % 10 == 0:
                 print(f"File {i}/{numero_file} creato ({argomento}).")

        except IOError as e:
            print(f"Errore durante la creazione del file {nome_file}: {e}")

    print(f"\nGenerazione completata! Creati {numero_file} file con contenuti da Wikipedia.")

if __name__ == "__main__":
    crea_file_wiki(numero_file=100)
    