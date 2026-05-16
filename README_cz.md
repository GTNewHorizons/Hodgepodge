# A Hodgepodge of Fixes

🌐 **Jazyky:** [English](README.md) | [Čeština](README_cz.md)

Požaduje UniMixins 0.1.14+ (https://github.com/LegacyModdingMC/UniMixins/) pro funkci.  
Požaduje GTNHLib 0.2.2+ (https://github.com/GTNewHorizons/GTNHLib) pro funkci.

## Spuštění

Pokud spouštíte aplikaci v dev režimu, přidejte následující argumenty:: 
```
--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.hodgepodge.json
```

## License

Hodgepodge je licensován pod LGPL-3.

## Funkce úprav

### Chat
- Zprávy postele "nelze spát" se ukazjí nad rychlým panelem, místo v chatu.
- Na macOS se ve všech textových polích pro zkratky kopírování/vyjmutí/vložení používá klávesa Command.
- Pokud chat není otevřený, pozadí za zprávami chatu se nezobrazuje.
- Opakující se stejné po sobě jdoucí zprávy v chatu jsou sloučeny do jednoho řádku s počtem opakování.
- Velikost historie chatu byla navýšena. (výchozí 8191, vanilla 100)
- Zprávy v chatu mohou mít maximálně 256 znaků místo 100.

### Základní Minecraft

- Zaklínadla a vylepšené lektvary používají arabská čísla (1, 5, 9) místo římských čísel (I, V, IX).
- Tlačítko Minecraft Říše je zakázáno.
- Nepřátelské stvoření, která seberou vyhozené předměty, je po despawnu znovu upustí.
- Nepřátelským stvořením je zakázáno sbírat vyhozené předměty.
- Inventář se už neposouvá do strany při aktivních efektech lektvarů.
- V third-person režimu (F5) je skrytý zaměřovač.
- Limit vykreslování částic byl zvýšen. (výchozí 8000, vanilla 4000)
- Pick-block je povolen v survivalu (vymění se za aktuální slot na hotbaru).
- Zvuk při pokládání vozíku byl odstraněn.
- Klávesa sprintu byla přesunuta do kategorie ovládání pohybu.
- Interval automatického ukládání byl snížen na 45 sekund.
- Neprůhlednost vody byla snížena z 3 na 1, aby odpovídala moderním verzím Minecraftu.
- Po zavření kontejneru jsou předměty přesunuty do inventáře hráče místo toho, aby byly vyhozeny.
- Už nevidíte vlastní částicové efekty lektvarů.

### Základní Minecraft, výhozí: vypnuto
- Noční vidění lze upravit tak, aby nemělo ten modravý nádech oblohy.
- Lze snížit prodlevu při opakovaném pokládání bloků.
- Barvu zaměřovače lze nastavit tak, aby nebyla opačná.
- Port LAN serveru lze změnit.
- Endermanům je zabráněno v braní a pokládání bloků.
- Podpora Skyblocku
  - Generování terénu je nahrazeno pouze vzdušnými bloky.
  - Generování struktur v oblastech (chunk) je vypnuto.
  - Generování oblastí z modů je vypnuto.
 
### Úpravy modů
- Několik modů má při prvním spuštění odpojené klávesové zkratky, aby se předešlo konfliktům.
- Forge: Načítací ukazatel průběhu je zrychlený.
- Automagy: Thirsty Tank nyní podporuje rozhraní pro kapalní kontejnery.
- Biomes O' Plenty: 5 jedlových saplingů zasazených do tvaru plusu (+) vyroste ve velký jedlový strom.
- Biomes O' Plenty: Generování tekutého písku lze vypnout.
- Extra Utilities: Když je zombík zabit pomocí bodce, který používá falešné hráče (zlaté, diamantové), nespustí se zombí pomoc.
- Extra Utilities: Drums už „nepožírají“ IC2 články ani Forestry kapsle, prázdný kontejner se vrátí hráči.
- Extra Utilities: Dimenze Poslední Milénium už nemá déšť ani moby.
- Extra Utilities: Uzel odběru kapaliny už nemaže tekutiny.
- Industrialcraft 2: Kapalinové články zobrazují lokalizované jméno tekutiny.
- Industrialcraft 2: Nádrž pro chladící kapalinu nejde automatizovaně používat, pokud reaktor není kapalný reaktor.
- Industrialcraft 2: Kapalinové články nyní podporují rozhraní kapalních nádrží.
- Industrialcraft 2: Semínka se hromadí po 64 kusech, pokud mají stejné vlastnosti.
- Journey Map: Opraveno rolování v obrazovce waypointů.
- Minechem: ID efektu Atropine High (Delirium) je konfigurovatelné kvůli konfliktům ID.
- NotEnoughItems: Rozhraní NEI se správně přizpůsobí vanilla efektům lektvarů.
- Optifine: Shader „GL error“ zprávy jsou skryté. 
- ProjectRed: Opraven problém, kdy komponenty odpadávaly kvůli nenačteným chunkům.
- ProjectRed: Opraven problém s osvětlením HUDu.
- Railcraft: Osobní a Pasivní kotvy se po přihlášení správně probudí.
- Thaumcraft: Aspekty jsou řazené podle lokalizovaného názvu místo interního tagu.
- Thaumcraft: Golemové fungují i v dimenzích vyšších než výška 255.
- Thaumcraft: Podstavce pro dobití hůlek přijímají Centivis.
- Traveller's Gear: Předměty vložené do TG slotů se vrátí, pokud je mod odstraněn.
- VoxelMap: Přípona souborů byla změněna z .zip na .data, aby se zabránilo problémům s Technic launcherem.
- VoxelMap: Y souřadnice už není posunutá o jedničku.
- Witchery: Opraveny odrazy skinů hráčů v obydlených zrcadlech.

## GregTech znečištění

- Tráva, květiny a voda jsou přebarveny podle míry znečištění.
- Znečištění je přidáno k Minecraft pecím, všem výbuchům, Galacticraft raketám a Railcraft koksovým pecím, topenišť, tunelového vrtáku a hobby parního stroje. 

## Ostatní změny

Jsou implementována různá zrychlení a opravy pádů, například prevence "chunk banů" zvýšením limitů velikosti paketů.

## Debugovací příkazy a JVM argumenty

Seznam JVM argumentů používaných k aktivaci debugovacích nástrojů:  

- `-Dhodgepodge.dumpClass=true` exportuje třídu upravenou různými ASM transformátory tohoto modu
- `-Dhodgepodge.debugtextures=true` vytváří soubory `TexturesDebug.csv` a `DynamicTextures.txt`, které obsahují velikosti všech textur
- `-Dhodgepodge.logModTimes=true` vytváři dva soubory `modtimes_.csv`, které obsahují časy jednotlivých kroků načítání modů
- `-Dhodgepodge.logEventTimes=true` vytváří soubor `EventRegistrationTime.txt`, který obsahuje čas potřebný k registraci jednotlivých event handlerů do Event Busu
- `-Dhodgepodge.logConfigTimes=true` vytváří soubor `ConfigParsingTimes.csv`, který obsahuje čas potřebný ke zpracování jednotlivých konfiguračních souborů
- `-Dhodgepodge.logEnumValues=true` loguje případy, kdy je `Enum#values()` voláno příliš často, a vytváří soubor `EnumValuesDebug.csv` s výsledky
- `-Dhodgepodge.logIntervalEnumValues=500` (používá se s `logEnumValues`) určuje práh, při kterém se vypíše logovací zpráva
- `-Dhodgepodge.logStacktraceEnumValues=true` (používá se s `logEnumValues`) vypisuje stacktrace místa, odkud je `Enum#values()` voláno

Debugovací příkazy:

-  `/dumptextureatlas` exportuje atlas textur itemů i bloků, včetně všech prázdných a chybných textur
