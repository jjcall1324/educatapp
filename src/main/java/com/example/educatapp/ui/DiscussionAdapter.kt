package com.example.educatapp.ui

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educatapp.R
import java.util.Date
import java.util.Locale

class DiscussionAdapter(private val discussions: List<Discussion>) : RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder>() {

    // ViewHolder class to hold the views for each discussion item
    class DiscussionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
    }

    // Inflate the discussion item layout and return a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_discussion, parent, false)
        return DiscussionViewHolder(itemView)
    }

    // Bind data to the views in each discussion item
    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        val currentDiscussion = discussions[position]

        // Bind discussion data to the views in the ViewHolder
        holder.titleTextView.text = currentDiscussion.title
        holder.contentTextView.text = currentDiscussion.content
        holder.authorTextView.text = currentDiscussion.author

        // Format and display the timestamp
        val timestamp = currentDiscussion.timestamp
        val formattedTimestamp = convertTimestampToReadableFormat(timestamp)
        holder.timestampTextView.text = formattedTimestamp
    }
    private fun convertTimestampToReadableFormat(timestamp: String): String {
        // Implement your desired timestamp formatting logic here
        // For example, you can use SimpleDateFormat to format the timestamp
        // into a more readable date and time format.
        return timestamp
    }

    // Return the number of discussions in the list
    override fun getItemCount(): Int {
        return discussions.size
    }
}
