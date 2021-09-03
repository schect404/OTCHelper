package com.atittoapps.otchelper.details.levels

sealed class LevelsItems {

    data class Level(val level: String) : LevelsItems()

    object Skeleton : LevelsItems() {

        fun createSkeletons() = arrayListOf<Skeleton>().apply {
            for(i in 0..10) add(Skeleton)
        }
    }
}

