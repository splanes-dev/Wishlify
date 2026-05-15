package com.splanes.uoc.wishlify.data.feature.ai.datasource

import com.splanes.uoc.wishlify.data.feature.ai.InterestTextPreprocessor
import com.splanes.uoc.wishlify.data.feature.ai.model.InterestClassificationResult
import com.splanes.uoc.wishlify.data.feature.ai.util.AiAssetsLoader
import org.tensorflow.lite.Interpreter

class InterestClassifierLocalDataSource(
  private val interpreter: Interpreter,
  private val preprocessor: InterestTextPreprocessor,
  private val assetsLoader: AiAssetsLoader,
) {

  private val labels by lazy { assetsLoader.loadLabels() }

  fun classify(
    text: String,
    threshold: Float = 0.35f,
    top: Int = 5,
  ): List<InterestClassificationResult> {

    require(text.isNotBlank()) { "Text input cannot be blank" }

    val inputVector = preprocessor.preprocess(text)
    val input = arrayOf(inputVector)
    val output = Array(1) { FloatArray(labels.size) }

    interpreter.run(input, output)

    return output
      .first()
      .mapIndexed { index, score ->
        InterestClassificationResult(
          label = labels.getOrElse(index) { "unknown_$index" },
          score = score,
        )
      }
      .filter { it.score >= threshold }
      .sortedByDescending { it.score }
      .take(top)
  }

  fun close() {
    interpreter.close()
  }
}