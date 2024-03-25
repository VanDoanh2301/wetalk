package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.wetalk.data.model.objectmodel.TopicRequest

class VocabularyArrayAdapter(context: Context, topics: List<TopicRequest>) :
    ArrayAdapter<TopicRequest>(context, android.R.layout.simple_spinner_item, topics) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as TextView
        textView.text = getItem(position)?.content
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView
        textView.text = getItem(position)?.content
        return view
    }
}