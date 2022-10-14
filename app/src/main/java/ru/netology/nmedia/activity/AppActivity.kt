package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_app)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            //передаём аргументы (текст) между фрагментами через
            //запись в bundle и отдачу его в navigate
//в разметке activity_app указан androidx.fragment.app.FragmentContainerView вместо fragment
//из-за этого при попытке передать текст внутрь нашего приложения (неявный интент на создание поста)
//происходит проблема, описанная ниже, под кодом - решаем её первым способом
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//            findNavController(R.id.nav_host_fragment).navigate(
            navHostFragment.navController.navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }

    }
}

// When the NavHostFragment is inflated using FragmentContainerView, if you attempt to use
// findNavController in the onCreate() of the Activity, the nav controller cannot be found.
// This is because when the fragment is inflated in the constructor of FragmentContainerView,
// the fragmentManager is in the INITIALIZING state, and therefore the added fragment only goes
// up to initializing. For the nav controller to be properly set, the fragment view needs to be
// created and onViewCreated() needs to be dispatched, which does not happen until the
// ACTIVITY_CREATED state.
//
// We should either force the fragment to create a view right after being added to the
// FragmentManager, or find a way to wait for the FragmentManager to reach a higher state before
// allowing the Navigation onCreate to be called.
//
// The following options can be used as viable work arounds:
//
// 1. Retrieve the navController directly from the NavHostFragment.
//
// val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
// val navController = navHostFragment.navController
//
// 2. Post the call to the findNavController method on a handler and execute all actions that need
// it after that post is complete.
// 3. Continue to use the fragment tag (<fragment>) to inflate the NavHostFragment

// Status: Won't Fix (Infeasible)
//
// It is not possible for the view of the NavHostFragment to be available in the onCreate() of the
// Activity. In this case, the NavHostFragment lifecycle method follows the Activity lifecycle
// methods directly therefore, when the activity is on onCreate() so is the NavHostFragment.
// However, if the Fragment Lifecycle was driven with a LifecycleObserver
// (https://issuetracker.google.com/issues/127528777), onCreate() could be dispatched for a
// Fragment separately from the onCreate() for an Activity (or a parent Fragment if using a child
// Fragment). This means that if you were using a LifecycleObserver, it would be possible for the
// views to be available when onCreate() is dispatched and findNavController() would work in the
// LifecycleObserver's onCreate().