package com.example.habitua.ui.home

import com.example.habitua.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habitua.adapter.HabitAdapter
import com.example.habitua.databinding.FragmentHomeBinding
import com.example.habitua.data.Habit
import androidx.lifecycle.Observer
import com.example.habitua.data.ConditionLogsDao
import com.example.habitua.data.HabitDao
import com.example.habitua.data.StreakLogsDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

    return root
  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.habitContainer)


        val adapter = HabitAdapter(emptyList(), homeViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Observe changes to the habit list
        homeViewModel.habitList.observe(viewLifecycleOwner, Observer { habits ->
            adapter.updateHabits(habits)
        })

        val name = view.findViewById<EditText>(R.id.editTextName)
        val description = view.findViewById<EditText>(R.id.editTextDescription)
        val createButton = view.findViewById<Button>(R.id.createButton)

        createButton.setOnClickListener {

            val nameText = name.text.toString().trim()
            val conditionText = description.text.toString().trim()

            if (nameText.isEmpty() || conditionText.isEmpty()) {
                Toast.makeText(context, "A habit has a name and a condition. Gib plox, as the kids say", Toast.LENGTH_SHORT).show()
            }
            else {

                val newHabit = Habit(
                    dateCreated = System.currentTimeMillis(),
                    name = nameText,
                    condition = conditionText
                )
                // CLEANSE
                name.text.clear()
                description.text.clear()

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}