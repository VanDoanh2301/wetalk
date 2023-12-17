package com.example.wetalk.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.regex.Pattern

@Parcelize
data class Question(
    var id: Int = 0,
    var question: String,
    var answer_a: String,
    var answer_b: String,
    var answer_c: String,
    var answer_d: String,
    var answer_correct: String,
    var explain: String,
    var hint: String,
    var image: String
) : Parcelable, Cloneable {

    fun getQuestionKo(): String {
        val pattern = Pattern.compile("\\$[^\"]+?.webp\\$")
        val matcher = pattern.matcher(question)
        while (matcher.find()) {
            question = question.replace(
                matcher.group(),
                "<img src='${matcher.group().replace("$", "").replace("$", "")}'>"
            )
        }
        return question
    }

    fun getExplainKo(): String {
        val pattern = Pattern.compile("\\$[^\"]+?.webp\\$")
        val matcher = pattern.matcher(explain)
        while (matcher.find()) {
            explain = explain.replace(
                matcher.group(),
                "<img src='${matcher.group().replace("$", "").replace("$", "")}'>"
            )
        }
        return explain
    }

    fun getImageKo(): String {
        return if (image.isEmpty()) {
            ""
        } else {
            image.replace(".png", ".webp")
        }
    }

    fun getAnswer_correctABCD(): String {
        return when {
            answer_correct == answer_a -> "A"
            answer_correct == answer_b -> "B"
            answer_correct == answer_c -> "C"
            answer_correct == answer_d -> "D"
            else -> answer_correct
        }
    }

}
