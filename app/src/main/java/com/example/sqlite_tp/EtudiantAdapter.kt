import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite_tp.EtudiantBC
import com.example.sqlite_tp.R

class EtudiantAdapter(
    val etudiants: MutableList<EtudiantBC>,
    private val clickListener: (EtudiantBC) -> Unit,
    private val deleteListener: (EtudiantBC) -> Unit
) : RecyclerView.Adapter<EtudiantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_etudiant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val etudiant = etudiants[position]
        holder.bind(etudiant, clickListener, deleteListener)
    }

    override fun getItemCount(): Int {
        return etudiants.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNom: TextView = itemView.findViewById(R.id.textViewNom)
        private val textViewPrenom: TextView = itemView.findViewById(R.id.textViewPrenom)
        private val textViewPhone: TextView = itemView.findViewById(R.id.textViewPhone)
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
        private val imageViewGender: ImageView = itemView.findViewById(R.id.imageViewGender)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(
            etudiant: EtudiantBC,
            clickListener: (EtudiantBC) -> Unit,
            deleteListener: (EtudiantBC) -> Unit
        ) {
            textViewNom.text = "Nom : ${etudiant.nom} "
            textViewPrenom.text = "Prenom : ${etudiant.prenom} "
            textViewPhone.text = "Phone : ${etudiant.phone}"
            textViewEmail.text = "Email : ${etudiant.email} "

            if (etudiant.gender.equals("male", ignoreCase = true)) {
                imageViewGender.setImageResource(R.drawable.man)
            } else {
                imageViewGender.setImageResource(R.drawable.woman)
            }

            btnDelete.setOnClickListener {
                showDeleteConfirmationDialog(etudiant)
            }

            itemView.setOnClickListener {
                clickListener(etudiant)
            }
        }

        private fun showDeleteConfirmationDialog(etudiant: EtudiantBC) {
            val builder = AlertDialog.Builder(itemView.context)

            builder.setTitle("Confirmer la suppression")
            builder.setMessage("Êtes-vous sûr de supprimer cette entrée?")
            builder.setPositiveButton("Oui") { _, _ ->
                deleteListener(etudiant)
            }
            builder.setNegativeButton("Non") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }
    }

    fun updateEtudiant(etudiant: EtudiantBC) {
        val position = etudiants.indexOfFirst { it.nom == etudiant.nom && it.prenom == etudiant.prenom }
        if (position != -1) {
            etudiants[position] = etudiant
            notifyItemChanged(position)
        }
    }
}
