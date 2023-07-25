package com.example.educatapp.ui

import StudyMaterial
import StudyMaterialAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educatapp.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContentFragment : Fragment(),StudyMaterialAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val REQUEST_FILE_PICKER = 100

    // This variable will store the selected file URI
    private var selectedFileUri: Uri? = null

    // At the top of the ContentFragment class, declare a variable to store the Firebase Storage reference.
    private lateinit var storageRef: StorageReference

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText




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
        val root = inflater.inflate(R.layout.fragment_content, container, false)

        // Find the EditText views by their IDs and assign them to the variables.
        titleEditText = root.findViewById(R.id.titleEditText)
        descriptionEditText = root.findViewById(R.id.descriptionEditText)


        val selectFileButton: Button = root.findViewById(R.id.selectFileButton)
        selectFileButton.setOnClickListener {
            openFilePicker()
        }

        // Step 2: Set up RecyclerView for displaying study materials
        val contentRecyclerView: RecyclerView = root.findViewById(R.id.contentRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        contentRecyclerView.layoutManager = layoutManager

// Pass the OnItemClickListener to the adapter
        val adapter = StudyMaterialAdapter(this)
        contentRecyclerView.adapter = adapter



        // Step 2: Retrieve study materials from Firebase Realtime Database
        val studyMaterialsRef = FirebaseDatabase.getInstance().getReference("study_materials")
        val query = studyMaterialsRef.orderByKey() // Optionally, you can order the study materials by some key
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val studyMaterialsList = mutableListOf<StudyMaterial>()
                for (snapshot in dataSnapshot.children) {
                    val studyMaterial = snapshot.getValue(StudyMaterial::class.java)
                    studyMaterial?.let { studyMaterialsList.add(it) }
                }
                adapter.submitList(studyMaterialsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the database error
            }
        })
        // Get the "Upload" button and handle its click
        val uploadButton: Button = root.findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            uploadFile() // Call the method to initiate file upload
        }

        return root
    }
    // Method to open the file picker
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Set the MIME type to allow any file type
        startActivityForResult(intent, REQUEST_FILE_PICKER)
    }

    override fun onItemClick(studyMaterial: StudyMaterial) {
        val options = arrayOf("View", "Delete")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // View option selected, navigate to the FileFragment
                    navigateToFileFragment(studyMaterial.fileUrl)
                }
                1 -> {
                    // Delete option selected, implement logic to delete the file
                    // For example, you can show a confirmation dialog before deleting.
                    showDeleteConfirmationDialog(studyMaterial)
                }
            }
        }
        builder.show()
    }



    // Override the onActivityResult() method to handle the result of the file picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            // File is selected
            data?.data?.let { uri ->
                // Save the selected file URI
                selectedFileUri = uri

                // Update the TextView to display the selected file name
                val selectedFileNameTextView: TextView = requireView().findViewById(R.id.selectedFileNameTextView)
                selectedFileNameTextView.text = "Selected File: " + getFileNameFromUri(uri)
            }
        }
    }

    // Method to get the file name from the file URI
    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameColumnIndex >= 0) {
                    val displayName = it.getString(displayNameColumnIndex)
                    // Extract the file extension from the display name
                    val fileExtension = displayName.substringAfterLast(".")
                    return "file_${System.currentTimeMillis()}.$fileExtension"
                }
            }
        }
        return null
    }


    // Method to handle the file upload button click
    private fun uploadFile() {
        // Get the text from the title and description EditText fields.
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()

        // Call the method to upload the file to Firebase Storage, passing the title and description as parameters.
        selectedFileUri?.let { uri ->
            uploadFileToStorage(uri, title, description)
        } ?: run {
            Toast.makeText(requireContext(), "Please select a file first", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to upload the file to Firebase Storage
    private fun uploadFileToStorage(fileUri: Uri,title: String, description: String) {
        // Get a reference to the Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference


        // Generate a unique file name or path for the upload
        val fileName = getFileNameFromUri(fileUri)

        if (fileName == null) {
            Toast.makeText(requireContext(), "Failed to get file name.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileRef = storageRef.child("study_materials/$fileName")


        // Upload the file to Firebase Storage
        fileRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                // File upload success
                // Now retrieve the download URL and save the file metadata to Firebase Realtime Database
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Add a null-check here
                    downloadUrl?.let {
                        // Ensure that the fileUrl is not null before saving the metadata
                        val fileUrl = it.toString()
                        if (fileUrl.isNotEmpty()) {
                            // Call a method to save the file metadata to Firebase Realtime Database
                            saveFileMetadataToDatabase(fileName, title, description, fileUrl)
                        } else {
                            Toast.makeText(requireContext(), "File URL is empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // File upload failed
                // Handle the failure case
                // For example, show a toast with the error message
                Toast.makeText(requireContext(), "File upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Method to save the file metadata to Firebase Realtime Database
    private fun saveFileMetadataToDatabase(fileName: String,title: String, description: String, downloadUrl: String) {
        val studyMaterialsRef = FirebaseDatabase.getInstance().getReference("study_materials")
        val newStudyMaterialRef = studyMaterialsRef.push()

        val studyMaterialId = studyMaterialsRef.push().key ?: ""

// Create a StudyMaterial object with the file metadata and the generated ID
        val studyMaterial = StudyMaterial(studyMaterialId, title, description, downloadUrl)

        // Save the StudyMaterial object to Firebase Realtime Database using the generated ID as the key


        studyMaterialsRef.child(studyMaterialId!!).setValue(studyMaterial)
            .addOnSuccessListener {
                // File metadata save success
                // Handle the success case
                // For example, show a toast with a success message
                Toast.makeText(requireContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // File metadata save failed
                // Handle the failure case
                // For example, show a toast with the error message
                Toast.makeText(requireContext(), "File metadata save failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun navigateToFileFragment(fileUrl: String?) {
        // Provide a default URL in case the fileUrl is null
        val defaultFileUrl = "DEFAULT_FILE_URL"

        // Get the actual file URL or use the default URL if it's null
        val actualFileUrl = fileUrl ?: defaultFileUrl

        // Create a new fragment with the selected file's contents
        val fileFragment = FileFragment.newInstance(actualFileUrl)

        // Create a new bundle to pass the fileUrl as an argument to the FileFragment
        val bundle = Bundle()
        bundle.putString(FileFragment.ARG_FILE_URL, actualFileUrl)


        // Use the NavController to navigate to the FileFragment
        findNavController().navigate(R.id.action_contentFragment_to_fileFragment, bundle)
    }
    private fun removeStudyMaterialFromDatabase(studyMaterial: StudyMaterial) {
        val studyMaterialsRef = FirebaseDatabase.getInstance().getReference("study_materials")
        val studyMaterialId = studyMaterial.id

        if (studyMaterialId != null) {
            val studyMaterialRef = studyMaterialsRef.child(studyMaterialId)

            studyMaterialRef.removeValue()
                .addOnSuccessListener {
                    // File metadata removed successfully
                    // Handle the success case
                    // For example, show a toast with a success message
                    Toast.makeText(requireContext(), "File removed successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // File metadata removal failed
                    // Handle the failure case
                    // For example, show a toast with the error message
                    Toast.makeText(requireContext(), "File removal failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle the case where studyMaterial.id is null (optional)
            Toast.makeText(requireContext(), "Study material ID is null", Toast.LENGTH_SHORT).show()
        }
    }



    private fun showDeleteConfirmationDialog(studyMaterial: StudyMaterial) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete File")
            .setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Delete") { _, _ ->
                // User clicked the "Delete" button, proceed with deletion
                removeStudyMaterialFromDatabase(studyMaterial)
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}




