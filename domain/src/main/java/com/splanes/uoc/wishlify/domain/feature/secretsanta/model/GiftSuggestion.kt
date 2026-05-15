package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

data class GiftSuggestion(
  val type: Type,
  val category: HobbyCategory,
  val confidence: Float,
) {

  enum class Type {
    TechAudioAccessories,
    TechHeadphones,
    TechGadgets,
    TechUsefulDevices,
    TechMobileAccessories,
    TechChargersAndCables,
    TechProductivitySetup,
    TechOfficeAccessories,
    TechSmartHomeDevices,
    TechHomeAutomation,

    GamingGames,
    GamingGiftCards,
    GamingAccessories,
    GamingPeripherals,
    GamingCollectibles,
    GamingSpecialEditions,
    GamingMerchandising,
    GamingDecor,

    BooksNovels,
    BooksReadingAccessories,
    BooksTechnical,
    BooksLearning,
    BooksComicsManga,
    BooksGraphicNovels,
    BooksEssays,
    BooksNonFiction,

    OutdoorHikingAccessories,
    OutdoorMountainGear,
    OutdoorCampingGear,
    OutdoorBasicKit,
    OutdoorTravelAccessories,
    OutdoorWeekendTrip,

    SportsGymAccessories,
    SportsTrainingMaterial,
    SportsRunningGear,
    SportsTechnicalClothing,
    SportsSwimmingAccessories,
    SportsPoolGear,
    SportsTeamMerch,
    SportsMatchExperience,
    SportsCyclingAccessories,
    SportsBikeGear,

    FashionClothing,
    FashionStyleItems,
    FashionAccessories,
    FashionPersonalComplements,
    FashionSneakers,
    FashionShoeCare,
    FashionJewelry,
    FashionSmallDetails,

    CookingKitchenTools,
    CookingUsefulAccessories,
    CookingGourmetProducts,
    CookingFoodExperience,

    MusicInstrumentAccessories,
    MusicLearningMaterial,
    MusicLiveEvents,
    MusicExperiences,

    WellbeingRelaxation,
    WellbeingRest,
    WellbeingSelfcare,
    WellbeingRoutine,
    WellbeingFitness,
    WellbeingActiveCare,

    ExperiencesCultural,
    ExperiencesMuseumTheatreCinema,
    ExperiencesAdventure,
    ExperiencesOutdoorActivity,
    ExperiencesFood,
    ExperiencesTastingOrDinner,
    ExperiencesRomantic,
    ExperiencesCouplePlan,

    EntertainmentMerch,
    EntertainmentCollectibles,
    EntertainmentEvents,
    EntertainmentConventions,

    ArtCreativeMaterial,
    ArtCraftKit,

    GenericPractical,
    GenericExperience,
    GenericFlexibleDetail,
  }

  enum class HobbyCategory {
    Technology,
    Gaming,
    Books,
    Sports,
    Outdoor,
    Cooking,
    Music,
    Fashion,
    Experiences,
    Wellbeing,
    Entertainment,
    Art,
    Unknown;

    companion object {
      fun fromLabel(label: String): HobbyCategory =
        when {
          label.startsWith("tech_") -> Technology
          label.startsWith("gaming_") -> Gaming
          label.startsWith("books_") -> Books
          label.startsWith("sports_") -> Sports
          label.startsWith("outdoor_") -> Outdoor
          label.startsWith("cooking_") -> Cooking
          label.startsWith("music_") -> Music
          label.startsWith("fashion_") -> Fashion
          label.startsWith("experiences_") -> Experiences
          label.startsWith("wellbeing_") -> Wellbeing
          label.startsWith("entertainment_") -> Entertainment
          label.startsWith("art_") -> Art
          else -> Unknown
        }
    }
  }

  companion object {
    val Fallback get() = listOf(
      GiftSuggestion(
        type = Type.GenericPractical,
        category = HobbyCategory.Unknown,
        confidence = 0f,
      ),
      GiftSuggestion(
        type = Type.GenericExperience,
        category = HobbyCategory.Unknown,
        confidence = 0f,
      ),
      GiftSuggestion(
        type = Type.GenericFlexibleDetail,
        category = HobbyCategory.Unknown,
        confidence = 0f,
      ),
    )
  }
}
