import requests
from bs4 import BeautifulSoup
import time


# URL della tua app Spring Boot
BASE_URL = "http://localhost:8080/"

# Lista di query da testare (campo + testo)
QUERIES = [
    'contenuto "cibo"',
    'nome "cibo"',
    'contenuto "sistema"',
    'nome "sistema"',
    'contenuto "gocce"',
    'nome "gocce"',
    'contenuto ""',
    'nome ""',
    'contenuto "alto livello"',
    'nome "alto livello"',
    'nome "001"'
]

def run_queries():
    for query in QUERIES:
        print(f"\n==============================")
        print(f"Eseguo query: {query}")
        print(f"==============================")

        try:
            response = requests.post(BASE_URL, data={'query': query})
            if response.status_code == 200:
                # Parsing HTML per estrarre risultati
                soup = BeautifulSoup(response.text, 'html.parser')
                results = soup.select('.filename')
                
                if results:
                    print(f"Trovati {len(results)} risultati:")
                    for r in results:
                        print(" -", r.text.strip())
                else:
                    print("Nessun risultato trovato.")
            else:
                print(f"Errore HTTP {response.status_code}")

        except Exception as e:
            print(f"Errore nella query '{query}': {e}")

        time.sleep(1)  

if __name__ == "__main__":
    run_queries()
