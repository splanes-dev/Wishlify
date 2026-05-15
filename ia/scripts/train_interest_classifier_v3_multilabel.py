import json
import re
from collections import Counter
from pathlib import Path

import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split


DATASET_PATH = Path("../datasets/wishlify_interest_classifier_dataset_v2_multilabel.csv")
OUTPUT_DIR = Path("../output_v3")
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

MAX_TOKENS = 6000
SEQUENCE_LENGTH = 80
EMBEDDING_DIM = 64
EPOCHS = 20
BATCH_SIZE = 32

PAD_TOKEN = "<PAD>"
UNK_TOKEN = "<UNK>"


def normalize_text(text: str) -> str:
    text = text.lower()
    text = re.sub(r"[^\w\sàèéíïòóúüçñ·&]", " ", text)
    text = re.sub(r"\s+", " ", text).strip()
    return text


def tokenize(text: str) -> list[str]:
    return normalize_text(text).split()


def build_vocab(texts: list[str]) -> dict[str, int]:
    counter = Counter()

    for text in texts:
        counter.update(tokenize(text))

    vocab = {
        PAD_TOKEN: 0,
        UNK_TOKEN: 1,
    }

    for index, (token, _) in enumerate(
        counter.most_common(MAX_TOKENS - 2),
        start=2,
    ):
        vocab[token] = index

    return vocab


def encode_text(text: str, vocab: dict[str, int]) -> list[int]:
    ids = [vocab.get(token, vocab[UNK_TOKEN]) for token in tokenize(text)]

    ids = ids[:SEQUENCE_LENGTH]

    while len(ids) < SEQUENCE_LENGTH:
        ids.append(vocab[PAD_TOKEN])

    return ids


def encode_texts(texts: list[str], vocab: dict[str, int]) -> np.ndarray:
    return np.array(
        [encode_text(text, vocab) for text in texts],
        dtype=np.int32,
    )


def parse_labels(raw_labels: list[str]) -> list[str]:
    labels = set()

    for row in raw_labels:
        for label in row.split(","):
            clean_label = label.strip()
            if clean_label:
                labels.add(clean_label)

    return sorted(labels)


def multi_hot_encode(raw_labels: list[str], label_to_index: dict[str, int]) -> np.ndarray:
    y = np.zeros((len(raw_labels), len(label_to_index)), dtype=np.float32)

    for row_index, row in enumerate(raw_labels):
        for label in row.split(","):
            clean_label = label.strip()
            if clean_label:
                y[row_index, label_to_index[clean_label]] = 1.0

    return y


def create_model(num_labels: int) -> tf.keras.Model:
    inputs = tf.keras.Input(shape=(SEQUENCE_LENGTH,), dtype=tf.int32)

    x = tf.keras.layers.Embedding(
        input_dim=MAX_TOKENS,
        output_dim=EMBEDDING_DIM,
        mask_zero=True,
    )(inputs)

    x = tf.keras.layers.GlobalAveragePooling1D()(x)

    x = tf.keras.layers.Dense(128, activation="relu")(x)
    x = tf.keras.layers.Dropout(0.3)(x)

    x = tf.keras.layers.Dense(64, activation="relu")(x)
    x = tf.keras.layers.Dropout(0.2)(x)

    outputs = tf.keras.layers.Dense(num_labels, activation="sigmoid")(x)

    return tf.keras.Model(inputs=inputs, outputs=outputs)


def print_top_predictions(
    model: tf.keras.Model,
    vocab: dict[str, int],
    labels: list[str],
    examples: list[str],
    threshold: float = 0.35,
):
    encoded = encode_texts(examples, vocab)
    predictions = model.predict(encoded, verbose=0)

    print("\nInferències de prova:")

    for text, prediction in zip(examples, predictions):
        print(f"\nInput: {text}")

        indexed_scores = list(enumerate(prediction))
        indexed_scores.sort(key=lambda item: item[1], reverse=True)

        printed = False

        for index, score in indexed_scores[:8]:
            if score >= threshold:
                print(f"  {labels[index]}: {score:.3f}")
                printed = True

        if not printed:
            print("  Cap label supera el threshold. Top candidates:")
            for index, score in indexed_scores[:5]:
                print(f"  {labels[index]}: {score:.3f}")


def main():
    if not DATASET_PATH.exists():
        raise FileNotFoundError(f"No s'ha trobat el dataset: {DATASET_PATH}")

    df = pd.read_csv(DATASET_PATH)

    if "text" not in df.columns or "labels" not in df.columns:
        raise ValueError("El CSV ha de contenir les columnes 'text' i 'labels'.")

    texts = df["text"].astype(str).tolist()
    raw_labels = df["labels"].astype(str).tolist()

    labels = parse_labels(raw_labels)
    label_to_index = {label: index for index, label in enumerate(labels)}

    y = multi_hot_encode(raw_labels, label_to_index)

    x_train_texts, x_test_texts, y_train, y_test = train_test_split(
        texts,
        y,
        test_size=0.15,
        random_state=42,
    )

    x_train_texts, x_val_texts, y_train, y_val = train_test_split(
        x_train_texts,
        y_train,
        test_size=0.15,
        random_state=42,
    )

    vocab = build_vocab(x_train_texts)

    x_train = encode_texts(x_train_texts, vocab)
    x_val = encode_texts(x_val_texts, vocab)
    x_test = encode_texts(x_test_texts, vocab)

    model = create_model(num_labels=len(labels))

    model.compile(
        optimizer="adam",
        loss="binary_crossentropy",
        metrics=[
            tf.keras.metrics.BinaryAccuracy(name="binary_accuracy"),
            tf.keras.metrics.Precision(name="precision"),
            tf.keras.metrics.Recall(name="recall"),
        ],
    )

    model.summary()

    model.fit(
        x_train,
        y_train,
        validation_data=(x_val, y_val),
        batch_size=BATCH_SIZE,
        epochs=EPOCHS,
    )

    results = model.evaluate(x_test, y_test, verbose=0)

    print("\nEvaluation:")
    for metric_name, metric_value in zip(model.metrics_names, results):
        print(f"{metric_name}: {metric_value:.4f}")

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()

    (OUTPUT_DIR / "interest_classifier_multilabel.tflite").write_bytes(tflite_model)

    (OUTPUT_DIR / "labels.json").write_text(
        json.dumps(labels, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    (OUTPUT_DIR / "vocab.json").write_text(
        json.dumps(vocab, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    (OUTPUT_DIR / "metadata.json").write_text(
        json.dumps(
            {
                "max_tokens": MAX_TOKENS,
                "sequence_length": SEQUENCE_LENGTH,
                "embedding_dim": EMBEDDING_DIM,
                "num_labels": len(labels),
                "pad_token": PAD_TOKEN,
                "unk_token": UNK_TOKEN,
            },
            ensure_ascii=False,
            indent=2,
        ),
        encoding="utf-8",
    )

    print("\nExport completat:")
    print(f"- {OUTPUT_DIR / 'interest_classifier_multilabel.tflite'}")
    print(f"- {OUTPUT_DIR / 'labels.json'}")
    print(f"- {OUTPUT_DIR / 'vocab.json'}")
    print(f"- {OUTPUT_DIR / 'metadata.json'}")

    print_top_predictions(
        model=model,
        vocab=vocab,
        labels=labels,
        examples=[
            "interessos principals: gaming, videojocs, tecnologia. wishlist productes: hollow knight, teclat gaming, switch 2",
            "interessos principals: excursions, camping, senderisme. wishlist productes: tenda campanya, botes muntanya",
            "interessos principals: lectura tecnica, programació. wishlist productes: clean code, teclat mecànic",
            "interessos principals: roba, sneakers, moda. wishlist productes: sabatilles nike, barret, camisa",
        ],
    )


if __name__ == "__main__":
    main()