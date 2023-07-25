package com.example.educatapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.educatapp.R
import com.example.educatapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.educatapp.User
import com.example.educatapp.ui.home.HomeViewModel
import com.google.android.material.navigation.NavigationView
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
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProfileBinding
    private lateinit var nameEditText: EditText
    private lateinit var roleDropdown: Spinner
    private lateinit var saveButton: Button
    private lateinit var valueEventListener: ValueEventListener


    private val roleOptions = arrayOf("Student", "Teacher")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = binding.root

        // Access the views from the binding object
        nameEditText = binding.nameEditText
        roleDropdown = binding.roleDropdown
        saveButton = binding.saveButton


        // Set up the role dropdown menu
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleDropdown.adapter = adapter


// Manually set the role options to the dropdown
        roleDropdown.setSelection(0) // Set the initial selection to the first option




        // Handle save button click
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val selectedRole = roleDropdown.selectedItem.toString()
            // Retrieve user information from Firebase Realtime Database


            // Retrieve a reference to the "users" node in the database
            val usersRef = FirebaseDatabase.getInstance().getReference("users")

            // Create a new User object with the name and role
            val user = User(name, selectedRole)

            // Generate a unique identifier for the user
            val userRef = usersRef.push()

            // Set the user object as the value under the generated child node
            userRef.setValue(user)
                .addOnSuccessListener {
                    // Display a success message or perform any other required actions
                    Toast.makeText(requireContext(), "User information saved successfully", Toast.LENGTH_SHORT).show()
                    val userName = nameEditText.text.toString()

// Find the NavigationView and access the HeaderView
                    val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)
                    val headerView: View = navView.getHeaderView(0)

// Find the TextView in the HeaderView
                    val userNameTextView: TextView = headerView.findViewById(R.id.userNameTextView)

// Update the text of the TextView with the user's name
                    userNameTextView.text = userName

                    val homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
                    val selectedRole = roleDropdown.selectedItem.toString()

                    if (selectedRole == "Student") {
                        homeViewModel.setHeroTitle("Welcome Student")
                    } else if (selectedRole == "Teacher") {
                        homeViewModel.setHeroTitle("Welcome Teacher")
                    }


                }
                .addOnFailureListener { exception ->
                    // Handle the failure case
                    Toast.makeText(requireContext(), "Failed to save user information: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

            // Save the user's role to shared preferences






        }

        return root
    }

    companion object {
        private val roleOptions = arrayOf("Student", "Teacher")
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}





