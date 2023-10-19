package com.cns.mytaskmanager.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.databinding.FragmentSplashBinding
import com.cns.mytaskmanager.utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private val splashViewModel: SplashViewModel by viewModels()
    lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splashViewModel.delay.observe(viewLifecycleOwner) {
            safeNavigate(
                SplashFragmentDirections.actionSplashFragmentToHomeFragment()
            )
        }

//        binding.welcome.setOnClickListener {
//            // binding.welcome.text = "Hello 2"
//            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
//        }

    }
}