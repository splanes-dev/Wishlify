import json
from pathlib import Path

import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder


DATASET_PATH = Path("../datasets/wishlify_interest_classifier_dataset_minimal.csv")
OUTPUT_DIR = Path("../output")
OUTPUT_DIR.mkdir(exist_ok=True)

MAX_TOKENS = 3000
SEQUENCE_LENGTH = 40
EMBEDDING_DIM = 32
EPOCHS = 25
BATCH_SIZE = 16


def main():
    if not DATASET_PATH.exists():
        raise FileNotFoundError(f"No s'ha trobat el dataset: {DATASET_PATH}")

    df = pd.read_csv(DATASET_PATH)

    if "text" not in df.columns or "label" not in df.columns:
        raise ValueError("El CSV ha de contenir les columnes 'text' i 'label'.")

    texts = df["text"].astype(str).to_numpy()
    labels = df["label"].astype(str).to_numpy()

    label_encoder = LabelEncoder()
    encoded_labels = label_encoder.fit_transform(labels)
    num_classes = len(label_encoder.classes_)

    x_train, x_test, y_train, y_test = train_test_split(
        texts,
        encoded_labels,
        test_size=0.2,
        random_state=42,
        stratify=encoded_labels,
    )

    vectorizer = tf.keras.layers.TextVectorization(
        max_tokens=MAX_TOKENS,
        output_mode="int",
        output_sequence_length=SEQUENCE_LENGTH,
        standardize="lower_and_strip_punctuation",
    )

    vectorizer.adapt(x_train)

    model = tf.keras.Sequential(
        [
            tf.keras.Input(shape=(1,), dtype=tf.string),
            vectorizer,
            tf.keras.layers.Embedding(MAX_TOKENS, EMBEDDING_DIM),
            tf.keras.layers.GlobalAveragePooling1D(),
            tf.keras.layers.Dense(64, activation="relu"),
            tf.keras.layers.Dropout(0.2),
            tf.keras.layers.Dense(num_classes, activation="softmax"),
        ]
    )

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
    labels_path.write_text("\n".join(label_encoder.classes_), encoding="utf-8")

    metadata_path = OUTPUT_DIR / "metadata.json"
    metadata_path.write_text(
        json.dumps(
            {
                "max_tokens": MAX_TOKENS,
                "sequence_length": SEQUENCE_LENGTH,
                "labels": label_encoder.classes_.tolist(),
                "test_accuracy": float(accuracy),
            },
            indent=2,
            ensure_ascii=False,
        ),
        encoding="utf-8",
    )

    saved_model_dir = OUTPUT_DIR / "saved_model"
    model.export(saved_model_dir)

    converter = tf.lite.TFLiteConverter.from_saved_model(str(saved_model_dir))
    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS,
        tf.lite.OpsSet.SELECT_TF_OPS,
    ]
    converter._experimental_lower_tensor_list_ops = False

    tflite_model = converter.convert()

    tflite_path = OUTPUT_DIR / "interest_classifier.tflite"
    tflite_path.write_bytes(tflite_model)

    print("\nModel exportat correctament:")
    print(f"- {tflite_path}")
    print(f"- {labels_path}")
    print(f"- {metadata_path}")

    run_manual_tests(model, label_encoder)


def run_manual_tests(model, label_encoder):
    examples = [
        "interessos: tecnologia, gadgets, informàtica. wishlist: auriculars bluetooth, smartwatch",
        "interessos: acampades, excursions, muntanya. wishlist: motxilla trekking, cantimplora",
        "interessos: llibres, fantasia, lectura. wishlist: novel·la, ebook",
        "interessos: cuina, receptes, gastronomia. wishlist: motlles, davantal, cafetera",
        "interessos: gaming, videojocs, consoles. wishlist: comandament, teclat mecànic",
    ]

    print("\nInferències de prova:")

    input_tensor = tf.constant(examples, dtype=tf.string)
    input_tensor = tf.reshape(input_tensor, (-1, 1))
    predictions = model.predict(input_tensor, verbose=0)

    for text, prediction in zip(examples, predictions):
        top_indices = prediction.argsort()[-3:][::-1]

        print(f"\nInput: {text}")
        for index in top_indices:
            label = label_encoder.classes_[index]
            score = prediction[index]
            print(f"  {label}: {score:.3f}")


if __name__ == "__main__":
    main()