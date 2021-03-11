package com.woody.cat.holic.framework.base

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.Interactors

open class BaseViewModel(protected val interactors: Interactors) : ViewModel() {
}