package com.splanes.uoc.wishlify.domain.feature.secretsanta.helper

class SecretSantaDrawExecutor {

  fun isFeasible(
    participants: List<String>,
    exclusions: Map<String, List<String>>
  ): Boolean {
    return solve(participants, exclusions) != null
  }

  fun executeOrThrow(
    participants: List<String>,
    exclusions: Map<String, List<String>>
  ): Map<String, String> {
     return solve(participants, exclusions) ?: throw IllegalStateException("Draw is not possible.")
  }

  private fun solve(
    participants: List<String>,
    exclusions: Map<String, List<String>>
  ): Map<String, String>? {

    // Fast fail 1: less than 2 participants -> not valid
    if (participants.size < 2) return null

    val exclusionSet = exclusions
      .flatMap { (giver, blocked) -> blocked.map { receiver -> giver to receiver } }
      .toSet()

    // Graph: giver index to possible receiver indexes
    val graph: List<List<Int>> = participants.map { giver ->
      participants.mapIndexedNotNull { receiverIndex, receiver ->
        when {
          giver == receiver -> null
          (giver to receiver) in exclusionSet -> null
          else -> receiverIndex
        }
      }
    }

    // Fast fail 2: Somebody can't gift to anybody -> not valid
    if (graph.any { it.isEmpty() }) return null

    val matchToGiver = IntArray(participants.size) { -1 }

    var matched = 0
    for (giverIndex in participants.indices.shuffled()) {
      val visited = BooleanArray(participants.size)
      if (tryKuhn(graph, matchToGiver, giverIndex, visited)) {
        matched++
      }
    }

    if (matched != participants.size) return null

    return buildMap {
      for (receiverIndex in participants.indices) {
        val giverIndex = matchToGiver[receiverIndex]
        if (giverIndex == -1) return null

        put(
          participants[giverIndex],
          participants[receiverIndex]
        )
      }
    }
  }

  /**
   * Refs:
   * https://stackoverflow.com/questions/25519832/augmenting-path-algorithm-maximum-matching
   * https://cp-algorithms.com/graph/kuhn_maximum_bipartite_matching.html
   */
  private fun tryKuhn(
    graph: List<List<Int>>,
    matchToGiver: IntArray,
    giverIndex: Int,
    visited: BooleanArray
  ): Boolean {
    for (receiverIndex in graph[giverIndex].shuffled()) {
      if (visited[receiverIndex]) continue
      visited[receiverIndex] = true

      if (
        matchToGiver[receiverIndex] == -1 ||
        tryKuhn(graph, matchToGiver, matchToGiver[receiverIndex], visited)
      ) {
        matchToGiver[receiverIndex] = giverIndex
        return true
      }
    }
    return false
  }
}