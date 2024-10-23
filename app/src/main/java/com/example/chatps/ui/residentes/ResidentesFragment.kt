package com.example.chatps.ui.residentes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatps.databinding.FragmentResidentesBinding
import com.example.chatps.ui.chat.activities.ChatActivity
import com.example.chatps.ui.chat.adapters.UsersAdapter
import com.example.chatps.ui.chat.listeners.UserListener
import com.example.chatps.ui.chat.models.User
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ResidentesFragment : Fragment(), UserListener {

    private var _binding: FragmentResidentesBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResidentesBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())

        setupRecyclerView()
        getUsers()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getUsers() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_ROL, "Residente")
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                loading(false)
                if (task.isSuccessful && task.result != null) {
                    val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot in task.result!!) {
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }
                        val user = User().apply {
                            name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                            email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                            token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                            id = queryDocumentSnapshot.id
                        }
                        users.add(user)
                    }
                    if (users.isNotEmpty()) {
                        val usersAdapter = UsersAdapter(users, this)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE
                        binding.textErrorMessage.visibility = View.GONE
                    } else {
                        showErrorMessage("No hay usuarios disponibles")
                    }
                } else {
                    // Log the exception for debugging
                    task.exception?.let { e ->
                        Log.e("ResidentesFragment", "Error fetching users", e)
                    }
                    showErrorMessage("Error al cargar usuarios")
                }
            }
    }

    private fun showErrorMessage(message: String) {
        binding.textErrorMessage.text = message
        binding.textErrorMessage.visibility = View.VISIBLE
        binding.usersRecyclerView.visibility = View.GONE
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onUserClicked(user: User?) {
        val intent = Intent(requireContext(), ResidenteProfileActivity::class.java)
        intent.putExtra(Constants.KEY_USER_ID, user?.id) // Pass the user ID, ensure the key is correct
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

