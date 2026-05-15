package com.splanes.uoc.wishlify.data.feature.ai.mapper

import com.splanes.uoc.wishlify.data.feature.ai.model.AiModelLabel
import com.splanes.uoc.wishlify.data.feature.ai.model.InterestClassificationResult
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion

class AiDataMapper {

  fun mapSuggestions(
    results: List<InterestClassificationResult>,
    maxSuggestions: Int = 6,
    maxSuggestionsPerCategory: Int = 3,
    minScore: Float = 0.35f,
  ): List<GiftSuggestion> {
    val suggestions = results
      .asSequence()
      .filter { it.score >= minScore }
      .sortedByDescending { it.score }
      .flatMap { result ->
        val modelLabel = AiModelLabel.fromValue(result.label)
          ?: return@flatMap emptyList()

        suggestionCatalog[modelLabel]
          .orEmpty()
          .map { type ->
            GiftSuggestion(
              type = type,
              category = modelLabel.category,
              confidence = result.score,
            )
          }
      }
      .distinctBy { it.type }
      .groupBy { it.category }
      .flatMap { (_, categorySuggestions) ->
        categorySuggestions.take(maxSuggestionsPerCategory)
      }
      .sortedByDescending { it.confidence }
      .take(maxSuggestions)
      .toList()

    return suggestions.ifEmpty { GiftSuggestion.Fallback }
  }

  private companion object {

    val suggestionCatalog = mapOf(
      AiModelLabel.TechAudio to listOf(
        GiftSuggestion.Type.TechAudioAccessories,
        GiftSuggestion.Type.TechHeadphones,
      ),
      AiModelLabel.TechGadgets to listOf(
        GiftSuggestion.Type.TechGadgets,
        GiftSuggestion.Type.TechUsefulDevices,
      ),
      AiModelLabel.TechMobile to listOf(
        GiftSuggestion.Type.TechMobileAccessories,
        GiftSuggestion.Type.TechChargersAndCables,
      ),
      AiModelLabel.TechProductivity to listOf(
        GiftSuggestion.Type.TechProductivitySetup,
        GiftSuggestion.Type.TechOfficeAccessories,
      ),
      AiModelLabel.TechSmartHome to listOf(
        GiftSuggestion.Type.TechSmartHomeDevices,
        GiftSuggestion.Type.TechHomeAutomation,
      ),

      AiModelLabel.GamingGames to listOf(
        GiftSuggestion.Type.GamingGames,
        GiftSuggestion.Type.GamingGiftCards,
      ),
      AiModelLabel.GamingAccessories to listOf(
        GiftSuggestion.Type.GamingAccessories,
        GiftSuggestion.Type.GamingPeripherals,
      ),
      AiModelLabel.GamingCollectibles to listOf(
        GiftSuggestion.Type.GamingCollectibles,
        GiftSuggestion.Type.GamingSpecialEditions,
      ),
      AiModelLabel.GamingMerch to listOf(
        GiftSuggestion.Type.GamingMerchandising,
        GiftSuggestion.Type.GamingDecor,
      ),

      AiModelLabel.BooksNovels to listOf(
        GiftSuggestion.Type.BooksNovels,
        GiftSuggestion.Type.BooksReadingAccessories,
      ),
      AiModelLabel.BooksTechnical to listOf(
        GiftSuggestion.Type.BooksTechnical,
        GiftSuggestion.Type.BooksLearning,
      ),
      AiModelLabel.BooksComics to listOf(
        GiftSuggestion.Type.BooksComicsManga,
        GiftSuggestion.Type.BooksGraphicNovels,
      ),
      AiModelLabel.BooksEssays to listOf(
        GiftSuggestion.Type.BooksEssays,
        GiftSuggestion.Type.BooksNonFiction,
      ),

      AiModelLabel.OutdoorHiking to listOf(
        GiftSuggestion.Type.OutdoorHikingAccessories,
        GiftSuggestion.Type.OutdoorMountainGear,
      ),
      AiModelLabel.OutdoorCamping to listOf(
        GiftSuggestion.Type.OutdoorCampingGear,
        GiftSuggestion.Type.OutdoorBasicKit,
      ),
      AiModelLabel.OutdoorTravel to listOf(
        GiftSuggestion.Type.OutdoorTravelAccessories,
        GiftSuggestion.Type.OutdoorWeekendTrip,
      ),

      AiModelLabel.SportsGym to listOf(
        GiftSuggestion.Type.SportsGymAccessories,
        GiftSuggestion.Type.SportsTrainingMaterial,
      ),
      AiModelLabel.SportsRunning to listOf(
        GiftSuggestion.Type.SportsRunningGear,
        GiftSuggestion.Type.SportsTechnicalClothing,
      ),
      AiModelLabel.SportsSwimming to listOf(
        GiftSuggestion.Type.SportsSwimmingAccessories,
        GiftSuggestion.Type.SportsPoolGear,
      ),
      AiModelLabel.SportsTeam to listOf(
        GiftSuggestion.Type.SportsTeamMerch,
        GiftSuggestion.Type.SportsMatchExperience,
      ),
      AiModelLabel.SportsCycling to listOf(
        GiftSuggestion.Type.SportsCyclingAccessories,
        GiftSuggestion.Type.SportsBikeGear,
      ),

      AiModelLabel.FashionClothing to listOf(
        GiftSuggestion.Type.FashionClothing,
        GiftSuggestion.Type.FashionStyleItems,
      ),
      AiModelLabel.FashionAccessories to listOf(
        GiftSuggestion.Type.FashionAccessories,
        GiftSuggestion.Type.FashionPersonalComplements,
      ),
      AiModelLabel.FashionSneakers to listOf(
        GiftSuggestion.Type.FashionSneakers,
        GiftSuggestion.Type.FashionShoeCare,
      ),
      AiModelLabel.FashionJewelry to listOf(
        GiftSuggestion.Type.FashionJewelry,
        GiftSuggestion.Type.FashionSmallDetails,
      ),

      AiModelLabel.CookingTools to listOf(
        GiftSuggestion.Type.CookingKitchenTools,
        GiftSuggestion.Type.CookingUsefulAccessories,
      ),
      AiModelLabel.CookingGourmet to listOf(
        GiftSuggestion.Type.CookingGourmetProducts,
        GiftSuggestion.Type.CookingFoodExperience,
      ),

      AiModelLabel.MusicInstruments to listOf(
        GiftSuggestion.Type.MusicInstrumentAccessories,
        GiftSuggestion.Type.MusicLearningMaterial,
      ),
      AiModelLabel.MusicLiveEvents to listOf(
        GiftSuggestion.Type.MusicLiveEvents,
        GiftSuggestion.Type.MusicExperiences,
      ),

      AiModelLabel.WellbeingRelaxation to listOf(
        GiftSuggestion.Type.WellbeingRelaxation,
        GiftSuggestion.Type.WellbeingRest,
      ),
      AiModelLabel.WellbeingSelfcare to listOf(
        GiftSuggestion.Type.WellbeingSelfcare,
        GiftSuggestion.Type.WellbeingRoutine,
      ),
      AiModelLabel.WellbeingFitness to listOf(
        GiftSuggestion.Type.WellbeingFitness,
        GiftSuggestion.Type.WellbeingActiveCare,
      ),

      AiModelLabel.ExperiencesCultural to listOf(
        GiftSuggestion.Type.ExperiencesCultural,
        GiftSuggestion.Type.ExperiencesMuseumTheatreCinema,
      ),
      AiModelLabel.ExperiencesAdventure to listOf(
        GiftSuggestion.Type.ExperiencesAdventure,
        GiftSuggestion.Type.ExperiencesOutdoorActivity,
      ),
      AiModelLabel.ExperiencesFood to listOf(
        GiftSuggestion.Type.ExperiencesFood,
        GiftSuggestion.Type.ExperiencesTastingOrDinner,
      ),
      AiModelLabel.ExperiencesRomantic to listOf(
        GiftSuggestion.Type.ExperiencesRomantic,
        GiftSuggestion.Type.ExperiencesCouplePlan,
      ),

      AiModelLabel.EntertainmentMerch to listOf(
        GiftSuggestion.Type.EntertainmentMerch,
        GiftSuggestion.Type.EntertainmentCollectibles,
      ),
      AiModelLabel.EntertainmentEvents to listOf(
        GiftSuggestion.Type.EntertainmentEvents,
        GiftSuggestion.Type.EntertainmentConventions,
      ),

      AiModelLabel.ArtCreative to listOf(
        GiftSuggestion.Type.ArtCreativeMaterial,
        GiftSuggestion.Type.ArtCraftKit,
      ),
    )
  }
}