package com.example.habitua.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habitua.databinding.HabitCardBinding
import com.example.habitua.data.Habit
import com.example.habitua.ui.home.HomeViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class HabitAdapter (private var habits: List<Habit>, private val viewModel: HomeViewModel) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>()  {

        inner class HabitViewHolder(private val binding: HabitCardBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(habit: Habit) {
                binding.habit = habit
                binding.executePendingBindings()

                // since we want actions when toggleSwitch is changed
                binding.toggleSwitch.setOnCheckedChangeListener { _, isActive ->
                    /*
                    viewModel.viewModelScope.launch {
                        viewModel.onHabitToggled(habit, isActive)
                    }
                    */

                    // other things
                    viewModel.logHabitChange(habit)

                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = HabitCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

       return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit)
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyItemInserted(habits.size - 1)
    }

// notifyDataSetChanged() for when  we want a full refresh, aka, when the data changes.


}
