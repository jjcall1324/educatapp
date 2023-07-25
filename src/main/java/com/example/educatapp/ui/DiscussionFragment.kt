package com.example.educatapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educatapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiscussionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiscussionFragment : Fragment() {

    private lateinit var discussionAdapter: DiscussionAdapter
    private val discussionsList = mutableListOf<Discussion>()
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var postButton: Button





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_discussion, container, false)

        // Set up RecyclerView for displaying discussions
        val discussionRecyclerView: RecyclerView = root.findViewById(R.id.discussionRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        discussionRecyclerView.layoutManager = layoutManager

        // Initialize the DiscussionAdapter with the list of discussions
        discussionAdapter = DiscussionAdapter(discussionsList)
        discussionRecyclerView.adapter = discussionAdapter

        // Fetch discussions data from Firebase Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().getReference("discussions")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                discussionsList.clear() // Clear the existing list before adding new data

                // Loop through the dataSnapshot to extract Discussion objects
                for (snapshot in dataSnapshot.children) {
                    val discussion = snapshot.getValue(Discussion::class.java)
                    discussion?.let { discussionsList.add(it) }
                }

                // Notify the adapter that the data has changed
                discussionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur while fetching data from Firebase
                Log.e("DiscussionFragment", "Failed to fetch discussions: ${databaseError.message}")
            }
        })

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the EditText views and the Button by their IDs
        titleEditText = view.findViewById(R.id.titleEditText)
        contentEditText = view.findViewById(R.id.contentEditText)
        postButton = view.findViewById(R.id.postButton)

        // Set click listener on the postButton to handle new discussion post
        postButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()
            if (title.isNotEmpty() && content.isNotEmpty()) {
                // Create a new Discussion object with the user input
                val timestamp = System.currentTimeMillis().toString() // Convert Long to String
                val newDiscussion = Discussion(title, content, "John Doe", timestamp)

                // Get a reference to the Firebase Realtime Database
                val databaseRef = FirebaseDatabase.getInstance().getReference("discussions")

                // Push the new discussion object to Firebase with a unique ID
                val newDiscussionRef = databaseRef.push()
                newDiscussionRef.setValue(newDiscussion)
                    .addOnSuccessListener {
                        // Clear the EditText fields after successful post
                        titleEditText.text.clear()
                        contentEditText.text.clear()
                        Toast.makeText(requireContext(), "Discussion posted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to post discussion: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiscussionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}




