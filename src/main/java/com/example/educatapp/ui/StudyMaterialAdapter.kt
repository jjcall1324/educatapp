import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.educatapp.R



class StudyMaterial {
    var id: String? = null
    var title: String? = null
    var description: String? = null
    var fileUrl: String? = null

    constructor() {
        // Default no-argument constructor required by Firebase
    }

    constructor(id: String, title: String, description: String, fileUrl: String) {
        this.id = id
        this.title = title
        this.description = description
        this.fileUrl = fileUrl
    }
}



class StudyMaterialAdapter(
    private val itemClickListener: OnItemClickListener?
) : RecyclerView.Adapter<StudyMaterialAdapter.StudyMaterialViewHolder>() {

    // List to hold the study materials
    private var studyMaterials: List<StudyMaterial> = emptyList()

    // Function to update the study materials list
    fun submitList(newList: List<StudyMaterial>) {
        studyMaterials = newList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(studyMaterial: StudyMaterial)
    }

    // ViewHolder class to hold the views for each item
    class StudyMaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        // Add other views for the study material item if needed
    }

    // Inflate the layout for each item and return a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyMaterialViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_study_material, parent, false)
        return StudyMaterialViewHolder(itemView)
    }

    // Bind data to the views in each item
    override fun onBindViewHolder(holder: StudyMaterialViewHolder, position: Int) {
        val studyMaterial = studyMaterials[position]
        holder.titleTextView.text = studyMaterial.title
        holder.descriptionTextView.text = studyMaterial.description
        // Bind other data to the views for the study material item if needed

        // Set click listener for the item view using itemClickListener
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(studyMaterial)
        }
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return studyMaterials.size
    }
}

