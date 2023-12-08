package com.gurumlab.vocaroutine.data.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.model.Vocabulary

class ReviewTypeConverter {
    @TypeConverter
    fun fromReview(value: Review): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toReview(value: String): Review {
        return Gson().fromJson(value, Review::class.java)
    }
}

class VocabularyListTypeConverter {
    @TypeConverter
    fun fromVocabularyList(value: List<Vocabulary>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toVocabularyList(value: String): List<Vocabulary> {
        return Gson().fromJson(value, Array<Vocabulary>::class.java).toList()
    }

}
