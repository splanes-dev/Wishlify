package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.mapper

import android.content.Context
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.model.GiftSuggestionUiModel

class GiftSuggestionUiMapper(
  private val context: Context,
) {

  fun map(suggestions: List<GiftSuggestion>): List<GiftSuggestionUiModel> =
    suggestions.map { suggestion ->
      GiftSuggestionUiModel(
        text = context.getString(suggestion.type.toStringRes()),
        confidence = suggestion.confidence
      )
    }.sortedByDescending { it.confidence }

  private fun GiftSuggestion.Type.toStringRes(): Int =
    when (this) {
      GiftSuggestion.Type.TechAudioAccessories -> R.string.gift_suggestion_tech_audio_accessories
      GiftSuggestion.Type.TechHeadphones -> R.string.gift_suggestion_tech_headphones
      GiftSuggestion.Type.TechGadgets -> R.string.gift_suggestion_tech_gadgets
      GiftSuggestion.Type.TechUsefulDevices -> R.string.gift_suggestion_tech_useful_devices
      GiftSuggestion.Type.TechMobileAccessories -> R.string.gift_suggestion_tech_mobile_accessories
      GiftSuggestion.Type.TechChargersAndCables -> R.string.gift_suggestion_tech_chargers_and_cables
      GiftSuggestion.Type.TechProductivitySetup -> R.string.gift_suggestion_tech_productivity_setup
      GiftSuggestion.Type.TechOfficeAccessories -> R.string.gift_suggestion_tech_office_accessories
      GiftSuggestion.Type.TechSmartHomeDevices -> R.string.gift_suggestion_tech_smart_home_devices
      GiftSuggestion.Type.TechHomeAutomation -> R.string.gift_suggestion_tech_home_automation

      GiftSuggestion.Type.GamingGames -> R.string.gift_suggestion_gaming_games
      GiftSuggestion.Type.GamingGiftCards -> R.string.gift_suggestion_gaming_gift_cards
      GiftSuggestion.Type.GamingAccessories -> R.string.gift_suggestion_gaming_accessories
      GiftSuggestion.Type.GamingPeripherals -> R.string.gift_suggestion_gaming_peripherals
      GiftSuggestion.Type.GamingCollectibles -> R.string.gift_suggestion_gaming_collectibles
      GiftSuggestion.Type.GamingSpecialEditions -> R.string.gift_suggestion_gaming_special_editions
      GiftSuggestion.Type.GamingMerchandising -> R.string.gift_suggestion_gaming_merchandising
      GiftSuggestion.Type.GamingDecor -> R.string.gift_suggestion_gaming_decor

      GiftSuggestion.Type.BooksNovels -> R.string.gift_suggestion_books_novels
      GiftSuggestion.Type.BooksReadingAccessories -> R.string.gift_suggestion_books_reading_accessories
      GiftSuggestion.Type.BooksTechnical -> R.string.gift_suggestion_books_technical
      GiftSuggestion.Type.BooksLearning -> R.string.gift_suggestion_books_learning
      GiftSuggestion.Type.BooksComicsManga -> R.string.gift_suggestion_books_comics_manga
      GiftSuggestion.Type.BooksGraphicNovels -> R.string.gift_suggestion_books_graphic_novels
      GiftSuggestion.Type.BooksEssays -> R.string.gift_suggestion_books_essays
      GiftSuggestion.Type.BooksNonFiction -> R.string.gift_suggestion_books_non_fiction

      GiftSuggestion.Type.OutdoorHikingAccessories -> R.string.gift_suggestion_outdoor_hiking_accessories
      GiftSuggestion.Type.OutdoorMountainGear -> R.string.gift_suggestion_outdoor_mountain_gear
      GiftSuggestion.Type.OutdoorCampingGear -> R.string.gift_suggestion_outdoor_camping_gear
      GiftSuggestion.Type.OutdoorBasicKit -> R.string.gift_suggestion_outdoor_basic_kit
      GiftSuggestion.Type.OutdoorTravelAccessories -> R.string.gift_suggestion_outdoor_travel_accessories
      GiftSuggestion.Type.OutdoorWeekendTrip -> R.string.gift_suggestion_outdoor_weekend_trip

      GiftSuggestion.Type.SportsGymAccessories -> R.string.gift_suggestion_sports_gym_accessories
      GiftSuggestion.Type.SportsTrainingMaterial -> R.string.gift_suggestion_sports_training_material
      GiftSuggestion.Type.SportsRunningGear -> R.string.gift_suggestion_sports_running_gear
      GiftSuggestion.Type.SportsTechnicalClothing -> R.string.gift_suggestion_sports_technical_clothing
      GiftSuggestion.Type.SportsSwimmingAccessories -> R.string.gift_suggestion_sports_swimming_accessories
      GiftSuggestion.Type.SportsPoolGear -> R.string.gift_suggestion_sports_pool_gear
      GiftSuggestion.Type.SportsTeamMerch -> R.string.gift_suggestion_sports_team_merch
      GiftSuggestion.Type.SportsMatchExperience -> R.string.gift_suggestion_sports_match_experience
      GiftSuggestion.Type.SportsCyclingAccessories -> R.string.gift_suggestion_sports_cycling_accessories
      GiftSuggestion.Type.SportsBikeGear -> R.string.gift_suggestion_sports_bike_gear

      GiftSuggestion.Type.FashionClothing -> R.string.gift_suggestion_fashion_clothing
      GiftSuggestion.Type.FashionStyleItems -> R.string.gift_suggestion_fashion_style_items
      GiftSuggestion.Type.FashionAccessories -> R.string.gift_suggestion_fashion_accessories
      GiftSuggestion.Type.FashionPersonalComplements -> R.string.gift_suggestion_fashion_personal_complements
      GiftSuggestion.Type.FashionSneakers -> R.string.gift_suggestion_fashion_sneakers
      GiftSuggestion.Type.FashionShoeCare -> R.string.gift_suggestion_fashion_shoe_care
      GiftSuggestion.Type.FashionJewelry -> R.string.gift_suggestion_fashion_jewelry
      GiftSuggestion.Type.FashionSmallDetails -> R.string.gift_suggestion_fashion_small_details

      GiftSuggestion.Type.CookingKitchenTools -> R.string.gift_suggestion_cooking_kitchen_tools
      GiftSuggestion.Type.CookingUsefulAccessories -> R.string.gift_suggestion_cooking_useful_accessories
      GiftSuggestion.Type.CookingGourmetProducts -> R.string.gift_suggestion_cooking_gourmet_products
      GiftSuggestion.Type.CookingFoodExperience -> R.string.gift_suggestion_cooking_food_experience

      GiftSuggestion.Type.MusicInstrumentAccessories -> R.string.gift_suggestion_music_instrument_accessories
      GiftSuggestion.Type.MusicLearningMaterial -> R.string.gift_suggestion_music_learning_material
      GiftSuggestion.Type.MusicLiveEvents -> R.string.gift_suggestion_music_live_events
      GiftSuggestion.Type.MusicExperiences -> R.string.gift_suggestion_music_experiences

      GiftSuggestion.Type.WellbeingRelaxation -> R.string.gift_suggestion_wellbeing_relaxation
      GiftSuggestion.Type.WellbeingRest -> R.string.gift_suggestion_wellbeing_rest
      GiftSuggestion.Type.WellbeingSelfcare -> R.string.gift_suggestion_wellbeing_selfcare
      GiftSuggestion.Type.WellbeingRoutine -> R.string.gift_suggestion_wellbeing_routine
      GiftSuggestion.Type.WellbeingFitness -> R.string.gift_suggestion_wellbeing_fitness
      GiftSuggestion.Type.WellbeingActiveCare -> R.string.gift_suggestion_wellbeing_active_care

      GiftSuggestion.Type.ExperiencesCultural -> R.string.gift_suggestion_experiences_cultural
      GiftSuggestion.Type.ExperiencesMuseumTheatreCinema -> R.string.gift_suggestion_experiences_museum_theatre_cinema
      GiftSuggestion.Type.ExperiencesAdventure -> R.string.gift_suggestion_experiences_adventure
      GiftSuggestion.Type.ExperiencesOutdoorActivity -> R.string.gift_suggestion_experiences_outdoor_activity
      GiftSuggestion.Type.ExperiencesFood -> R.string.gift_suggestion_experiences_food
      GiftSuggestion.Type.ExperiencesTastingOrDinner -> R.string.gift_suggestion_experiences_tasting_or_dinner
      GiftSuggestion.Type.ExperiencesRomantic -> R.string.gift_suggestion_experiences_romantic
      GiftSuggestion.Type.ExperiencesCouplePlan -> R.string.gift_suggestion_experiences_couple_plan

      GiftSuggestion.Type.EntertainmentMerch -> R.string.gift_suggestion_entertainment_merch
      GiftSuggestion.Type.EntertainmentCollectibles -> R.string.gift_suggestion_entertainment_collectibles
      GiftSuggestion.Type.EntertainmentEvents -> R.string.gift_suggestion_entertainment_events
      GiftSuggestion.Type.EntertainmentConventions -> R.string.gift_suggestion_entertainment_conventions

      GiftSuggestion.Type.ArtCreativeMaterial -> R.string.gift_suggestion_art_creative_material
      GiftSuggestion.Type.ArtCraftKit -> R.string.gift_suggestion_art_craft_kit

      GiftSuggestion.Type.GenericPractical -> R.string.gift_suggestion_generic_practical
      GiftSuggestion.Type.GenericExperience -> R.string.gift_suggestion_generic_experience
      GiftSuggestion.Type.GenericFlexibleDetail -> R.string.gift_suggestion_generic_flexible_detail
    }
}