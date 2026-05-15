package com.splanes.uoc.wishlify.data.feature.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteInterpreterFactory(private val context: Context) {

  fun create(
    modelPath: String = "ml/interest_classifier.tflite",
    numThreads: Int = 2
  ): Interpreter {
    val modelBuffer = loadModelFile(modelPath)
    val options = Interpreter.Options().apply {
      setNumThreads(numThreads)
    }

    return Interpreter(modelBuffer, options)
  }

  private fun loadModelFile(modelPath: String): MappedByteBuffer {

    val assetFileDescriptor = context.assets.openFd(modelPath)

    FileInputStream(assetFileDescriptor.fileDescriptor).use { inputStream ->
      val fileChannel = inputStream.channel

      return fileChannel.map(
        FileChannel.MapMode.READ_ONLY,
        assetFileDescriptor.startOffset,
        assetFileDescriptor.declaredLength,
        )
    }
  }
}