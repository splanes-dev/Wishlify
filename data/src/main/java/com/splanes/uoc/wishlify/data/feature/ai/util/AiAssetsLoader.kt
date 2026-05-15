package com.splanes.uoc.wishlify.data.feature.ai.util

import android.content.Context
import com.splanes.uoc.wishlify.data.feature.ai.model.AiModelMetadata
import org.json.JSONArray
import org.json.JSONObject

class AiAssetsLoader(
  private val context: Context,
) {

  fun loadVocabulary(path: String = "ml/vocab.json"): Map<String, Int> {
    val json = readAsset(path)
    val jsonObject = JSONObject(json)

    return jsonObject.keys()
      .asSequence()
      .associateWith { key -> jsonObject.getInt(key) }
  }

  fun loadLabels(path: String = "ml/labels.json"): List<String> {
    val json = readAsset(path)
    val jsonArray = JSONArray(json)

    return buildList {
      for (index in 0 until jsonArray.length()) {
        add(jsonArray.getString(index))
      }
    }
  }

  fun loadMetadata(path: String = "ml/metadata.json"): AiModelMetadata {
    val json = readAsset(path)
    val jsonObject = JSONObject(json)

    return AiModelMetadata(
      sequenceLength = jsonObject.getInt("sequence_length"),
      maxTokens = jsonObject.getInt("max_tokens"),
      embeddingDim = jsonObject.getInt("embedding_dim"),
      padToken = jsonObject.getString("pad_token"),
      unkToken = jsonObject.getString("unk_token"),
    )
  }

  private fun readAsset(path: String): String =
    context.assets
      .open(path)
      .bufferedReader()
      .use { it.readText() }
}