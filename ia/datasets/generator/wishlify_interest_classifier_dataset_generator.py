import csv
import random
from pathlib import Path


OUTPUT_PATH = Path("../wishlify_interest_classifier_dataset_v2_multilabel.csv")
RANDOM_SEED = 42
DIRTY_EXTRA_ROWS = 3000

random.seed(RANDOM_SEED)


LABEL_DATA = {
    "tech_audio": {
        "hobbies": ["música", "podcasts", "àudio", "tecnologia", "cinema"],
        "products": ["auriculars bluetooth", "altaveu portàtil", "micròfon USB", "barra de so", "DAC portàtil"],
        "tags": ["audio", "auriculars", "so", "podcast", "bluetooth"],
        "descriptions": ["millorar la qualitat de so", "accessoris per escoltar música i podcasts"],
    },
    "tech_gadgets": {
        "hobbies": ["tecnologia", "gadgets", "domòtica", "informàtica"],
        "products": ["smartwatch", "powerbank", "localitzador bluetooth", "llum intel·ligent", "mini impressora"],
        "tags": ["gadget", "tech", "smart", "electrònica"],
        "descriptions": ["gadgets útils per al dia a dia", "petits dispositius tecnològics"],
    },
    "tech_mobile": {
        "hobbies": ["mòbils", "fotografia mòbil", "tecnologia"],
        "products": ["funda mòbil", "carregador ràpid", "suport de mòbil", "cable USB-C", "protector de pantalla"],
        "tags": ["mòbil", "smartphone", "carregador", "usb-c"],
        "descriptions": ["accessoris per al telèfon", "complements per carregar i protegir el mòbil"],
    },
    "tech_productivity": {
        "hobbies": ["productivitat", "programació", "estudiar", "oficina", "tecnologia"],
        "products": ["teclat mecànic", "ratolí ergonòmic", "suport de portàtil", "hub USB-C", "llum d'escriptori"],
        "tags": ["oficina", "setup", "productivitat", "programació"],
        "descriptions": ["millorar el setup de treball", "accessoris per estudiar o programar"],
    },
    "tech_smart_home": {
        "hobbies": ["domòtica", "casa intel·ligent", "tecnologia"],
        "products": ["bombeta intel·ligent", "endoll intel·ligent", "sensor de moviment", "altaveu intel·ligent"],
        "tags": ["domòtica", "smart home", "casa", "automatització"],
        "descriptions": ["automatitzar petites coses de casa", "dispositius per a la llar intel·ligent"],
    },

    "gaming_games": {
        "hobbies": ["videojocs", "gaming", "consoles", "PC gaming", "Steam"],
        "products": ["Hollow Knight Silksong", "joc de Nintendo Switch", "targeta regal Steam", "joc de PS5", "DLC videojoc"],
        "tags": ["jocs", "steam", "switch", "ps5", "videojocs"],
        "descriptions": ["jocs que vull que em regalin", "novetats per consola i PC"],
    },
    "gaming_accessories": {
        "hobbies": ["gaming", "setup gaming", "videojocs", "PC"],
        "products": ["comandament bluetooth", "teclat gaming", "ratolí gaming", "alfombreta XXL", "suport auriculars"],
        "tags": ["gaming", "perifèrics", "setup", "accessoris"],
        "descriptions": ["accessoris per millorar el setup de joc", "perifèrics per jugar millor"],
    },
    "gaming_collectibles": {
        "hobbies": ["videojocs", "col·leccionisme", "Nintendo", "retro gaming"],
        "products": ["figura de Link", "amiibo", "edició col·leccionista", "mini consola retro", "artbook videojoc"],
        "tags": ["col·lecció", "figura", "retro", "nintendo"],
        "descriptions": ["objectes de col·lecció relacionats amb videojocs", "figures i edicions especials"],
    },
    "gaming_merch": {
        "hobbies": ["gaming", "videojocs", "fandom"],
        "products": ["samarreta Zelda", "pòster gaming", "tassa videojocs", "dessuadora Playstation"],
        "tags": ["merch", "samarreta", "poster", "gaming"],
        "descriptions": ["marxandatge de videojocs", "roba i decoració gaming"],
    },

    "books_novels": {
        "hobbies": ["llegir", "novel·les", "lectura", "ficció"],
        "products": ["novel·la contemporània", "thriller", "llibre de fantasia", "novel·la de ciència ficció"],
        "tags": ["novel·la", "ficció", "lectura"],
        "descriptions": ["llibres pendents de llegir", "novel·les recomanades"],
    },
    "books_technical": {
        "hobbies": ["programació", "tecnologia", "negoci", "aprendre", "lectura tècnica"],
        "products": ["Clean Code", "llibre de Kotlin", "manual de productivitat", "llibre d'arquitectura software"],
        "tags": ["tècnic", "programació", "software", "aprenentatge"],
        "descriptions": ["llibres tècnics per aprendre", "manuals de programació i enginyeria"],
    },
    "books_comics": {
        "hobbies": ["manga", "còmic", "anime", "novel·la gràfica"],
        "products": ["manga Berserk", "còmic Marvel", "novel·la gràfica", "volum manga"],
        "tags": ["manga", "comic", "còmic", "anime"],
        "descriptions": ["còmics i manga pendents", "novel·les gràfiques"],
    },
    "books_essays": {
        "hobbies": ["història", "filosofia", "assaig", "política", "divulgació"],
        "products": ["assaig històric", "llibre de filosofia", "divulgació científica", "biografia"],
        "tags": ["assaig", "història", "filosofia", "divulgació"],
        "descriptions": ["llibres per reflexionar", "assaigs i divulgació"],
    },

    "outdoor_hiking": {
        "hobbies": ["excursions", "senderisme", "muntanya", "trekking"],
        "products": ["botes de muntanya", "motxilla trekking", "bastons de senderisme", "jaqueta impermeable"],
        "tags": ["muntanya", "senderisme", "trekking", "excursions"],
        "descriptions": ["material per fer excursions", "equipament per caminar per muntanya"],
    },
    "outdoor_camping": {
        "hobbies": ["camping", "acampades", "muntanya", "natura"],
        "products": ["sac de dormir", "tenda de campanya", "frontal LED", "cantimplora", "estoreta inflable"],
        "tags": ["camping", "acampada", "tenda", "natura"],
        "descriptions": ["coses per anar de camping", "material per acampar"],
    },
    "outdoor_travel": {
        "hobbies": ["viatjar", "escapades", "rutes", "road trips"],
        "products": ["organitzador de maleta", "motxilla de viatge", "adaptador universal", "mapa rascable"],
        "tags": ["viatge", "escapada", "maleta", "travel"],
        "descriptions": ["accessoris útils per viatjar", "idees per escapades i viatges"],
    },

    "sports_gym": {
        "hobbies": ["gimnàs", "fitness", "entrenament", "força"],
        "products": ["peses 3kg", "banda elàstica", "estoreta fitness", "guants de gimnàs"],
        "tags": ["gim", "fitness", "entrenament", "força"],
        "descriptions": ["material per entrenar a casa", "accessoris de gimnàs"],
    },
    "sports_running": {
        "hobbies": ["running", "córrer", "marató", "trail"],
        "products": ["sabatilles running", "ronyonera esportiva", "mitjons tècnics", "armilla running"],
        "tags": ["running", "córrer", "trail", "sabatilles"],
        "descriptions": ["equipament per sortir a córrer", "material de running"],
    },
    "sports_swimming": {
        "hobbies": ["natació", "piscina", "nedar"],
        "products": ["ulleres de natació", "casquet de piscina", "tovallola microfibra", "pull buoy"],
        "tags": ["natació", "piscina", "aigua", "nedar"],
        "descriptions": ["material per entrenar natació", "accessoris per piscina"],
    },
    "sports_team": {
        "hobbies": ["futbol", "bàsquet", "equip", "esport"],
        "products": ["samarreta del Barça", "bufanda equip", "pilota futbol", "entrada partit"],
        "tags": ["futbol", "bàsquet", "equip", "partit"],
        "descriptions": ["regals relacionats amb el seu equip", "material esportiu d'equip"],
    },
    "sports_cycling": {
        "hobbies": ["ciclisme", "bicicleta", "BTT", "gravel"],
        "products": ["llum bicicleta", "bidó ciclisme", "guants ciclisme", "bossa sillín"],
        "tags": ["ciclisme", "bici", "bicicleta", "BTT"],
        "descriptions": ["accessoris per bicicleta", "material per sortir en bici"],
    },

    "fashion_clothing": {
        "hobbies": ["moda", "roba", "outfits", "estil"],
        "products": ["camisa", "pantalons", "jaqueta", "dessuadora", "jersei"],
        "tags": ["roba", "moda", "outfit", "estil"],
        "descriptions": ["peces de roba que m'agraden", "idees de roba per renovar armari"],
    },
    "fashion_accessories": {
        "hobbies": ["moda", "accessoris", "estil"],
        "products": ["barret", "bossa", "cinturó", "bufanda", "cartera"],
        "tags": ["accessori", "moda", "complement", "bossa"],
        "descriptions": ["complements de moda", "accessoris personals"],
    },
    "fashion_sneakers": {
        "hobbies": ["sneakers", "sabates", "streetwear", "moda"],
        "products": ["sabatilles Nike", "sneakers Adidas", "sabates casual", "neteja sabatilles"],
        "tags": ["sneakers", "sabates", "streetwear"],
        "descriptions": ["sabatilles i calçat", "idees de sneakers"],
    },
    "fashion_jewelry": {
        "hobbies": ["joieria", "complements", "moda"],
        "products": ["polsera", "collaret", "arracades", "anell senzill"],
        "tags": ["joieria", "polsera", "collaret", "arracades"],
        "descriptions": ["joieria senzilla", "complements personals"],
    },

    "cooking_tools": {
        "hobbies": ["cuinar", "receptes", "gastronomia"],
        "products": ["ganivet de cuina", "motlle silicona", "paella antiadherent", "davantal"],
        "tags": ["cuina", "estris", "receptes"],
        "descriptions": ["estris de cuina útils", "accessoris per cuinar millor"],
    },
    "cooking_gourmet": {
        "hobbies": ["gastronomia", "menjar", "cafè", "te", "restaurants"],
        "products": ["pack gourmet", "cafè especialitat", "oli d'oliva premium", "xocolata artesanal"],
        "tags": ["gourmet", "cafè", "menjar", "delicatessen"],
        "descriptions": ["productes gourmet", "experiències gastronòmiques"],
    },

    "music_instruments": {
        "hobbies": ["música", "guitarra", "piano", "tocar música"],
        "products": ["pues guitarra", "afinador", "partitures", "suport guitarra"],
        "tags": ["instrument", "guitarra", "piano", "música"],
        "descriptions": ["accessoris per tocar música", "material per instruments"],
    },
    "music_live_events": {
        "hobbies": ["concerts", "música en directe", "festivals"],
        "products": ["entrada concert", "abonament festival", "concert acústic"],
        "tags": ["concert", "festival", "música en directe"],
        "descriptions": ["entrades i experiències musicals", "plans de música en directe"],
    },

    "wellbeing_relaxation": {
        "hobbies": ["relax", "descans", "desconnexió", "spa"],
        "products": ["espelmes aromàtiques", "difusor aromes", "manta suau", "coixí cervical"],
        "tags": ["relax", "descans", "spa", "desconnexió"],
        "descriptions": ["coses per relaxar-se", "regals per descansar"],
    },
    "wellbeing_selfcare": {
        "hobbies": ["autocura", "skincare", "rutines", "benestar"],
        "products": ["set skincare", "crema mans", "kit autocura", "sals de bany"],
        "tags": ["autocura", "skincare", "benestar"],
        "descriptions": ["productes d'autocura", "rutines de benestar"],
    },
    "wellbeing_fitness": {
        "hobbies": ["fitness", "salut", "ioga", "pilates"],
        "products": ["estoreta ioga", "bloc ioga", "rodet massatge", "ampolla fitness"],
        "tags": ["ioga", "pilates", "fitness", "salut"],
        "descriptions": ["benestar actiu", "material per cuidar el cos"],
    },

    "experiences_cultural": {
        "hobbies": ["museus", "teatre", "cinema", "cultura", "exposicions"],
        "products": ["entrada museu", "entrada teatre", "sessió de cinema", "exposició immersiva"],
        "tags": ["cultura", "museu", "teatre", "cinema"],
        "descriptions": ["experiències culturals", "entrades per activitats culturals"],
    },
    "experiences_adventure": {
        "hobbies": ["aventura", "adrenalina", "escapades", "outdoor"],
        "products": ["via ferrada", "kayak", "parc aventura", "bateig escalada"],
        "tags": ["aventura", "adrenalina", "activitat"],
        "descriptions": ["experiències d'aventura", "plans actius i diferents"],
    },
    "experiences_food": {
        "hobbies": ["gastronomia", "restaurants", "tastos", "cuina"],
        "products": ["tast de vins", "sopar degustació", "classe de cuina", "brunch especial"],
        "tags": ["menjar", "restaurant", "tast", "gastronomia"],
        "descriptions": ["experiències gastronòmiques", "plans relacionats amb menjar"],
    },
    "experiences_romantic": {
        "hobbies": ["plans en parella", "escapades romàntiques", "sorpreses"],
        "products": ["escapada romàntica", "sopar especial", "spa en parella", "hotel rural"],
        "tags": ["romàntic", "parella", "escapada", "sorpresa"],
        "descriptions": ["plans especials en parella", "experiències romàntiques"],
    },

    "entertainment_merch": {
        "hobbies": ["anime", "sèries", "cinema", "fandom", "Marvel", "Star Wars"],
        "products": ["Funko Pop", "pòster anime", "figura Marvel", "samarreta Star Wars", "tassa Ghibli"],
        "tags": ["merch", "anime", "sèries", "fandom"],
        "descriptions": ["marxandatge de sèries i pel·lícules", "figures i objectes de fandom"],
    },
    "entertainment_events": {
        "hobbies": ["anime", "cinema", "comic con", "convencions", "sèries"],
        "products": ["entrada Comic Con", "sessió cinema", "saló del manga", "exposició anime"],
        "tags": ["event", "cinema", "comic con", "anime"],
        "descriptions": ["entrades per esdeveniments de fandom", "plans relacionats amb cinema o anime"],
    },

    "art_creative": {
        "hobbies": ["dibuix", "pintura", "manualitats", "fotografia", "DIY"],
        "products": ["quadern de dibuix", "retoladors", "aquarel·les", "kit lettering", "càmera instantània"],
        "tags": ["art", "creativitat", "dibuix", "manualitats"],
        "descriptions": ["material creatiu", "eines per dibuixar o fer manualitats"],
    },
}


# Combinacions realistes multi-label
COMBOS = [
    ["gaming_games", "tech_gadgets"],
    ["gaming_games", "gaming_accessories"],
    ["gaming_merch", "entertainment_merch"],
    ["books_comics", "entertainment_merch"],
    ["books_technical", "tech_productivity"],
    ["books_novels", "experiences_cultural"],
    ["outdoor_hiking", "outdoor_camping"],
    ["outdoor_hiking", "sports_running"],
    ["outdoor_travel", "experiences_adventure"],
    ["sports_gym", "wellbeing_fitness"],
    ["sports_running", "tech_audio"],
    ["fashion_clothing", "fashion_accessories"],
    ["fashion_sneakers", "fashion_clothing"],
    ["cooking_tools", "experiences_food"],
    ["cooking_gourmet", "experiences_food"],
    ["music_live_events", "experiences_cultural"],
    ["wellbeing_relaxation", "wellbeing_selfcare"],
    ["experiences_romantic", "experiences_food"],
    ["art_creative", "books_comics"],
    ["tech_audio", "music_live_events"],
    ["tech_mobile", "tech_gadgets"],
    ["tech_smart_home", "tech_gadgets"],
]


CONTEXT_PATTERNS = [
    "interessos principals: {hobbies}. hobbies: {hobbies}. interessos: {hobbies}. wishlist productes: {products}. tags rellevants: {tags}. descripcions productes: {descriptions}. pressupost orientatiu: {budget}",
    "hobbies: {hobbies}. wishlist productes: {products}. tags rellevants: {tags}. pressupost orientatiu: {budget}",
    "interessos: {hobbies}. wishlist títols: {titles}. wishlist productes: {products}. tags rellevants: {tags}. descripcions productes: {descriptions}",
    "interessos principals: {hobbies}. descripcions wishlist: {descriptions}. wishlist productes: {products}. pressupost orientatiu: {budget}",
]


DIRTY_PATTERNS = [
    "interessos principals: {hobbies}. wishlist títols: {titles}. wishlist productes: {products}. tags rellevants: {tags}. descripcions productes: {descriptions}. pressupost orientatiu: {budget}",
    "hobbies: {hobbies}. interessos: {hobbies}. productes: {products}. tags: {tags}. notes: {descriptions}",
    "wishlist productes: {products}. descripcions wishlist: {descriptions}. interessos principals: {hobbies}. pressupost orientatiu: {budget}",
    "{hobbies}. coses que li agraden: {products}. tags rellevants: {tags}. {descriptions}",
]


GENERIC_TITLES = [
    "idees regal",
    "idees nadal",
    "cumple",
    "aniversari",
    "wishlist",
    "llista regals",
    "coses que m'agraden",
]


NOISE_HOBBIES = [
    "amics",
    "familia",
    "nadal",
    "cumple",
    "regals",
    "sorpreses",
    "amazon",
    "zara",
    "casa",
    "sortir",
    "plans",
    "cap de setmana",
    "netflix",
    "youtube",
    "instagram",
    "manualitats random",
]


NOISE_PRODUCTS = [
    "tassa amistat",
    "mitjons",
    "gorra",
    "targeta regal",
    "caixa sorpresa",
    "clauer",
    "espelma",
    "llibre pendent",
    "bossa petita",
    "organitzador",
    "pack sorpresa",
    "detall personalitzat",
]


NOISE_TAGS = [
    "regal",
    "amazon",
    "zara",
    "casa",
    "accessori",
    "barat",
    "idea",
    "sorpresa",
    "nadal",
    "cumple",
    "amic invisible",
]


NOISE_DESCRIPTIONS = [
    "idees per a l aniversari",
    "coses que m agraden pero no se exactament que comprar",
    "llistat provisional amb idees variades",
    "producte pendent de revisar",
    "regal orientatiu per si algu no sap que comprar",
    "opcio flexible dins del pressupost",
    "m agrada pero no es imprescindible",
]


def sample_values(labels, key, min_items=2, max_items=5):
    values = []

    for label in labels:
        values.extend(LABEL_DATA[label][key])

    values = list(dict.fromkeys(values))
    random.shuffle(values)

    return values[: random.randint(min_items, min(max_items, len(values)))]


def maybe_add_noise(values, noise_pool, probability=0.45, min_noise=1, max_noise=3):
    result = list(values)

    if random.random() < probability:
        result.extend(random.sample(noise_pool, random.randint(min_noise, max_noise)))

    random.shuffle(result)
    return result


def build_example(labels):
    hobbies = sample_values(labels, "hobbies", 2, 5)
    products = sample_values(labels, "products", 3, 7)
    tags = sample_values(labels, "tags", 2, 6)
    descriptions = sample_values(labels, "descriptions", 1, 3)

    if random.random() < 0.35:
        products.append(random.choice(NOISE_PRODUCTS))

    if random.random() < 0.25:
        tags.append(random.choice(NOISE_TAGS))

    titles = random.sample(GENERIC_TITLES, random.randint(1, 3))
    budget = random.choice([15, 20, 25, 30, 40, 50, 75, 100])

    pattern = random.choice(CONTEXT_PATTERNS)

    text = pattern.format(
        hobbies=", ".join(hobbies),
        products=", ".join(products),
        tags=", ".join(tags),
        descriptions=", ".join(descriptions),
        titles=", ".join(titles),
        budget=budget,
    )

    return text, ",".join(labels)


def build_dirty_example(labels):
    labels = list(labels)

    distractor_labels = [
        label for label in LABEL_DATA.keys()
        if label not in labels
    ]

    context_labels = list(labels)

    if random.random() < 0.45 and distractor_labels:
        context_labels.append(random.choice(distractor_labels))

    hobbies = sample_values(context_labels, "hobbies", 2, 6)
    products = sample_values(context_labels, "products", 3, 8)
    tags = sample_values(context_labels, "tags", 2, 7)
    descriptions = sample_values(context_labels, "descriptions", 1, 4)

    hobbies = maybe_add_noise(hobbies, NOISE_HOBBIES, probability=0.55)
    products = maybe_add_noise(products, NOISE_PRODUCTS, probability=0.65)
    tags = maybe_add_noise(tags, NOISE_TAGS, probability=0.65)
    descriptions = maybe_add_noise(descriptions, NOISE_DESCRIPTIONS, probability=0.55)

    if random.random() < 0.25:
        tags = []

    if random.random() < 0.20:
        descriptions = []

    if random.random() < 0.15:
        hobbies = hobbies[: random.randint(1, max(1, len(hobbies)))]

    titles = random.sample(GENERIC_TITLES, random.randint(1, 3))
    budget = random.choice([10, 15, 20, 25, 30, 40, 50, 75, 100])

    pattern = random.choice(DIRTY_PATTERNS)

    text = pattern.format(
        hobbies=", ".join(hobbies),
        products=", ".join(products),
        tags=", ".join(tags),
        descriptions=", ".join(descriptions),
        titles=", ".join(titles),
        budget=budget,
    )

    return text, ",".join(labels)


def generate_dirty_rows(total_rows=DIRTY_EXTRA_ROWS):
    rows = []
    labels = list(LABEL_DATA.keys())

    for _ in range(total_rows):
        mode = random.random()

        if mode < 0.55:
            selected_labels = [random.choice(labels)]
        elif mode < 0.90:
            selected_labels = random.sample(labels, 2)
        else:
            selected_labels = random.sample(labels, 3)

        rows.append(build_dirty_example(selected_labels))

    return rows


def generate_dataset(
    single_per_label=120,
    combo_per_pair=80,
    dirty_extra_rows=DIRTY_EXTRA_ROWS,
):
    rows = []
    labels = list(LABEL_DATA.keys())

    for label in labels:
        for _ in range(single_per_label):
            rows.append(build_example([label]))

    for combo in COMBOS:
        for _ in range(combo_per_pair):
            rows.append(build_example(combo))

    rows.extend(generate_dirty_rows(dirty_extra_rows))

    random.shuffle(rows)
    return rows


def main():
    rows = generate_dataset()

    with OUTPUT_PATH.open("w", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        writer.writerow(["text", "labels"])
        writer.writerows(rows)

    print(f"Dataset generated: {OUTPUT_PATH}")
    print(f"Rows: {len(rows)}")
    print(f"Labels: {len(LABEL_DATA)}")
    print(f"Dirty extra rows: {DIRTY_EXTRA_ROWS}")


if __name__ == "__main__":
    main()