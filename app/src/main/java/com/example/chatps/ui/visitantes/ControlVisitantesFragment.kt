package com.example.chatps.ui.visitantes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatps.databinding.FragmentVisitantesBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ControlVisitantesFragment : Fragment(), VisitListener {

    private lateinit var database: FirebaseFirestore
    private var _binding: FragmentVisitantesBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitantesBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(requireContext())
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID) ?: ""

        if ("Residente" == preferenceManager.getString(Constants.KEY_ROL)) {
            binding.addVisita.setOnClickListener {
                startActivity(Intent(requireContext(), AddVisitantesActivity::class.java))
            }
            getVisits()
        } else {
            getAllVisitantes()
            binding.addVisita.visibility = View.INVISIBLE
        }

        setupRecyclerView()
        return view
    }

    private fun getVisits() {
        loading(true)

        database.collection(Constants.KEY_COLLECTION_VISITANTES)
            .whereEqualTo(Constants.KEY_VISITANTES_USER_ID, currentUserId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                loading(false)
                if (task.isSuccessful) {
                    val visits = task.result?.mapNotNull { queryDocumentSnapshot ->
                        queryDocumentSnapshot.toObject(Visit::class.java).apply {
                            visitId = queryDocumentSnapshot.id
                        }
                    } ?: emptyList()

                    if (visits.isNotEmpty()) {
                        binding.visitanteView.adapter = VisitsAdapter(visits, this)
                        binding.visitanteView.visibility = View.VISIBLE
                        binding.textErrorMessage.visibility = View.GONE
                    } else {
                        showErrorMessage("No tienes visitantes")
                    }
                } else {
                    showErrorMessage("Error al cargar visitantes")
                }
            }
    }

    private fun getAllVisitantes() {
        loading(true)

        database.collection(Constants.KEY_COLLECTION_VISITANTES)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                loading(false)
                if (task.isSuccessful) {
                    val visits = task.result?.mapNotNull { queryDocumentSnapshot ->
                        queryDocumentSnapshot.toObject(Visit::class.java).apply {
                            visitId = queryDocumentSnapshot.id
                        }
                    } ?: emptyList()

                    if (visits.isNotEmpty()) {
                        binding.visitanteView.adapter = VisitsAdapter(visits, this)
                        binding.visitanteView.visibility = View.VISIBLE
                        binding.textErrorMessage.visibility = View.GONE
                    } else {
                        showErrorMessage("No hay visitantes")
                    }
                } else {
                    showErrorMessage("Error al cargar visitantes")
                }
            }
    }

    private fun showErrorMessage(message: String) {
        binding.textErrorMessage.text = message
        binding.textErrorMessage.visibility = View.VISIBLE
        binding.visitanteView.visibility = View.GONE // Oculta la vista de visitantes si hay un error
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onVisitClicked(visit: Visit?) {
        val intent = Intent(requireContext(), VisitanteDatesActivity::class.java).apply {
            putExtra(Constants.KEY_VISITANTES_ID, visit?.visitId)
        }
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        binding.visitanteView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
