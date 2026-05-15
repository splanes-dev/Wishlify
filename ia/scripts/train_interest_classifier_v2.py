import json
import re
from collections import Counter
from pathlib import Path

import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder


DATASET_PATH = Path("../datasets/wishlify_interest_classifier_dataset_minimal.csv")
OUTPUT_DIR = Path("../output_v2")
OUTPUT_DIR.mkdir(exist_ok=True)

MAX_TOKENS = 3000
SEQUENCE_LENGTH = 40
EMBEDDING_DIM = 32
EPOCHS = 35
BATCH_SIZE = 16

PAD_TOKEN = "<PAD>"
UNK_TOKEN = "<UNK>"


def normalize_text(text: str) -> str:
    text = text.lower()
    text = re.sub(r"[^\w\sàèéíïòóúüçñ]", " ", text)
    text = re.sub(r"\s+", " ", text).strip()
    return text


def tokenize(text: str) -> list[str]:
    return normalize_text(text).split()


def build_vocab(texts: list[str]) -> dict[str, int]:
    counter = Counter()

    for text in texts:
        counter.update(tokenize(text))

    most_common = counter.most_common(MAX_TOKENS - 2)

    vocab = {
        PAD_TOKEN: 0,
        UNK_TOKEN: 1,
    }

    for index, (token, _) in enumerate(most_common, start=2):
        vocab[token] = index

    return vocab


def encode_text(text: str, vocab: dict[str, int]) -> list[int]:
    tokens = tokenize(text)

    ids = [vocab.get(token, vocab[UNK_TOKEN]) for token in tokens]

    if len(ids) > SEQUENCE_LENGTH:
        ids = ids[:SEQUENCE_LENGTH]

    while len(ids) < SEQUENCE_LENGTH:
        ids.append(vocab[PAD_TOKEN])

    return ids


def encode_texts(texts: list[str], vocab: dict[str, int]) -> np.ndarray:
    return np.array(
        [encode_text(text, vocab) for text in texts],
        dtype=np.int32,
    )


def create_model(num_classes: int) -> tf.keras.Model:
    inputs = tf.keras.Input(shape=(SEQUENCE_LENGTH,), dtype=tf.int32)

    x = tf.keras.layers.Embedding(
        input_dim=MAX_TOKENS,
        output_dim=EMBEDDING_DIM,
        mask_zero=True,
    )(inputs)

    x = tf.keras.layers.GlobalAveragePooling1D()(x)
    x = tf.keras.layers.Dense(64, activation="relu")(x)
    x = tf.keras.layers.Dropout(0.25)(x)
    outputs = tf.keras.layers.Dense(num_classes, activation="softmax")(x)

    return tf.keras.Model(inputs=inputs, outputs=outputs)


def main():
    if not DATASET_PATH.exists():
        raise FileNotFoundError(f"No s'ha trobat el dataset: {DATASET_PATH}")

    df = pd.read_csv(DATASET_PATH)

    if "text" not in df.columns or "label" not in df.columns:
        raise ValueError("El CSV ha de contenir les columnes 'text' i 'label'.")

    texts = df["text"].astype(str).tolist()
    labels = df["label"].astype(str).to_numpy()

    label_encoder = LabelEncoder()
    encoded_labels = label_encoder.fit_transform(labels)
    num_classes = len(label_encoder.classes_)

    x_train_texts, x_test_texts, y_train, y_test = train_test_split(
        texts,
        encoded_labels,
        test_size=0.2,
        random_state=42,
        stratify=encoded_labels,
    )

    vocab = build_vocab(x_train_texts)

    x_train = encode_texts(x_train_texts, vocab)
    x_test = encode_texts(x_test_texts, vocab)

    model = create_model(num_classes)

    model.compile(
        optimizer="adam",
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"],
    )

    model.summary()

    model.fit(
        x_train,
        y_train,
        validation_split=0.2,
        epochs=EPOCHS,
        batch_size=BATCH_SIZE,
    )

    loss, accuracy = model.evaluate(x_test, y_test)
    print(f"\nTest accuracy: {accuracy:.4f}")

    labels_path = OUTPUT_DIR / "labels.txt"
    labels_path.write_text(
        "\n".join(label_encoder.classes_),
        encoding="utf-8",
    )

    vocab_path = OUTPUT_DIR / "vocab.json"
    vocab_path.write_text(
        json.dumps(vocab, indent=2, ensure_ascii=False),
        encoding="utf-8",
    )

    metadata_path = OUTPUT_DIR / "metadata.json"
    metadata_path.write_text(
        json.dumps(
            {
                "max_tokens": MAX_TOKENS,
                "sequence_length": SEQUENCE_LENGTH,
                "embedding_dim": EMBEDDING_DIM,
                "labels": label_encoder.classes_.tolist(),
                "test_accuracy": float(accuracy),
                "pad_token": PAD_TOKEN,
                "unk_token": UNK_TOKEN,
            },
            indent=2,
            ensure_ascii=False,
        ),
        encoding="utf-8",
    )

    converter = tf.lite.TFLiteConverter.from_keras_model(model)

    tflite_model = converter.convert()

    tflite_path = OUTPUT_DIR / "interest_classifier.tflite"
    tflite_path.write_bytes(tflite_model)

    print("\nModel exportat correctament:")
    print(f"- {tflite_path}")
    print(f"- {labels_path}")
    print(f"- {vocab_path}")
    print(f"- {metadata_path}")

    run_manual_tests(model, label_encoder, vocab)


def run_manual_tests(model, label_encoder, vocab):
    examples = [
        "interessos: tecnologia, gadgets, informàtica. wishlist: auriculars bluetooth, smartwatch",
        "interessos: acampades, excursions, muntanya. wishlist: motxilla trekking, cantimplora",
        "interessos: llibres, fantasia, lectura. wishlist: novel·la, ebook",
        "interessos: cuina, receptes, gastronomia. wishlist: motlles, davantal, cafetera",
        "interessos: gaming, videojocs, consoles. wishlist: comandament, teclat mecànic",
    ]

    print("\nInferències de prova:")

    encoded_examples = encode_texts(examples, vocab)
    predictions = model.predict(encoded_examples, verbose=0)

    for text, prediction in zip(examples, predictions):
        top_indices = prediction.argsort()[-3:][::-1]

        print(f"\nInput: {text}")
        for index in top_indices:
            label = label_encoder.classes_[index]
            score = prediction[index]
            print(f"  {label}: {score:.3f}")


if __name__ == "__main__":
    main()