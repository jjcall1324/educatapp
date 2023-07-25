package com.example.educatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import com.example.educatapp.R
import com.bumptech.glide.Glide


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FileFragment : Fragment() {
    // Define a companion object to handle creating a new instance of the fragment with arguments
    companion object {
        const val ARG_FILE_URL = "file_url"

        fun newInstance(fileUrl: String): FileFragment {
            val fragment = FileFragment()
            val args = Bundle()
            args.putString(ARG_FILE_URL, fileUrl)
            fragment.arguments = args
            return fragment
        }
    }

    // Define other necessary variables and UI elements
    private lateinit var fileUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the file URL from arguments
        arguments?.let {
            fileUrl = it.getString(ARG_FILE_URL) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_file, container, false)

        // Get the file URL from the arguments
        val fileUrl = arguments?.getString(ARG_FILE_URL)

        // Load the file content into the WebView
        val fileWebView: WebView = root.findViewById(R.id.fileWebView)
        fileWebView.settings.javaScriptEnabled = true // Enable JavaScript if needed

        // Load the file URL into the WebView if it's not null, otherwise show an error page
        fileWebView.loadUrl(fileUrl ?: "file:///android_asset/error.html")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the file extension from the file URL
        val fileExtension = fileUrl.substringAfterLast(".", "")

        // Check the file extension and load the file content accordingly
        when {
            fileExtension.equals("pdf", ignoreCase = true) -> {
                // Load PDF file
                loadPdfFile(fileUrl)
            }
            fileExtension.equals("jpg", ignoreCase = true) ||
                    fileExtension.equals("jpeg", ignoreCase = true) ||
                    fileExtension.equals("png", ignoreCase = true) -> {
                // Load image file (assuming the URL points to an image)
                loadImageFile(fileUrl)
            }
            // Handle other file types here
            else -> {
                // For unsupported file types, show a message or handle as needed
                showUnsupportedFileTypeMessage()
            }
        }
    }


    // Example method to load a PDF file into the WebView
    private fun loadPdfFile(fileUrl: String) {
        val fileWebView: WebView = requireView().findViewById(R.id.fileWebView)


        // Load the PDF file URL into the WebView
        fileWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=$fileUrl")
    }

    // Example method to load an image file into an ImageView (you can use a library like Glide)
    private fun loadImageFile(fileUrl: String) {
        val imageView: ImageView = requireView().findViewById(R.id.imageView)

        // Use a library like Glide to load the image into the ImageView
        Glide.with(requireContext())
            .load(fileUrl)
            .into(imageView)
    }

    // Example method to show a message for unsupported file types
    private fun showUnsupportedFileTypeMessage() {
        // For unsupported file types, you can show a message to the user or handle as needed
        Toast.makeText(requireContext(), "Unsupported file type", Toast.LENGTH_SHORT).show()
    }

}
