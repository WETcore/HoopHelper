package app.appworks.school.publisher.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.MainActivity
import com.aqua.hoophelper.MainActivityViewModel
import com.aqua.hoophelper.database.HoopRepository
import com.aqua.hoophelper.home.HomeViewModel
import com.aqua.hoophelper.live.LiveViewModel
import com.aqua.hoophelper.match.MatchViewModel
import com.aqua.hoophelper.profile.ProfileViewModel
import com.aqua.hoophelper.team.TeamViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: HoopRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainActivityViewModel::class.java) ->
                    MainActivityViewModel(repository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                isAssignableFrom(TeamViewModel::class.java) ->
                    TeamViewModel(repository)

                isAssignableFrom(MatchViewModel::class.java) ->
                    MatchViewModel(repository)

                isAssignableFrom(LiveViewModel::class.java) ->
                    LiveViewModel(repository)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
