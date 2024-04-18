package com.aidevu.signage.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.aidevu.signage.databinding.ActivityEmptyBinding
import com.aidevu.signage.repository.Repository
import com.aidevu.signage.ui.base.BaseActivity
import com.aidevu.signage.viewmodel.EmptyViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmptyActivity : BaseActivity<ActivityEmptyBinding, EmptyViewModel>() {

    @Inject
    lateinit var repository: Repository

    override fun createViewModel(): EmptyViewModel {
        return ViewModelProvider(this)[EmptyViewModel::class.java]
    }

    override fun createViewBinding(layoutInflater: LayoutInflater): ActivityEmptyBinding {
        return ActivityEmptyBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeData()
    }

    override fun observeData() {

    }
}