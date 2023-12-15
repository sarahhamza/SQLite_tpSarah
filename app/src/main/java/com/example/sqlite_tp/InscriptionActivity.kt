package com.example.sqlite_tp

import EtudiantAdapter
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class InscriptionActivity : AppCompatActivity() {

    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTel: EditText
    private lateinit var etGender: Spinner
    private lateinit var etEmail: EditText
    private lateinit var etMdp: EditText
    private var isUpdate: Boolean = false
    private lateinit var existingEtudiant: EtudiantBC
    private lateinit var adapter: EtudiantAdapter  // Assuming you have an adapter variable

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        etNom = findViewById(R.id.etNom)
        etPrenom = findViewById(R.id.etPrenom)
        etTel = findViewById(R.id.etTel)
        etEmail = findViewById(R.id.etEmail)
        etMdp = findViewById(R.id.etMdp)
        etGender = findViewById(R.id.etGender)
        val spinnerGender: Spinner = findViewById(R.id.etGender)
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }
        spinnerGender.setSelection(0)

        val btnValider: Button = findViewById(R.id.btnValider)
        val btnAnnuler: Button = findViewById(R.id.btnAnnuler)

        val updateIntent = intent.getSerializableExtra("ETUDIANT") as? EtudiantBC
        if (updateIntent != null) {
            isUpdate = true
            existingEtudiant = updateIntent
            // Set existing data to the UI fields
            etNom.setText(existingEtudiant.nom)
            etPrenom.setText(existingEtudiant.prenom)
            etTel.setText(existingEtudiant.phone)
            etEmail.setText(existingEtudiant.email)
            etMdp.setText(existingEtudiant.mdp)
            // Set spinner selection
            val genderArray = resources.getStringArray(R.array.gender_array)
            etGender.setSelection(genderArray.indexOf(existingEtudiant.gender))
        }

        btnValider.setOnClickListener {
            if (champsNonVides()) {
                val dbHelper = EtudiantDBHelper(applicationContext)
                val db = dbHelper.writableDatabase

                val values = ContentValues().apply {
                    put(EtudiantBC.EtudiantEntry.COLUMN_NAME_NOM, etNom.text.toString())
                    put(EtudiantBC.EtudiantEntry.COLUMN_NAME_PRENOM, etPrenom.text.toString())
                    put(EtudiantBC.EtudiantEntry.COLUMN_NAME_PHONE, etTel.text.toString())
                    put(EtudiantBC.EtudiantEntry.COLUMN_NAME_GENDER, etGender.selectedItem.toString())
                    put(EtudiantBC.EtudiantEntry.COLUMN_NAME_EMAIL, etEmail.text.toString())
                    put(EtudiantBC.EtudiantEntry.COLUMN_NAME_MDP, etMdp.text.toString())
                }

                if (isUpdate) {
                    // Update existing student
                    val whereClause =
                        "${EtudiantBC.EtudiantEntry.COLUMN_NAME_NOM} = ? AND " +
                                "${EtudiantBC.EtudiantEntry.COLUMN_NAME_PRENOM} = ?"
                    val whereArgs = arrayOf(existingEtudiant.nom, existingEtudiant.prenom)
                    val updatedRows =
                        db.update(EtudiantBC.EtudiantEntry.TABLE_NAME, values, whereClause, whereArgs)

                    db.close()
                    dbHelper.close()

                    if (updatedRows > 0) {
                        afficherMessage("Validation", "Mise à jour réussie!")
                        // Update the adapter
                        adapter.updateEtudiant(
                            EtudiantBC(
                                etNom.text.toString(),
                                etPrenom.text.toString(),
                                etTel.text.toString(),
                                etGender.selectedItem.toString(),
                                etEmail.text.toString(),
                                etMdp.text.toString()
                            )
                        )
                        finish()
                    } else {
                        afficherMessage("Erreur", "Une erreur s'est produite lors de la mise à jour.")
                    }
                } else {
                    // Insert new student
                    val newRowId = db.insert(EtudiantBC.EtudiantEntry.TABLE_NAME, null, values)

                    db.close()
                    dbHelper.close()

                    if (newRowId != -1L) {
                        afficherMessage("Validation", "Inscription réussie!")
                        // Notify the adapter
                        adapter.notifyItemInserted(adapter.itemCount)
                        finish()
                    } else {
                        afficherMessage("Erreur", "Une erreur s'est produite lors de l'inscription.")
                    }
                }
            } else {
                afficherMessage("Erreur", "Veuillez remplir tous les champs.")
            }
        }

        btnAnnuler.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun afficherMessage(titre: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titre)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun champsNonVides(): Boolean {
        return etNom.text.isNotBlank() &&
                etPrenom.text.isNotBlank() &&
                etTel.text.isNotBlank() &&
                etGender.selectedItem.toString().isNotBlank() &&
                etEmail.text.isNotBlank() &&
                etMdp.text.isNotBlank()
    }
}

