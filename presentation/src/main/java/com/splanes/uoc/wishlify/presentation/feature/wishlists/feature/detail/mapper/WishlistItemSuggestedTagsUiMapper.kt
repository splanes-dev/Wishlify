package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper

import android.content.Context
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.SuggestedTag
import com.splanes.uoc.wishlify.presentation.R
import java.util.Locale

class WishlistItemSuggestedTagsUiMapper(
  private val context: Context,
) {

  fun map(suggested: List<SuggestedTag>): String {
    return suggested
      .map { tag -> context.getString(tag.toStringRes()) }
      .joinToString(", ") { it.capitalize() }
  }

  private fun String.capitalize(): String {
    return replaceFirstChar {
      if (it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
      } else {
        it.toString()
      }
    }
  }

  private fun SuggestedTag.toStringRes(): Int {
    return when (this) {
      SuggestedTag.Technology -> R.string.suggested_tag_technology
      SuggestedTag.Audio -> R.string.suggested_tag_audio
      SuggestedTag.Mobile -> R.string.suggested_tag_mobile
      SuggestedTag.Productivity -> R.string.suggested_tag_productivity
      SuggestedTag.SmartHome -> R.string.suggested_tag_smart_home

      SuggestedTag.Gaming -> R.string.suggested_tag_gaming
      SuggestedTag.VideoGames -> R.string.suggested_tag_video_games
      SuggestedTag.GamingSetup -> R.string.suggested_tag_gaming_setup
      SuggestedTag.Collectibles -> R.string.suggested_tag_collectibles

      SuggestedTag.Books -> R.string.suggested_tag_books
      SuggestedTag.TechnicalBooks -> R.string.suggested_tag_technical_books
      SuggestedTag.ComicsManga -> R.string.suggested_tag_comics_manga
      SuggestedTag.Essays -> R.string.suggested_tag_essays

      SuggestedTag.Outdoor -> R.string.suggested_tag_outdoor
      SuggestedTag.Hiking -> R.string.suggested_tag_hiking
      SuggestedTag.Camping -> R.string.suggested_tag_camping
      SuggestedTag.Travel -> R.string.suggested_tag_travel

      SuggestedTag.Sports -> R.string.suggested_tag_sports
      SuggestedTag.Gym -> R.string.suggested_tag_gym
      SuggestedTag.Running -> R.string.suggested_tag_running
      SuggestedTag.Swimming -> R.string.suggested_tag_swimming
      SuggestedTag.Cycling -> R.string.suggested_tag_cycling

      SuggestedTag.Fashion -> R.string.suggested_tag_fashion
      SuggestedTag.Sneakers -> R.string.suggested_tag_sneakers
      SuggestedTag.Jewelry -> R.string.suggested_tag_jewelry

      SuggestedTag.Cooking -> R.string.suggested_tag_cooking
      SuggestedTag.Gourmet -> R.string.suggested_tag_gourmet

      SuggestedTag.Music -> R.string.suggested_tag_music
      SuggestedTag.Concerts -> R.string.suggested_tag_concerts

      SuggestedTag.Wellbeing -> R.string.suggested_tag_wellbeing
      SuggestedTag.Selfcare -> R.string.suggested_tag_selfcare
      SuggestedTag.Fitness -> R.string.suggested_tag_fitness

      SuggestedTag.Experiences -> R.string.suggested_tag_experiences
      SuggestedTag.Culture -> R.string.suggested_tag_culture
      SuggestedTag.Adventure -> R.string.suggested_tag_adventure
      SuggestedTag.Food -> R.string.suggested_tag_food
      SuggestedTag.Romantic -> R.string.suggested_tag_romantic

      SuggestedTag.Entertainment -> R.string.suggested_tag_entertainment
      SuggestedTag.Anime -> R.string.suggested_tag_anime
      SuggestedTag.Art -> R.string.suggested_tag_art
    }
  }
}