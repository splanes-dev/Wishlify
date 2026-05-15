package com.splanes.uoc.wishlify.data.feature.ai.infrastructure.di

import com.splanes.uoc.wishlify.data.feature.ai.InterestTextPreprocessorFactory
import com.splanes.uoc.wishlify.data.feature.ai.TFLiteInterpreterFactory
import com.splanes.uoc.wishlify.data.feature.ai.datasource.InterestClassifierLocalDataSource
import com.splanes.uoc.wishlify.data.feature.ai.mapper.AiDataMapper
import com.splanes.uoc.wishlify.data.feature.ai.repository.AiRepositoryImpl
import com.splanes.uoc.wishlify.data.feature.ai.util.AiAssetsLoader
import com.splanes.uoc.wishlify.domain.feature.ia.repository.AiRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val AiDataModule = module {
  singleOf(::AiAssetsLoader)
  single { InterestTextPreprocessorFactory(loader = get()).create() }
  single { TFLiteInterpreterFactory(androidContext()).create() }
  singleOf(::InterestClassifierLocalDataSource)
  singleOf(::AiRepositoryImpl) bind AiRepository::class
  singleOf(::AiDataMapper)
}