package com.example.chatps.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatps.R
import com.example.chatps.databinding.FragmentHomeBinding
import com.example.chatps.ui.LlamadasActivity
import com.example.chatps.ui.chat.activities.MainChatActivity
import com.example.chatps.ui.chat.activities.SignInActivity
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.example.chatps.ui.dudas_e_inquietudes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions


@Suppress("UNREACHABLE_CODE")
@SuppressLint("ResourceType")
class HomeFragment : Fragment() {

    private var isExpanded = false

    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.animator.from_bottom_fab)
    }
    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.animator.to_bottom_fab)
    }
    private val rotateClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.animator.rotate_clock_wise)
    }
    private val fromBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.animator.from_bottom_anim)
    }
    private val toBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.animator.to_bottom_anim)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())

        val root: View = binding.root

        binding.callButton1.setOnClickListener {
            if (isExpanded) {
                shrinkFab()
            } else {
                expandFab()
            }
        }
        binding.callButton2.setOnClickListener {
            val intent = Intent(requireContext(), LlamadasActivity::class.java)
            startActivity(intent)
        }

        binding.callButton3.setOnClickListener {
            val intent = Intent(requireContext(), MainChatActivity::class.java)
            startActivity(intent)
        }

        binding.callButton4.setOnClickListener {
            val intent = Intent(requireContext(), dudas_e_inquietudes::class.java)
            startActivity(intent)
        }

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        userDetails()
        return root

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shrinkFab() {
        binding.transparentBg.startAnimation(toBottomBgAnim)
        binding.callButton2.startAnimation(toBottomFabAnim)
        binding.callButton3.startAnimation(toBottomFabAnim)
        binding.callButton4.startAnimation(toBottomFabAnim)
        isExpanded = false
    }

    private fun expandFab() {
        binding.transparentBg.startAnimation(fromBottomBgAnim)
        binding.callButton1.startAnimation(rotateClockWiseFabAnim)
        binding.callButton2.startAnimation(fromBottomFabAnim)
        binding.callButton3.startAnimation(fromBottomFabAnim)
        binding.callButton4.startAnimation(fromBottomFabAnim)
        isExpanded = true
    }

    fun handleOnBackPressed() {
        if (isExpanded) {
            shrinkFab()
        }
    }

    fun userDetails() {
        val name = preferenceManager.getString(Constants.KEY_NAME)
        binding.textName.text = if (name != null) "¡Bienvenido $name!" else "Nombre no disponible"

        val imageString = preferenceManager.getString(Constants.KEY_IMAGE)
        if (imageString != null) {
            try {
                // Cargar la imagen usando Glide para mejorar la calidad
                Glide.with(this)
                    .asBitmap()
                    .load(Base64.decode(imageString, Base64.DEFAULT))
                    .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                    .into(binding.imageProfile)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                binding.imageProfile.setImageResource(R.drawable.default_profile_image)
            }
        } else {
            binding.imageProfile.setImageResource(R.drawable.default_profile_image)
        }
    }

}

