package com.example.wetalk.ui.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.wetalk.data.local.PracticeQuest
import com.example.wetalk.data.local.TestQuest
import com.example.wetalk.ui.fragment.PracticeFragment

class ViewPagerPracticeAdapter(
    fm: FragmentManager,
    behavior: Int,
    questionNews: ArrayList<TestQuest>
) :
    FragmentStatePagerAdapter(fm, behavior) {
    private val questionNews: ArrayList<TestQuest>

    init {
        this.questionNews = questionNews
    }

    override fun getItem(position: Int): Fragment {
        return PracticeFragment.init(questionNews[position], position, questionNews.size)
    }

    override fun getCount(): Int {
        return questionNews.size
    }

    override fun saveState(): Parcelable? {
        val bundle = super.saveState() as Bundle?
        bundle?.putParcelableArray("states", null)
        return bundle
    }
}