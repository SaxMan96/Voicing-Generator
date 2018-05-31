# Voicing Generator
## TKOM - Techniki Kompilacji
Projekt kompilatora generującego plik MIDI z układami podanych akordów.  
Program jest w stanie z pliku wejściowego o formacie podanym w pliku files/example.txt 
wygenerować plik .mid z układami akordów (voicing-ami) odpowiednio zdefiniowanymi w ustawieniach pliku.
* Do generacji klas parsera i leksera używam narzędzia ANTL4 http://www.antlr.org/
* Do generacji plików MIDI używam biblioteki jMusic w wersji 1.6.5 http://explodingart.com/jmusic/
##


Każdy plik wejściowy powinien składać się z trzech sekcji: **settings**, **parts**, **form**.  
W sekcji **settings** można ustawiś takie parametry partytury jak:
* **beats** - liczba wartości w takcie
* **beatType** - typ wartości podstawowej metrum
* **key** - tonacja
* **defaultVoicings** - reguły voicingów dla podanych typów akordów. 
Pierwsza wartość to typ akordu a liczby podane w nawiasach <> to stopnie z których składał się będzie voicing

      settings
      {
          beats 4
          beatType quarter
          key F
          defaultVoicings
          {
                  7 <3 7 9 13>
                  7alt <7 9 3 5>
                  -6 <3 5 6 9>
                  -7 <3 5 7 9>
                  -7b5 <3 5 7 9>
                  -^7 <3 5 7 9>
                  ^7 <3 5 7 9>
          }
      }
W sekcji **parts** umieszczone są części utworu oznaczone nazwami.  
W każdej z części są umieszczone takty oddzielone pionowymi kreskami | obok nazw akodrów mogą zostać umieszczone voicingi 
jakie chcemy dla nich zastosować. Jeżeli tego nie zrobimy to zostanie przypisany im domyślny voicing ustawiony w sekcji **settings**.  
Jeżeli nawet tam go nie ma to układ jaki zostanie wygenerowany to czterodźwięk bez przewrotu <1 3 5 7>  

      parts
      {
          PartA:
              Eb7 <7 9 3 13>  | D-6 <3 5 6 9> | Eb7                   | D-6   |
              Eb7             | D-6           | E-7b5<5 7 9 11> A7alt | D-7 
          PartB:
              A-7b5     |  D7       |  G-7 D7   |    G-7      |
              G-7b5     | C7b5b9    |   F^7     |   E-7b5 A7 
          PartC:
              E-7b5 |  %   | Eb7#11   |  %    | 
              D-7   |  %   | G7#11    |  %    |
              G-^7  | G-7  | Gb7#11   |  %    |
              F^7   |  %   | E-7b5    | A7b9
      }
W sekcji **form** ustalana jest forma utworu. Można tu skorzystać z wcześniej utworzonych części.

      form
      {
          PartA,PartA,PartB,PartA,PartC
      }
Plik wyjściowy utworzony z omówionego wyżej pliku konfiguracyjnego wygląda tak: 
[PDF](files/Example.pdf) (nazwy akodrów zostały dodane dla czytelności)
