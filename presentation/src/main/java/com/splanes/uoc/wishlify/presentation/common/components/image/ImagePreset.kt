package com.splanes.uoc.wishlify.presentation.common.components.image

import androidx.annotation.DrawableRes
import com.splanes.uoc.wishlify.presentation.R

enum class ImagePreset(
  val id: Int,
  @param:DrawableRes val res: Int
) {
  Gift(1, R.drawable.preset_gift),
  Star(2, R.drawable.preset_star),
  GiftBox(3, R.drawable.preset_gift_box),
  BirthCake(4, R.drawable.preset_birth_cake),
  Christmas(5, R.drawable.preset_christmas),
  Group(6, R.drawable.preset_group),
  Romantic(7, R.drawable.preset_romantic),
  Tech(8, R.drawable.preset_tech),
  Travel(9, R.drawable.preset_travel),
}