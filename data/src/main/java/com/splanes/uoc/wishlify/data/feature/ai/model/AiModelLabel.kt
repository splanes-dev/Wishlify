package com.splanes.uoc.wishlify.data.feature.ai.model

import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion

enum class AiModelLabel(
  val value: String,
  val category: GiftSuggestion.HobbyCategory,
) {

  TechAudio(
    value = "tech_audio",
    category = GiftSuggestion.HobbyCategory.Technology,
  ),

  TechGadgets(
    value = "tech_gadgets",
    category = GiftSuggestion.HobbyCategory.Technology,
  ),

  TechMobile(
    value = "tech_mobile",
    category = GiftSuggestion.HobbyCategory.Technology,
  ),

  TechProductivity(
    value = "tech_productivity",
    category = GiftSuggestion.HobbyCategory.Technology,
  ),

  TechSmartHome(
    value = "tech_smart_home",
    category = GiftSuggestion.HobbyCategory.Technology,
  ),

  GamingGames(
    value = "gaming_games",
    category = GiftSuggestion.HobbyCategory.Gaming,
  ),

  GamingAccessories(
    value = "gaming_accessories",
    category = GiftSuggestion.HobbyCategory.Gaming,
  ),

  GamingCollectibles(
    value = "gaming_collectibles",
    category = GiftSuggestion.HobbyCategory.Gaming,
  ),

  GamingMerch(
    value = "gaming_merch",
    category = GiftSuggestion.HobbyCategory.Gaming,
  ),

  BooksNovels(
    value = "books_novels",
    category = GiftSuggestion.HobbyCategory.Books,
  ),

  BooksTechnical(
    value = "books_technical",
    category = GiftSuggestion.HobbyCategory.Books,
  ),

  BooksComics(
    value = "books_comics",
    category = GiftSuggestion.HobbyCategory.Books,
  ),

  BooksEssays(
    value = "books_essays",
    category = GiftSuggestion.HobbyCategory.Books,
  ),

  OutdoorHiking(
    value = "outdoor_hiking",
    category = GiftSuggestion.HobbyCategory.Outdoor,
  ),

  OutdoorCamping(
    value = "outdoor_camping",
    category = GiftSuggestion.HobbyCategory.Outdoor,
  ),

  OutdoorTravel(
    value = "outdoor_travel",
    category = GiftSuggestion.HobbyCategory.Outdoor,
  ),

  SportsGym(
    value = "sports_gym",
    category = GiftSuggestion.HobbyCategory.Sports,
  ),

  SportsRunning(
    value = "sports_running",
    category = GiftSuggestion.HobbyCategory.Sports,
  ),

  SportsSwimming(
    value = "sports_swimming",
    category = GiftSuggestion.HobbyCategory.Sports,
  ),

  SportsTeam(
    value = "sports_team",
    category = GiftSuggestion.HobbyCategory.Sports,
  ),

  SportsCycling(
    value = "sports_cycling",
    category = GiftSuggestion.HobbyCategory.Sports,
  ),

  FashionClothing(
    value = "fashion_clothing",
    category = GiftSuggestion.HobbyCategory.Fashion,
  ),

  FashionAccessories(
    value = "fashion_accessories",
    category = GiftSuggestion.HobbyCategory.Fashion,
  ),

  FashionSneakers(
    value = "fashion_sneakers",
    category = GiftSuggestion.HobbyCategory.Fashion,
  ),

  FashionJewelry(
    value = "fashion_jewelry",
    category = GiftSuggestion.HobbyCategory.Fashion,
  ),

  CookingTools(
    value = "cooking_tools",
    category = GiftSuggestion.HobbyCategory.Cooking,
  ),

  CookingGourmet(
    value = "cooking_gourmet",
    category = GiftSuggestion.HobbyCategory.Cooking,
  ),

  MusicInstruments(
    value = "music_instruments",
    category = GiftSuggestion.HobbyCategory.Music,
  ),

  MusicLiveEvents(
    value = "music_live_events",
    category = GiftSuggestion.HobbyCategory.Music,
  ),

  WellbeingRelaxation(
    value = "wellbeing_relaxation",
    category = GiftSuggestion.HobbyCategory.Wellbeing,
  ),

  WellbeingSelfcare(
    value = "wellbeing_selfcare",
    category = GiftSuggestion.HobbyCategory.Wellbeing,
  ),

  WellbeingFitness(
    value = "wellbeing_fitness",
    category = GiftSuggestion.HobbyCategory.Wellbeing,
  ),

  ExperiencesCultural(
    value = "experiences_cultural",
    category = GiftSuggestion.HobbyCategory.Experiences,
  ),

  ExperiencesAdventure(
    value = "experiences_adventure",
    category = GiftSuggestion.HobbyCategory.Experiences,
  ),

  ExperiencesFood(
    value = "experiences_food",
    category = GiftSuggestion.HobbyCategory.Experiences,
  ),

  ExperiencesRomantic(
    value = "experiences_romantic",
    category = GiftSuggestion.HobbyCategory.Experiences,
  ),

  EntertainmentMerch(
    value = "entertainment_merch",
    category = GiftSuggestion.HobbyCategory.Entertainment,
  ),

  EntertainmentEvents(
    value = "entertainment_events",
    category = GiftSuggestion.HobbyCategory.Entertainment,
  ),

  ArtCreative(
    value = "art_creative",
    category = GiftSuggestion.HobbyCategory.Art,
  );

  companion object {

    fun fromValue(value: String): AiModelLabel? =
      entries.firstOrNull { it.value == value }
  }
}